package sockets;

public class TestMultiplayer {
    public static void main(String[] args) throws InterruptedException {
        //joueur host
        GameClient player1 = new GameClient("localhost", 12345);
        player1.connect();

        //joueur2
        GameClient player2 = new GameClient("localhost", 12345);
        player2.connect();

        Thread.sleep(1000);

        //host lance round
        System.out.println("---Start roundd---");
        player1.sendStartSignal();

        Thread.sleep(2000);

        //exemple de formulaire
        //exmp: Lettre -----> P
        System.out.println("--- Envoi des mots ---");
        player1.sendWordForValidation("Paris", 'P', 1);
        player2.sendWordForValidation("Porto", 'P', 1);
    }
}