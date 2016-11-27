package com.otakukingdom.audiobook.services;

import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.observers.FileListObserver;
import com.otakukingdom.audiobook.observers.MediaPlayerObserver;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mistlight on 11/25/2016.
 */
public class MediaPlayerService implements FileListObserver {

    public MediaPlayerService() {
        this.listeners = new ArrayList<MediaPlayerObserver>();
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
    public void fileListUpdated(List<AudioBookFile> newFileList) {
        // do nothing
    }

    @Override
    public void selectedFileUpdated(AudioBookFile selectedFile) {

        // only perform this IF and ONLY IF the selectedFile is different
        if(this.currentFile == null ||
                !this.currentFile.getId().equals(selectedFile.getId())) {
            this.currentFile = selectedFile;
            initMedia();
            notifyListeners();
        }
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
    }


    private void notifyListeners() {
        for(MediaPlayerObserver mediaPlayerObserver : listeners) {
            mediaPlayerObserver.mediaPlayerUpdated(this.mediaPlayer);
        }
    }

    private boolean hasError;
    private Media media;
    private MediaPlayer mediaPlayer;
    private AudioBookFile currentFile;

    private List<MediaPlayerObserver> listeners;
}
