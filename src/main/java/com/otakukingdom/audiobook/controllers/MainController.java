package com.otakukingdom.audiobook.controllers;

import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.observers.FileListObserver;
import com.otakukingdom.audiobook.services.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mistlight on 11/20/2016.
 */
public class MainController implements FileListObserver {


    public void initialize() {
        this.settingService = new SettingService();
        this.fileListService = new FileListService();
        this.fileListService.addListener(this);

        // ensures that the fileList service knows about the change
        this.libraryListViewUI.getSelectionModel().selectedItemProperty().addListener(fileListService);
    }


    public void setDirectoryService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    public void setAudioBookScanService(AudioBookScanService audioBookScanService) {
        this.audioBookScanService = audioBookScanService;
        this.audioBookScanService.scan();

        try {
            this.libraryService = new LibraryService(audioBookScanService);
            this.audioBookList = this.libraryService.getAudioBookList();
        } catch (SQLException e) {
            this.audioBookList = new ArrayList<AudioBook>();
            e.printStackTrace();
        }

        libraryListViewUI.setItems(FXCollections.<AudioBook>observableArrayList(this.audioBookList));
    }

    @FXML
    public void handleSettingsAction(ActionEvent event) throws IOException {
        Parent root;

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("SettingsWindow.fxml"));
        root = loader.load();

        // pass the directory service variables
        SettingController settingController = loader.getController();
        settingController.setDirectoryService(this.directoryService);
        settingController.setAudioBookScanService(this.audioBookScanService);

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

    @Override
    public void fileListUpdated(List<AudioBookFile> newFileList) {
        this.fileListUI.setItems(FXCollections.observableArrayList(newFileList));
    }

    @Override
    public void selectedFileUpdated(AudioBookFile selectedFile) {
        this.fileListUI.getSelectionModel().select(selectedFile);
    }

    @FXML
    private VBox mainPane;

    @FXML
    private ListView<AudioBookFile> fileListUI;

    @FXML
    private ListView<AudioBook> libraryListViewUI;

    // non FXML instance vars
    private SettingService settingService;
    private AudioBookScanService audioBookScanService;
    private LibraryService libraryService;
    private DirectoryService directoryService;
    private FileListService fileListService;

    private List<AudioBook> audioBookList;
}
