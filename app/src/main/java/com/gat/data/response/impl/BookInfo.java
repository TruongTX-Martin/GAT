package com.gat.data.response.impl;

import java.util.List;

/**
 * Created by mryit on 4/18/2017.
 */

public class BookInfo {

    private int editionId;
    private int bookId;
    private String isbn10;
    private String isbn13;
    private String title;

    private String imageId;
    private String publish;
    private int publisherId;
    private String publisher;
    private String description;
    private int numberOfPage;
    private int language;
    private int formatId;
    private List<Category> category;
    private List<String> genre;
    private List<Author> author;
    private List<Translator> translator;
    private int rateAvg;
    private int sharingCount;

    public int getEditionId() {
        return editionId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getTitle() {
        return title;
    }

    public String getImageId() {
        return imageId;
    }

    public String getPublish() {
        return publish;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfPage() {
        return numberOfPage;
    }

    public int getLanguage() {
        return language;
    }

    public int getFormatId() {
        return formatId;
    }

    public List<Category> getCategory() {
        return category;
    }

    public List<String> getGenre() {
        return genre;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public List<Translator> getTranslator() {
        return translator;
    }

    public int getRateAvg() {
        return rateAvg;
    }

    public int getSharingCount() {
        return sharingCount;
    }
}
