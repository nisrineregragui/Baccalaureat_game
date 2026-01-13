package sockets;

import models.GameSession;
import services.GameService;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private GameServer server;
    private boolean running;

    public ClientHandler(Socket socket, String username, GameServer server,
            PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.username = username;
        this.server = server;
        this.out = out;
        this.in = in;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            String input;
            while (running && (input = in.readLine()) != null) {
                handleMessage(input);
            }
        } catch (IOException e) {
            System.out.println("Erreur de connexion avec " + username);
        } finally {
            cleanup();
        }
    }

    // Traite les messages reçus du client

    private void handleMessage(String message) {
        String[] parts = message.split(":", 2);
        String command = parts[0];

        try {
            GameService gameService = server.getGameService();
            String sessionId = server.getSessionId();

            switch (command) {

                // ========== CONFIGURATION DU LOBBY ==========

                case "SELECT_CATEGORIES":
                    // Format: SELECT_CATEGORIES:1,3,5,7
                    if (parts.length > 1) {
                        String[] catIds = parts[1].split(",");
                        List<Integer> categoryIds = new ArrayList<>();

                        for (String id : catIds) {
                            try {
                                categoryIds.add(Integer.parseInt(id.trim()));
                            } catch (NumberFormatException e) {
                                sendMessage("ERROR:ID de catégorie invalide");
                                return;
                            }
                        }

                        boolean success = gameService.selectCategories(sessionId, username, categoryIds);
                        if (success) {
                            server.broadcastCategoriesSelected(categoryIds);
                        } else {
                            sendMessage("ERROR:Seul l'hôte peut sélectionner les catégories");
                        }
                    }
                    break;

                case "SET_DURATION":
                    // Format: SET_DURATION:150
                    if (parts.length > 1) {
                        try {
                            int duration = Integer.parseInt(parts[1]);
                            boolean success = gameService.setDuration(sessionId, username, duration);

                            if (success) {
                                server.broadcastDurationSet(duration);
                            } else {
                                sendMessage("ERROR:Seul l'hôte peut définir la durée");
                            }
                        } catch (NumberFormatException e) {
                            sendMessage("ERROR:Durée invalide");
                        }
                    }
                    break;

                // ========== DÉMARRAGE DU JEU ==========

                case "START_GAME_CONFIG":
                    // Format: START_GAME_CONFIG:duration:catIds
                    if (parts.length >= 2) {
                        String[] configParts = parts[1].split(":", 2); // 120:1,2,3
                        if (configParts.length >= 2) {
                            int duration = Integer.parseInt(configParts[0]);
                            String catIds = configParts[1];
                            server.startGame(duration, catIds);
                        }
                    }
                    break;

                case "START_GAME":
                    // Format: START_GAME
                    server.startGame(username);
                    break;

                // ========== SOUMISSION DES RÉPONSES ==========

                case "SUBMIT_ALL_ANSWERS":
                    // Format: SUBMIT_ALL_ANSWERS:cat1;mot1,cat2;mot2,cat3;mot3
                    if (parts.length > 1) {
                        Map<Integer, String> answers = parseAnswers(parts[1]);

                        // Then validate (this is slow, happens in background)
                        Map<Integer, Boolean> results = gameService.submitAllAnswers(
                                sessionId, username, answers);

                        // Send validation results to client
                        sendValidationResults(results);
                    }
                    break;
                // ========== CHAT ==========

                case "CHAT":
                    // Format: CHAT:message
                    if (parts.length > 1) {
                        server.broadcast("CHAT:" + username + ":" + parts[1]);
                    }
                    break;

                // ========== DEMANDES D'INFO ==========

                case "GET_LOBBY":
                    // Envoie l'état actuel du lobby
                    server.broadcastLobbyUpdate();
                    break;

                case "GET_REMAINING_TIME":
                    // Envoie le temps restant
                    long remaining = gameService.getRemainingTime(sessionId);
                    sendMessage("REMAINING_TIME:" + remaining);
                    break;

                // ========== DÉCONNEXION ==========

                case "DISCONNECT":
                    running = false;
                    break;

                default:
                    sendMessage("ERROR:Commande inconnue : " + command);
            }

        } catch (Exception e) {
            sendMessage("ERROR:Erreur lors du traitement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Parse les reponses envoyees par le client
    // Format: cat1;mot1,cat2;mot2,cat3;mot3

    private Map<Integer, String> parseAnswers(String answersStr) {
        Map<Integer, String> answers = new HashMap<>();

        if (answersStr == null || answersStr.trim().isEmpty()) {
            return answers;
        }

        String[] pairs = answersStr.split(",");
        for (String pair : pairs) {
            String[] parts = pair.split(";");
            if (parts.length == 2) {
                try {
                    int categoryId = Integer.parseInt(parts[0].trim());
                    String word = parts[1].trim();
                    answers.put(categoryId, word);
                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Format invalide pour : " + pair);
                }
            }
        }

        return answers;
    }

    // Envoie les résultats de validation au client

    private void sendValidationResults(Map<Integer, Boolean> results) {
        StringBuilder msg = new StringBuilder("VALIDATION_RESULTS:");
        boolean first = true;

        for (Map.Entry<Integer, Boolean> entry : results.entrySet()) {
            if (!first)
                msg.append(",");
            msg.append(entry.getKey()).append(";").append(entry.getValue() ? "1" : "0");
            first = false;
        }

        sendMessage(msg.toString());
    }

    // Envoie un message au client

    public void sendMessage(String message) {
        if (out != null && !socket.isClosed()) {
            out.println(message);
        }
    }

    // Nettoie les ressources

    private void cleanup() {
        try {
            running = false;
            server.removePlayer(username);

            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}