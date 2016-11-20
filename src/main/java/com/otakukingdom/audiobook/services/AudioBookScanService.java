package com.otakukingdom.audiobook.services;

import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.model.Directory;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mistlight on 11/20/2016.
 */
public class AudioBookScanService {

    public AudioBookScanService(List<Directory> directoryList) {
        this.directoryList = directoryList;
    }

    /**
     * Perform the scanning of Audiobooks
     */
    public void scan() {
        for(Directory currentDirectory : this.directoryList) {
            scanDirectory(currentDirectory, currentDirectory.getFullPath());
        }
    }

    public void scanDirectory(Directory baseDirectory, String currentDirectory) {
        File[] allFiles = new File(currentDirectory).listFiles();
        List<File> loadedDirectories = new ArrayList<File>();
        List<File> loadedAudioFiles = new ArrayList<File>();

        // load all of the sub directories/files into the array list
        for(File currentFile : allFiles) {
            if(currentFile.isDirectory()) {
                loadedDirectories.add(currentFile);
            } else {
                if(isAudiobookFile(currentFile)) {
                    loadedAudioFiles.add(currentFile);
                }
            }
        }

        if(loadedDirectories.size() > 0) {
            // if we have a loaded directory, we should first check if all of the matching
            // directory names are similar
            if(checkDirectorySimilarity(loadedDirectories)) {
                // if they are similar, it means the current directory is an Audiobook directory
                // scan for all audiobook files recursively and add it to the current audiobook
                registerAudioBook(baseDirectory, currentDirectory, loadedAudioFiles);
            } else {
                for(File subdirectory : loadedDirectories) {
                    scanDirectory(baseDirectory, subdirectory.getAbsolutePath());
                }
            }
        } else {
            if(loadedAudioFiles.size() > 0) {
                registerAudioBook(baseDirectory, currentDirectory, loadedAudioFiles);
            }
        }

    }

    public void registerAudioBook(Directory directory, String directoryPath, List<File> audioFiles) {
        System.out.println("REGISTER AUDIO BOOK CALLED ON:" + directoryPath);
    }

    // check if the file is an audiobook file (aka an audio file)
    public boolean isAudiobookFile(File file) {
        Tika tika = new Tika();
        try {
            String mediaType = tika.detect(file);
            System.out.println("MEDIATYPE IS:" + mediaType);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public boolean checkDirectorySimilarity(List<File> directories) {
        // if the num of directories here is less than 1, then
        // we simply just don't do anything
        if(directories.size() == 1 || directories.size() < 1) {
            return false;
        }

        return false;
    }


    private List<Directory> directoryList;
    private List<AudioBook> audioBooks;
    private List<AudioBookFile> audioBookFiles;
}
