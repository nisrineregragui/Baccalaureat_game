package com.example.java_project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sockets.GameClient;

public class WaitingController {

    @FXML
    private TableView<PlayerScore> scoresTable;

    @FXML
    private TableColumn<PlayerScore, String> playerColumn;

    @FXML
    private TableColumn<PlayerScore, Integer> scoreColumn;

    private ObservableList<PlayerScore> playersList = FXCollections.observableArrayList();
    private GameClient client;

    @FXML
    public void initialize() {
        playerColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoresTable.setItems(playersList);
    }

    public void setClient(GameClient client) {
        this.client = client;
        // Listen for updates
        this.client.setOnMessageReceived(this::onMessageReceived);
    }

    public void addFinishedPlayer(String username, int score) {
        // Check if exists
        for (PlayerScore p : playersList) {
            if (p.getUsername().equals(username)) {
                p.setScore(score);
                sortList();
                return;
            }
        }
        playersList.add(new PlayerScore(username, score));
        sortList();
    }

    private void sortList() {
        // Sort descending
        FXCollections.sort(playersList, (a, b) -> Integer.compare(b.getScore(), a.getScore()));
        scoresTable.refresh();
    }

    private void onMessageReceived(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("PLAYER_FINISHED:")) {
                String[] parts = message.split(":");
                String username = parts[1];
                int score = Integer.parseInt(parts[2]);
                addFinishedPlayer(username, score);
            } else if (message.startsWith("GAME_END:")) {
                // Navigate to Final Results
                // For now, we reuse the Logic in LobbyController or similar to show alert, but
                // ideally we navigate to results view
                String scores = message.substring(9);
                navigateToFinalResults(scores);
            } else if (message.startsWith("VALIDATION_RESULTS:")) {
                // Ignore validation results here (handled previously)
            }
        });
    }

    private void navigateToFinalResults(String scores) {
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                    HelloApplication.class.getResource("results-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 800, 600);

            ResultsController controller = fxmlLoader.getController();

            // Parse scores string and update controller
            // Using same logic as LobbyController's showGameResults but adapting to
            // ResultsController
            String[] entries = scores.split(",");
            for (String entry : entries) {
                String[] parts = entry.split(";");
                if (parts.length >= 2) {
                    controller.addResult(parts[0], Integer.parseInt(parts[1]));
                }
            }

            javafx.stage.Stage stage = (javafx.stage.Stage) scoresTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class for TableView
    public static class PlayerScore {
        private String username;
        private int score;

        public PlayerScore(String username, int score) {
            this.username = username;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
