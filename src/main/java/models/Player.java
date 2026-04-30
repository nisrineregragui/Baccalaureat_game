package models;

import java.util.HashMap;
import java.util.Map;

public class Player{
    private int id;
    private String  username;
    private int Score;
    private boolean is_host;
    private boolean is_ready;
    private Map<Integer,String> recent_guess;

    public Player(String username) {
        this.username = username;
        this.Score = 0;
        this.is_host = false;
        this.is_ready = false;
        this.recent_guess = new HashMap<>();
    }

}
