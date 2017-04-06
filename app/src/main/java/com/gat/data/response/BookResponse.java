package com.gat.data.response;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/6/2017.
 */

public class BookResponse{

    @SerializedName("editionId")
    public long editionId;

    @SerializedName("bookId")
    public long bookId;

    @SerializedName("title")
    public String title;

    @SerializedName("imageId")
    public String imageId;

    @SerializedName("author")
    public String author;

    @SerializedName("rateAvg")
    public float rateAvg;

    public long getEditionId() {
        return editionId;
    }

    public long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getImageId() {
        return imageId;
    }

    public String getAuthor() {
        return author;
    }

    public float getRateAvg() {
        return rateAvg;
    }

    @Override
    public String toString() {
        return "BookResponse{" +
                "editionId=" + editionId +
                ", bookId=" + bookId +
                ", title='" + title + '\'' +
                ", imageId='" + imageId + '\'' +
                ", author='" + author + '\'' +
                ", rateAvg=" + rateAvg +
                '}';
    }
}