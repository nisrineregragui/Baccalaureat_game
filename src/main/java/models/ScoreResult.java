package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Représente les résultats détaillés d'un joueur pour un round
 */
public class ScoreResult {

    private String playerName;
    private int roundScore;
    private int totalScore;
    private Map<Integer, WordScore> wordScores; // categoryId -> détails du mot

    public ScoreResult(String playerName) {
        this.playerName = playerName;
        this.roundScore = 0;
        this.totalScore = 0;
        this.wordScores = new HashMap<>();
    }

    /**
     * Ajoute le score d'un mot pour une catégorie
     */

    public void addWordScore(int categoryId, WordScore wordScore) {
        wordScores.put(categoryId, wordScore);
        this.roundScore += wordScore.getPoints();
    }

    /**
     * Ajoute des points au score total du joueur
     */
    public void addToTotal(int points) {
        this.totalScore += points;
    }

    // ============= GETTERS ET SETTERS =============

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getRoundScore() {
        return roundScore;
    }

    public void setRoundScore(int roundScore) {
        this.roundScore = roundScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public Map<Integer, WordScore> getWordScores() {
        return wordScores;
    }

    public WordScore getWordScore(int categoryId) {
        return wordScores.get(categoryId);
    }

    @Override
    public String toString() {
        return "ScoreResult{" +
                "playerName='" + playerName + '\'' +
                ", roundScore=" + roundScore +
                ", totalScore=" + totalScore +
                ", wordsCount=" + wordScores.size() +
                '}';
    }
}

