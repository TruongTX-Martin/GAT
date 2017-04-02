package com.gat.feature.suggestion.nearby_user;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.app.screen.Screen;
import com.gat.common.util.MZDebug;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.mvp2.PresenterManager;
import com.rey.mvp2.impl.MvpActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistanceActivity
        extends ScreenActivity<ShareNearByUserDistanceScreen, ShareNearByUserDistancePresenter>
        implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = ShareNearByUserDistanceActivity.class.getSimpleName();
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 2000;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String PASS_LIST_USER_DISTANCE = "LIST_USERS";
    public static final String PASS_USER_LOCATION_LATITUDE = "USER_LOCATION_LATITUDE";
    public static final String PASS_USER_LOCATION_LONGITUDE = "USER_LOCATION_LONGITUDE";

    @BindView(R.id.rv_users_near)
    RecyclerView recyclerViewUsersNear;

    private List<UserNearByDistance> mListUserShareNearByDistance;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double mCurrentLongitude;
    private double mCurrentLatitude;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        // get LIST USER, CURRENT LOCATION passed via bundle
        Bundle bundle = getIntent().getExtras();
        mListUserShareNearByDistance = bundle.getParcelableArrayList(PASS_LIST_USER_DISTANCE);
        mCurrentLongitude = bundle.getDouble(PASS_USER_LOCATION_LONGITUDE);
        mCurrentLatitude = bundle.getDouble(PASS_USER_LOCATION_LATITUDE);

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
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @OnClick(R.id.tv_header)
    void onHeaderBackTap() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        MZDebug.w("_______________________USER ACCEPT PERMISSION_________________________________");
        processLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        MZDebug.w("_______________________USER REFUSE PERMISSION_________________________________");
    }

    @AfterPermissionGranted(PERMISSION_ACCESS_COARSE_LOCATION)
    private void processLocation() {
        if (EasyPermissions.hasPermissions(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {

        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, "Location permission is required! Ops! Ops!",
                    PERMISSION_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MZDebug.w("_______________ON MAP READY____MOVE TO CURRENT LOCATION____________________");

        mMap = googleMap;
        Location currentLocation = new Location("");
        currentLocation.setLatitude(mCurrentLatitude);
        currentLocation.setLongitude(mCurrentLongitude);
        moveCameraToNewLocation(currentLocation);
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

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    private void moveCameraToNewLocation(Location location) {
        Log.d(TAG, location.toString());
        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
//        MarkerOptions options = new MarkerOptions()
//                .position(latLng)
//                .title("I am here!");
//        mMap.addMarker(options);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
//        locationManager.removeUpdates(this);
    }

}
