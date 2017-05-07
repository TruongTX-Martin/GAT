package com.gat.feature.personaluser;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by root on 20/04/2017.
 */

public interface PersonalUserPresenter extends Presenter{
    void requestBookUserSharing(BookSharingUserInput input);
    Observable<Data> getResponseBookUserSharing();
    Observable<ServerResponse<ResponseData>> onErrorBookUserSharing();


    void requestBookUserReading(BookReadingInput input);
    Observable<Data> getResponseBookUserReading();
    Observable<ServerResponse<ResponseData>> onErrorBookUserReading();


    void requestBorrowBook(BorrowRequestInput input);
    Observable<Data> getResponseBorrowBook();
    Observable<String> onErrorBorrowBook();

    void requestVisitorInfo(int userId);
    Observable<User> getResponseVisitorInfo();
    Observable<ServerResponse<ResponseData>> onErrorVisitorInfo();

}
