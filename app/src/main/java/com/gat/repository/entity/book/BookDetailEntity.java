package com.gat.repository.entity.book;

import com.gat.common.util.Strings;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 23/04/2017.
 */

public class BookDetailEntity {

    @SerializedName("recordId")
    private int recordId;

    @SerializedName("editionInfo")
    private EditionInfo editionInfo;

    @SerializedName("instanceId")
    private int instanceId;

    @SerializedName("borrowerInfo")
    private BorrowerInfo borrowerInfo;

    @SerializedName("ownerInfo")
    private OwnerInfo ownerInfo;

    @SerializedName("messageId")
    private int messageId;

    @SerializedName("recordStatus")
    private int recordStatus;

    @SerializedName("recordType")
    private int recordType;

    @SerializedName("requestTime")
    private long requestTime;

    @SerializedName("approveTime")
    private long approveTime;

    @SerializedName("borrowTime")
    private long borrowTime;

    @SerializedName("completeTime")
    private long completeTime;

    @SerializedName("cancelTime")
    private String cancelTime = Strings.EMPTY;

    @SerializedName("rejectTime")
    private long rejectTime;


    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public EditionInfo getEditionInfo() {
        return editionInfo;
    }

    public void setEditionInfo(EditionInfo editionInfo) {
        this.editionInfo = editionInfo;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public BorrowerInfo getBorrowerInfo() {
        return borrowerInfo;
    }

    public void setBorrowerInfo(BorrowerInfo borrowerInfo) {
        this.borrowerInfo = borrowerInfo;
    }

    public OwnerInfo getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(OwnerInfo ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(int recordStatus) {
        this.recordStatus = recordStatus;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }


    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public long getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(long approveTime) {
        this.approveTime = approveTime;
    }

    public long getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(long borrowTime) {
        this.borrowTime = borrowTime;
    }

    public long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public long getRejectTime() {
        return rejectTime;
    }

    public void setRejectTime(long rejectTime) {
        this.rejectTime = rejectTime;
    }

    public class EditionInfo {
        @SerializedName("editionId")
        private int editionId;

        @SerializedName("bookId")
        private int bookId;

        @SerializedName("title")
        private String title = Strings.EMPTY;

        @SerializedName("imageId")
        private String imageId = Strings.EMPTY;

        @SerializedName("author")
        private String author = Strings.EMPTY;

        @SerializedName("rateAvg")
        private double rateAvg;

        @SerializedName("rateCount")
        private int rateCount;

        @SerializedName("reviewCount")
        private int reviewCount;

        public int getEditionId() {
            return editionId;
        }

        public int getBookId() {
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

        public double getRateAvg() {
            return rateAvg;
        }

        public int getRateCount() {
            return rateCount;
        }

        public int getReviewCount() {
            return reviewCount;
        }


    }

    public class BorrowerInfo {
        @SerializedName("userId")
        private int userId;

        @SerializedName("name")
        private String name = Strings.EMPTY;

        @SerializedName("email")
        private String email = Strings.EMPTY;

        @SerializedName("imageId")
        private String imageId = Strings.EMPTY;

        @SerializedName("userTypeFlag")
        private int userTypeFlag;

        @SerializedName("address")
        private String address = Strings.EMPTY;

        @SerializedName("deleteFlag")
        private int deleteFlag;

        @SerializedName("sharingCount")
        private int sharingCount;

        @SerializedName("readCount")
        private int readCount;

        public int getSharingCount() {
            return sharingCount;
        }

        public int getReadCount() {
            return readCount;
        }

        public int getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getImageId() {
            return imageId;
        }

        public int getUserTypeFlag() {
            return userTypeFlag;
        }

        public String getAddress() {
            return address;
        }

        public int getDeleteFlag() {
            return deleteFlag;
        }

    }

    public class OwnerInfo {
        @SerializedName("userId")
        private int userId;

        @SerializedName("name")
        private String name = Strings.EMPTY;

        @SerializedName("email")
        private String email = Strings.EMPTY;

        @SerializedName("imageId")
        private String imageId = Strings.EMPTY;

        @SerializedName("userTypeFlag")
        private int userTypeFlag;

        @SerializedName("address")
        private String address = Strings.EMPTY;

        @SerializedName("deleteFlag")
        private int deleteFlag;

        @SerializedName("sharingCount")
        private int sharingCount;

        @SerializedName("readCount")
        private int readCount;

        public int getSharingCount() {
            return sharingCount;
        }

        public int getReadCount() {
            return readCount;
        }

        public int getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getImageId() {
            return imageId;
        }

        public int getUserTypeFlag() {
            return userTypeFlag;
        }

        public String getAddress() {
            return address;
        }

        public int getDeleteFlag() {
            return deleteFlag;
        }


        public OwnerInfo(int userId, String name, String email, String imageId, int userTypeFlag, String address, int deleteFlag) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.imageId = imageId;
            this.userTypeFlag = userTypeFlag;
            this.address = address;
            this.deleteFlag = deleteFlag;


        }
    }
}
