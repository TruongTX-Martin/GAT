package com.gat.feature.personal.entity;

import com.gat.common.util.Strings;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class BookEntity {
    private String name = Strings.EMPTY;
    private String author = Strings.EMPTY;


    public BookEntity(String name,String author) {
        this.name = name;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
