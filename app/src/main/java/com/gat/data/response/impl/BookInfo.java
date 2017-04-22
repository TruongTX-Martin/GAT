package com.gat.data.response.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mryit on 4/18/2017.
 */

public class BookInfo implements Parcelable{

    @SerializedName("editionId")
    private int editionId;

    @SerializedName("bookId")
    private int bookId;

    @SerializedName("isbn10")
    private String isbn10;

    @SerializedName("isbn13")
    private String isbn13;

    @SerializedName("title")
    private String title;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("publish")
    private String publish;

    @SerializedName("publisherId")
    private int publisherId;

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("description")
    private String description;

    @SerializedName("numberOfPage")
    private int numberOfPage;

    @SerializedName("language")
    private int language;

    @SerializedName("formatId")
    private int formatId;

    @SerializedName("category")
    private List<Category> category;

    @SerializedName("genre")
    private List<Genre> genre;

    @SerializedName("author")
    private List<Author> author;

    @SerializedName("translator")
    private List<Translator> translator;

    @SerializedName("rateAvg")
    private float rateAvg;

    @SerializedName("sharingCount")
    private int sharingCount;

    protected BookInfo(Parcel in) {
        editionId = in.readInt();
        bookId = in.readInt();
        isbn10 = in.readString();
        isbn13 = in.readString();
        title = in.readString();
        imageId = in.readString();
        publish = in.readString();
        publisherId = in.readInt();
        publisher = in.readString();
        description = in.readString();
        numberOfPage = in.readInt();
        language = in.readInt();
        formatId = in.readInt();
        rateAvg = in.readFloat();
        sharingCount = in.readInt();
    }

    public static final Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel in) {
            return new BookInfo(in);
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };

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

    public List<Genre> getGenre() {
        return genre;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public List<Translator> getTranslator() {
        return translator;
    }

    public float getRateAvg() {
        return rateAvg;
    }

    public int getSharingCount() {
        return sharingCount;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "editionId=" + editionId +
                ", bookId=" + bookId +
                ", isbn10='" + isbn10 + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", title='" + title + '\'' +
                ", imageId='" + imageId + '\'' +
                ", publish='" + publish + '\'' +
                ", publisherId=" + publisherId +
                ", publisher='" + publisher + '\'' +
                ", description='" + description + '\'' +
                ", numberOfPage=" + numberOfPage +
                ", language=" + language +
                ", formatId=" + formatId +
                ", category=" + category +
                ", genre=" + genre +
                ", author=" + author +
                ", translator=" + translator +
                ", rateAvg=" + rateAvg +
                ", sharingCount=" + sharingCount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(editionId);
        dest.writeInt(bookId);
        dest.writeString(isbn10);
        dest.writeString(isbn13);
        dest.writeString(title);
        dest.writeString(imageId);
        dest.writeString(publish);
        dest.writeInt(publisherId);
        dest.writeString(publisher);
        dest.writeString(description);
        dest.writeInt(numberOfPage);
        dest.writeInt(language);
        dest.writeInt(formatId);
        dest.writeFloat(rateAvg);
        dest.writeInt(sharingCount);
    }
}
