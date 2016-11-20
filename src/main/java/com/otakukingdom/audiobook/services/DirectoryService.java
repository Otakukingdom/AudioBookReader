package com.otakukingdom.audiobook.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.otakukingdom.audiobook.model.Directory;
import com.otakukingdom.audiobook.observers.DirectoryObserver;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mistlight on 11/20/2016.
 */
public class DirectoryService {

    public DirectoryService() {
        this.directoryObservers = new ArrayList<DirectoryObserver>();
    }

    public void addObserver(DirectoryObserver observer) {
        this.directoryObservers.add(observer);
    }


    /**
     *
     * @return All directories
     */
    public List<Directory> getDirectories() {
        ConnectionSource connectionSource = DatabaseService.getInstance().getConnectionSource();
        Dao<Directory, Integer> directoryDao = null;
        List<Directory> result = null;
        try {
            directoryDao = DaoManager.createDao(connectionSource, Directory.class);
            result = directoryDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param file The path of the directory to add
     * @return true if this function has been successfully called, otherwise false
     */
    public boolean addDirectory(File file) {
        ConnectionSource connectionSource = DatabaseService.getInstance().getConnectionSource();
        Dao<Directory, Integer> directoryDao = null;
        try {
            directoryDao = DaoManager.createDao(connectionSource, Directory.class);
            List<Directory> directoryList = directoryDao.queryForEq("fullPath", file.getAbsolutePath());

            if(directoryList.size() > 0) {
                // the directory has already been added to the list
                return true;
            }

            Directory newDirectory = new Directory(file.getAbsolutePath());
            directoryDao.create(newDirectory);

            for(DirectoryObserver observer : directoryObservers) {
                observer.directoryListUpdated();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    /**
     * @param path The string of the value of the path of the directory
     * @return true if file has been successfully removed, otherwise false
     */
    public boolean removeDirectory(String path) {
        ConnectionSource connectionSource = DatabaseService.getInstance().getConnectionSource();
        Dao<Directory, Integer> directoryDao = null;

        try {
            directoryDao = DaoManager.createDao(connectionSource, Directory.class);
            List<Directory> directoryList = directoryDao.queryForEq("fullPath", path);

            if(directoryList.size() > 0) {
                for(Directory currentDirectory : directoryList) {
                    directoryDao.delete(currentDirectory);
                }
            }

            for(DirectoryObserver observer : directoryObservers) {
                observer.directoryListUpdated();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private List<DirectoryObserver> directoryObservers;
}
