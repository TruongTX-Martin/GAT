package com.gat.data.response;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/6/2017.
 */

public class BookResponse {

    @SerializedName("editionId")
    private long editionId;

    @SerializedName("bookId")
    private long bookId;

    @SerializedName("title")
    private String title;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("author")
    private String author;

    @SerializedName("rateAvg")
    private float rateAvg;

    @SerializedName("borrowingCount")
    private int borrowingCount;

    @SerializedName("sharingCount")
    private int sharingCount;

    @SerializedName("rateCount")
    private int rateCount;

    @SerializedName("reviewCount")
    private int reviewCount;

    public int getRateCount() {
        return rateCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

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

    public int getBorrowingCount() {
        return borrowingCount;
    }

    public int getSharingCount() {
        return sharingCount;
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
                ", borrowingCount=" + borrowingCount +
                ", sharingCount=" + sharingCount +
                '}';
    }
}