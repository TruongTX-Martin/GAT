package com.gat.data.response.impl;

/**
 * Created by mryit on 4/16/2017.
 */

public class EvaluationItemResponse {

    private String evaluationId;
    private long userId;
    private String name;
    private String imageId;
    private long bookId;
    private long editionId;
    private long value;
    private String review;
    private boolean spoiler;
    private String evaluationTime;

    public String getEvaluationId() {
        return evaluationId;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }

    public long getBookId() {
        return bookId;
    }

    public long getEditionId() {
        return editionId;
    }

    public long getValue() {
        return value;
    }

    public String getReview() {
        return review;
    }

    public boolean isSpoiler() {
        return spoiler;
    }

    public String getEvaluationTime() {
        return evaluationTime;
    }

}