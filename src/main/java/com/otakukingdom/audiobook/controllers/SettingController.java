package com.otakukingdom.audiobook.controllers;

import com.otakukingdom.audiobook.services.DirectoryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by mistlight on 11/20/2016.
 */
public class SettingController {


    @FXML
    public void handleAddDirectoryAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(rootPane.getScene().getWindow());

        if(selectedDirectory == null) {
            System.out.println("No directory chosen");
        } else {
            System.out.println(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    public void handleRemoveDirectoryAction(ActionEvent event) {

    }

    @FXML
    public void handleSaveAction(ActionEvent event) {
        Stage currentSage = (Stage) rootPane.getScene().getWindow();
        currentSage.close();
    }

    public void initialize() {
        directoryService = new DirectoryService();
    }

    @FXML
    private TabPane rootPane;

    // non FXML instance vars
    private DirectoryService directoryService;
}
