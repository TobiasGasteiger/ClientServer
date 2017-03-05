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
	private static boolean running = true;


	public static void main(String[] args) {
		//Create the Server
		serverSocket = createServer();

		//When everything was successful and the client did not close the server, then the server waits for another client
		System.out.println("Server gestartet\nWarte auf eingehende Verbindungen:");
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
			while (!Thread.currentThread().isInterrupted() && running) {
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

	protected static void closeServer(ObjectOutputStream out) throws IOException {
		out.writeObject(new Answer("ok", "Der Server wurde erfolgreich geschlossen, keine neuen Clients können sich mehr verbinden"));
		//Set running = flase so the while loop stops and no clients are able to connect to the server
		running = false;
		//Close the server and do not allow any client to connect again to the server
		serverSocket.close();
	}

	private static void printError(String error){
		JOptionPane.showMessageDialog(null,
				error,
				"Error-Server",
				JOptionPane.ERROR_MESSAGE);
	}
}
