package com.otakukingdom.audiobook.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.observers.FileListObserver;
import com.otakukingdom.audiobook.observers.MediaPlayerObserver;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mistlight on 11/25/2016.
 */
public class MediaPlayerService implements FileListObserver {

    public MediaPlayerService(FileListService fileListService) {
        this.listeners = new ArrayList<MediaPlayerObserver>();
        this.fileListService = fileListService;
    }

    public MediaPlayer getMediaPlayer() {
        return this.mediaPlayer;
    }

    public void setHasError(boolean error) {
        this.hasError = error;
    }

    public void addListener(MediaPlayerObserver mediaPlayerObserver) {
        this.listeners.add(mediaPlayerObserver);
    }

    @Override
    public void fileListUpdated(ObservableList<AudioBookFile> newFileList) {
        // do nothing
    }

    @Override
    public void selectedFileUpdated(AudioBookFile selectedFile) {
        // only perform this IF and ONLY IF the selectedFile is different
        if(this.currentFile == null ||
                !this.currentFile.getId().equals(selectedFile.getId())) {

            // before we update, save the current state if we can
            if(this.mediaPlayer != null) {
                saveState();
            }

            // update the selected file
            this.currentFile = selectedFile;
            initMedia();
            notifyListeners(false);
        }
    }

    public AudioBookFile getCurrentFile() {
        return this.currentFile;
    }

    private void initMedia() {
        // start from a clean slate
        setHasError(false);

        File file = new File(this.currentFile.getFullPath());

        // dispose the previous media player if there is one..
        if(this.mediaPlayer != null) {
            this.mediaPlayer.dispose();
        }

        try {
            this.media = new Media(file.toURI().toString());
        } catch (Exception mediaException) {
            setHasError(true);
            return;
        }

        if (media.getError() == null) {
            media.setOnError(() ->{
                // handle error in media object
                setHasError(true);
            });

            try {
                this.mediaPlayer = new MediaPlayer(media);

                if(this.mediaPlayer.getError() == null) {
                    this.mediaPlayer.setOnError(() -> {
                        // handle error in mediaplayer object
                        setHasError(true);
                    });
                } else {
                    setHasError(true);
                }
            } catch (Exception mediaPlayerException) {
                // handle exception in mediaplayer constructor
                setHasError(true);
            }
        } else {
            // create media failed
            setHasError(true);
        }
    }

    // save the current media playing state to the db
    public void saveState() {
        Integer completeness = null;
        if(this.mediaPlayer.getTotalDuration() != null) {
            Duration duration = this.mediaPlayer.getCurrentTime();
            double durationSeconds = duration.toSeconds();
            currentFile.setSeekPosition(durationSeconds);

            completeness = (int) ((durationSeconds / this.mediaPlayer.getTotalDuration().toSeconds()) * 100);
            if(completeness >= currentFile.getCompleteness()) {
                currentFile.setCompleteness(completeness);
            }
        }

        try {
            Dao<AudioBookFile, Integer> dao = DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBookFile.class);
            dao.update(this.currentFile);

            this.fileListService.updateCompletionStatus(this.currentFile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void notifyListeners(boolean autoplay) {
        for(MediaPlayerObserver mediaPlayerObserver : listeners) {
            mediaPlayerObserver.mediaPlayerUpdated(this.mediaPlayer);
        }
    }

    private boolean hasError;
    private Media media;
    private MediaPlayer mediaPlayer;
    private AudioBookFile currentFile;
    private FileListService fileListService;

    private List<MediaPlayerObserver> listeners;
}
