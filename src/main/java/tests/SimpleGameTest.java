package tests;

import sockets.GameServer;
import sockets.GameClient;
import java.util.*;

public class SimpleGameTest {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Starting server on port 12345...");
        Thread serverThread = new Thread(() -> {
            GameServer server = new GameServer(12345);
            server.startServer();
        });
        serverThread.start();
        Thread.sleep(2000);

        System.out.println("\nAlice connecting...");
        GameClient alice = new GameClient("localhost", 12345);
        alice.connect("Alice");
        Thread.sleep(1000);

        System.out.println("Bob connecting...");
        GameClient bob = new GameClient("localhost", 12345);
        bob.connect("Bob");
        Thread.sleep(1000);

        System.out.println("\nAlice selecting categories 1,2,3...");
        alice.selectCategories(Arrays.asList(1, 2, 3));
        Thread.sleep(1000);

        System.out.println("Alice setting duration to 10 seconds...");
        alice.setDuration(10);
        Thread.sleep(1000);

        System.out.println("\nAlice starting game...");
        alice.sendStartSignal();
        Thread.sleep(3000);

        // ============================================
        // LOOK AT THE CONSOLE - SEE WHAT LETTER WAS GENERATED
        // Then submit answers with that letter!
        // ============================================

        System.out.println("\n*** SUBMITTING ANSWERS WITH LETTER 'P' ***");
        System.out.println("(Change these if the letter is different!)\n");

        Map<Integer, String> aliceAnswers = new HashMap<>();
        aliceAnswers.put(1, "Paris");      // Valid - unique = 10 pts
        aliceAnswers.put(2, "Portugal");   // Valid - unique = 10 pts
        aliceAnswers.put(3, "Pomme");      // Valid - common = 5 pts
        alice.submitAllAnswers(aliceAnswers);

        Map<Integer, String> bobAnswers = new HashMap<>();
        bobAnswers.put(1, "Pekin");        // Valid - unique = 10 pts
        bobAnswers.put(2, "Pakistan");     // Valid - unique = 10 pts
        bobAnswers.put(3, "Pomme");        // Valid - common = 5 pts
        bob.submitAllAnswers(bobAnswers);

        System.out.println("\nWaiting for game to end and calculate scores...");
        Thread.sleep(20000);

        System.out.println("\nDisconnecting players...");
        Thread.sleep(1000);
        alice.disconnect();
        bob.disconnect();

        System.out.println("\nTest complete.");
        System.out.println("\nEXPECTED RESULTS (if letter was 'P'):");
        System.out.println("  Alice: 10 + 10 + 5 = 25 points");
        System.out.println("  Bob: 10 + 10 + 5 = 25 points");
        System.out.println("  (Tie or first player wins)");
    }
}