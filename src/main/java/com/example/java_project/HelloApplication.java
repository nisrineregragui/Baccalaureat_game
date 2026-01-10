package com.example.java_project;

import com.example.java_project.database.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.APIService;
import services.ValidationService;
import sockets.GameClient;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void init() {
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("Hibernate est prêt et les tables sont créées !");
        } catch (Exception e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
        /*
         * //test API
         * APIService api = new APIService();
         * System.out.println("Test Nisrine/Nom/N : " + api.verify_word("nisrine",
         * "nompropre", 'n'));
         * System.out.println("Test Banane/Animal/B : " + api.verify_word("Banane",
         * "Animal", 'B'));
         * 
         * //validation test
         * ValidationService validationService = new ValidationService();
         * System.out.println("--- test validatiion ---");
         * //id= 1 ---------------> VILLE ---> true + insert in db
         * boolean test1 = validationService.word_validation("Casablanca", 'c', 1);
         * long startTime = System.currentTimeMillis();
         * long endTime = System.currentTimeMillis();
         * System.out.println("test 1(Casablanca/Ville/C) : " + (test1 ? "RÉUSSI" :
         * "ÉCHEC ") + " en " + (endTime - startTime) + "ms");
         * //test 2 false
         * boolean test2 = validationService.word_validation("Lyon", 'P', 1);
         * System.out.println("test2(Lyon/Ville/P) : " + (!test2 ?
         * "RÉUSSI (Refusé car mauvaise lettre) " : "ÉCHEC "));
         * 
         * //relancer le test sur "Paris"
         * boolean test3 = validationService.word_validation("Paris", 'P', 1);
         * System.out.println("test 3(SQL) : " + (test3 ? "RÉUSSI " : "ÉCHEC ") + " en "
         * + (endTime - startTime) + "ms");
         * System.out.println("--- FIN DES TESTS ---");
         * 
         * 
         * System.out.println("TESTING DU SERVER");
         * GameClient client = new GameClient("localhost", 12345);
         * client.connect();
         * client.sendWordForValidation("NEWYORK", 'N', 1);
         */
    }
}