package sockets;

import models.Player;
import models.ScoreResult;
import services.GameService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private int port;
    private Map<String, ClientHandler> clients; // username -> ClientHandler
    private GameService gameService;
    private boolean serverRunning;
    private static final String SESSION_ID = "main_session";

    public GameServer(int port) {
        this.port = port;
        this.clients = new ConcurrentHashMap<>();
        this.gameService = new GameService();
        this.serverRunning = false;
    }

    public void startServer() {
        serverRunning = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("serveur petit bac");
            System.out.println(" Port : " + port);
            System.out.println("En attente de joueurs...\n");

            while (serverRunning) {
                Socket socket = serverSocket.accept();

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Demande le nom du joueur
                out.println("ENTER_NAME");
                String username = in.readLine();

                if (username != null && !username.trim().isEmpty()) {
                    username = username.trim();

                    // Vérifie si le nom est déjà pris
                    if (clients.containsKey(username)) {
                        out.println("ERROR:Nom déjà pris");
                        socket.close();
                        continue;
                    }

                    // Crée le joueur et l'ajoute à la session
                    Player player = new Player(username);
                    gameService.addPlayerToSession(SESSION_ID, player);

                    // Crée le handler
                    ClientHandler handler = new ClientHandler(socket, username, this, out, in);
                    clients.put(username, handler);

                    out.println("CONNECTED:" + username);

                    System.out.println("✅ " + username + " connecté" +
                            (player.isHost() ? " (HÔTE)" : ""));

                    // Notifie tous les clients de la mise à jour du lobby
                    broadcastLobbyUpdate();

                    // Lance le thread du handler
                    new Thread(handler).start();
                }
            }
        } catch (IOException e) {
            if (serverRunning) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Diffuse la mise à jour du lobby à tous les clients
     */
    public void broadcastLobbyUpdate() {
        List<Player> players = gameService.getPlayers(SESSION_ID);
        StringBuilder msg = new StringBuilder("LOBBY_UPDATE:");

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            if (i > 0) msg.append(",");
            msg.append(p.getUsername());
            if (p.isHost()) {
                msg.append("(HOST)");
            }
        }

        broadcast(msg.toString());
    }

    /**
     * Diffuse la sélection des catégories
     */
    public void broadcastCategoriesSelected(List<Integer> categoryIds) {
        StringBuilder msg = new StringBuilder("CATEGORIES_SELECTED:");
        for (int i = 0; i < categoryIds.size(); i++) {
            if (i > 0) msg.append(",");
            msg.append(categoryIds.get(i));
        }
        broadcast(msg.toString());
    }

    /**
     * Diffuse la durée sélectionnée
     */
    public void broadcastDurationSet(int duration) {
        broadcast("DURATION_SET:" + duration);
    }

    /**
     * Démarre la partie
     */
    public void startGame(String username) {
        boolean started = gameService.startGame(SESSION_ID, username);

        if (!started) {
            ClientHandler handler = clients.get(username);
            if (handler != null) {
                handler.sendMessage("ERROR:Impossible de démarrer la partie");
            }
            return;
        }

        // Récupère les infos de la session
        var session = gameService.getDefaultSession();
        char letter = session.getCurrentLetter();
        int duration = session.getDuration(); // ← Get duration here
        List<Integer> categoryIds = session.getSelectedCategoryIds();

        // Construit le message de démarrage
        StringBuilder msg = new StringBuilder("GAME_START:");
        msg.append(letter).append(":").append(duration).append(":");

        for (int i = 0; i < categoryIds.size(); i++) {
            if (i > 0) msg.append(",");
            msg.append(categoryIds.get(i));
        }

        broadcast(msg.toString());

        // Lance le timer pour terminer automatiquement la partie
        final int gameDuration = duration; // ← Make it final to use in thread
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                long endTime = startTime + (gameDuration * 1000L);

                // Check every second if time is up OR all players submitted
                while (System.currentTimeMillis() < endTime) {
                    Thread.sleep(1000);

                    if (gameService.allPlayersSubmitted(SESSION_ID)) {
                        System.out.println("✅ All players submitted! Ending early...");
                        Thread.sleep(2000); // Give time for validation to complete
                        break;
                    }
                }

                System.out.println("⏰ Time's up! Ending game...");
                endGame();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Termine la partie et envoie les résultats
     */
    public void endGame() {
        Map<String, ScoreResult> results = gameService.endGame(SESSION_ID);

        // Construit le message des résultats
        StringBuilder msg = new StringBuilder("GAME_END:");
        boolean first = true;

        for (Map.Entry<String, ScoreResult> entry : results.entrySet()) {
            if (!first) msg.append(",");
            msg.append(entry.getKey()).append(";").append(entry.getValue().getRoundScore());
            first = false;
        }

        System.out.println("🔍 DEBUG - Sending GAME_END message: " + msg.toString()); // ← ADD THIS LINE
        broadcast(msg.toString());
    }

    /**
     * Envoie un message à tous les clients
     */
    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler handler : clients.values()) {
                handler.sendMessage(message);
            }
        }
    }

    /**
     * Envoie un message à tous sauf un client
     */
    public void broadcastExcept(String exceptUsername, String message) {
        synchronized (clients) {
            for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
                if (!entry.getKey().equals(exceptUsername)) {
                    entry.getValue().sendMessage(message);
                }
            }
        }
    }

    /**
     * Retire un joueur
     */
    public void removePlayer(String username) {
        clients.remove(username);
        gameService.removePlayerFromSession(SESSION_ID, username);
        broadcastLobbyUpdate();
        System.out.println("❌ " + username + " déconnecté");
    }

    /**
     * Arrête le serveur
     */
    public void stopServer() {
        serverRunning = false;
        System.out.println("\n🛑 Serveur arrêté");
    }

    // Getters
    public GameService getGameService() {
        return gameService;
    }

    public String getSessionId() {
        return SESSION_ID;
    }
}