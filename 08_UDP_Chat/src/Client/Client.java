package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client {

    protected static boolean running = true;

    public static void main(String[] args){
        //Get the ServerIP and his Port over the CLI
        InetAddress serverAddress = null;
        int serverPort = 9090;
        try{
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Serveradresse eingeben: ");
            serverAddress = InetAddress.getByName(input.readLine());
            System.out.print("ServerPort eingeben: ");
            serverPort = Integer.parseInt(input.readLine());
        } catch(IOException e){
            System.err.println("Fehler beim Einlesen der Daten sie sind möglicherweise ungültig. Client neu starten");
            System.exit(-1);
        }

        //Create the UDP Socket
        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Fehler beim erstellen des UPD Client Socktes. Client neu starten");
            System.exit(-1);
        }

        //Create the Server threads
        SendThread send = new SendThread(clientSocket, serverAddress, serverPort);
        send.start();
        ListenThread listen = new ListenThread(clientSocket);
        listen.start();
    }

    public static void stop(){
        running = false;
        System.exit(0);
    }
}
