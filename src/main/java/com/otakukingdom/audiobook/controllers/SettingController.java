package com.otakukingdom.audiobook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * Created by mistlight on 11/20/2016.
 */
public class SettingController {

    @FXML
    private TabPane rootPane;

    @FXML
    public void handleAddDirectoryAction(ActionEvent event) {

    }

    @FXML
    public void handleRemoveDirectoryAction(ActionEvent event) {

    }

    @FXML
    public void handleSaveAction(ActionEvent event) {
        Stage currentSage = (Stage) rootPane.getScene().getWindow();
        currentSage.close();
    }
}
