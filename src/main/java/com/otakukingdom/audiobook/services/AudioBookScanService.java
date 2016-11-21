package com.otakukingdom.audiobook.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.model.Directory;
import com.otakukingdom.audiobook.observers.DirectoryObserver;
import com.otakukingdom.audiobook.observers.ScanObserver;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mistlight on 11/20/2016.
 */
public class AudioBookScanService implements DirectoryObserver {

    public AudioBookScanService(DirectoryService directoryService) {
        this.directoryService = directoryService;
        this.directoryList = this.directoryService.getDirectories();
        this.directoryService.addObserver(this);
        this.scanObserverList = new ArrayList<ScanObserver>();
    }

    /**
     * Perform the scanning of Audiobooks
     */
    public void scan() {
        boolean hasScanned = false;

        for(Directory currentDirectory : this.directoryList) {
            Date lastScanned = currentDirectory.getLastScanned();
            if(lastScanned == null) {
                // perform scanning
                scanDirectory(currentDirectory, currentDirectory.getFullPath());

                // update the last scanned date
                currentDirectory.setLastScanned();
                try {
                    Dao<Directory, Integer> directoryDao =
                            DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), Directory.class);
                    directoryDao.update(currentDirectory);
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                hasScanned = true;
            }
        }

        // if we have recently scanned anything, notify the observers
        if (hasScanned) {
            for (ScanObserver currentObserver : scanObserverList) {
                currentObserver.audioBookDirectoryScanned();
            }
        }
    }

    /**
     * Perform the re-scanning of Audiobooks
     */
    public void rescanAll() {
        boolean hasScanned = false;

        for(Directory currentDirectory : this.directoryList) {
            // perform the scanning
            scanDirectory(currentDirectory, currentDirectory.getFullPath());

            // update the last scanned date
            currentDirectory.setLastScanned();
            try {
                Dao<Directory, Integer> directoryDao =
                        DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), Directory.class);
                directoryDao.update(currentDirectory);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            hasScanned = true;
        }


        // if we have recently scanned anything, notify the observers
        if (hasScanned) {
            for (ScanObserver currentObserver : scanObserverList) {
                currentObserver.audioBookDirectoryScanned();
            }
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

    public boolean registerAudioBook(Directory directory, String directoryPath, List<File> audioFiles) {
        // check if there is an audio book entry already on this audiobook
        try {
            Dao<AudioBook, Integer> audiobookDao =
                    DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBook.class);
            List<AudioBook> audioBookList = audiobookDao.queryForEq("fullPath", directoryPath);
            AudioBook audioBook = null;
            if(audioBookList.size() > 0) {
                audioBook = audioBookList.get(0);
            } else {
                audioBook = new AudioBook(directory.getId(), directoryPath);
                audiobookDao.create(audioBook);
            }

            Integer audiobookId = audioBook.getId();

            // will be set to true if even one failure occurs
            boolean hasNoFail = true;
            for(File currentAudioFile : audioFiles) {
                if(!registerAudioFile(audiobookId, currentAudioFile)) {
                    hasNoFail = false;
                }
            }

            return hasNoFail;

        } catch (SQLException e) {
            // if we are here, it means something is wrong with the database
            return false;
        }
    }

    private boolean registerAudioFile(Integer audiobookId, File currentAudioFile) {
        try {
            Dao<AudioBookFile, Integer> audiobookFileDao =
                    DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBookFile.class);
            List<AudioBookFile> fileList = audiobookFileDao.
                    queryBuilder().
                    where().
                    eq("fullPath", currentAudioFile.getAbsolutePath()).and().
                    eq("audiobookId", audiobookId).query();

            if(fileList.size() == 0) {
                AudioBookFile audiobookFile = new AudioBookFile(audiobookId, currentAudioFile.getAbsolutePath());
                audiobookFileDao.create(audiobookFile);
            }

        } catch (SQLException e) {
            // if we are here, it means something went seriously wrong connecting to the database
            return false;
        }

        return true;
    }

    // check if the file is an audiobook file (aka an audio file)
    public boolean isAudiobookFile(File file) {
        Tika tika = new Tika();
        try {
            String mediaType = tika.detect(file);
            if(!mediaType.startsWith("audio")) {
                return false;
            }
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

    public void directoryListUpdated() {
        // update the directory list we have
        this.directoryList = this.directoryService.getDirectories();

        // when directory list updates, we must rescan everything
        this.scan();
    }

    public void addObserver(ScanObserver scanObserver) {
        this.scanObserverList.add(scanObserver);
    }

    private DirectoryService directoryService;
    private List<Directory> directoryList;
    private List<AudioBook> audioBooks;
    private List<AudioBookFile> audioBookFiles;

    private List<ScanObserver> scanObserverList;

}
