package com.gat.repository.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ducbtsn on 5/13/17.
 */
@AutoValue
public class InterestCategory implements Serializable, Parcelable {
    public InterestCategory() {
        categoryId = 1;
    }

    public InterestCategory(int categoryId) {
        this.categoryId = categoryId;
    }

    protected InterestCategory(Parcel in) {
        categoryId = in.readInt();
    }

    public static final Creator<InterestCategory> CREATOR = new Creator<InterestCategory>() {
        @Override
        public InterestCategory createFromParcel(Parcel in) {
            return new InterestCategory(in);
        }

        @Override
        public InterestCategory[] newArray(int size) {
            return new InterestCategory[size];
        }
    };

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @SerializedName("categoryId")
    private int categoryId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(categoryId);
    }
}
