package com.otakukingdom.audiobook.controllers;

import com.otakukingdom.audiobook.model.Directory;
import com.otakukingdom.audiobook.observers.DirectoryObserver;
import com.otakukingdom.audiobook.services.DirectoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
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

    }

    @FXML
    public void handleSaveAction(ActionEvent event) {
        Stage currentSage = (Stage) rootPane.getScene().getWindow();
        currentSage.close();
    }

    public void initialize() {
        directoryService = new DirectoryService();
        directoryService.addObserver(this);

        directoryListUpdated();
    }

    @FXML
    private TabPane rootPane;

    @FXML
    private ListView<Directory> directoryListUI;

    // non FXML instance vars
    private DirectoryService directoryService;

    public void directoryListUpdated() {
        List<Directory> directoryList = directoryService.getDirectories();
        if(directoryList != null) {
            directoryListUI.setItems(FXCollections.<Directory>observableArrayList(directoryList));
        }
    }
}
