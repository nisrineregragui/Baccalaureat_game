package com.example.java_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sockets.GameClient;
import sockets.GameServer;

import java.net.InetAddress;
import java.util.Arrays;

public class LobbyController {

    @FXML
    private Label connectionStatusLabel;
    @FXML
    private Label codeLabel;
    @FXML
    private ListView<String> playerList;
    @FXML
    private VBox hostControls;
    @FXML
    private Button startButton;
    @FXML
    private Slider durationSlider;
    @FXML
    private Label durationLabel;
    @FXML
    private VBox categoriesContainer;

    private boolean isHost;
    private GameServer server;
    private GameClient client;
    private String username;

    public void initData(String username, boolean isHost, String joinCode) {
        this.username = username;
        this.isHost = isHost;

        if (isHost) {
            setupHost();
        } else {
            setupClient(joinCode);
        }

        setupControls();
    }

    private void setupControls() {
        hostControls.setVisible(isHost);
        startButton.setVisible(isHost);
        hostControls.setManaged(isHost); // Don't take space if hidden

        if (isHost) {
            durationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                durationLabel.setText((int) newVal.doubleValue() + " sec");
                // Avoid spamming server, maybe only on release? but for now ok
                if (client != null)
                    client.setDuration((int) newVal.doubleValue());
            });
        } else {
            // Disable controls for client
            durationSlider.setDisable(true);
        }
    }

    private void setupHost() {
        try {
            // Start Server
            server = new GameServer(12345);
            new Thread(() -> server.startServer()).start();

            // Get IP
            String ip = InetAddress.getLocalHost().getHostAddress();
            codeLabel.setText("Code de la partie : " + ip);
            connectionStatusLabel.setText("Serveur démarré. En attente...");

            // Connect self
            connectToServer("localhost");

        } catch (Exception e) {
            connectionStatusLabel.setText("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupClient(String ip) {
        codeLabel.setText("Tentative de connexion à : " + ip);
        connectToServer(ip);
    }

    private void connectToServer(String ip) {
        client = new GameClient(ip, 12345);
        client.setOnMessageReceived(this::onMessageReceived);

        new Thread(() -> {
            boolean success = client.connect(username);
            Platform.runLater(() -> {
                if (success) {
                    connectionStatusLabel.setText("Connecté !");
                    client.requestLobbyUpdate();
                } else {
                    connectionStatusLabel.setText("Échec de connexion.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Connexion Impossible");
                    alert.setContentText("Impossible de rejoindre le serveur à l'adresse : " + ip);
                    alert.showAndWait();
                    // Go back?
                }
            });
        }).start();
    }

    private void onMessageReceived(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("LOBBY_UPDATE:")) {
                updateLobby(message.substring(13));
            } else if (message.startsWith("GAME_START:")) {
                navigateToGame(message);
            } else if (message.startsWith("DURATION_SET:")) {
                String val = message.split(":")[1];
                durationLabel.setText(val + " sec");
                durationSlider.setValue(Double.parseDouble(val));
            }
        });
    }

    private void updateLobby(String playerNames) {
        playerList.getItems().clear();
        String[] players = playerNames.split(",");
        playerList.getItems().addAll(Arrays.asList(players));
    }

    private void navigateToGame(String gameInfo) {
        Platform.runLater(() -> {
            try {
                // gameInfo format: GAME_START:letter:duration:id1,id2,id3
                String[] parts = gameInfo.split(":");
                // parts[0] is GAME_START
                char letter = parts[1].charAt(0);
                int duration = Integer.parseInt(parts[2]);
                String[] catIds = parts[3].split(",");

                java.util.List<models.Category> categories = new java.util.ArrayList<>();
                DAO.CategoryDAO dao = new DAO.CategoryDAO();
                for (String id : catIds) {
                    models.Category c = dao.getCategory(Integer.parseInt(id));
                    if (c != null)
                        categories.add(c);
                }

                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("game-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 800, 600);

                GameController controller = fxmlLoader.getController();
                controller.initData(duration, categories, letter);

                // Pass client to controller if needed in future
                // controller.setClient(client);

                Stage stage = (Stage) connectionStatusLabel.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    protected void onStartGameClick() {
        if (client != null) {
            client.sendStartSignal();
        }
    }

    public void cleanup() {
        if (client != null)
            client.disconnect();
        if (server != null)
            server.stopServer();
    }
}
