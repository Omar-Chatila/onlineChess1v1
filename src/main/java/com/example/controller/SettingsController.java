package com.example.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import themes.StandardTheme;
import themes.SwagTheme;
import themes.Theme;
import themes.WoodTheme;
import util.ApplicationData;
import util.SoundPlayer;

import java.io.IOException;

public class SettingsController {

    @FXML
    private JFXButton backButton;
    @FXML
    private VBox backgroundPane;
    @FXML
    private JFXRadioButton highlightOff;
    @FXML
    private JFXRadioButton highlightOn;
    @FXML
    private JFXRadioButton soundOff;
    @FXML
    private JFXRadioButton soundOn;
    @FXML
    private JFXComboBox<AnchorPane> themeselector;
    @FXML
    private JFXRadioButton animationsOff;
    @FXML
    private JFXRadioButton animationsOn;
    private Theme theme;

    @FXML
    private void initialize() throws IOException {
        setCombobox();
        soundOff.setOnAction(e -> SoundPlayer.muted = true);
        soundOn.setOnAction(e -> SoundPlayer.muted = false);
        highlightOff.setOnAction(e -> Theme.setNoHighlighting(true));
        highlightOn.setOnAction(e -> Theme.setNoHighlighting(false));
        animationsOn.setOnAction(e -> ChessboardController.animations = true);
        animationsOff.setOnAction(e -> ChessboardController.animations = false);
    }

    private void setCombobox() throws IOException {
        themeselector.setStyle("-fx-background-color: transparent");
        backButton.setOnAction(e -> ApplicationData.getInstance().getLoginViewController().switchPanes());
        themeselector.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AnchorPane item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Set the graphic of the cell to be your AnchorPane
                    setGraphic(item);
                    AnchorPane.setTopAnchor(item, 0.0);
                    AnchorPane.setRightAnchor(item, 0.0);
                    AnchorPane.setBottomAnchor(item, 0.0);
                    AnchorPane.setLeftAnchor(item, 0.0);
                }
            }
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/themeselector/standardtheme.fxml"));
        AnchorPane standard = loader.load();
        standard.setId("Standard");

        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/themeselector/swag.fxml"));
        AnchorPane swag = loader2.load();
        swag.setId("Swag");

        FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/themeselector/wood.fxml"));
        AnchorPane wood = loader3.load();
        wood.setId("Wood");

        themeselector.getItems().addAll(standard, wood, swag);
        themeselector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue.getId()) {
                    case "Standard" -> this.theme = new StandardTheme();
                    case "Swag" -> this.theme = new SwagTheme();
                    case "Wood" -> this.theme = new WoodTheme();
                }
                ApplicationData.getInstance().setTheme(this.theme);
                ApplicationData.getInstance().getLoginViewController().setLeftPane(theme.getLoginViewPaneStyle());
                backgroundPane.setStyle(this.theme.getSettingsStyle());
            }
        });
    }
}
