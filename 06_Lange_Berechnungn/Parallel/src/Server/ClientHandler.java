package Server;

import Protocol.Answer;
import Protocol.Request;
import javax.swing.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientHandler implements Runnable{
    private final ClientPack clientPack;


    public ClientHandler(ClientPack clientPack) {
        System.out.println("Ein Client wurde angenommen");
        this.clientPack = clientPack;
    }

    @Override
    public void run() {
        try {
            handleConnection();
        } catch (IOException e) {
            System.out.println("Ein Client hat die Verbindung verloren bzw. abgebrochen");
        }
    }

    private void handleConnection() throws IOException {
        Request r;
        try {
            r = (Request) clientPack.getIn().readObject();
            //Check if the received data is valid and calculate the result
            if (r != null) {
                if (r.getMessage().equals("kill server")) {
                    //Close the server and the client
                    Server.closeServer();
                } else {
                    try{
                        if(r.getMessage().equals("getRandom")){
                            int random = (int)(Math.random() * 1000000000);
                            //Sleep 0 to 10 seconds, this should simulate the long calculation
                            long timeToSleep = (long)(Math.random()*5000);
                            System.out.println("Random Berechnung wird durchgeführt sie dauert "+ timeToSleep +" Millisekunden");
                            Thread.sleep(timeToSleep);
                            //send the message
                            clientPack.getOut().writeObject(new Answer("ok", Integer.toString(random)));
                            closeClient();
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
                            System.out.println("Time Berechnung wird durchgeführt sie dauert "+ timeToSleep +" Millisekunden");
                            Thread.sleep(timeToSleep);
                            clientPack.getOut().writeObject(new Answer("ok", reportDate));
                            closeClient();
                        } else {
                            //When the input is invalid send error and wait for a new request
                            clientPack.getOut().writeObject(new Answer("error", "Ungültige Eingabe, bitte wiederholen"));
                            System.out.println("Eine Berechnung konnte nicht erfolgreich durchgeführt werden. Client wird Daten erneut senden");
                            handleConnection();
                        }
                    } catch (InterruptedException e){
                        printError("Die Berechnung wurde unerwartet unterbrochen der Server wird geschlossen");
                        System.exit(-1);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            printError("Klasse nicht gefunden");
        }
    }

    private void closeClient() throws IOException {
        //Remove it from the Array List in the Server Class
        Server.removeClient(clientPack);
        //Close the client and his sockets
        clientPack.getIn().close();
        clientPack.getOut().close();
        clientPack.getClient().close();
    }

    private static void printError(String error){
        JOptionPane.showMessageDialog(null,
                error,
                "Error-Server",
                JOptionPane.ERROR_MESSAGE);
    }
}
