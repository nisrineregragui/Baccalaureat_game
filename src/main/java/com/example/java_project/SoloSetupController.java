package com.example.java_project;

import DAO.CategoryDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import models.Category;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SoloSetupController {

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private ListView<Category> categoriesListView;

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML
    public void initialize() {
        // Load categories
        categoryDAO.initCategoriesIfEmpty(); // Ensure data exists
        List<Category> categories = categoryDAO.getCategories();
        if (categories != null) {
            categoriesListView.getItems().addAll(categories);
        }

        // Allow multiple selection (optional, but good for game customization)
        categoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Custom Cell Factory for Checkmark
        categoriesListView.setCellFactory(lv -> new javafx.scene.control.ListCell<Category>() {
            private final javafx.scene.layout.HBox content;
            private final javafx.scene.control.Label nameLabel;
            private final javafx.scene.control.Label checkLabel;
            private final javafx.scene.layout.Region spacer;

            {
                content = new javafx.scene.layout.HBox();
                content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                nameLabel = new javafx.scene.control.Label();
                nameLabel.setStyle("-fx-text-fill: inherit; -fx-font-size: inherit;"); // Inherit styles
                checkLabel = new javafx.scene.control.Label("✔");
                checkLabel.setStyle("-fx-text-fill: #FF69B4; -fx-font-size: 24px; -fx-font-weight: bold;");
                spacer = new javafx.scene.layout.Region();
                javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                content.getChildren().addAll(nameLabel, spacer, checkLabel);
            }

            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getName());
                    checkLabel.visibleProperty().bind(selectedProperty()); // Bind check visibility to selection
                    setGraphic(content);
                    setText(null); // Use graphic for text
                }
            }
        });

        // Select first time option by default
        timeComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    protected void onStartGameClick(ActionEvent event) {
        String selectedTimeStr = timeComboBox.getSelectionModel().getSelectedItem();
        List<Category> selectedCategories = categoriesListView.getSelectionModel().getSelectedItems();

        if (selectedCategories.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Aucune catégorie sélectionnée");
            alert.setContentText("Veuillez choisir au moins une catégorie pour jouer !");
            alert.showAndWait();
            return;
        }

        // Parse time
        int timeInSeconds = 60;
        if (selectedTimeStr.contains("90"))
            timeInSeconds = 90;
        else if (selectedTimeStr.contains("120"))
            timeInSeconds = 120;

        System.out.println(
                "Starting game with: " + timeInSeconds + "s and " + selectedCategories.size() + " categories.");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("game-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            GameController controller = fxmlLoader.getController();
            controller.initData(timeInSeconds, selectedCategories);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
