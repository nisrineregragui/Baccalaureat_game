package sockets;

import java.net.Socket;
import java.util.List;

public class GameServer {
    private int port;
    private List<ClientHandler> client;
    public void StartServer(){
        //serversocket.accept
    }
    public void broadcastLetter(char letter){}

    private class ClientHandler implements Runnable {
        private Socket socket;
        public void run() { }
    }

}
