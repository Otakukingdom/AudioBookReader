package com.otakukingdom.audiobook.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.util.Date;

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

    @DatabaseField
    private Integer defaultOrder;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date createdAt;


    // ormlite requires an empty constructor
    public AudioBook() {

    }

    public AudioBook(Integer directoryId, String fullPath) {
        this.fullPath = fullPath;
        this.directoryId = directoryId;
        this.completeness = 0;
        this.name = generateName(fullPath);
        this.createdAt = new Date();
    }

    public Integer getDefaultOrder() {
        return this.defaultOrder;
    }

    public void setDefaultOrder(Integer defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    public String generateName(String path) {
        File file = new File(path);
        return file.getName();
    }


    public Integer getId() {
        return this.id;
    }

    public String toString() {
        return this.name;
    }
}
