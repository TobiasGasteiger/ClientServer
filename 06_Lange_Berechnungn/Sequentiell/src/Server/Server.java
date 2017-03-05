package Server;

import Protocol.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

public class Server {

	public static void main(String[] args) {
		//Create the Server
		ServerSocket serverSocket = createServer();
		waitClient(serverSocket);
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
	//When everything was successful and the client did not close the server, then the server waits for another client
	private static void waitClient(ServerSocket serverSocket){
		Socket client;
		ObjectOutputStream out;
		ObjectInputStream in;

		while (true) {
			try {
				System.out.println("Warte auf Client");
				client = serverSocket.accept();
				out = new ObjectOutputStream(client.getOutputStream());
				in = new ObjectInputStream(client.getInputStream());
				System.out.println("Client angenommen:");
				//Calculate the calculation
				calculate(serverSocket, client, out, in);
				//Close client
				closeClient(client, in, out);
			} catch (IOException e) {
				System.out.println("\tDer Client hat die Verbindung verloren bzw. abgebrochen");
			} catch (ClassNotFoundException e) {
				printError("Klasse nicht gefunden");
			}
		}
	}

	private static void calculate(ServerSocket serverSocket, Socket client, ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
		Request r = (Request) in.readObject();
		//Check if the received data is valid and calculate the result
		if (r != null) {
			if (r.getMessage().equals("kill server")) {
				//Close the server and the client
				closeServer(serverSocket, client, in, out);
			} else {
				try{
					if(r.getMessage().equals("getRandom")){
						int random = (int)(Math.random() * 1000000000);
						//Sleep 0 to 10 seconds, this should simulate the long calculation
						long timeToSleep = (long)(Math.random()*5000);
						System.out.println("\tRandom Berechnung wird durchgeführt sie dauert "+ timeToSleep +" Millisekunden");
						Thread.sleep(timeToSleep);
						//send the message
						out.writeObject(new Answer("ok", Integer.toString(random)));
					} else if(r.getMessage().equals("getTime")){
						// Used to format the date
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						// Get the date today
						Date today = Calendar.getInstance().getTime();
						// Create string with right format using df
						String reportDate = df.format(today);
						//send the message
						//Sleep 0 to 10 seconds, this should simulate the long calculation
						long timeToSleep = (long)(Math.random()*5000);
						System.out.println("\tTime Berechnung wird durchgeführt sie dauert "+ timeToSleep +" Millisekunden");
						Thread.sleep(timeToSleep);
						out.writeObject(new Answer("ok", reportDate));
					} else {
						//When the input is invalid send error and wait for a new request
						out.writeObject(new Answer("error", "Ungültige Eingabe, bitte wiederholen"));
						System.out.println("\tBerechnung nicht erfolgreich durchgeführt. Client wird Daten erneut senden");
						calculate(serverSocket, client, out, in);
					}
				} catch (InterruptedException e){
					printError("Die Berechnung wurde unerwartet unterbrochen der Server wird geschlossen");
					System.exit(-1);
				}
			}

		}
	}


	private static void closeServer(ServerSocket serverSocket, Socket client, ObjectInputStream in, ObjectOutputStream out) throws IOException {
		out.writeObject(new Answer("warning", "Server wird geschlossen"));
		closeClient(client, in, out);
		serverSocket.close();
		System.out.println("\tDer Client hat den Server geschlossen");
		printError("Der Server wurde vom Client beendet");
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
