package Server;

import Protocol.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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
				authenticate(out, in);
				calculate(serverSocket, client, out, in);
				closeClient(client, in, out);
			} catch (IOException e) {
				System.out.println("\tDer Client hat die Verbindung verloren bzw. abgebrochen");
			} catch (ClassNotFoundException e) {
				printError("Klasse nicht gefunden");
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

	//Authenticate the connected client
	private static void authenticate(ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException{
		Authentication a;
		do{
			a = (Authentication) in.readObject();
			if(a.getUsername().equals("admin") && a.getPassword().equals("ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f")){
				out.writeObject(new Answer("ok", "Authentifizierung erfolgreich"));
				System.out.println("\tDer Client hat sich erfolgreich authentifieziert");
			} else {
				out.writeObject(new Answer("error", "Authentifizierung fehlgeschlagen. Passwort oder Benutzername sind falsch"));
				System.out.println("\tDer Client hat sich nicht erfolgreich authentifieziert, neue Anmeldedaten wurden angefragt");
			}
		} while(!a.getUsername().equals("admin") || !a.getPassword().equals("ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f")); //password = password123;
	}

	//Wait for a client (Only one can be handled)
	private static Socket waitClient(ServerSocket serverSocket) throws IOException {
		System.out.println("Warte auf Client");
		Socket client = serverSocket.accept();
		System.out.println("Client angenommen:");
		return client;
	}

	private static void calculate(ServerSocket serverSocket, Socket client, ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
		Request calculation = (Request) in.readObject();
		//Check if the received data is valid and calculate the result
		if (calculation != null) {
			if (calculation.getMessage().equals("kill server")) {
				//Close the server and the client
				closeServer(serverSocket, client, in, out);
			} else {
				//The ScriptEngineManager cuts the string and calculates the result on its own
				ScriptEngineManager mgr = new ScriptEngineManager();
				ScriptEngine engine = mgr.getEngineByName("JavaScript");
				try {
					out.writeObject(new Answer("ok", engine.eval(calculation.getMessage()).toString()));
					System.out.println("\tBerechnung erfolgreich durchgeführt. Client wird geschlossen");
				} catch (ScriptException e) {
					out.writeObject(new Answer("error", "Ungültige Eingabe, bitte wiederholen!"));
					System.out.println("\tBerechnung nicht erfolgreich durchgeführt. Client wird Daten erneut senden");
					//Restart function
					calculate(serverSocket, client, out, in);
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
