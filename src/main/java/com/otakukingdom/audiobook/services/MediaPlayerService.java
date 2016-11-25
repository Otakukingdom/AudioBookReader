package com.otakukingdom.audiobook.services;

import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.observers.FileListObserver;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.List;

/**
 * Created by mistlight on 11/25/2016.
 */
public class MediaPlayerService implements FileListObserver {

    public MediaPlayerService() {
    }

    public void play() {
        if (hasError) {
            return;
        }

        if(mediaPlayer.getStatus() == MediaPlayer.Status.READY) {
            this.mediaPlayer.play();
        }
    }

    public void pause() {
        if (hasError) {
            return;
        }

        if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            this.mediaPlayer.pause();
        }
    }

    public void setHasError(boolean error) {
        this.hasError = error;
    }

    @Override
    public void fileListUpdated(List<AudioBookFile> newFileList) {
        this.currentFile = null;
    }

    @Override
    public void selectedFileUpdated(AudioBookFile selectedFile) {
        this.currentFile = selectedFile;
        initMedia();
    }

    private void initMedia() {
        setHasError(false);

        File file = new File(this.currentFile.getFullPath());

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

    private boolean hasError;
    private Media media;
    private MediaPlayer mediaPlayer;
    private AudioBookFile currentFile;
}
