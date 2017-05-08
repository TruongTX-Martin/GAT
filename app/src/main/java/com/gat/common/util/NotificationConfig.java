package com.gat.common.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ducbtsn on 5/6/17.
 */

public class NotificationConfig {
    public static final String NOTIFICATION_SERVICE = "com.gat.private_message";
    @Retention(RetentionPolicy.SOURCE)
    public @interface PushType {
        /**
         * senderID - sender
         */
        int PRIVATE_MESSAGE             = 1;
        /**
         * requestID - requestID
         */
        int REQUEST_BORROW              = 2;
        /**
         * requestID - requestID
         */
        int BOOK_BORROWED               = 3;
        /**
         * senderID - borrower
         */
        int BOOK_ACCEPTED               = 4;
        /**
         * requestID - requestID
         */
        int BOOK_INFORM_LENT            = 5;
        /**
         * requestID - requestID
         */
        int BOOK_INFORM_RETURN          = 6;
        /**
         * requestID - requestID
         */
        int BOOK_REJECTED               = 7;
        /**
         * requestID - requestID
         */
        int BOOK_INFORM_LOST            = 8;
        /**
         * requestID - requestID
         */
        int BOOK_INFORM_BORROW          = 9;
        int BOOK_REQUESTED_QUANTITY     = 10;
        /**
         * requestID - requestID
         */
        int BOOK_REQUEST_CANCEL         = 11;
    }
}
