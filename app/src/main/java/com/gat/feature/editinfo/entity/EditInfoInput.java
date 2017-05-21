package com.gat.feature.editinfo.entity;

import com.gat.common.util.Strings;
import com.gat.data.user.UserAddressData;

import java.io.File;
import java.util.List;

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
    private UserAddressData addressData;
    private List<Integer> categories;

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


    public UserAddressData getAddressData() {
        return addressData;
    }

    public void setAddressData(UserAddressData addressData) {
        this.addressData = addressData;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }
}
