package com.gat.common.util;

/**
 * Created by truongtechno on 29/03/2017.
 */

public class ClientUtils {

    public static boolean validate(String input) {
        if (input == null || input.length() == 0 || input.equals("null")) {
            return false;
        }
        return true;
    }
}
