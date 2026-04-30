package models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Représente une session de jeu active en mémoire (NON persistante en DB).
 * Contient toutes les données nécessaires pendant qu'une partie est en cours.
 */
public class GameSession {

    private String sessionId;
    private GameState state;
    private GameMode gameMode;


    // Joueurs
    private Map<String, Player> players; // username -> Player
    private String hostUsername;

    // Configuration de la partie
    private List<Integer> selectedCategoryIds;
    private int duration; // en secondes (60, 150, 120, 210)
    private char currentLetter;

    // Réponses des joueurs pendant la partie
    private Map<String, Map<Integer, String>> playerAnswers; // username -> (categoryId -> word)

    // Timer
    private long startTime;
    private long endTime;

    // Round persistant (optionnel - pour sauvegarder en DB après)
    private Round round;

    public GameSession(String sessionId, GameMode gameMode) {
        this.sessionId = sessionId;
        this.gameMode = gameMode;
        this.state = GameState.LOBBY;
        this.hostUsername = null;
        this.players = new ConcurrentHashMap<>();
        this.selectedCategoryIds = new ArrayList<>();
        this.playerAnswers = new ConcurrentHashMap<>();
        this.duration = 60; // Par défaut 1 minute
    }

    /**
     * Ajoute un joueur à la session
     * Le premier joueur devient automatiquement l'hôte
     */
    public void addPlayer(Player player) {
        if (players.isEmpty()) {
            // FIRST PLAYER = HOST
            player.setHost(true);
            this.hostUsername = player.getUsername(); // ← THIS IS THE CRITICAL LINE
            System.out.println("👑 " + player.getUsername() + " is now HOST");
        } else {
            player.setHost(false);
        }
        players.put(player.getUsername(), player);
    }

    /**
     * Retire un joueur de la session
     * Si c'est l'hôte, désigne un nouvel hôte
     */
    public void removePlayer(String username) {
        Player removedPlayer = players.remove(username);
        playerAnswers.remove(username);

        // Si c'était l'hôte, désigner un nouvel hôte
        if (username.equals(hostUsername) && !players.isEmpty()) {
            String newHostUsername = players.keySet().iterator().next();
            this.hostUsername = newHostUsername;
            players.get(newHostUsername).setHost(true);
        }
    }

    /**
     * Démarre la partie (génère une lettre aléatoire)
     */
    public void startGame() {
        this.state = GameState.PLAYING;
        this.currentLetter = 'P';
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + (duration * 1000L);

        // Réinitialise les réponses
        for (Map<Integer, String> answers : playerAnswers.values()) {
            answers.clear();
        }
    }

    /**
     * Termine la partie
     */
    public void endGame() {
        this.state = GameState.FINISHED;
    }

    /**
     * Soumet toutes les réponses d'un joueur en une fois
     */
    public void submitPlayerAnswers(String username, Map<Integer, String> answers) {
        if (state != GameState.PLAYING) {
            return;
        }

        Map<Integer, String> playerAnswerMap = playerAnswers.get(username);
        if (playerAnswerMap != null) {
            playerAnswerMap.putAll(answers);
        }
    }

    /**
     * Vérifie si tous les joueurs ont soumis leurs réponses
     */
    public boolean allPlayersSubmitted() {
        for (Map.Entry<String, Map<Integer, String>> entry : playerAnswers.entrySet()) {
            Map<Integer, String> answers = entry.getValue();
            // Si un joueur n'a rempli aucune catégorie
            if (answers.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcule le temps restant en secondes
     */
    public long getRemainingTime() {
        if (state != GameState.PLAYING) {
            return 0;
        }
        long remaining = (endTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * Vérifie si le temps est écoulé
     */
    public boolean isTimeUp() {
        return state == GameState.PLAYING && System.currentTimeMillis() >= endTime;
    }

    // ============= GETTERS ET SETTERS =============

    public String getSessionId() {
        return sessionId;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Player getPlayer(String username) {
        return players.get(username);
    }



    public Player getHost() {
        return players.get(hostUsername);
    }

    public List<Integer> getSelectedCategoryIds() {
        return selectedCategoryIds;
    }

    public void setSelectedCategoryIds(List<Integer> selectedCategoryIds) {
        this.selectedCategoryIds = selectedCategoryIds;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public char getCurrentLetter() {
        return currentLetter;
    }

    public void setCurrentLetter(char currentLetter) {
        this.currentLetter = currentLetter;
    }

    public Map<String, Map<Integer, String>> getPlayerAnswers() {
        return playerAnswers;
    }

    public Map<Integer, String> getPlayerAnswers(String username) {
        return playerAnswers.get(username);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    @Override
    public String toString() {
        return "GameSession{" +
                "sessionId='" + sessionId + '\'' +
                ", state=" + state +
                ", players=" + players.size() +
                ", host='" + hostUsername + '\'' +
                ", letter=" + currentLetter +
                ", duration=" + duration +
                '}';
    }
}