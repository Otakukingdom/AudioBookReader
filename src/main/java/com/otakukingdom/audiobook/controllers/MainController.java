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
    private static double EPSILON = 1.0;


    public void initialize() {
        // we need this flag so slider to seek functionality doesn't enable during startup
        // when the mediaplayer hasn't initialized yet
        this.sliderToSeekEnabled = false;
        this.nextFileCalled = false;


        // initialize the various services we need
        this.settingService = SettingService.getInstance();
        this.fileListService = new FileListService();
        this.mediaPlayerService = new MediaPlayerService(this.fileListService);


        // bind the file list service to this controller as well as the media player service
        this.fileListService.addListener(this);
        this.fileListService.addListener(mediaPlayerService);

        // ensures that the fileList service knows about the change when
        // user changes the library selection
        this.libraryListViewUI.getSelectionModel().selectedItemProperty().addListener(fileListService);

        // ensures that when the audio book is changed, it is written in the settings file
        this.libraryListViewUI.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                this.settingService.setCurrentAudioBook(newValue);
            }
        }));

        // set the new audiobook file based on the filelist UI event
        this.fileListUI.getSelectionModel().selectedItemProperty().
                addListener(((observable, oldValue, newValue) -> {
                    if(newValue != null) {
                        // notify the fileListService
                        fileListService.setSelectedAudioBookFile(newValue);
                    }
                }));


        // set the media position based on the slider change
        this.mediaSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if(!sliderToSeekEnabled) {
                return;
            }

            double min = 0.5;
            if(!this.mediaSlider.isValueChanging()) {
                double currentTime = this.mediaPlayer.getCurrentTime().toSeconds();
                if(Math.abs(currentTime - newValue.doubleValue()) > min) {
                    this.mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                    this.mediaPlayerService.saveState();
                }
            }
        }));

        this.mediaSlider.valueChangingProperty().addListener(((observable, wasChanging, isChanging) -> {
            if(!isChanging) {
                this.mediaPlayer.seek(Duration.seconds(this.mediaSlider.getValue()));
                this.mediaPlayerService.saveState();
            }
        }));

        // set the volume based on settings
        this.volumeSlider.setValue(this.settingService.getVolume());

        // update the volume setting when the slider changes
        this.volumeSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            // update the volume for the settings service
            this.settingService.setVolume((double) newValue);

            // update the volume for the current media player, if it exists
            if(this.mediaPlayer != null) {
                this.mediaPlayer.setVolume((double) newValue);
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

        // update on a rescan
        this.audioBookScanService.addObserver(() -> {
            this.audioBookList = this.libraryService.getAudioBookList();
            libraryListViewUI.setItems(FXCollections.<AudioBook>observableArrayList(this.audioBookList));
        });

        // once we are done, we can finally load the audiobook that was last selected
        // if possible
        AudioBook lastAudioBook = this.settingService.getCurrentAudioBook();
        if(!(lastAudioBook == null)) {
            // get the index of the matching audiobook if there is one
            List<AudioBook> abList = this.libraryListViewUI.getItems();

            // select the last loaded audiobook if we can find a match with the one on the libraryListViewUI
            int found = -1;
            for(int i = 0; i < abList.size(); i++) {
                // see if we found it
                if(abList.get(i).getId().equals(lastAudioBook.getId())) {
                    found = i;
                }
            }

            // see if we found it
            if(found != -1) {
                this.libraryListViewUI.getSelectionModel().select(found);
            }
        }
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
    public void fileListUpdated(ObservableList<AudioBookFile> newFileList) {
        this.fileListUI.setItems(newFileList);
    }

    @Override
    public void selectedFileUpdated(AudioBookFile selectedFile) {
        this.fileListUI.getSelectionModel().select(selectedFile);
    }

    private void setMediaPlayerListener() {
        this.mediaPlayerService.addListener((MediaPlayer mediaPlayer) -> {
            this.sliderToSeekEnabled = false;
            this.mediaPlayer = mediaPlayer;

            if (this.mediaPlayer == null) {
                // return early if there is nothing
                return;
            }

            if(this.nextFileCalled) {
                // set autoplay status if nextfile was called
                this.mediaPlayer.setAutoPlay(true);
            }


            // since we changed the media player, we should reset the UI
            resetMediaUI();

            this.mediaPlayer.setOnReady(() -> {
                mediaSlider.setMax(this.mediaPlayer.getTotalDuration().toSeconds());

                // update slider position
                AudioBookFile currentFile = this.mediaPlayerService.getCurrentFile();
                mediaPlayer.seek(Duration.seconds(currentFile.getSeekPosition()));
                mediaSlider.setValue(currentFile.getSeekPosition());

                // update the label
                long seconds = currentFile.getSeekPosition().longValue();
                setDurationLabel(seconds);

                // enable the slider to seek functionality so we can use the slider to change
                // the seek position
                sliderToSeekEnabled = true;

                // if the next file was called previous (which will set the autoplay), check
                // if we need to skip this file as well
                if(this.nextFileCalled) {
                    // reset this flag
                    this.nextFileCalled = false;

                    // check if we are in a file that is already near the end, we should skip this file entirely
                    double diff = mediaPlayer.getTotalDuration().toSeconds() - currentFile.getSeekPosition();
                    if(mediaPlayer.getTotalDuration().toSeconds() - currentFile.getSeekPosition() < EPSILON) {
                        // skip to the next file if this is indeed the case
                        if(fileListService.nextFile() != null) {
                            this.nextFileCalled = true;
                            this.fileListService.setNextFile();
                        }
                    }

                    // if we are here, it means we are good to go, play this file
                    this.mediaPlayer.play();
                }

            });

            this.mediaPlayer.setOnPlaying(()-> {
                playButton.setText("Pause");
            });

            this.mediaPlayer.setOnPaused(() -> {
                playButton.setText("Play");
                this.mediaPlayerService.saveState();
            });

            this.mediaPlayer.setOnEndOfMedia(() -> {
                if(this.fileListService.nextFile() != null) {
                    this.fileListService.setNextFile();
                    this.nextFileCalled = true;
                }
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

            this.mediaPlayer.setVolume(settingService.getVolume());
        });
    }

    private void resetMediaUI() {
        // steps for resetting the media UI
        playButton.setText("Play");

        if(this.mediaPlayer == null) {
            mediaSlider.setValue(0);
            setDurationLabel(0);
        }
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

    // media player flags
    private boolean sliderToSeekEnabled;
    private boolean nextFileCalled;
}
