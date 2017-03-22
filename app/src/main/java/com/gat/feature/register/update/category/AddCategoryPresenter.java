package com.gat.feature.register.update.category;

import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.rey.mvp2.Presenter;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/19/17.
 */

public interface AddCategoryPresenter extends Presenter{
    void setCategories(List<Integer> categories);
    Observable<ServerResponse<ResponseData>> onError();
    Observable<ServerResponse> updateResult();
}
