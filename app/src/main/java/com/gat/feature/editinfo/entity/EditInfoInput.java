package com.gat.feature.editinfo.entity;

import com.gat.common.util.Strings;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by root on 19/04/2017.
 */

public class EditInfoInput {

    private String name = Strings.EMPTY;
    private String imageBase64 = Strings.EMPTY;
    private boolean isChangeImage = false;


    public EditInfoInput() {
    }


    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getName() {
        return name;
    }

    public boolean isChangeImage() {
        return isChangeImage;
    }

    public void setChangeImage(boolean changeImage) {
        isChangeImage = changeImage;
    }

    public void setName(String name) {
        this.name = name;
    }


}
