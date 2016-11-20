package com.otakukingdom.audiobook.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mistlight on 11/20/2016.
 */
@DatabaseTable(tableName = "audiobookfiles")
public class AudioBookFile {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private Integer id;

    @DatabaseField
    private Integer audiobookId;

    @DatabaseField
    private String name;

    @DatabaseField
    private String fullPath;

    @DatabaseField
    private Integer position;

    @DatabaseField
    private Integer completeness;

    public AudioBookFile() {

    }

    public AudioBookFile(Integer audiobookId, String fullPath) {
        this.audiobookId = audiobookId;
        this.fullPath = fullPath;
        this.position = 0;
        this.completeness = 0;
    }
}
