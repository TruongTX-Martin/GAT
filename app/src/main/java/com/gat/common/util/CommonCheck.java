package com.gat.common.util;

import android.support.annotation.IntDef;
import android.util.Pair;

import com.gat.repository.entity.LoginData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ducbtsn on 2/24/17.
 */

public class CommonCheck {
    public final static int PASSWORD_LENGTH_MIN = 6;
    public final static int TOKEN_LENGTH = 6;
    private static final int ISBN_13 = 13;
    private static final int ISBN_10 = 10;

    @IntDef({Error.NO_ERROR, Error.FIELD_EMPTY, Error.EMAIL_INVALID, Error.PASSWORD_LENGTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Error{
        int NO_ERROR                = 0;
        int FIELD_EMPTY             = 1;
        int EMAIL_INVALID           = 2;
        int PASSWORD_LENGTH         = 3;
    }
    /**
     * Validate email
     * @param email
     * @return
     */
    public static Pair<Boolean, Integer> validateEmail(String email) {
        boolean valid = true;
        int error = Error.NO_ERROR;
        if (email.isEmpty()) {
            error = Error.FIELD_EMPTY;
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = Error.EMAIL_INVALID;
            valid = false;
        } else {
            // Do nothing
        }

        return new Pair<>(valid, new Integer(error));
    }

    /**
     * Validate password
     * @param password
     * @return
     */
    public static Pair<Boolean, Integer> validatePassword(String password) {
        boolean valid = true;
        int error = Error.NO_ERROR;
        if (password.isEmpty()) {
            error = Error.FIELD_EMPTY;
            valid = false;
        } else if (password.length() < PASSWORD_LENGTH_MIN) {
            error = Error.PASSWORD_LENGTH;
            valid = false;
        } else {
            // Do nothing
        }

        return new Pair<>(valid, new Integer(error));
    }

    /**
     * Make a hash string
     * @param s
     * @return
     */
    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Check token length
     * @param token
     * @return
     */
    public static boolean checkToken(String token) {
        boolean ret;
        if (token.length() == TOKEN_LENGTH) {
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    public static boolean checkIsbnCode(String isbn) {
        if (isbn.length() == ISBN_10 || isbn.length() == ISBN_13) {
            return true;
        } else {
            return false;
        }
    }

    public static String getGroupId(int user1, int user2) {
        return (user1 < user2) ? (user1 + "" + user2) : (user2 + "" + user1);
    }

}
