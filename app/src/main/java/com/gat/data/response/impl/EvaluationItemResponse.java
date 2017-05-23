package com.gat.data.response.impl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mryit on 4/16/2017.
 */

public class EvaluationItemResponse implements Parcelable{

    private int evaluationId;
    private int userId;
    private String name;
    private String imageId;
    private int bookId;
    private int editionId;
    private float value;
    private String review;
    private boolean spoiler;
    private long evaluationTime;

    public EvaluationItemResponse() {

    }

    public EvaluationItemResponse(Parcel in) {
        evaluationId = in.readInt();
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

    public EvaluationItemResponse setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public EvaluationItemResponse setImageId(String imageId) {
        this.imageId = imageId;
        return this;
    }

    public EvaluationItemResponse setReview(String review) {
        this.review = review;
        return this;
    }

    public EvaluationItemResponse setSpoiler(boolean spoiler) {
        this.spoiler = spoiler;
        return this;
    }

    public EvaluationItemResponse setEvaluationTime(long evaluationTime) {
        this.evaluationTime = evaluationTime;
        return this;
    }

    public EvaluationItemResponse setValue(float value) {
        this.value = value;
        return this;
    }

    public EvaluationItemResponse setName(String name) {
        this.name = name;
        return this;
    }

    public int getEvaluationId() {
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
        dest.writeInt(evaluationId);
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