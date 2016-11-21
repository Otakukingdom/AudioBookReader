package com.otakukingdom.audiobook.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by mistlight on 11/20/2016.
 */

@DatabaseTable(tableName = "directories")
public class Directory {
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private Integer id;

    @DatabaseField(canBeNull = false)
    private String fullPath;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date createdAt;

    @DatabaseField(dataType = DataType.DATE_LONG)
    private Date lastScanned;

    // ormlite requires a no-arg constructor
    public Directory() {
    }

    public Directory(String fullPath) {
        this.fullPath = fullPath;
        this.createdAt = new Date();
    }

    public void setLastScanned() {
        this.lastScanned = new Date();
    }

    public Date getLastScanned() {
        return this.lastScanned;
    }

    public Integer getId() { return this.id; }

    public String getFullPath() {
        return this.fullPath;
    }

    public String toString() {
        return this.fullPath;
    }

}
