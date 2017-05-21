package com.gat.feature.editinfo;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.UserAddressData;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.repository.entity.Data;
import com.rey.mvp2.Presenter;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by root on 18/04/2017.
 */

public interface EditInfoPresenter extends Presenter {
    void requestEditInfo(EditInfoInput input);
    Observable<String> getResponseEditInfo();
    Observable<ServerResponse<ResponseData>> onErrorEditInfo();

}
