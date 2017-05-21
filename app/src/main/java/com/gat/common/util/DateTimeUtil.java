package com.gat.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by mryit on 4/22/2017.
 */

public class DateTimeUtil {

    public static final String FORMAT_TYPE_1 = "yyyy-MM-dd HH:mm:ss";


    public static String calculateDayAgo (String formatType, String timeString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatType);
            Date past = format.parse(timeString);
            Date now = new Date();
            return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " ngày";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "hôm nay";
    }

    public static String calculateTimeAgo (String formatType, String timeString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatType);
            Date past = format.parse(timeString);
            Date now = new Date();

            long hoursAgo = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());

            if (hoursAgo <=1) {
                return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " phút";
            } else if (hoursAgo >=24){
                return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " ngày";
            } else {
                return hoursAgo + " giờ";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "vừa nãy";
    }


    public static String calculateTimeAgo (long milliseconds) {

        Date now = new Date();
        long minutesAgo = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - milliseconds);

        if (minutesAgo >= 1440) { // 24h x 60p = 1440
            long daysAgo= TimeUnit.MILLISECONDS.toDays(now.getTime() - milliseconds);
            if (daysAgo >= 30) {
                return (int)Math.ceil(daysAgo/30) + " tháng";
            } else {
                return TimeUnit.MILLISECONDS.toDays(now.getTime() - milliseconds) + " ngày";
            }
        } else if (minutesAgo >= 60) {
            return TimeUnit.MILLISECONDS.toHours(now.getTime() - milliseconds) + " giờ";
        } else {
            return minutesAgo + " phút";
        }
    }


    public static String transformDate (long milliseconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        return mDay + " tháng " + (mMonth + 1) + " năm " + mYear;
    }


    public static String transformDate (String timeString) {

        if (timeString == null || timeString.isEmpty()) {
            return "";
        }

        String[] split = timeString.split(" ");
        String[] date = split[0].split("-");
        String result = date[2] + " tháng " + date[1] + " năm " + date[0];

        return result;
    }


}
