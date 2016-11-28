package com.otakukingdom.audiobook.observers;

import javafx.scene.media.MediaPlayer;

/**
 * Created by mistlight on 11/25/2016.
 */
@FunctionalInterface
public interface MediaPlayerObserver {

    public void mediaPlayerUpdated(MediaPlayer mediaPlayer, boolean autoplay);

}
