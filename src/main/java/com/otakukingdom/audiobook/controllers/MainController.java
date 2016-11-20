package com.otakukingdom.audiobook.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by mistlight on 11/20/2016.
 */
public class MainController {

    @FXML
    private VBox mainPane;

    @FXML
    public void handleSettingsAction(ActionEvent event) throws IOException {
        Parent root;

        root = FXMLLoader.load(getClass().getClassLoader().getResource("SettingsWindow.fxml"));
        Stage stage = new Stage();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainPane.getScene().getWindow());

        stage.setTitle("Settings");
        stage.setScene(new Scene(root, 450, 450));
        stage.show();
    }

    @FXML
    public void handleCloseAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void handlePlayAction(ActionEvent event) {

    }

    public void initialize() {
    }
}
