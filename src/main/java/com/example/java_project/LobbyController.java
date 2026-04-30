package com.example.java_project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    private javafx.scene.layout.HBox hostControls;
    @FXML
    private Button startButton;
    @FXML
    private ComboBox<String> durationComboBox;
    @FXML
    private ListView<models.Category> categoriesListView;
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
            setupCategories();
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
            durationComboBox.getItems().addAll("60 secondes", "90 secondes", "120 secondes");
            durationComboBox.setValue("120 secondes");

            durationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    int duration = Integer.parseInt(newVal.split(" ")[0]);
                    if (client != null) {
                        client.setDuration(duration);
                    }
                }
            });
        } else {
            // Disable controls for client
            // Controls are hidden for client anyway by hostControls visibility
        }
    }

    private void setupHost() {
        try {
            // Start Server
            server = new GameServer(12345);
            new Thread(() -> server.startServer()).start();

            // Get IP
            String ip = getLocalIpAddress();
            String code = services.CodeConverter.ipToCode(ip);

            codeLabel.setText(code);
            connectionStatusLabel.setText("Serveur démarré (IP: " + ip + ")"); // Keep IP visible for debug if needed

            // Connect self
            connectToServer("localhost");

        } catch (Exception e) {
            connectionStatusLabel.setText("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getLocalIpAddress() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface iface = interfaces.nextElement();
                // Filters
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual())
                    continue;
                if (iface.getDisplayName().toLowerCase().contains("vmware")
                        || iface.getDisplayName().toLowerCase().contains("virtual"))
                    continue;

                java.util.Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
            // Fallback
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Inconnu";
        }
    }

    private void setupClient(String ip) {
        codeLabel.setText("Tentative de connexion à : " + ip);
        connectToServer(ip);
    }

    // --- Category Selection Logic (Host) ---
    private void setupCategories() {
        categoriesContainer.setVisible(true);
        categoriesContainer.setManaged(true);

        DAO.CategoryDAO categoryDAO = new DAO.CategoryDAO();
        categoryDAO.initCategoriesIfEmpty();
        java.util.List<models.Category> categories = categoryDAO.getCategories();
        if (categories != null) {
            categoriesListView.getItems().addAll(categories);
        }

        categoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Checkbox Cell Factory
        categoriesListView.setCellFactory(lv -> new ListCell<models.Category>() {
            private final javafx.scene.layout.HBox content;
            private final Label nameLabel;
            private final Label checkLabel;
            private final javafx.scene.layout.Region spacer;

            {
                content = new javafx.scene.layout.HBox();
                content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                nameLabel = new Label();
                nameLabel.setStyle("-fx-text-fill: inherit; -fx-font-size: inherit;");
                checkLabel = new Label("✔");
                checkLabel.setStyle("-fx-text-fill: #FF69B4; -fx-font-size: 24px; -fx-font-weight: bold;");
                spacer = new javafx.scene.layout.Region();
                javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                content.getChildren().addAll(nameLabel, spacer, checkLabel);
            }

            @Override
            protected void updateItem(models.Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getName());
                    checkLabel.visibleProperty().bind(selectedProperty());
                    setGraphic(content);
                    setText(null);
                }
            }
        });
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
                    alert.setContentText("Impossible de rejoindre le serveur à l'adresse : " + ip + "\n\nRaison: "
                            + client.getLastError());
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
                durationComboBox.setValue(val + " secondes");
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
            String durationStr = durationComboBox.getValue();
            if (durationStr == null)
                durationStr = "60 secondes"; // default
            int duration = Integer.parseInt(durationStr.split(" ")[0]);

            java.util.List<models.Category> selected = categoriesListView.getSelectionModel().getSelectedItems();
            if (selected.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention");
                alert.setHeaderText("Aucune catégorie");
                alert.setContentText("Veuillez sélectionner au moins une catégorie.");
                alert.showAndWait();
                return;
            }

            // Create ID string "1,2,5"
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < selected.size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append(selected.get(i).getId());
            }

            client.sendStartSignal(duration, sb.toString());
        }
    }

    public void cleanup() {
        if (client != null)
            client.disconnect();
        if (server != null)
            server.stopServer();
    }
}
