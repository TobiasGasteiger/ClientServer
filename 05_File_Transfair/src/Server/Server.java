package Server;

import Protocol.Acknowledgement;
import Protocol.Answer;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	private static ServerSocket serverSocket;
	private static ArrayList<ClientPack> clients = new ArrayList<>();


	public static void main(String[] args) {
		//Create the Server
		serverSocket = createServer();

		//When everything was successful and the client did not close the server, then the server waits for another client
		System.out.println("Warte auf eingehende Verbindungen:");
		try{
			waitClients();
		} catch (IOException e) {
			System.out.println("\tDer Client hat die Verbindung verloren bzw. abgebrochen");
		}
	}


	private static ServerSocket createServer(){
		ServerSocket s = null;
		try {
			//Create a new Server Socket on the default 9090 Port
			s = new ServerSocket(9090);
		} catch (IOException e) {
			printError("Der Port ist bereits besetzt");
		}
		return s;
	}

	//Wait for a client (Only one can be handled)
	private static void waitClients() throws IOException {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		ClientPack clientPack;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				// wait for a client to connect
				Socket clientSocket = serverSocket.accept();
				// create a new client handler object for that socket,
				// and fork it in a background thread
				clientPack = new ClientPack(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream()), new ObjectInputStream(clientSocket.getInputStream()));
				clients.add(clientPack);
				threadPool.submit(new ClientHandler(clientPack));
			}
		} finally {
			//shutdown the thread-pool when we are done
			threadPool.shutdown();
		}
	}

	protected static void removeClient(ClientPack c){
		clients.remove(c);
	}

	protected static void informClients(String status, String message){
		for(ClientPack c : clients){
			try {
				c.getOut().writeObject(new Answer(status, message));
			} catch (IOException e) {
				System.out.println("Fehler beim Schreiben einer Nachricht");
			}
		}
	}

	protected static void closeServer() throws IOException {
		informClients("close", "Der Server wurde vorzeitig beendet. Die Dateiübertragung kann nicht abgeschlossen werden");
		for(ClientPack c : clients){
			//Send the answer to the server and wait for the acknowledgement, so the server can close the connection to the client
			//do that, when the client is not already closed or finished itself
			if(!c.getClient().isClosed()){
				Acknowledgement a = null;
				try {
					a = (Acknowledgement)c.getIn().readObject();
					if(a.isNoticed())
						c.getClient().close();
				} catch (ClassNotFoundException e) {
					printError("Klasse nicht gefunden");
				} finally {
					serverSocket.close();
					System.exit(0);
				}

			}
		}
	}

	private static void printError(String error){
		JOptionPane.showMessageDialog(null,
				error,
				"Error-Server",
				JOptionPane.ERROR_MESSAGE);
	}
}
