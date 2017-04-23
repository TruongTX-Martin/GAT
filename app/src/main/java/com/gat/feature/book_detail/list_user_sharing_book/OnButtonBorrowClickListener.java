package com.gat.feature.book_detail.list_user_sharing_book;

import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;

/**
 * Created by mryit on 4/22/2017.
 */

public interface OnButtonBorrowClickListener {

    void onButtonBorrowClick (int position, UserResponse userResponse);

}
