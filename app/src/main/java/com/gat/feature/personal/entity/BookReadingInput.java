package com.gat.feature.personal.entity;

import org.json.JSONObject;

/**
 * Created by truongtechno on 30/03/2017.
 */

public class BookReadingInput {

    private int userId;
    private boolean readingFilter;
    private boolean toReadFilter;
    private boolean readFilter;

    private int page = 1;
    private int per_page = 10;

    public BookReadingInput() {
    }

    public BookReadingInput(boolean readFilter, boolean readingFilter, boolean toReadFilter) {
        this.readFilter = readFilter;
        this.readingFilter = readingFilter;
        this.toReadFilter = toReadFilter;
    }


    public String getString() {
        JSONObject object = new JSONObject();
        try {
            object.put("readingFilter", readingFilter);
            object.put("toReadFilter", toReadFilter);
            object.put("readFilter", readFilter);
        } catch (Exception e) {
        }
        return object.toString();
    }
    public static BookReadingInput getObject(String jsonString) {
        BookReadingInput input = new BookReadingInput();
        try {
            JSONObject object = new JSONObject(jsonString);
            if (object.has("readingFilter")) {
                boolean readingFilter = object.getBoolean("readingFilter");
                input.setReadingFilter(readingFilter);
            }
            if (object.has("toReadFilter")) {
                boolean toReadFilter = object.getBoolean("toReadFilter");
                input.setToReadFilter(toReadFilter);
            }
            if (object.has("readFilter")) {
                boolean readFilter = object.getBoolean("readFilter");
                input.setReadFilter(readFilter);
            }

        } catch (Exception e) {
        }
        return input;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getPer_page() {
        return per_page;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isReadingFilter() {
        return readingFilter;
    }

    public void setReadingFilter(boolean readingFilter) {
        this.readingFilter = readingFilter;
    }

    public boolean isToReadFilter() {
        return toReadFilter;
    }

    public void setToReadFilter(boolean toReadFilter) {
        this.toReadFilter = toReadFilter;
    }

    public boolean isReadFilter() {
        return readFilter;
    }

    public void setReadFilter(boolean readFilter) {
        this.readFilter = readFilter;
    }
}
