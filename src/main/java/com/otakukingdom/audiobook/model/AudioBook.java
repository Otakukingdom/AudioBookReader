package com.otakukingdom.audiobook.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mistlight on 11/20/2016.
 */
@DatabaseTable(tableName = "audiobooks")
public class AudioBook {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private Integer id;

    @DatabaseField
    private Integer directoryId;

    @DatabaseField
    private String name;

    @DatabaseField
    private String fullPath;

    @DatabaseField
    private Integer completeness;


    // ormlite requires an empty constructor
    public AudioBook() {

    }

    public AudioBook(Integer directoryId, String fullPath) {
        this.fullPath = fullPath;
        this.directoryId = directoryId;
        this.completeness = 0;
    }
}
