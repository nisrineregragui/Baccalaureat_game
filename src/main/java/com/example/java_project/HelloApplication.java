package com.example.java_project;


import com.example.java_project.database.HibernateUtil;
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

    }
}