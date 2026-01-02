package models;

/**
 * Détails du score pour un mot spécifique
 */
public class WordScore {

    private String word;
    private boolean isValid;
    private int points; // 0, 5 ou 10
    private boolean isUnique;
    private String reason; // Raison si invalide

    public WordScore(String word, boolean isValid, int points, boolean isUnique) {
        this.word = word;
        this.isValid = isValid;
        this.points = points;
        this.isUnique = isUnique;
        this.reason = "";
    }

    public WordScore(String word, boolean isValid, int points, boolean isUnique, String reason) {
        this.word = word;
        this.isValid = isValid;
        this.points = points;
        this.isUnique = isUnique;
        this.reason = reason;
    }

    /**
     * Crée un WordScore pour un mot invalide
     */
    public static WordScore invalid(String word, String reason) {
        return new WordScore(word, false, 0, false, reason);
    }

    /**
     * Crée un WordScore pour un mot unique (10 points)
     */
    public static WordScore unique(String word) {
        return new WordScore(word, true, 10, true);
    }

    /**
     * Crée un WordScore pour un mot commun (5 points)
     */
    public static WordScore common(String word) {
        return new WordScore(word, true, 5, false);
    }

    /**
     * Crée un WordScore pour une catégorie vide (0 points)
     */
    public static WordScore empty() {
        return new WordScore("", false, 0, false, "Aucune réponse");
    }

    // ============= GETTERS ET SETTERS =============

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "WordScore{" +
                "word='" + word + '\'' +
                ", valid=" + isValid +
                ", points=" + points +
                ", unique=" + isUnique +
                (reason.isEmpty() ? "" : ", reason='" + reason + '\'') +
                '}';
    }
}
