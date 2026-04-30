package com.example.java_project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import models.Category;

import java.util.List;
import java.util.Random;

public class GameController {

    @FXML
    private Label letterLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private VBox categoriesContainer;

    private int timeInSeconds;
    private List<Category> selectedCategories;
    private Timeline timeline;
    private char currentLetter;

    @FXML
    public void initialize() {
        System.out.println("Game View Initialized");
    }

    public void initData(int timeInSeconds, List<Category> selectedCategories, Character forceLetter) {
        this.timeInSeconds = timeInSeconds;
        this.selectedCategories = selectedCategories;
        if (forceLetter != null) {
            this.currentLetter = forceLetter;
        } else {
            this.currentLetter = (char) ('A' + new Random().nextInt(26));
        }
        System.out.println("Game initialized with " + timeInSeconds + "s and categories: " + selectedCategories);

        setupGame();
    }

    // Maintain backward compatibility for Solo
    public void initData(int timeInSeconds, List<Category> selectedCategories) {
        initData(timeInSeconds, selectedCategories, null);
    }

    private void setupGame() {
        // 1. Set Letter (already set in initData if forced)
        letterLabel.setText(String.valueOf(currentLetter));

        // Letter Pop Animation
        letterLabel.setScaleX(0);
        letterLabel.setScaleY(0);
        letterLabel.setRotate(-180);
        javafx.animation.ScaleTransition letterScale = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(800), letterLabel);
        letterScale.setToX(1);
        letterScale.setToY(1);
        letterScale.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        javafx.animation.RotateTransition letterRotate = new javafx.animation.RotateTransition(
                javafx.util.Duration.millis(800), letterLabel);
        letterRotate.setToAngle(0);

        new javafx.animation.ParallelTransition(letterScale, letterRotate).play();

        // 2. Generate Forms
        categoriesContainer.getChildren().clear();
        int delay = 0;
        for (Category category : selectedCategories) {
            VBox fieldBox = new VBox(5);
            fieldBox.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);");
            fieldBox.setMaxWidth(600);

            // Initial state for animation (invisible and slightly lower)
            fieldBox.setOpacity(0);
            fieldBox.setTranslateY(20);

            Label nameLabel = new Label(category.getName());
            nameLabel.getStyleClass().add("header-label");
            nameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #FF69B4;");

            TextField textField = new TextField();
            textField.setPromptText("Mot commençant par " + currentLetter + "...");
            textField.setId("field_" + category.getId());
            textField.getStyleClass().add("text-field");

            // Focus Animation (Zoom)
            textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(
                        javafx.util.Duration.millis(200), fieldBox);
                if (newVal) {
                    st.setToX(1.05);
                    st.setToY(1.05);
                    fieldBox.setStyle(
                            "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(255, 105, 180, 0.4), 10, 0, 0, 5);"); // Pink
                                                                                                                                                                                        // glow
                } else {
                    st.setToX(1.0);
                    st.setToY(1.0);
                    fieldBox.setStyle(
                            "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 3);"); // Reset
                }
                st.play();
            });

            fieldBox.getChildren().addAll(nameLabel, textField);
            categoriesContainer.getChildren().add(fieldBox);

            // Create Animation
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500),
                    fieldBox);
            fade.setToValue(1);

            javafx.animation.TranslateTransition translate = new javafx.animation.TranslateTransition(
                    javafx.util.Duration.millis(500), fieldBox);
            translate.setToY(0);

            javafx.animation.ParallelTransition pt = new javafx.animation.ParallelTransition(fade, translate);
            pt.setDelay(javafx.util.Duration.millis(delay));
            pt.play();

            delay += 100; // Stagger effect
        }

        // 3. Start Timer (Updated with Sound)
        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> {
            timeInSeconds--;
            int minutes = timeInSeconds / 60;
            int seconds = timeInSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

            // Heartbeat animation and sound for last 10 seconds
            if (timeInSeconds <= 10 && timeInSeconds > 0) {
                timerLabel.setStyle("-fx-text-fill: red; -fx-font-size: 45px;");
                services.SoundService.playTick(); // Play Tick Sound

                javafx.animation.ScaleTransition pulse = new javafx.animation.ScaleTransition(
                        javafx.util.Duration.millis(200), timerLabel);
                pulse.setByX(0.2);
                pulse.setByY(0.2);
                pulse.setCycleCount(2);
                pulse.setAutoReverse(true);
                pulse.play();
            }

            if (timeInSeconds <= 0) {
                services.SoundService.playAlarm(); // Play Alarm Sound
                onSubmitClick();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Play Pop sound on start
        services.SoundService.playPop();
    }

    private void updateTimerLabel() {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @FXML
    protected void onSubmitClick() {
        if (timeline != null)
            timeline.stop();
        System.out.println("Partie terminée !");

        services.ValidationService validationService = new services.ValidationService();
        int score = 0;
        int correctWords = 0;
        int totalCategories = selectedCategories.size();

        for (javafx.scene.Node node : categoriesContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox fieldBox = (VBox) node;
                // Assuming TextField is the second child (index 1)
                if (fieldBox.getChildren().size() > 1 && fieldBox.getChildren().get(1) instanceof TextField) {
                    TextField textField = (TextField) fieldBox.getChildren().get(1);
                    String text = textField.getText().trim();
                    String idString = textField.getId().replace("field_", "");
                    int categoryId = Integer.parseInt(idString);

                    if (!text.isEmpty()) {
                        // Validate word
                        boolean isValid = validationService.word_validation(text, currentLetter, categoryId);
                        if (isValid) {
                            score += 10;
                            correctWords++;
                            System.out.println("Mot valide (+10): " + text);
                        } else {
                            System.out.println("Mot invalide: " + text);
                        }
                    }
                }
            }
        }

        // Navigate to Results
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(
                    HelloApplication.class.getResource("solo-results-view.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 800, 600);

            SoloResultsController controller = fxmlLoader.getController();
            controller.setScore(score, correctWords, totalCategories);

            javafx.stage.Stage stage = (javafx.stage.Stage) categoriesContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
