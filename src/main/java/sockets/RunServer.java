package sockets;

public class RunServer {
    public static void main(String[] args) {
        GameServer server = new GameServer(12345);
        server.startServer();

    }
}