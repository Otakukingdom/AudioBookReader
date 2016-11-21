package com.otakukingdom.audiobook.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.otakukingdom.audiobook.model.AudioBook;
import com.otakukingdom.audiobook.model.Directory;
import com.otakukingdom.audiobook.observers.ScanObserver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mistlight on 11/20/2016.
 */
public class LibraryService implements ScanObserver {


    public LibraryService(AudioBookScanService audioBookScanService) throws SQLException {
        this.audioBookScanService = audioBookScanService;
        this.audioBookScanService.addObserver(this);

        this.audioBookList = new ArrayList<AudioBook>();
        this.audioBookDao = DaoManager.createDao(DatabaseService.getInstance().getConnectionSource(), AudioBook.class);

        initLibrary();
        setOrder();
    }

    // TODO: improve upon this...
    // sets an order if there isn't one already
    public void setOrder() throws SQLException {
        for(int i = 0; i < this.audioBookList.size(); i++) {
            AudioBook currentItem = audioBookList.get(i);
            if(currentItem.getDefaultOrder() != null) {
                // update the order
                currentItem.setDefaultOrder(i + 1);
                audioBookDao.update(currentItem);
            }
        }
    }

    public void initLibrary() throws SQLException {
        this.audioBookList = this.audioBookDao.queryBuilder().orderBy("defaultOrder", true).query();
    }

    public List<AudioBook> getAudioBookList() {
        return this.audioBookList;
    }

    public void audioBookDirectoryScanned() {
        // do something here...
    }

    private AudioBookScanService audioBookScanService;
    private List<AudioBook> audioBookList;
    private Dao<AudioBook, Integer> audioBookDao;
}
