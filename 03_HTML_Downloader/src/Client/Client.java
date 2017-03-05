package Client;

import Protocol.*;

import java.io.*;
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
            //Send URL to the server and get the answer which contains the html file
            answer = handleConnection(s, out, in);

        } catch (ClassNotFoundException e) {
            printError("Kommunikation zwischen Server und Client unterbrochen, bitte Verbindung überprüfen");
        } catch (IOException e) {
            printError("Fehler bei der Verbindung. Client neu starten");
        }
        //Print the result
        JOptionPane.showMessageDialog(null, answer.getMessage());
        //Save the HTML File if the client did not close the Server
        if(!answer.getStatus().equals("warning"))
            save_HTML(answer.getHtml_content(), readIn("Geben sie den Dateinamen an um die HTML Datei auch lokal zu speichern", s, in, out));
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


    private static Answer handleConnection(Socket s, ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
        String url = "";
        Answer answer;
        //Repeat till the url is in the correct format [http://www.google.com]
        do{
            //Send a request to the server, which contains the calculation
            url = readIn("URL eingeben [kill server = Server schließen]", s, in, out);
            out.writeObject(new Request(url));
            out.flush();
            answer = (Answer)in.readObject();
            //Print the error information if one exist and then redo the whole while
            if(answer.getStatus().equals("error")) {
                printError(answer.getMessage());
            }
        }while(answer.getStatus().equals("error"));
        return answer;
    }

    private static void save_HTML(String html, String name){
        try {
            File file = new File(name + ".html");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(html);
            out.close();
        } catch (IOException e) {
            printError("Die Datei " + name + " konnte nicht erstellt werden");
        }
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


