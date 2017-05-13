package com.gat.feature.book_detail.list_user_sharing_book;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mozaa on 21/04/2017.
 */


@IntDef({UserSharingType.IS_ME, UserSharingType.CAN_BORROW, UserSharingType.WAIT_FOR_BORROW,
        UserSharingType.WAIT_FOR_ACCEPT, UserSharingType.WAIT_FOR_CONNECT,
        UserSharingType.IS_BORROWING, UserSharingType.BORROW_IN_QUEUE})
@Retention(RetentionPolicy.SOURCE)
public @interface UserSharingType {

    // I'm sharing this book
    int IS_ME = 1;

    /**
     * I can borrow this book
     *
     * availbleStatus = 1 && requestingStatus = 0
     *
     * -> button text = Mượn sách
     */
    int CAN_BORROW = 2;

    /**
     * The book were borrowed by another one
     *
     * availbleStatus = 0 && requestingStatus = 0
     *
     * -> info text = Đang được mượn & button text = Đợi mượn
     */
    int WAIT_FOR_BORROW = 3;

    /**
     * availbleStatus = 1 && requestingStatus = 1
     *
     * -> info text -> View.GONE
     */
    // && recordStatus = 0 -> button text =  Đợi đồng ý
    int WAIT_FOR_ACCEPT = 4;

    // && recordStatus = 2 -> button text =  Đang liên lạc
    int WAIT_FOR_CONNECT = 5;

    // && recordStatus = 3 -> button text =  Đang mượn
    int IS_BORROWING = 6;

    /**
     * availbleStatus = 0 && requestingStatus = 1 && recordStatus = 1
     *
     * -> info text = Đang được mượn & button text = Đợi đến lượt
     */
    int BORROW_IN_QUEUE = 7;

}
