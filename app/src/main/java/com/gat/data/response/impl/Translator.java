package com.gat.data.response.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mryit on 4/18/2017.
 */

public class Translator implements Parcelable{

    @SerializedName("editionId")
    private int editionId;

    @SerializedName("authorName")
    private String authorName;

    protected Translator(Parcel in) {
        editionId = in.readInt();
        authorName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(editionId);
        dest.writeString(authorName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Translator> CREATOR = new Creator<Translator>() {
        @Override
        public Translator createFromParcel(Parcel in) {
            return new Translator(in);
        }

        @Override
        public Translator[] newArray(int size) {
            return new Translator[size];
        }
    };

    public int getEditionId() {
        return editionId;
    }

    public String getAuthorName() {
        return authorName;
    }
}
