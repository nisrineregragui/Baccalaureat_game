package tests;

import services.ValidationService;
import sockets.GameClient;

public class AppTest {
    public static void main(String[] args) {
        ValidationService vs = new ValidationService();
        System.out.println("Test Marrakesh: " + vs.word_validation("Marrakesh", 'M', 1));

        //test du server
        System.out.println("TESTING DU SERVER...");
        GameClient client = new GameClient("localhost", 12345);
        client.connect();
        client.sendWordForValidation("NEWYORK", 'N', 1);
    }
}