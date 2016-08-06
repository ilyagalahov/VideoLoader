package com.empiritek.maground.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FileMetadata {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long folderId;
    private String md5;
    private String initialFileName;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public String getInitialFileName() {
        return initialFileName;
    }

    public void setInitialFileName(String initialFileName) {
        this.initialFileName = initialFileName;
    }
}
