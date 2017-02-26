package Client;

import Protocol.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;


public class Client {
	public static void main(String[] args) throws IOException {
        //Create the Socket
        Socket s = connect();
        //Create ObjectOutputStream and ObjectInputStream
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        //Send calculation to the server and get the answer
        Answer answer = handleConnection(s, out, in);
        //Print the result
        JOptionPane.showMessageDialog(null, answer.getMessage());
        //Close connections
        closeSockets(s, in, out);
        System.exit(0);
    }

    private static Socket connect(){
        boolean ipAddressCorrect = false;
        Socket s = null;
        //Repeat is so long until the IP Address is correct or the user presses cancel
        while(!ipAddressCorrect) {
            try {
                String serverAddress = JOptionPane.showInputDialog("IP Adresse des Servers auf Port 9090");
                if (serverAddress == null)
                    //If the user clicks cancel close Client
                    System.exit(0);
                //Connect to the server that is running on Port 9090
                s = new Socket(serverAddress, 9090);
                ipAddressCorrect = true;
            } catch (UnknownHostException e) {
                printError("Die IP Adresse ist ungültig");
            } catch (ConnectException e) {
                printError("Die Verbindung kann nicht aufgebaut werden (Ist der Server gestartet?)");
            } catch (IOException e) {
                printError("Fehler bei der Verbindungsherstellung");
            }
        }
        return s;
    }

    private static Answer handleConnection(Socket s, ObjectOutputStream out, ObjectInputStream in){
        String calculation;
        Answer answer = null;
        do{
            //Send a request to the server, which contains the calculation
            calculation = JOptionPane.showInputDialog("Rechnung eingeben [kill server = Server schließen]");
            if(calculation == null){
                //If the user clicks cancel close Client
                closeSockets(s, in, out);
                System.exit(0);
            }
            try {
                out.writeObject(new Request(calculation));
                out.flush();
                answer = (Answer)in.readObject();
                //Print the error information if one exist and then redo the whole while
                if(answer.getStatus().equals("error")) {
                    printError(answer.getMessage());
                }
            } catch (ClassNotFoundException e) {
                printError("Kommunikation zwischen Server und Client unterbrochen, bitte Verbindung überprüfen");
            } catch (IOException e) {
                printError("Fehler bei der Verbindung. Client neu starten");
            }
        }while(answer.getStatus().equals("error"));
        return answer;
    }


    private static void closeSockets(Socket s, ObjectInputStream in, ObjectOutputStream out){
        try {
            out.close();
            s.close();
            in.close();
        } catch (IOException e) {
            printError("Verbindung abgebrochen, Client wird beendet");
        }
    }

    private static void printError(String error){
        JOptionPane.showMessageDialog(null,
                error,
                "Error-Client",
                JOptionPane.ERROR_MESSAGE);
    }
}

