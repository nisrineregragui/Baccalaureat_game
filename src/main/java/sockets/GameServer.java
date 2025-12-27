package sockets;

import services.ValidationService;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameServer {
    private int port;
    private List<ClientHandler> clients;
    private ValidationService validationService;

    public GameServer(int port) {
        this.port = port;
        this.clients = Collections.synchronizedList(new ArrayList<>());
        this.validationService = new ValidationService();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("serveur démarré sur le port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("nouveau joueur connecté : " + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastLetter(char letter) {
        synchronized (clients) {
            for (ClientHandler h : clients) {
                h.sendMessage("LETTER:" + letter);
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String input;
                while ((input = in.readLine()) != null) {
                    // Format attendu du client : "MOT;LETTRE;ID_CATEGORIE"
                    String[] parts = input.split(";");
                    if (parts.length == 3) {
                        String word = parts[0];
                        char letter = parts[1].charAt(0);
                        int catId = Integer.parseInt(parts[2]);

                        //service validation
                        boolean isValid = validationService.word_validation(word, letter, catId);

                        //cliebt reponse
                        out.println(isValid ? "VALID" : "INVALID");
                    }
                }
            } catch (IOException e) {
                System.out.println("connexion perdue avec un joueur");
            } finally {
                clients.remove(this);
            }
        }

        public void sendMessage(String msg) {
            if (out != null) out.println(msg);
        }
    }
}