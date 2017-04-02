package com.gat.feature.suggestion.nearby_user;

import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.app.screen.Screen;
import com.gat.common.util.MZDebug;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.rey.mvp2.PresenterManager;
import com.rey.mvp2.impl.MvpActivity;

import java.util.List;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistanceActivity
        extends ScreenActivity<ShareNearByUserDistanceScreen, ShareNearByUserDistancePresenter>
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = ShareNearByUserDistanceActivity.class.getSimpleName();
    public static final String PASS_LIST_USER_DISTANCE = "LIST_USERS";
    List<UserNearByDistance> mListUserShareNearByDistance;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_share_near_by_user_distance;
    }

    @Override
    protected ShareNearByUserDistanceScreen getDefaultScreen() {
        return ShareNearByUserDistanceScreen.instance();
    }

    @Override
    protected Class getPresenterClass() {
        return ShareNearByUserDistancePresenter.class;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get list user passedvia bundle
        Bundle bundle = getIntent().getExtras();
        mListUserShareNearByDistance = bundle.getParcelableArrayList(PASS_LIST_USER_DISTANCE);

        // for debug
        if (mListUserShareNearByDistance == null) {
            MZDebug.w("list pass is NULL");
        } else {
            MZDebug.w("List pass size: " + mListUserShareNearByDistance.size());
        } // end debug
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

}
