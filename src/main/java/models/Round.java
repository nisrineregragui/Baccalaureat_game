package models;
import java.util.List;

public class Round {
    private int id;
    private char letter;
    private GameMode game_mode;
    private int duration;
    private List<Player> players;
    private List<Category> categories;
    private boolean is_ended;


public void Add_player(Player player) {
    this.players.add(player);
}

public void StartRound(){}
    public void EndRound(){
    this.is_ended = true;
    }

}
