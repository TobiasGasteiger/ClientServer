package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendThread extends Thread{

    private final DatagramSocket clientSocket;
    private final InetAddress serverAddress;
    private final int serverPort;

    public SendThread(DatagramSocket clientSocket, InetAddress serverAddress, int serverPort) {
        this.clientSocket = clientSocket;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        //Create the input to read messages from the CLI
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        //Send a message with our name so the Server knows that we joined the chat
        String name = "";
        try {
            System.out.print("Name eingeben: ");
            name = input.readLine();
        } catch (IOException e) {
            System.err.println("Fehler beim Einlesen der Daten sie sind möglicherweise ungültig. Client neu starten");
            System.exit(-1);
        }
        String message = name;
        sendMessage(message);

        //While the server is running wait for an input
        while(Client.running){
            try {
                message = input.readLine();
                if(message.equals("exit")){
                    sendMessage(message);
                    Client.stop();
                } else if(message.matches(".*\\w.*")){
                    message = name + ": " + message;
                    sendMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Fehler beim Einlesen der Daten sie sind möglicherweise ungültig. Client evtl. neu starten");
            }
        }
    }

    private void sendMessage(String message){
        //Checks if the string is empty or not
        if(message.length() > 0){
            // Save the String into a Byte Buffer
            byte[] sendData = new byte[1024];
            sendData = message.getBytes();
            // Create a DatagramPacket with the data, IP address and port number
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            // Send the UDP packet to server
            try {
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                System.err.println("Das UDP Paket konnte nicht versendet werden");
            }
        }
    }
}
