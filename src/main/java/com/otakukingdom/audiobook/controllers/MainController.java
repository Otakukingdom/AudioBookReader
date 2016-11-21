package com.otakukingdom.audiobook.controllers;

import com.otakukingdom.audiobook.services.AudioBookScanService;
import com.otakukingdom.audiobook.services.DirectoryService;
import com.otakukingdom.audiobook.services.SettingService;
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

    public void initialize() {
        settingService = new SettingService();
    }

    public void setDirectoryService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    public void setAudioBookScanService(AudioBookScanService audioBookScanService) {
        this.audioBookScanService = audioBookScanService;
        this.audioBookScanService.scan();
    }

    @FXML
    public void handleSettingsAction(ActionEvent event) throws IOException {
        Parent root;

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("SettingsWindow.fxml"));
        root = loader.load();

        // pass the directory service variables
        SettingController settingController = loader.getController();
        settingController.setDirectoryService(this.directoryService);

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

    @FXML
    private VBox mainPane;

    // non FXML instance vars
    private SettingService settingService;
    private AudioBookScanService audioBookScanService;
    private DirectoryService directoryService;

}
