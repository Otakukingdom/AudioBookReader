package com.otakukingdom.audiobook.services;

import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.observers.LibraryObserver;

/**
 * Created by mistlight on 11/24/2016.
 */
public class FileListService implements LibraryObserver {

    public FileListService() {

    }

    public void selectionUpdated(AudioBook newSelection) {
        setSelectedAudiobook(newSelection);
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
