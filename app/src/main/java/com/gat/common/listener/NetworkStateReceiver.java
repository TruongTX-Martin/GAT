package com.gat.common.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gat.app.GatApplication;
import com.gat.common.event.NetWorkEvent;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.feature.book_detail.BookDetailActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by root on 14/05/2017.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    GatApplication gatApplication;
    OnNetworkChangeListener observer;

    @Override
    public void onReceive(Context context, Intent intent) {

        gatApplication = (GatApplication) context.getApplicationContext();
        observer = gatApplication.getObserverNetworkChange();

        // cứ thằng activity nào implement Observer thì phải addObserver vào
        // observer.addObserver(new BookDetailActivity());

//        if(intent.getExtras()!=null) {
//            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
//            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
//                //ClientUtils.showToast(context, "Network connected");
//                EventBus.getDefault().postSticky(new NetWorkEvent(true));
//            } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
//               // ClientUtils.showToast(context, "Network not connected");
//                EventBus.getDefault().postSticky(new NetWorkEvent(false));
//            }
//        }

        MZDebug.w("NetworkStateReceiver changed _________________________________________________");
        observer.onNetworkChange(isOnline(context));

    }


    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
