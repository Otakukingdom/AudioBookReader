package com.otakukingdom.audiobook.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mistlight on 11/20/2016.
 */

@DatabaseTable(tableName = "directories")
public class Directory {
    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String fullPath;

    @DatabaseField()
    private String name;

    public Directory() {

    }
}
