package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Stores all the Information about the Client
//It is useful because there are several errors if you create always new Input and Output Streams
public class ClientPack {

    final Socket client;
    final ObjectOutputStream out;
    final ObjectInputStream in;

    public ClientPack(Socket client, ObjectOutputStream out, ObjectInputStream in) {
        this.client = client;
        this.out = out;
        this.in = in;
    }

    public Socket getClient() {
        return client;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
