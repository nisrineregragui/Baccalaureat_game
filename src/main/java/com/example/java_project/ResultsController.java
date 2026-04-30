package com.example.java_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ResultsController {

    @FXML
    private TableView<PlayerResult> scoresTable;

    @FXML
    private TableColumn<PlayerResult, String> playerColumn;

    @FXML
    private TableColumn<PlayerResult, Integer> scoreColumn;

    @FXML
    private Label winnerLabel;

    private ObservableList<PlayerResult> resultsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoresTable.setItems(resultsList);
    }

    public void addResult(String username, int score) {
        resultsList.add(new PlayerResult(username, score));
        sortResults();
    }

    private void sortResults() {
        FXCollections.sort(resultsList, (a, b) -> Integer.compare(b.getScore(), a.getScore()));

        if (!resultsList.isEmpty()) {
            int maxScore = resultsList.get(0).getScore();
            long winnersCount = resultsList.stream().filter(p -> p.getScore() == maxScore).count();

            if (winnersCount > 1) {
                winnerLabel.setText("Égalité ! 🤝");
            } else {
                winnerLabel.setText("Vainqueur : " + resultsList.get(0).getUsername() + " 🏆");
            }
        }
    }

    @FXML
    protected void onBackToLobbyClick(ActionEvent event) {
        try {
            // Note: In a real app we might want to keep the connection open or reconnect
            // For now, we go back to Start View or Lobby View.
            // Going back to Lobby usually requires re-joining.
            // Let's go to Start View for simplicity.
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start_view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class PlayerResult {
        private String username;
        private int score;

        public PlayerResult(String username, int score) {
            this.username = username;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public int getScore() {
            return score;
        }
    }
}
