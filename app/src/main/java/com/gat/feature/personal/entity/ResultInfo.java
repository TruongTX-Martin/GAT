package com.gat.feature.personal.entity;

import com.gat.common.util.Strings;
import com.google.gson.annotations.SerializedName;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class ResultInfo {

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
    private int imageId;

    @SerializedName("borrowingUserName")
    private String borrowingUserName;

    @SerializedName("rateAvg")
    private int rateAvg;

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
}
