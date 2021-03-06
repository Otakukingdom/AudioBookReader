package com.otakukingdom.audiobook.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.observers.FileListObserver;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mistlight on 11/24/2016.
 */
public class FileListService implements ChangeListener<AudioBook>{

    public FileListService() {
        this.fileList = FXCollections.observableList(new ArrayList<AudioBookFile>());
        this.listeners = new ArrayList<FileListObserver>();
    }

    public void addListener(FileListObserver listener) {
        this.listeners.add(listener);
    }

    @Override
    public void changed(ObservableValue<? extends AudioBook> observable, AudioBook oldValue, AudioBook newValue) {
        this.setSelectedAudiobook(newValue);
        updateList();
        notifyFileListChanged();
    }

    // based on the current selected file, return the next audio book file in sequence
    public AudioBookFile nextFile() {
        int currentIndex = this.fileList.indexOf(this.selectedAudioBookFile);
        if(currentIndex + 1 < this.fileList.size()) {
            return this.fileList.get(currentIndex + 1);
        }

        return null;
    }

    public void setNextFile() {
        if(nextFile() != null) {
            setSelectedAudioBookFile(nextFile());
        }
    }

    public void setSelectedAudioBookFile(AudioBookFile selectedAudiobook) {
        this.setSelectedAudioBookFile(selectedAudiobook, true);
    }

    public void updateCompletionStatus(AudioBookFile currentFile) {
        for(int i = 0; i < this.fileList.size(); i++) {
            AudioBookFile file = this.fileList.get(i);
            if(file.getId().equals(currentFile.getId())) {
                try {
                    Dao<AudioBookFile, Integer> fileDao = DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBookFile.class);
                    AudioBookFile retrievedFile = fileDao.queryForSameId(currentFile);
                    file.setSeekPosition(retrievedFile.getSeekPosition());
                    file.setCompleteness(retrievedFile.getCompleteness());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            this.fileList.set(i, file);
        }
    }

    public void setSelectedAudiobook(AudioBook selectedAudiobook) {
        if(selectedAudiobook == null) {
            return;
        }

        if(this.selectedAudiobook == null) {
            this.selectedAudiobook = selectedAudiobook;
            return;
        }

        if(!this.selectedAudiobook.getId().equals(selectedAudiobook.getId())) {
            this.selectedAudiobook = selectedAudiobook;
        }
    }

    // check if the audio book files has been sorted
    // we check this by checking the indices of the AudioBookFile
    private boolean isSorted() {
        for(AudioBookFile audioBookFile : this.fileList) {
            if(audioBookFile.getPosition() <= 0) {
                return false;
            }
        }
        return true;
    }

    private void sortList() {
        Collections.sort(this.fileList, Comparator.comparing(AudioBookFile::getFullPath));

        writeSortedList();
    }

    private void notifyFileListChanged() {
        for(FileListObserver listener : this.listeners) {
            listener.fileListUpdated(this.fileList);
        }
    }

    private void notifySelectedFileChanged() {
        for(FileListObserver listener : this.listeners) {
            listener.selectedFileUpdated(this.selectedAudioBookFile);
        }
    }

    private void updateList() {
        try {
            Dao<AudioBookFile, Integer> fileDao = DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBookFile.class);
            this.fileList = FXCollections.observableArrayList(fileDao.
                    queryBuilder().
                    orderBy("position", true).
                    where().
                    eq("audioBookId", this.selectedAudiobook.getId()).
                    query());

            if(!this.isSorted()) {
                sortList();
            }
            
            updateSelectedAudiobookFile();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSelectedAudiobookFile() {
        // check if we have a selected audiobook file
        Integer selectedAudioBookFileId = this.selectedAudiobook.getSelectedFile();
        AudioBookFile foundFile = null;

        for(AudioBookFile currentFile : this.fileList) {
            if(currentFile.getId().equals(selectedAudioBookFileId)) {
                foundFile = currentFile;
                break;
            }
        }

        if(foundFile != null) {
            setSelectedAudioBookFile(foundFile, true);
            return;
        }

        // if we are here it means we don't have a selected file for whatever reason, we simply pick the
        // first one
        AudioBookFile firstFile = this.fileList.get(0);
        setSelectedAudioBookFile(firstFile, false);
    }

    private void setSelectedAudioBookFile(AudioBookFile audioBookFile, boolean writeToDb) {
        // set the new audiobook file to be the currently selected audiobook file
        this.selectedAudioBookFile = audioBookFile;

        // update the audiobook db entry with the new selected file if writeToDb is true
        if (writeToDb) {
            try {
                this.selectedAudiobook.setSelectedFile(audioBookFile.getId());
                Dao<AudioBook, Integer> abDao = DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBook.class);
                abDao.update(this.selectedAudiobook);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        // notify the observers
        notifySelectedFileChanged();
    }

    private void writeSortedList() {
        try {
            Dao<AudioBookFile, Integer> fileDao = DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBookFile.class);

            for(int i = 0; i < this.fileList.size(); i++ ) {
                this.fileList.get(i).setPosition(i + 1);

                fileDao.update(this.fileList.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AudioBook getSelectedAudiobook() {
        return this.selectedAudiobook;
    }

    private ObservableList<AudioBookFile> fileList;
    private AudioBook selectedAudiobook;
    private AudioBookFile selectedAudioBookFile;
    private List<FileListObserver> listeners;

}
