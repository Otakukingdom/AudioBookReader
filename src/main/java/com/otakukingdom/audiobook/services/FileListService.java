package com.otakukingdom.audiobook.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.observers.FileListObserver;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mistlight on 11/24/2016.
 */
public class FileListService implements ChangeListener<AudioBook>{

    public FileListService() {
        this.fileList = new ArrayList<AudioBookFile>();
        this.listeners = new ArrayList<FileListObserver>();
    }

    public void addListener(FileListObserver listener) {
        this.listeners.add(listener);
    }

    @Override
    public void changed(ObservableValue<? extends AudioBook> observable, AudioBook oldValue, AudioBook newValue) {
        this.setSelectedAudiobook(newValue);
        updateList();
        notifyListeners();
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

    private void notifyListeners() {
        for(FileListObserver listener : listeners) {
            listener.fileListUpdated(this.fileList);
        }
    }

    private void updateList() {
        try {
            Dao<AudioBookFile, Integer> fileDao = DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBookFile.class);
            this.fileList = fileDao.
                    queryBuilder().
                    orderBy("position", true).
                    where().
                    eq("audioBookId", this.selectedAudiobook.getId()).
                    query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AudioBook getSelectedAudiobook() {
        return this.selectedAudiobook;
    }

    private List<AudioBookFile> fileList;
    private AudioBook selectedAudiobook;
    private List<FileListObserver> listeners;
}
