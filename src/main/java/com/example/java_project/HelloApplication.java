package com.example.java_project;

import config.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        try {
            System.out.println("Tentative de connexion...");
            Connection testConn = DatabaseConnection.getConnection();

            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✅ SUCCÈS : La connexion à MySQL est établie !");
            }
        } catch (Exception e) {
            System.err.println("❌ ÉCHEC : Impossible de se connecter.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}