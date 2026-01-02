package models;

/**
 * Représente les différents états d'une session de jeu
 */
public enum GameState {
    /**
     * État initial - Les joueurs rejoignent le lobby,
     * l'hôte configure les paramètres (catégories, durée)
     */
    LOBBY,

    /**
     * La partie est en cours - Les joueurs remplissent leurs réponses,
     * le timer est actif
     */
    PLAYING,

    /**
     * La partie est terminée - Affichage des résultats et scores,
     * en attente d'une nouvelle partie ou déconnexion
     */
    FINISHED
}