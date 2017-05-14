package com.gat.repository.entity.book;

import com.gat.common.util.Strings;
import com.google.gson.annotations.SerializedName;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class BookSharingEntity {

    @SerializedName("instanceId")
    private int instanceId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("bookId")
    private int bookId;

    @SerializedName("editionId")
    private int editionId;

    @SerializedName("title")
    private String title = Strings.EMPTY;

    @SerializedName("imageId")
    private String imageId;

    @SerializedName("borrowingRecordId")
    private int borrowingRecordId;

    @SerializedName("borrowingUserId")
    private int borrowingUserId;

    @SerializedName("borrowingUserName")
    private String borrowingUserName;

    @SerializedName("rateAvg")
    private double rateAvg;

    @SerializedName("rateCount")
    private int rateCount;

    @SerializedName("reviewCount")
    private int reviewCount;

    @SerializedName("author")
    private String author = Strings.EMPTY;

    @SerializedName("userAddDate")
    private String userAddDate = Strings.EMPTY;

    @SerializedName("startShareDate")
    private String startShareDate = Strings.EMPTY;

    @SerializedName("sharingStatus")
    private int sharingStatus;

    @SerializedName("sharingCompletedCount")
    private int sharingCompletedCount;


    //property for user
    @SerializedName("sharingCount")
    private int sharingCount;

    @SerializedName("availableStatus")
    private int availableStatus;

    @SerializedName("requestingStatus")
    private int requestingStatus;

    @SerializedName("recordStatus")
    private int recordStatus;



    public void setAvailableStatus(int availableStatus) {
        this.availableStatus = availableStatus;
    }

    public void setRequestingStatus(int requestingStatus) {
        this.requestingStatus = requestingStatus;
    }

    public int getBorrowingUserId() {
        return borrowingUserId;
    }

    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    private boolean isHeader = false;

    public BookSharingEntity() {
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public int getSharingCount() {
        return sharingCount;
    }

    public int getAvailableStatus() {
        return availableStatus;
    }

    public int getRequestingStatus() {
        return requestingStatus;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getEditionId() {
        return editionId;
    }

    public void setEditionId(int editionId) {
        this.editionId = editionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public String getBorrowingUserName() {
        return borrowingUserName;
    }



    public void setBorrowingUserName(String borrowingUserName) {
        this.borrowingUserName = borrowingUserName;
    }

    public double getRateAvg() {
        return rateAvg;
    }

    public void setRateAvg(int rateAvg) {
        this.rateAvg = rateAvg;
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUserAddDate() {
        return userAddDate;
    }

    public void setUserAddDate(String userAddDate) {
        this.userAddDate = userAddDate;
    }

    public String getStartShareDate() {
        return startShareDate;
    }

    public void setStartShareDate(String startShareDate) {
        this.startShareDate = startShareDate;
    }

    public int getSharingStatus() {
        return sharingStatus;
    }

    public void setSharingStatus(int sharingStatus) {
        this.sharingStatus = sharingStatus;
    }

    public int getSharingCompletedCount() {
        return sharingCompletedCount;
    }

    public void setSharingCompletedCount(int sharingCompletedCount) {
        this.sharingCompletedCount = sharingCompletedCount;
    }

    public int getBorrowingRecordId() {
        return borrowingRecordId;
    }

}
