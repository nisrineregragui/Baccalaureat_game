package models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rounds")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private char letter;

    @Enumerated(EnumType.STRING)
    private GameMode game_mode;

    private int duration;

    @ManyToMany
    @JoinTable(
            name = "round_players",
            joinColumns = @JoinColumn(name = "round_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();

    @ManyToMany
    private List<Category> categories = new ArrayList<>();

    private boolean is_ended;

    public Round() {}

    public void add_player(Player player) {
        this.players.add(player);
    }

    public void endRound() {
        this.is_ended = true;
    }
}