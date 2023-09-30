package com.example.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import util.ApplicationData;

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
    private JFXComboBox<?> themeselector;

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> ApplicationData.getInstance().getLoginViewController().switchPanes());
        
    }
}
