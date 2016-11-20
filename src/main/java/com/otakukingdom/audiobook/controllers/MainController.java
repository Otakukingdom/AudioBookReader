package com.otakukingdom.audiobook.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Created by mistlight on 11/20/2016.
 */
public class MainController {

    @FXML
    private MenuItem menuAbout;

    @FXML
    private MenuItem menuClose;

    @FXML
    private Button playButton;

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
