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

        Answer answer = null;
        try {
            //Send the authentication message to the client authenticate
            authenticate(s, out, in);
            //Send calculation to the server and get the answer
            answer = handleConnection(s, out, in);
        } catch (ClassNotFoundException e) {
            printError("Kommunikation zwischen Server und Client unterbrochen, bitte Verbindung überprüfen");
        } catch (IOException e) {
            printError("Fehler bei der Verbindung. Client neu starten");
        }
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


    private static void authenticate(Socket s, ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
        String password = "";
        String username = "";
        Answer answer;
        //Repeat till the Authentication is valid
        do{
            username = readIn("Username eingeben", s, in, out);
            password = readIn("Passwort eingeben", s, in, out);
            Authentication a = new Authentication(username, password);
            out.writeObject(a);
            out.flush();
            answer = (Answer)in.readObject();
            if(answer.getStatus().equals("error")) {
                printError(answer.getMessage());
            } else {
                JOptionPane.showMessageDialog(null, answer.getMessage());
            }
        }while(answer.getStatus().equals("error"));
    }

    private static Answer handleConnection(Socket s, ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
        String calculation;
        Answer answer;
        //Repeat till the calculation is in the correct format
        do{
            //Send a request to the server, which contains the calculation
            calculation = readIn("Rechnung eingeben [kill server = Server schließen]", s, in, out);
            out.writeObject(new Request(calculation));
            out.flush();
            answer = (Answer)in.readObject();
            //Print the error information if one exist and then redo the whole while
            if(answer.getStatus().equals("error")) {
                printError(answer.getMessage());
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

    //Close the connection + client if the user presses cancel
    private static String readIn(String message, Socket s, ObjectInputStream in, ObjectOutputStream out ){
        String input = JOptionPane.showInputDialog(message);
        if(input == null){
            //If the user clicks cancel close Client
            closeSockets(s, in, out);
            System.exit(0);
        }
        return input;
    }
}


