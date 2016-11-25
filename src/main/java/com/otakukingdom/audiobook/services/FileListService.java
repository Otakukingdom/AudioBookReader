package com.otakukingdom.audiobook.services;

import com.otakukingdom.audiobook.model.AudioBook;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by mistlight on 11/24/2016.
 */
public class FileListService implements ChangeListener<AudioBook>{

    public FileListService() {

    }

    @Override
    public void changed(ObservableValue<? extends AudioBook> observable, AudioBook oldValue, AudioBook newValue) {
        this.setSelectedAudiobook(newValue);
    }


    public void setSelectedAudiobook(AudioBook selectedAudiobook) {
        if(selectedAudiobook == null) {
            return;
        }

        if(this.selectedAudiobook == null) {
            this.selectedAudiobook = selectedAudiobook;
            return;
        }

        if(this.selectedAudiobook.getId() != selectedAudiobook.getId()) {
            this.selectedAudiobook = selectedAudiobook;
        }
    }

    public AudioBook getSelectedAudiobook() {
        return this.selectedAudiobook;
    }

    private AudioBook selectedAudiobook;
}
