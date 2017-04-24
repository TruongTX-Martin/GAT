package com.gat.data.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class InstanceIdService extends FirebaseInstanceIdService{

    private String firebaseToken;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirebaseToken:", firebaseToken);
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }
}
