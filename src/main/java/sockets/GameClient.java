package sockets;

import java.io.*;
import java.net.Socket;

public class GameClient {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //connexion au serveur
    public void connect() {
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("connecté au serveur de jeu !");

            //thread séparé pour écouter les messages du serveur
            new Thread(this::listenToServer).start();

        } catch (IOException e) {
            System.err.println("connexion impossible" + e.getMessage());
        }
    }

    //envoyer un mot pour validation
    public void sendWordForValidation(String word, char letter, int categoryId) {
        if (out != null) {
            String message = word + ";" + letter + ";" + categoryId;
            out.println(message);
        }
    }

    //écouter les messages envoyés par le serveur
    private void listenToServer() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("LETTER:")) {
                    System.out.println("nouvelle lettre reçue:" + message.split(":")[1]);
                    //------------maj interface JAVAFX !!!!!!!!!!!!!!!!-----------
                } else if (message.equals("VALID")) {
                    System.out.println("le mot est valide");
                } else if (message.equals("INVALID")) {
                    System.out.println("le mot est invalide");
                }
            }
        } catch (IOException e) {
            System.out.println("serveur DECONNECTE");
        }
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}