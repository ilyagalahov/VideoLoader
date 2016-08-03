package com.empiritek.maground.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String md5;
    private String initialFileName;


    public Index(String md5, String initialFileName) {
        this.md5 = md5;
        this.initialFileName = initialFileName;
    }


    @Override
    public String toString() {
        return "Index{" +
                "id=" + id +
                ", md5='" + md5 + '\'' +
                ", initialFileName='" + initialFileName + '\'' +
                '}';
    }

}
