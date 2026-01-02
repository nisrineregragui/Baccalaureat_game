package services;

import DAO.CategoryDAO;
import models.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service principal qui gère toute la logique métier du jeu.
 * Gère les sessions, les joueurs, les rounds et le calcul des scores.
 */
public class GameService {

    private ValidationService validationService;
    private CategoryDAO categoryDAO;

    // Gestion des sessions actives
    private Map<String, GameSession> activeSessions; // sessionId -> GameSession
    private static final String DEFAULT_SESSION_ID = "main_session";

    public GameService() {
        this.validationService = new ValidationService();
        this.categoryDAO = new CategoryDAO();
        this.activeSessions = new ConcurrentHashMap<>();

        // Crée une session par défaut pour le mode multijoueur simple
        createSession(DEFAULT_SESSION_ID, GameMode.Multiplayer);
    }

    // ==================== GESTION DES SESSIONS ====================

    /**
     * Crée une nouvelle session de jeu
     */
    public GameSession createSession(String sessionId, GameMode gameMode) {
        GameSession session = new GameSession(sessionId, gameMode);
        activeSessions.put(sessionId, session);
        System.out.println("✅ Session créée : " + sessionId);
        return session;
    }

    /**
     * Récupère une session existante
     */
    public GameSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    /**
     * Récupère la session par défaut
     */
    public GameSession getDefaultSession() {
        return activeSessions.get(DEFAULT_SESSION_ID);
    }

    /**
     * Supprime une session
     */
    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
        System.out.println("🗑️ Session supprimée : " + sessionId);
    }

    // ==================== GESTION DES JOUEURS ====================

    /**
     * Ajoute un joueur à une session
     * Le premier joueur devient automatiquement l'hôte
     */
    public void addPlayerToSession(String sessionId, Player player) {
        GameSession session = getSession(sessionId);
        if (session == null) {
            System.err.println("❌ Session introuvable : " + sessionId);
            return;
        }

        if (session.getState() != GameState.LOBBY) {
            System.err.println("❌ Impossible d'ajouter un joueur, la partie est déjà commencée");
            return;
        }

        session.addPlayer(player);
        System.out.println("✅ Joueur ajouté : " + player.getUsername() +
                (player.isHost() ? " (HÔTE)" : ""));
    }

    /**
     * Retire un joueur d'une session
     */
    public void removePlayerFromSession(String sessionId, String username) {
        GameSession session = getSession(sessionId);
        if (session == null) return;

        session.removePlayer(username);
        System.out.println("👋 Joueur retiré : " + username);

        // Si la session est vide, la supprimer (sauf la session par défaut)
        if (session.getPlayerCount() == 0 && !sessionId.equals(DEFAULT_SESSION_ID)) {
            removeSession(sessionId);
        }
    }

    /**
     * Récupère la liste des joueurs d'une session
     */
    public List<Player> getPlayers(String sessionId) {
        GameSession session = getSession(sessionId);
        if (session == null) return new ArrayList<>();
        return new ArrayList<>(session.getPlayers().values());
    }

    // ==================== CONFIGURATION DE LA PARTIE ====================

    /**
     * Sélectionne les catégories pour la partie (uniquement par l'hôte)
     */
    public boolean selectCategories(String sessionId, String username, List<Integer> categoryIds) {
        GameSession session = getSession(sessionId);
        if (session == null) {
            System.err.println("❌ Session null");
            return false;
        }

        String hostUsername = session.getHostUsername();

        // Debug logging
        System.out.println("🔍 DEBUG Host check:");
        System.out.println("   Requester: '" + username + "' (length: " + username.length() + ")");
        System.out.println("   Host: '" + hostUsername + "' (length: " + (hostUsername != null ? hostUsername.length() : "null") + ")");
        System.out.println("   Match: " + username.equals(hostUsername));

        // Vérifier que c'est l'hôte qui demande
        if (!username.equals(hostUsername)) {
            System.err.println("❌ Seul l'hôte peut sélectionner les catégories");
            return false;
        }

        if (session.getState() != GameState.LOBBY) {
            System.err.println("❌ Impossible de changer les catégories en cours de partie");
            return false;
        }

        session.setSelectedCategoryIds(categoryIds);
        System.out.println("📋 Catégories sélectionnées : " + categoryIds);
        return true;
    }

    /**
     * Définit la durée de la partie (uniquement par l'hôte)
     */
    public boolean setDuration(String sessionId, String username, int duration) {
        GameSession session = getSession(sessionId);
        if (session == null) return false;

        // Vérifier que c'est l'hôte qui demande
        if (!username.equals(session.getHostUsername())) {
            System.err.println("❌ Seul l'hôte peut définir la durée");
            return false;
        }

        if (session.getState() != GameState.LOBBY) {
            System.err.println("❌ Impossible de changer la durée en cours de partie");
            return false;
        }

        session.setDuration(duration);
        System.out.println("⏱️ Durée définie : " + duration + " secondes");
        return true;
    }

    // ==================== DÉMARRAGE DE LA PARTIE ====================

    /**
     * Démarre la partie (uniquement par l'hôte)
     */
    public boolean startGame(String sessionId, String username) {
        GameSession session = getSession(sessionId);
        if (session == null) return false;

        // Vérifier que c'est l'hôte qui demande
        if (!username.equals(session.getHostUsername())) {
            System.err.println("❌ Seul l'hôte peut démarrer la partie");
            return false;
        }

        if (session.getState() != GameState.LOBBY) {
            System.err.println("❌ La partie est déjà en cours");
            return false;
        }

        if (session.getPlayerCount() < 2) {
            System.err.println("❌ Au moins 2 joueurs requis");
            return false;
        }

        if (session.getSelectedCategoryIds().isEmpty()) {
            System.err.println("❌ Aucune catégorie sélectionnée");
            return false;
        }

        // Démarre la partie
        session.startGame();

        System.out.println("\n🎮 PARTIE DÉMARRÉE !");
        System.out.println("📝 Lettre : " + session.getCurrentLetter());
        System.out.println("⏱️ Durée : " + session.getDuration() + " secondes");
        System.out.println("👥 Joueurs : " + session.getPlayerCount());
        System.out.println("📋 Catégories : " + session.getSelectedCategoryIds().size());

        return true;
    }

    // ==================== SOUMISSION DES RÉPONSES ====================

    /**
     * Soumet toutes les réponses d'un joueur en une fois
     * @param answers Map<categoryId, word>
     * @return Map<categoryId, isValid>
     */
    public Map<Integer, Boolean> submitAllAnswers(String sessionId, String username, Map<Integer, String> answers) {
        GameSession session = getSession(sessionId);
        Map<Integer, Boolean> results = new HashMap<>();

        if (session == null || session.getState() != GameState.PLAYING) {
            System.err.println("❌ Aucune partie en cours");
            return results;
        }

        char letter = session.getCurrentLetter();

        // Valide chaque réponse
        for (Map.Entry<Integer, String> entry : answers.entrySet()) {
            int categoryId = entry.getKey();
            String word = entry.getValue();

            // Si le mot est vide, on skip
            if (word == null || word.trim().isEmpty()) {
                results.put(categoryId, false);
                continue;
            }

            boolean isValid = validationService.word_validation(word, letter, categoryId);
            results.put(categoryId, isValid);

            System.out.println(username + " → " + word + " (Cat " + categoryId + ") : " +
                    (isValid ? "✅" : "❌"));
        }

        // Enregistre les réponses dans la session
        session.submitPlayerAnswers(username, answers);

        return results;
    }

    // ==================== FIN DE LA PARTIE ====================

    /**
     * Termine la partie et calcule les scores
     * @return Map<username, ScoreResult>
     */
    public Map<String, ScoreResult> endGame(String sessionId) {
        GameSession session = getSession(sessionId);
        if (session == null) {
            return new HashMap<>();
        }

        session.endGame();

        // Récupère toutes les réponses
        Map<String, Map<Integer, String>> allAnswers = session.getPlayerAnswers();

        // Calcule les scores
        Map<String, ScoreResult> results = calculateScores(session, allAnswers);

        // Met à jour les scores des joueurs
        for (Map.Entry<String, ScoreResult> entry : results.entrySet()) {
            Player player = session.getPlayer(entry.getKey());
            if (player != null) {
                player.setScore(player.getScore() + entry.getValue().getRoundScore());
            }
        }

        System.out.println("\n🏆 PARTIE TERMINÉE !");
        displayResults(results);

        return results;
    }

    /**
     * Termine automatiquement la partie si le temps est écoulé
     */
    public boolean checkAndEndIfTimeUp(String sessionId) {
        GameSession session = getSession(sessionId);
        if (session == null) return false;

        if (session.isTimeUp()) {
            endGame(sessionId);
            return true;
        }
        return false;
    }

    // ==================== CALCUL DES SCORES ====================

    /**
     * Calcule les scores selon le système :
     * - Mot unique : 10 points
     * - Mot en commun : 5 points
     * - Mot invalide ou vide : 0 point
     */
    private Map<String, ScoreResult> calculateScores(GameSession session, Map<String, Map<Integer, String>> allAnswers) {
        Map<String, ScoreResult> results = new HashMap<>();
        char letter = session.getCurrentLetter();

        // Initialise les résultats pour chaque joueur
        for (String username : allAnswers.keySet()) {
            Player player = session.getPlayer(username);
            ScoreResult result = new ScoreResult(username);
            result.setTotalScore(player != null ? player.getScore() : 0);
            results.put(username, result);
        }

        // Pour chaque catégorie, compare les réponses
        for (int categoryId : session.getSelectedCategoryIds()) {
            Map<String, String> wordsForCategory = new HashMap<>(); // username -> word

            // Collecte tous les mots pour cette catégorie
            for (Map.Entry<String, Map<Integer, String>> playerEntry : allAnswers.entrySet()) {
                String username = playerEntry.getKey();
                String word = playerEntry.getValue().get(categoryId);

                if (word != null && !word.trim().isEmpty()) {
                    wordsForCategory.put(username, word.trim().toUpperCase());
                }
            }

            // Compte les occurrences de chaque mot
            Map<String, Integer> wordCounts = new HashMap<>();
            for (String word : wordsForCategory.values()) {
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }

            // Attribue les points
            for (Map.Entry<String, String> entry : wordsForCategory.entrySet()) {
                String username = entry.getKey();
                String word = entry.getValue();

                // Valide le mot
                boolean isValid = validationService.word_validation(word, letter, categoryId);

                if (isValid) {
                    int occurrences = wordCounts.get(word);
                    boolean isUnique = occurrences == 1;
                    int points = isUnique ? 10 : 5;

                    WordScore wordScore = isUnique ?
                            WordScore.unique(word) :
                            WordScore.common(word);

                    results.get(username).addWordScore(categoryId, wordScore);
                } else {
                    results.get(username).addWordScore(categoryId,
                            WordScore.invalid(word, "Mot invalide"));
                }
            }

            // Pour les joueurs qui n'ont pas répondu à cette catégorie
            for (String username : allAnswers.keySet()) {
                if (!wordsForCategory.containsKey(username)) {
                    results.get(username).addWordScore(categoryId, WordScore.empty());
                }
            }
        }

        return results;
    }

    /**
     * Affiche les résultats dans la console
     */
    private void displayResults(Map<String, ScoreResult> results) {
        System.out.println("\nresults");

        // Trie les joueurs par score décroissant
        List<Map.Entry<String, ScoreResult>> sortedResults = new ArrayList<>(results.entrySet());
        sortedResults.sort((a, b) -> Integer.compare(
                b.getValue().getRoundScore(),
                a.getValue().getRoundScore()
        ));

        int rank = 1;
        for (Map.Entry<String, ScoreResult> entry : sortedResults) {
            String username = entry.getKey();
            ScoreResult result = entry.getValue();

            String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : "  ";
            System.out.printf("%s %d. %-15s : %3d points (Total: %d)\n",
                    medal, rank, username, result.getRoundScore(), result.getTotalScore());
            rank++;
        }
        System.out.println("════════════════════════════════════════\n");
    }

    // ==================== UTILITAIRES ====================

    /**
     * Récupère l'état actuel d'une session
     */
    public GameState getSessionState(String sessionId) {
        GameSession session = getSession(sessionId);
        return session != null ? session.getState() : null;
    }

    /**
     * Récupère le temps restant dans une session
     */
    public long getRemainingTime(String sessionId) {
        GameSession session = getSession(sessionId);
        return session != null ? session.getRemainingTime() : 0;
    }

    /**
     * Récupère les catégories d'une session
     */
    public List<Category> getSessionCategories(String sessionId) {
        GameSession session = getSession(sessionId);
        if (session == null) return new ArrayList<>();

        List<Category> categories = new ArrayList<>();
        for (int catId : session.getSelectedCategoryIds()) {
            Category cat = categoryDAO.getCategory(catId);
            if (cat != null) {
                categories.add(cat);
            }
        }
        return categories;
    }

    /**
     * Réinitialise une session pour une nouvelle partie
     */
    public void resetSession(String sessionId) {
        GameSession session = getSession(sessionId);
        if (session == null) return;

        GameMode mode = session.getGameMode();
        List<Player> players = new ArrayList<>(session.getPlayers().values());
        String hostUsername = session.getHostUsername();

        // Recrée la session
        removeSession(sessionId);
        GameSession newSession = createSession(sessionId, mode);

        // Réajoute les joueurs
        for (Player p : players) {
            newSession.addPlayer(p);
            if (p.getUsername().equals(hostUsername)) {
                p.setHost(true);
            }
        }

        System.out.println("🔄 Session réinitialisée");
    }
    public boolean allPlayersSubmitted(String sessionId) {
        GameSession session = getSession(sessionId);
        if (session == null) return false;

        Map<String, Map<Integer, String>> answers = session.getPlayerAnswers();
        int submitted = answers.size();
        int total = session.getPlayerCount();

        System.out.println("📊 Players submitted: " + submitted + "/" + total);
        return submitted >= total;
    }
    
}