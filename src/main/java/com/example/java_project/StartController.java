package com.example.java_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class StartController {

    @FXML
    protected void onSoloButtonClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("solo-setup-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onMultiButtonClick(ActionEvent event) {
        TextInputDialog nameDialog = new TextInputDialog("Joueur");
        nameDialog.setTitle("Pseudo");
        nameDialog.setHeaderText("Choisissez votre pseudo");
        nameDialog.setContentText("Pseudo:");
        applyStyle(nameDialog.getDialogPane());

        var result = nameDialog.showAndWait();

        if (result.isPresent()) {
            String username = result.get();
            if (username.trim().isEmpty())
                username = "Joueur" + (int) (Math.random() * 100);

            //Host or Join?
            Alert choiceAlert = new Alert(Alert.AlertType.CONFIRMATION);
            choiceAlert.setTitle("Multijoueur");
            choiceAlert.setHeaderText("Que voulez-vous faire ?");
            choiceAlert.setContentText("Choisissez votre mode de jeu");
            applyStyle(choiceAlert.getDialogPane());

            ButtonType hostBtn = new ButtonType("Créer une partie");
            ButtonType joinBtn = new ButtonType("Rejoindre");
            ButtonType cancelBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

            choiceAlert.getButtonTypes().setAll(hostBtn, joinBtn, cancelBtn);

            var choice = choiceAlert.showAndWait();

            if (choice.isPresent()) {
                if (choice.get() == hostBtn) {
                    openLobby(event, username, true, null);
                } else if (choice.get() == joinBtn) {
                    TextInputDialog codeDialog = new TextInputDialog("");
                    codeDialog.setTitle("Rejoindre");
                    codeDialog.setHeaderText("Entrez le CODE de la partie");
                    codeDialog.setContentText("Code:");
                    applyStyle(codeDialog.getDialogPane());

                    var codeResult = codeDialog.showAndWait();
                    if (codeResult.isPresent()) {
                        String code = codeResult.get().trim();
                        String ip;

                        if (code.equalsIgnoreCase("LOCALHOST")) {
                            ip = "127.0.0.1";
                        } else {
                            ip = services.CodeConverter.codeToIp(code);
                        }

                        if (ip != null) {
                            openLobby(event, username, false, ip);
                        } else {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Erreur");
                            error.setHeaderText("Code invalide");
                            error.setContentText("Le code saisi n'est pas valide.");
                            applyStyle(error.getDialogPane());
                            error.showAndWait();
                        }
                    }
                }
            }
        }
    }

    private void applyStyle(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");
    }

    private void openLobby(ActionEvent event, String username, boolean isHost, String ip) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("lobby-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            LobbyController controller = fxmlLoader.getController();
            controller.initData(username, isHost, ip);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);

            // server stops if window closes
            stage.setOnCloseRequest(e -> controller.cleanup());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
