package com.gat.common.listener;


import java.util.Observable;
import java.util.Observer;

/**
 * Created by mryit on 5/16/2017.
 */

public class OnNetworkChangeListener extends Observable {

    boolean isOnline = true;

    void onNetworkChange (boolean isOnline) {
        setChanged();
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return this.isOnline;
    }
}
