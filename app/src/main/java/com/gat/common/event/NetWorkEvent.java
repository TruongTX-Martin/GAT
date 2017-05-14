package com.gat.common.event;

/**
 * Created by root on 14/05/2017.
 */

public class NetWorkEvent {

    private boolean isConnected;

    public NetWorkEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }


    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
