package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {

    private static ArrayList<ClientPack> clients = new ArrayList<>();
    private static DatagramSocket serverSocket;
    public static void main(String[] args) {
        //Create the Server UDP socket
        try {
            serverSocket = new DatagramSocket(9090);
            System.out.println("Der Server wurde gestartet");
        } catch (SocketException e) {
            System.err.println("Der Port ist bereits besetzt");
            System.exit(-1);
        }

        //Receive data and send it to all clients in the array list
        while(true){
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String clientMessage = (new String(receivePacket.getData())).trim();
                addClient(receivePacket.getAddress(), receivePacket.getPort());
                System.out.println("Empfangene Nachricht: "  + clientMessage);
                sendToClients(clientMessage, receivePacket.getAddress(), receivePacket.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void addClient(InetAddress address, int port){
        if(!clients.contains(new ClientPack(address, port))){
            clients.add(new ClientPack(address, port));
        }
    }

    private static void removeClient(InetAddress address, int port){
        if(clients.contains(new ClientPack(address, port))){
            clients.remove(new ClientPack(address, port));
        }
    }

    private static void sendToClients(String message, InetAddress address, int port){
        //Write the message to a byte array to send it
        byte[] sendData = new byte[1024];
        if(message.contains(":")){
            sendData = message.getBytes();
        } else if(message.equals("exit")){
            removeClient(address, port);
            return;
        } else {
            message = message + " hat den Chat betreten";
            sendData = message.getBytes();
        }
        //Go through the array list and send to every client a message
        for(ClientPack c : clients){
            if(!c.getAddress().equals(address) || c.getPort() != port){
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, c.getAddress(), c.getPort());
                try {
                    serverSocket.send(sendPacket);
                } catch (IOException e) {
                    System.err.println("Fehler beim Schreiben einer UDP Nachricht an " + c.getAddress() + ":" + c.getPort());
                }
            }
        }
    }



}
