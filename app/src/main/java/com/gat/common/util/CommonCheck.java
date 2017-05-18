package com.gat.common.util;

import android.support.annotation.IntDef;
import android.util.Log;
import android.util.Pair;

import com.gat.app.activity.ScreenActivity;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.ConflictDataException;
import com.gat.data.exception.LoginException;
import com.gat.data.firebase.entity.Notification;
import com.gat.data.response.ServerResponse;
import com.gat.feature.bookdetailowner.BookDetailOwnerActivity;
import com.gat.feature.bookdetailowner.BookDetailOwnerScreen;
import com.gat.feature.bookdetailsender.BookDetailSenderActivity;
import com.gat.feature.bookdetailsender.BookDetailSenderScreen;
import com.gat.feature.message.MessageActivity;
import com.gat.feature.message.presenter.MessageScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by ducbtsn on 2/24/17.
 */

public class CommonCheck {
    public final static int PASSWORD_LENGTH_MIN = 6;
    public final static int TOKEN_LENGTH = 6;

    private final static int ADMIN_USER_ID = 0;

    private static final int ISBN_13 = 13;
    private static final int ISBN_10 = 10;
    private static final int LOCAL_LOCALE = 2;


    public @interface LOCALE {
        int VN = 1;
        int US = 2;
    }

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

    public static <T extends Object> ServerResponse<T> checkResponse(Response<ServerResponse<T>> response) {
        if (response == null) {
            throw CommonException.FAILED_RESPONSE;
        } else if (!response.isSuccessful()) {
            ResponseBody errorBody = response.errorBody();
            if (errorBody == null) {
                throw CommonException.FAILED_RESPONSE;
            } else {
                try {
                    JSONObject errorContent = new JSONObject(errorBody.string());
                    if (errorContent.has("message")) {
                        if (response.code() == ServerResponse.HTTP_CODE.TOKEN) {
                            ServerResponse serverResponse = ServerResponse.TOKEN_CHANGED;
                            serverResponse.message(errorContent.getString("message"));
                            throw new LoginException(serverResponse);

                        } else if (response.code() == ServerResponse.HTTP_CODE.CONFLICT_DATA) {

                            ServerResponse serverResponse = ServerResponse.TOKEN_CHANGED;
                            serverResponse.message(errorContent.getString("message"));
                            throw new ConflictDataException(serverResponse);
                        }

                        else {
                            throw new CommonException(errorContent.getString("message"));
                        }
                    } else {
                        Log.d("ErrorBody", errorContent.toString());
                        throw CommonException.FAILED_RESPONSE;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw CommonException.FAILED_RESPONSE;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw CommonException.FAILED_RESPONSE;
                }
            }
        } else if (response.body() == null) {
            throw CommonException.FAILED_RESPONSE;
        } else {
            Log.d("Response", "Code: " + response.body().code());
            return response.body();
        }
    }

    public static String getGroupId(int user1, int user2) {
        return (user1 < user2) ? (user1 + ":" + user2) : (user2 + ":" + user1);
    }

    public static boolean isDiffDay(long time1, long time2) {
        if (Math.abs(time1 - time2) > 1000 * 60 * 60 * 24 ) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(time);
        if (LOCAL_LOCALE == LOCALE.VN) {
            return (cal.get(Calendar.DAY_OF_MONTH) + " tháng" + (cal.get(Calendar.MONTH) + 1) + " năm "
                    + cal.get(Calendar.YEAR));
        } else if (LOCAL_LOCALE == LOCALE.US){
            return (cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + ", " +cal.get(Calendar.YEAR));
        } else {
            return Strings.EMPTY;
        }
    }

    public static String getNameFromEmail(String email) {
        if (validateEmail(email).first) {
            return email.substring(0, email.indexOf('@'));
        } else {
            return Strings.EMPTY;
        }
    }


    public static boolean isAdmin(int userId) {
        return userId == ADMIN_USER_ID;
    }

    public static void processNotification(Notification notification, ScreenActivity activity) {
        int requestId = notification.requestId();
        switch (notification.pushType()) {
            case NotificationConfig.PushType.PRIVATE_MESSAGE:
            case NotificationConfig.PushType.BOOK_ACCEPTED:
                activity.start(activity, MessageActivity.class, MessageScreen.instance(notification.senderId()));
                break;
            case NotificationConfig.PushType.REQUEST_BORROW:
                activity.start(activity, BookDetailOwnerActivity.class, BookDetailOwnerScreen.instance(requestId));
            case NotificationConfig.PushType.BOOK_BORROWED:
            case NotificationConfig.PushType.BOOK_INFORM_LENT:
            case NotificationConfig.PushType.BOOK_INFORM_RETURN:
            case NotificationConfig.PushType.BOOK_REJECTED:
            case NotificationConfig.PushType.BOOK_INFORM_LOST:
            case NotificationConfig.PushType.BOOK_REQUEST_CANCEL:
                activity.start(activity, BookDetailSenderActivity.class, BookDetailSenderScreen.instance(requestId));
                break;
            case NotificationConfig.PushType.BOOK_INFORM_BORROW:
            case NotificationConfig.PushType.BOOK_REQUESTED_QUANTITY:
                // TODO go to personal fragement request tab
                break;
            default:
                break;

        }
    }
}
