package com.gat.feature.scanbarcode;

import com.rey.mvp2.Presenter;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/24/17.
 */

public interface ScanPresenter extends Presenter {
    void searchByIsbn(String isbn);
    Observable<Integer> onSuccess();
    Observable<String> onError();
}
