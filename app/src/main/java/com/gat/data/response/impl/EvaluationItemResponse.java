package com.gat.data.response.impl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mryit on 4/16/2017.
 */

public class EvaluationItemResponse implements Parcelable{

    private String evaluationId;
    private int userId;
    private String name;
    private String imageId;
    private int bookId;
    private int editionId;
    private float value;
    private String review;
    private boolean spoiler;
    private long evaluationTime;

    protected EvaluationItemResponse(Parcel in) {
        evaluationId = in.readString();
        userId = in.readInt();
        name = in.readString();
        imageId = in.readString();
        bookId = in.readInt();
        editionId = in.readInt();
        value = in.readFloat();
        review = in.readString();
        spoiler = in.readByte() != 0;
        evaluationTime = in.readLong();
    }

    public static final Creator<EvaluationItemResponse> CREATOR = new Creator<EvaluationItemResponse>() {
        @Override
        public EvaluationItemResponse createFromParcel(Parcel in) {
            return new EvaluationItemResponse(in);
        }

        @Override
        public EvaluationItemResponse[] newArray(int size) {
            return new EvaluationItemResponse[size];
        }
    };

    public void setValue(float value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getEditionId() {
        return editionId;
    }

    public float getValue() {
        return value;
    }

    public String getReview() {
        return review;
    }

    public boolean isSpoiler() {
        return spoiler;
    }

    public long getEvaluationTime() {
        return evaluationTime;
    }

    @Override
    public String toString() {
        return "EvaluationItemResponse{" +
                "evaluationId='" + evaluationId + '\'' +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", imageId='" + imageId + '\'' +
                ", bookId=" + bookId +
                ", editionId=" + editionId +
                ", value=" + value +
                ", review='" + review + '\'' +
                ", spoiler=" + spoiler +
                ", evaluationTime='" + evaluationTime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(evaluationId);
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(imageId);
        dest.writeInt(bookId);
        dest.writeInt(editionId);
        dest.writeFloat(value);
        dest.writeString(review);
        dest.writeByte((byte) (spoiler ? 1 : 0));
        dest.writeLong(evaluationTime);
    }
}