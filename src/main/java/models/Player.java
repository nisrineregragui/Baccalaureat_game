package models;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String username;

    private int score; // Note: J'ai passé 'Score' en minuscule pour respecter les conventions Java

    private boolean is_host;
    private boolean is_ready;

    /**
     * Pour une Map<Entity, String>, on utilise ElementCollection.
     * La table 'player_guesses' fera le lien entre le joueur,
     * l'ID de la catégorie (la clé) et le mot deviné (la valeur).
     */
    @ElementCollection
    @CollectionTable(name = "player_guesses",
            joinColumns = @JoinColumn(name = "player_id"))
    @MapKeyJoinColumn(name = "category_id") // Car la clé est une Entité (Category)
    @Column(name = "guessed_word")
    private Map<Category, String> recent_guess;

    public Player() {this.recent_guess = new HashMap<>();}

    public Player(String username) {
        this.username = username;
        this.score = 0;
        this.is_host = false;
        this.is_ready = false;
        this.recent_guess = new HashMap<>();
    }


    public int getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Map<Category, String> getRecent_guess() { return recent_guess; }
}