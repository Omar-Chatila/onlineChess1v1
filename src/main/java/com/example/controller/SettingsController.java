package com.example.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import util.ApplicationData;

import java.util.Objects;

public class SettingsController {

    @FXML
    private ToggleGroup animationstoggle;

    @FXML
    private JFXButton backButton;

    @FXML
    private ToggleGroup highlightToggle;

    @FXML
    private ToggleGroup highlightToggle1;

    @FXML
    private ToggleGroup soundToggle;

    @FXML
    private JFXComboBox<HBox> themeselector;

    @FXML
    private void initialize() {
        themeselector.setStyle("-fx-background-color: transparent");
        backButton.setOnAction(e -> ApplicationData.getInstance().getLoginViewController().switchPanes());
        HBox hBox = new HBox(5);
        Label label = new Label("Standard");
        ImageView imageView = new ImageView();
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bQG.png")));
        imageView.setImage(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-background-color: transparent");
        hBox.getChildren().addAll(label, imageView);
        hBox.setStyle("-fx-background-color: transparent");
        themeselector.getItems().add(hBox);
    }
}
