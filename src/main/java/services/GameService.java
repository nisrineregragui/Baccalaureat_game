package services;

import models.Round;

import java.util.Random;

public class GameService {
    private ValidationService validationService;
    private Round current_round;

    //generate a random letter
    public char generateLetter(){
        Random rand = new Random();
        return (char) ('A' + rand.nextInt(26));
    }

    //public void Result_treating

}
