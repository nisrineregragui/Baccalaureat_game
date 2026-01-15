package sockets;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.List;

public class GameClient {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private boolean connected;
    private java.util.function.Consumer<String> onMessageReceived;

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = false;
    }

    public void setOnMessageReceived(java.util.function.Consumer<String> listener) {
        this.onMessageReceived = listener;
    }

    private String lastError = "";

    public String getLastError() {
        return lastError;
    }

    public boolean connect(String username) {
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = in.readLine();
            if (!"ENTER_NAME".equals(response)) {
                lastError = "Protocole inattendu: " + response;
                System.err.println("❌ " + lastError);
                return false;
            }

            // Send username
            out.println(username);

            // Wait for CONNECTED confirmation
            response = in.readLine();
            if (response != null && response.startsWith("CONNECTED:")) {
                this.username = response.split(":")[1];
                this.connected = true;
                System.out.println("✅ Connecté en tant que: " + this.username);

                new Thread(this::listenToServer).start();
                return true;
            } else if (response != null && response.startsWith("ERROR:")) {
                lastError = response.split(":")[1];
                System.err.println("❌ " + lastError);
                return false;
            }

        } catch (IOException e) {
            lastError = e.getMessage();
            System.err.println("❌ Connexion impossible: " + e.getMessage());
        }
        return false;
    }

    // lancer la partie par hist
    public void sendStartSignal(int duration, String categoryIds) {
        if (out != null) {
            out.println("START_GAME_CONFIG:" + duration + ":" + categoryIds);
        }
    }

    public void submitAllAnswers(Map<Integer, String> answers) {
        StringBuilder msg = new StringBuilder("SUBMIT_ALL_ANSWERS:");
        boolean first = true;

        for (Map.Entry<Integer, String> entry : answers.entrySet()) {
            if (!first)
                msg.append(",");
            msg.append(entry.getKey()).append(";").append(entry.getValue());
            first = false;
        }

        out.println(msg.toString());
        // Envoie: "SUBMIT_ALL_ANSWERS:1;Paris,3;Pomme,5;Python"
    }

    // Pour l'hôte
    public void selectCategories(List<Integer> categoryIds) {
        StringBuilder msg = new StringBuilder("SELECT_CATEGORIES:");
        for (int i = 0; i < categoryIds.size(); i++) {
            if (i > 0)
                msg.append(",");
            msg.append(categoryIds.get(i));
        }
        out.println(msg.toString());
        // Envoie: "SELECT_CATEGORIES:1,3,5,7"
    }

    public void setDuration(int duration) {
        out.println("SET_DURATION:" + duration);
        // Envoie: "SET_DURATION:120"
    }

    // Pour tous les joueurs
    public void sendChatMessage(String message) {
        out.println("CHAT:" + message);
        // Envoie: "CHAT:Salut tout le monde!"
    }

    public void requestLobbyUpdate() {
        out.println("GET_LOBBY");
        // Envoie: "GET_LOBBY"
    }

    public void requestRemainingTime() {
        out.println("GET_REMAINING_TIME");
        // Envoie: "GET_REMAINING_TIME"
    }

    private void listenToServer() {
        try {
            String message;
            while (connected && (message = in.readLine()) != null) {
                if (onMessageReceived != null) {
                    onMessageReceived.accept(message);
                }

                if (message.startsWith("GAME_END:")) {
                    String scoresData = message.substring(9); // Remove "GAME_END:"

                    System.out.println("\n=== GAME OVER ===");

                    // Check if there are any scores
                    if (scoresData.isEmpty() || scoresData.trim().isEmpty()) {
                        System.out.println("No scores available");
                    } else {
                        String[] scores = scoresData.split(",");
                        String winner = "";
                        int maxScore = -1;

                        for (String score : scores) {
                            String[] parts = score.split(";");
                            if (parts.length >= 2) { // Safety check
                                String name = parts[0];
                                int points = Integer.parseInt(parts[1]);

                                System.out.println(name + ": " + points + " points");

                                if (points > maxScore) {
                                    maxScore = points;
                                    winner = name;
                                }
                            }
                        }

                        System.out.println("\nWINNER: " + winner);
                    }
                    System.out.println("=================\n");

                } else {
                    System.out.println("SERVER: " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}