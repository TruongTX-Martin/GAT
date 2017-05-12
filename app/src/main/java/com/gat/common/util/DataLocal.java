package com.gat.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;

/**
 * Created by root on 12/05/2017.
 */

public class DataLocal {

    public static SharedPreferences mSharedPre;
    private static Context context;
    public static final String NAME_REFERENCE = "gat";
    private static final String PERSONAL_INPUT_SHARING = "personal_input_sharing";
    private static final String PERSONAL_INPUT_READING = "personal_input_reading";
    private static final String PERSONAL_INPUT_REQUEST = "personal_input_request";


    public static void init(Context context1) {
        context = context1;
        mSharedPre = context.getSharedPreferences(NAME_REFERENCE,
                Context.MODE_PRIVATE);
    }

    public static void savePersonalInputSharing(BookInstanceInput input){
        if(input != null) {
            SharedPreferences.Editor editor = mSharedPre.edit();
            editor.putString(PERSONAL_INPUT_SHARING, input.getString());
            editor.commit();
        }

    }
    public static BookInstanceInput getPersonalInputSharing() {
        BookInstanceInput input = null;
        if(mSharedPre != null) {
            try {
                String stringSharing = mSharedPre.getString(PERSONAL_INPUT_SHARING,"");
                if(!Strings.isNullOrEmpty(stringSharing)) {
                    input = BookInstanceInput.getObject(stringSharing);
                }
            }catch (Exception e){}
        }
        return input;
    }

    public static void savePersonalInputReading(BookReadingInput input){
        if(input != null) {
            SharedPreferences.Editor editor = mSharedPre.edit();
            editor.putString(PERSONAL_INPUT_READING, input.getString());
            editor.commit();
        }

    }
    public static BookReadingInput getPersonalInputReading() {
        BookReadingInput input = null;
        if(mSharedPre != null) {
            try {
                String stringSharing = mSharedPre.getString(PERSONAL_INPUT_READING,"");
                if(!Strings.isNullOrEmpty(stringSharing)) {
                    input = BookReadingInput.getObject(stringSharing);
                }
            }catch (Exception e){}
        }
        return input;
    }

    public static void savePersonalInputRequest(BookRequestInput input){
        if(input != null) {
            SharedPreferences.Editor editor = mSharedPre.edit();
            editor.putString(PERSONAL_INPUT_REQUEST, input.getString());
            editor.commit();
        }

    }
    public static BookRequestInput getPersonalInputRequest() {
        BookRequestInput input = null;
        if(mSharedPre != null) {
            try {
                String stringSharing = mSharedPre.getString(PERSONAL_INPUT_REQUEST,"");
                if(!Strings.isNullOrEmpty(stringSharing)) {
                    input = BookRequestInput.getObject(stringSharing);
                }
            }catch (Exception e){}
        }
        return input;
    }


}
