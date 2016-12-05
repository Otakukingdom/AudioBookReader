package com.otakukingdom.audiobook.services;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.otakukingdom.audiobook.model.AudioBook;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by mistlight on 11/20/2016.
 */
public class SettingService {
    private static SettingService instance = null;

    protected SettingService() {
        try {
            this.ini = new Wini(getFileForSettings());
        } catch (IOException e) {
            this.ini = new Wini();
        }

        setDefaultValues();
        save();
    }

    public static SettingService getInstance() {
        if(instance == null) {
            instance = new SettingService();
        }
        return instance;
    }

    public void setCurrentAudioBook(AudioBook audioBook) {
        this.ini.put("main", "currentAudiobook", audioBook.getId());

        save();
    }

    public AudioBook getCurrentAudioBook() {
        int currentBook = this.ini.get("main", "currentAudiobook", int.class);

        try {
            Dao<AudioBook, Integer> audioBookDao =
                    DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBook.class);
            AudioBook audioBook = audioBookDao.queryForId(currentBook);
            return audioBook;
        } catch(SQLException e) {
            return null;
        }
    }


    public double getVolume() {
        return this.ini.get("main", "volume", double.class);
    }

    public void setVolume(double volume) {
        this.ini.put("main", "volume", volume);

        save();
    }

    public void save() {
        try {
            this.ini.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFileForSettings() {
        File settingsFile = new File("reader_settings.ini");
        try {
            settingsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return settingsFile;
    }

    private void setDefaultValues() {
        String volumeString = ini.get("main", "volume", String.class);
        if(volumeString == null) {
            ini.put("main", "volume", 1);
        }
    }

    private Wini ini;
    private Map<String, Object> settingsMap;
}
