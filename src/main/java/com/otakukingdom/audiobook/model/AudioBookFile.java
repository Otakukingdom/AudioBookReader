package com.otakukingdom.audiobook.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.util.Date;

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
    private Integer lengthOfFile;

    @DatabaseField
    private Integer position;

    @DatabaseField
    private Integer completeness;

    @DatabaseField
    private Double seekPosition;

    @DatabaseField
    private boolean fileExists;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
    private Date createdAt;

    public AudioBookFile() {

    }

    public AudioBookFile(Integer audiobookId, String fullPath) {
        this.audiobookId = audiobookId;
        this.fullPath = fullPath;
        this.position = -1;
        this.completeness = 0;
        this.name = generateName(fullPath);
        this.createdAt = new Date();
        this.seekPosition = 0.0;

        // this always default to true unless something happens when we
        // try to read the file
        this.fileExists = true;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String generateName(String path) {
        File file = new File(path);
        return file.getName();
    }

    public Integer getCompleteness() {
        return this.completeness;
    }

    public void setCompleteness(Integer completeness) {
        this.completeness = completeness;
    }

    public void setSeekPosition(Double seekPosition) {
        this.seekPosition = seekPosition;
    }

    public Double getSeekPosition() {
        return this.seekPosition;
    }

    public String toString() {
        String name = "(" + this.completeness + "% Completed) " + this.name;
        return name;
    }

    public Integer getId() { return this.id; }

    public String getState() {
        if (this.completeness.intValue() == 100) {
            return "Completed";
        }

        if (this.completeness.intValue() > 0) {
            return "Started";
        } else {
            return "Unstarted";
        }
    }
}
