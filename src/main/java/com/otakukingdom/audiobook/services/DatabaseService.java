package com.otakukingdom.audiobook.services;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.AudioBookFile;
import com.otakukingdom.audiobook.model.Directory;

import java.sql.SQLException;

/**
 * Created by mistlight on 11/20/2016.
 */
public class DatabaseService {
    private static DatabaseService instance = null;

    protected DatabaseService() {

    }

    public static DatabaseService getInstance() {
        if(instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    // attempt to connect to the database, and then get the connection source
    public ConnectionSource getConnectionSource() {
        // check if we have to connect to the database
        if(this.connectionSource != null) {
            return this.connectionSource;
        }

        // attempt to connect to the database
        String databaseUrl = "jdbc:sqlite:audiobook.db";
        try {
            this.connectionSource = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException e) {
            this.connectionSource = null;
        }

        return this.connectionSource;
    }

    public void initDb() {
        ConnectionSource cs = getConnectionSource();
        if(cs != null) {
            try {
                TableUtils.createTableIfNotExists(cs, Directory.class);
                TableUtils.createTableIfNotExists(cs, AudioBook.class);
                TableUtils.createTableIfNotExists(cs, AudioBookFile.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private ConnectionSource connectionSource;
}
