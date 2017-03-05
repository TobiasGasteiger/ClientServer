package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenThread extends Thread{

    private DatagramSocket clientSocket;

    public ListenThread(DatagramSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        while(Client.running){
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                //Extract the message/data from the receivePacket
                clientSocket.receive(receivePacket);
                String message =  new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(message);
            } catch (IOException e) {
                System.err.println("Fehler beim Empfangen eines UDP Paketes vom Server");
            }
        }
    }
}
