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

    public void connect() {
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("connecté au serveur de jeu");

            new Thread(this::listenToServer).start();

        } catch (IOException e) {
            System.err.println("Connexion impossible" + e.getMessage());
        }
    }

    //lancer la partie par hist
    public void sendStartSignal() {
        if (out != null) {
            out.println("START_GAME");
        }
    }

    public void sendWordForValidation(String word, char letter, int categoryId) {
        if (out != null) {
            String message = word + ";" + letter + ";" + categoryId;
            out.println(message);
        }
    }

    private void listenToServer() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("LETTER:")) {
                    System.out.println("LA PARTIE COMMENCE ! Nouvelle lettre : " + message.split(":")[1]);
                } else if (message.equals("GAME_OVER")) {
                    System.out.println("TEMPS ÉCOULÉ ! Le round est terminé");
                } else if (message.equals("VALID")) {
                    System.out.println("Le mot est valide");
                } else if (message.equals("INVALID")) {
                    System.out.println("Le mot est invalide");
                }
            }
        } catch (IOException e) {
            System.out.println("déconnecté.");
        }
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}