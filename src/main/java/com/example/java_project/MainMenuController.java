package com.example.java_project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private ToggleGroup gameModeGroup;

    @FXML
    private ToggleButton soloModeBtn;

    @FXML
    private ToggleButton multiModeBtn;

    @FXML
    private ComboBox<String> themeCombo;

    @FXML
    private VBox roomPanel;

    @FXML
    private Spinner<Integer> timeSpinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Init Themes
        themeCombo.getItems().addAll("Animaux", "Villes", "Métiers", "Fruits/Légumes");
        themeCombo.getSelectionModel().selectFirst();

        // Init Time Spinner (Minutes)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        timeSpinner.setValueFactory(valueFactory);

        // Handle Mode Switching
        gameModeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == multiModeBtn) {
                setMultiplayerMode(true);
            } else {
                setMultiplayerMode(false);
            }
        });

        // Default state
        setMultiplayerMode(false);
    }

    private void setMultiplayerMode(boolean isMulti) {
        roomPanel.setVisible(isMulti);
        roomPanel.setManaged(isMulti); // Collapse if not visible
    }

    @FXML
    private void onLaunchGame() {
        System.out.println("Lancement de la partie !");
        System.out.println("Mode: " + (soloModeBtn.isSelected() ? "SOLO" : "MULTI"));
        System.out.println("Thème: " + themeCombo.getValue());
        System.out.println("Durée: " + timeSpinner.getValue() + " min");

        // TODO: Navigate to Game Board
    }
}
