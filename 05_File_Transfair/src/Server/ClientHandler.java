package Server;

import Protocol.Answer;
import Protocol.Request;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

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
            if (r.getMessage().equals("kill server")) {
                //Close the server and the client
                Server.closeServer();
            } else {
                //Read the data from the file
                byte[] data = Files.readAllBytes(new File(r.getMessage()).toPath());
                //Fill the Answer class with the data
                Answer a = new Answer("ok", "Die Datei wurde erfolgreich übertragen");
                a.setData(data);
                //Send the Answer
                clientPack.getOut().writeObject(a);
                closeClient();
            }
        } catch (ClassNotFoundException e) {
            printError("Klasse nicht gefunden");
        } catch (NoSuchFileException e) {
            System.out.println("Der empfangene Pfad eines Clients ist nicht im gültig, warte auf neuem Pfad");
            clientPack.getOut().writeObject(new Answer("error", "Der Pfad ist ungültig, erneue Eingabe erforderlich"));
            handleConnection();
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
