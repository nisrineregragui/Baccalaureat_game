package tests;

import models.Category;
import models.GameSession;
import services.GameService;
import DAO.CategoryDAO;
import models.Player;


import java.util.*;

public class SoloGameTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        GameService gameService = new GameService();
        CategoryDAO categoryDAO = new CategoryDAO();
        //  Nom du joueur
        System.out.print("Entrez votre nom : ");
        String playerName = scanner.nextLine();

        GameSession session = gameService.createSoloSession(playerName);
        String sessionId = session.getSessionId();


        //  Afficher les catégories depuis la DB
        List<Category> categories = categoryDAO.getCategories();

        System.out.println("\nCatégories disponibles :");
        for (Category c : categories) {
            System.out.println(c.getId() + " - " + c.getName());
        }

        //  Sélection des catégories
        System.out.print("Entrez les IDs des catégories (ex: 1,3,5) : ");
        String input = scanner.nextLine();

        List<Integer> selectedCategories = new ArrayList<>();
        for (String id : input.split(",")) {
            selectedCategories.add(Integer.parseInt(id.trim()));
        }

        gameService.selectCategoriesSolo(sessionId, selectedCategories);

        //  Choix de la durée
        System.out.print("\nDurée de la partie (en secondes) : ");
        int duration = scanner.nextInt();
        scanner.nextLine();

        gameService.setDurationSolo(sessionId, duration);

        // Démarrer la partie
        gameService.startSoloGame(sessionId);
        char letter = session.getCurrentLetter();

        System.out.println("\nLettre générée : " + letter);

        // Saisie des réponses
        Map<Integer, String> answers = new HashMap<>();

        for (int catId : selectedCategories) {
            Category cat = categoryDAO.getCategory(catId);
            System.out.print(cat.getName() + " (" + letter + ") : ");
            String word = scanner.nextLine();
            answers.put(catId, word);
        }

        //Validation des réponses
        gameService.submitSoloAnswers(sessionId, playerName, answers);

        // Fin de partie SOLO : affichage du score
        Player player = session.getPlayer(playerName);

        System.out.println(" PARTIE TERMINÉE !");
        System.out.println(" SCORE FINAL : " + player.getScore() + " points");


        scanner.close();
    }
}
