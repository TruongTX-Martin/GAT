package com.gat.feature.notification;

/**
 * Created by mryit on 4/30/2017.
 */

public @interface NotifyType {

    /**
     * GaT Bạn có 10 tin nhắn chưa đọc
     * 4.1.1 page_message_main
     */
    int MESSAGE_UNREAD  = 00;

    /**
     * “user name" Gửi tin nhắn tới bạn
     * 4.1.2 page_message_private
     */
    int MESSAGE_FROM    = 01;

    /**
     * “user name" Gửi yêu cầu mượn sách “tên sách"
     * 6.4.1 request_detail
     */
    int BORROW_REQUEST    = 10;

    /**
     * Sách “abc" đã được cho người khác mượn.
     * 6.4.1 request_detail
     */
    int BORROW_UNLUCKY    = 11;

    /**
     * “user name" Đồng ý cho bạn mượn sách “tên sách"
     * 4.1.2 page_message_private
     */
    int BORROW_ACCEPT    = 12;

    /**
     * “user name" Thông báo bạn đã mượn sách “tên sách"
     * 6.4.1 request_detail
     */
    int BORROW_DONE    = 13;

    /**
     * “user name" Thông báo bạn trả sách “tên sách"
     * 6.4.1 request_detail
     */
    int BORROW_NEEDED    = 14;

    /**
     * “user name" Từ chối cho bạn mượn sách “tên sách"
     * 6.4.1 request_detail
     */
    int BORROW_REFUSE    = 15;

    /**
     * “user name" Thông báo bạn đã làm mất sách “tên sách" !!!
     * 6.4.1 request_detail
     */
    int BORROW_LOST    = 16;

    /**
     * GaT Bạn đang mượn 5 quyển sách từ người dùng khác.
     * 6.3.1 page_user_request
     */
    int BORROW_YOUR_TOTAL    = 17;

    /**
     * GaT Có 2 người gửi yêu cầu mượn sách “tên sách”
     * 6.3.1 page_user_request
     */
    int BORROW_FROM_ANOTHER    = 18;

    /**
     * “user name" Huỷ yêu cầu mượn sách “tên sách"
     * Target: 6.4.1 request_detail
     */
    int BORROW_CANCEL    = 19;

}
