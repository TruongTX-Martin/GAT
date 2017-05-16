package com.gat.common.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gat.common.event.NetWorkEvent;
import com.gat.common.util.ClientUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by root on 14/05/2017.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    public static boolean isConnected;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras()!=null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                ClientUtils.showToast(context, "Network connected");
                isConnected = true;
            } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
                isConnected = false;
            }
        }
    }
}
