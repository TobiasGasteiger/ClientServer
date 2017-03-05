package Server;

import Protocol.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public class Server {

	public static void main(String[] args) {
		//Create the Server
		ServerSocket serverSocket = createServer();

		//When everything was successful and the client did not close the server, then the server waits for another client
		while(true){
			try{
				Socket client = waitClient(serverSocket);
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				//Download the HTML content, save it and send it
				handleConnection(serverSocket, client, out, in);
				//Close the connections
				closeClient(client, in, out);
			} catch (IOException e) {
				System.out.println("\tDer Client hat die Verbindung verloren bzw. abgebrochen");
			}
		}
	}


	private static ServerSocket createServer(){
		ServerSocket s = null;
		try {
			//Create a new Server Socket ont the default 9090 Port
			s = new ServerSocket(9090);
		} catch (IOException e) {
			printError("Der Port ist bereits besetzt");
		}
		return s;
	}

	//Wait for a client (Only one can be handled)
	private static Socket waitClient(ServerSocket serverSocket) throws IOException {
		System.out.println("Warte auf Client");
		Socket client = serverSocket.accept();
		System.out.println("Client angenommen:");
		return client;
	}

	private static void handleConnection(ServerSocket serverSocket, Socket client, ObjectOutputStream out, ObjectInputStream in) throws IOException{
		String html = "";
		try {
			do {
				html = download_HTML(serverSocket, client, out, in);
			} while (html.equals(""));
			//Save it to a local file
			save_HTML(html);
			//Send the HTML content to the client
			send_HTML(out, html);
		} catch (ClassNotFoundException e) {
			printError("Klasse nicht gefunden");
		}
	}
	private static String download_HTML(ServerSocket serverSocket, Socket client, ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
		Request r = (Request) in.readObject();
		if (r.getMessage().equals("kill server")) {
			//Close the server and the client
			closeServer(serverSocket, client, in, out);
			return "";
		} else {
			String line, all = "";
			URL myUrl;
			BufferedReader html_in = null;
			try {
				myUrl = new URL(r.getMessage());
				//Read in UTF-8 format, else üöä are not displayed right
				html_in = new BufferedReader(new InputStreamReader(myUrl.openStream(), "UTF-8"));
				//Read line per line from the URL
				while ((line = html_in.readLine()) != null) {
					all += line;
				}
			} catch(MalformedURLException e) {
				System.out.println("\tDie empfangene URL ist nicht gültig, warte auf neue URL");
				out.writeObject(new Answer("error", "Die URL ist ungültig, erneute Eingabe erforderlich [FORMAT: http://www.google.com]"));
			}
			return all;
		}
	}

	private static void save_HTML(String html){
		try {
			//Use a timestamp for the Filename
			String fileName = new SimpleDateFormat("yyyyMMddhhmmss'.html'").format(new Date());
			File file = new File(fileName);
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(html);
			out.close();
			System.out.println("\tDie Datei "+ fileName +" wurde erfolgreich geschrieben");
		} catch (IOException e) {
			System.out.println("\tDie Datei  konnte nicht abgespeichert werden, ist genügend Speicherplatz vorhanden?");
		}
	}

	private static void send_HTML(ObjectOutputStream out, String html) throws IOException {
		Answer a = new Answer("ok", "Die HTML Datei wurde erfolgreich heruntergeladen und auf dem Server gespeichert");
		a.setHtml_content(html);
		out.writeObject(a);
	}

	private static void closeServer(ServerSocket serverSocket, Socket client, ObjectInputStream in, ObjectOutputStream out) throws IOException {
		out.writeObject(new Answer("warning", "Server wird geschlossen"));
		closeClient(client, in, out);
		serverSocket.close();
		System.out.println("\tDer Client hat den Server geschlossen");
		//printError("Der Server wurde vom Client beendet");
		System.exit(0);
	}

	private static void closeClient(Socket client, ObjectInputStream in, ObjectOutputStream out) throws IOException {
		in.close();
		out.close();
		client.close();
	}

	private static void printError(String error){
		JOptionPane.showMessageDialog(null,
				error,
				"Error-Server",
				JOptionPane.ERROR_MESSAGE);
	}
}
