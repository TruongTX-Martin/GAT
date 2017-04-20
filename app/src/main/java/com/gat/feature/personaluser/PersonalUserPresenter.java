package com.gat.feature.personaluser;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.repository.entity.Data;
import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by root on 20/04/2017.
 */

public interface PersonalUserPresenter extends Presenter{
    void requestBookUserSharing(BookSharingUserInput input);
    Observable<Data> getResponseBookUserSharing();
    Observable<ServerResponse<ResponseData>> onErrorBookUserSharing();
}
