package com.gat.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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


}
