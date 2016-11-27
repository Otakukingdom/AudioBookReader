package com.otakukingdom.audiobook.controllers;

import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.observers.FileListObserver;
import com.otakukingdom.audiobook.services.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        this.mediaPlayerService = new MediaPlayerService();



        this.fileListService.addListener(this);
        this.fileListService.addListener(mediaPlayerService);

        // ensures that the fileList service knows about the change
        this.libraryListViewUI.getSelectionModel().selectedItemProperty().addListener(fileListService);

        // set the new audiobook file based on the filelist UI event
        this.fileListUI.getSelectionModel().selectedItemProperty().
                addListener(((observable, oldValue, newValue) -> {
                    if(newValue != null) {
                        fileListService.setSelectedAudioBookFile(newValue);
                    }
                }));


        this.mediaSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            double min = 0.5;
            if(!this.mediaSlider.isValueChanging()) {
                double currentTime = this.mediaPlayer.getCurrentTime().toSeconds();
                if(Math.abs(currentTime - newValue.doubleValue()) > min) {
                    this.mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        }));

        this.mediaSlider.valueChangingProperty().addListener(((observable, wasChanging, isChanging) -> {
            if(!isChanging) {
                this.mediaPlayer.seek(Duration.seconds(this.mediaSlider.getValue()));
            }
        }));

        setMediaPlayerListener();
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
        if(this.mediaPlayer != null) {
            if(!mediaPlayer.statusProperty().get().equals(MediaPlayer.Status.PLAYING)) {
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();
            }
        }
    }

    @Override
    public void fileListUpdated(List<AudioBookFile> newFileList) {
        this.fileListUI.setItems(FXCollections.observableArrayList(newFileList));
    }

    @Override
    public void selectedFileUpdated(AudioBookFile selectedFile) {
        this.fileListUI.getSelectionModel().select(selectedFile);
    }

    private void setMediaPlayerListener() {
        this.mediaPlayerService.addListener((MediaPlayer mediaPlayer) -> {
            this.mediaPlayer = mediaPlayer;

            if (this.mediaPlayer == null) {
                // return early if there is nothing
                return;
            }

            // since we changed the media player, we should reset the UI
            resetMediaUI();

            this.mediaPlayer.setOnReady(() -> {
                mediaSlider.setMax(this.mediaPlayer.getTotalDuration().toSeconds());
            });

            this.mediaPlayer.setOnPlaying(()-> {
                playButton.setText("Pause");
            });

            this.mediaPlayer.setOnPaused(() -> {
                playButton.setText("Play");
            });

            this.mediaPlayer.setOnEndOfMedia(() -> {
            });

            this.mediaPlayer.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {

                // update the label
                long seconds = (long) newValue.toSeconds();
                setDurationLabel(seconds);

                // update slider position
                if(!mediaSlider.isValueChanging()) {
                    mediaSlider.setValue(newValue.toSeconds());
                }
            }));
        });
    }

    private void resetMediaUI() {
        // steps for resetting the media UI
        playButton.setText("Play");
        mediaSlider.setValue(0);
        setDurationLabel(0);
    }

    private void setDurationLabel(long seconds) {
        String label = String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
        mediaDuration.setText(label);
    }

    @FXML
    private Button playButton;

    @FXML
    private VBox mainPane;

    @FXML
    private ListView<AudioBookFile> fileListUI;

    @FXML
    private ListView<AudioBook> libraryListViewUI;


    @FXML
    private Label mediaDuration;

    @FXML
    private Slider mediaSlider;

    @FXML
    private Slider volumeSlider;


    // media related UI elements

    // non FXML instance vars
    private SettingService settingService;
    private AudioBookScanService audioBookScanService;
    private LibraryService libraryService;
    private DirectoryService directoryService;
    private FileListService fileListService;
    private MediaPlayerService mediaPlayerService;

    private MediaPlayer mediaPlayer;

    private List<AudioBook> audioBookList;

}
