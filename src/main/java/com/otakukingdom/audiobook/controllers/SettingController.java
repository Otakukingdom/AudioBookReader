package com.otakukingdom.audiobook.controllers;

import com.otakukingdom.audiobook.model.Directory;
import com.otakukingdom.audiobook.observers.DirectoryObserver;
import com.otakukingdom.audiobook.services.AudioBookScanService;
import com.otakukingdom.audiobook.services.DirectoryService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Created by mistlight on 11/20/2016.
 */
public class SettingController implements DirectoryObserver {


    @FXML
    public void handleAddDirectoryAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(rootPane.getScene().getWindow());

        if(selectedDirectory == null) {
            System.out.println("No directory chosen");
        } else {
            directoryService.addDirectory(selectedDirectory);
        }
    }

    @FXML
    public void handleRemoveDirectoryAction(ActionEvent event) {
        Directory selectedDirectory =
                this.directoryListUI.getSelectionModel().getSelectedItem();
        if(selectedDirectory != null) {
            directoryService.removeDirectory(selectedDirectory.getFullPath());
        }
    }

    @FXML
    public void handleListClickedAction(MouseEvent event) {
        Directory selectedDirectory =
                this.directoryListUI.getSelectionModel().getSelectedItem();
        if(selectedDirectory != null) {
            removeDirectoryButton.setDisable(false);
        } else {
            removeDirectoryButton.setDisable(true);
        }
    }

    @FXML
    public void handleSaveAction(ActionEvent event) {
        Stage currentSage = (Stage) rootPane.getScene().getWindow();
        currentSage.close();
    }

    public void setDirectoryService(DirectoryService directoryService) {
        this.directoryService = directoryService;
        this.directoryService.addObserver(this);

        directoryListUpdated();
    }

    public void setAudioBookScanService(AudioBookScanService audioBookScanService) {
        this.audioBookScanService = audioBookScanService;
    }

    public void handleRescanAction() {
        this.audioBookScanService.rescanAll();
    }

    // Callback
    public void directoryListUpdated() {
        List<Directory> directoryList = directoryService.getDirectories();
        if(directoryList != null) {
            directoryListUI.setItems(FXCollections.<Directory>observableArrayList(directoryList));
        }
    }

    @FXML
    private TabPane rootPane;

    @FXML
    private ListView<Directory> directoryListUI;

    @FXML
    private Button removeDirectoryButton;

    // non FXML instance vars
    private DirectoryService directoryService;
    private AudioBookScanService audioBookScanService;

}
