package com.example.java_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class SoloResultsController {

    @FXML
    private Label scoreLabel;

    @FXML
    private Label detailsLabel;

    public void setScore(int score, int correctWords, int totalWords) {
        scoreLabel.setText(score + " pts");
        detailsLabel.setText("Mots corrects : " + correctWords + "/" + totalWords);

        // Play success sound
        services.SoundService.playSuccess();
    }

    @FXML
    protected void onBackMenuClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
