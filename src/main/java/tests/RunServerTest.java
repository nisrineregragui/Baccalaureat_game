package tests;

import sockets.GameServer;

public class RunServerTest {
    public static void main(String[] args) {
        GameServer server = new GameServer(12345);
        new Thread(server::startServer).start();
        System.out.println("Server started on port 12345...");
    }
}
