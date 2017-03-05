package Server;


import java.net.InetAddress;

public class ClientPack {
    private final InetAddress address;
    private final int port;

    public ClientPack(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        ClientPack that = (ClientPack) o;

        if (getPort() != that.getPort()) return false;
        return getAddress().equals(that.getAddress());
    }
}
