package com.gat.feature.suggestion.nearby_user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistanceActivity
        extends ScreenActivity<ShareNearByUserDistanceScreen, ShareNearByUserDistancePresenter>
        implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMarkerClickListener{

    public static final String TAG = ShareNearByUserDistanceActivity.class.getSimpleName();
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 2000;
    private static final int DEFAULT_ZOOM = 15;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String PASS_LIST_USER_DISTANCE = "LIST_USERS";
    public static final String PASS_USER_LOCATION_LATITUDE = "USER_LOCATION_LATITUDE";
    public static final String PASS_USER_LOCATION_LONGITUDE = "USER_LOCATION_LONGITUDE";

    @BindView(R.id.rv_users_near)
    RecyclerView recyclerViewUsersNear;

    private CompositeDisposable disposables;
    private ProgressDialog progressDialog;
    private List<UserNearByDistance> mListUserShareNearByDistance;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double mCurrentLongitude;
    private double mCurrentLatitude;
    private LatLng mCurrentLatLng;
    private LatLng mTopLeftLatLng;
    private LatLng mBottomRightLatLng;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker mCurrentLocationMaker;
    private int mReasonMapMoved;

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

        // composite presenter
        disposables = new CompositeDisposable();

        progressDialog = new ProgressDialog(this);

        // setup google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get LIST USER, CURRENT LOCATION passed via bundle
        Bundle bundle = getIntent().getExtras();
        mListUserShareNearByDistance = bundle.getParcelableArrayList(PASS_LIST_USER_DISTANCE);
        mCurrentLongitude = bundle.getDouble(PASS_USER_LOCATION_LONGITUDE);
        mCurrentLatitude = bundle.getDouble(PASS_USER_LOCATION_LATITUDE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        checkToRequestPermission();
        // TODO process
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MZDebug.w("_______________ON MAP READY____MOVE TO CURRENT LOCATION____________________");
        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        buildGoogleApiClient();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MZDebug.w("_________________________________Google api client onConnected _______________");
        checkToRequestPermission();

        // get current location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mCurrentLatitude = mLastLocation.getLatitude();
            mCurrentLongitude = mLastLocation.getLongitude();
            MZDebug.w("______Current: lat = " + mCurrentLatitude + ", long= "  + mCurrentLongitude);

            // add current location = marker icon
            mCurrentLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(mCurrentLatLng)
                    .title("You're here")
                    .icon(icon);
            mCurrentLocationMaker = mMap.addMarker(markerOptions);
            // move camera
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, DEFAULT_ZOOM);
            mMap.moveCamera(cameraUpdate);
        }
    }

    private void checkToRequestPermission () {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, "Location permission is required! Ops! Ops!",
                    PERMISSION_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
            return;
        }
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

        mGoogleApiClient.connect();
    }

    private void addMaker (Location location) {

        // add icon type book stop

        // add icon type user share book

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLatitude()))
                .title("Your Location")
                .icon(icon);
        Marker maker = mMap.addMarker(markerOptions);
    }

    private void moveCameraToNewLocation(Location location) {
        Log.d(TAG, location.toString());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mMap.animateCamera(cameraUpdate);
    }

    private void moveCameraToNewLatLng(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onCameraIdle() {

        LatLngBounds curScreen = mMap.getProjection().getVisibleRegion().latLngBounds;
        mTopLeftLatLng = new LatLng(curScreen.southwest.latitude, curScreen.southwest.longitude);
        mBottomRightLatLng = new LatLng(curScreen.northeast.latitude, curScreen.northeast.longitude);

        switch (mReasonMapMoved) {
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE: {
                MZDebug.w("_____________The camera has stopped moving BY USER__ UPDATE DATA______");
                break;
            }
            case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION: {
                MZDebug.w("____The camera has stopped moving BY DEVELOPER______DO NOTHING________");

                MZDebug.w("TopLeft: lat= " + curScreen.southwest.latitude
                        + ", long= " + curScreen.southwest.longitude);
                MZDebug.w("BottomRight: lat= " + curScreen.northeast.latitude
                        + ", long= " +curScreen.northeast.longitude );
                break;
            }
        }
        mTopLeftLatLng = null;
        mBottomRightLatLng = null;
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            mReasonMapMoved = GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE;
            MZDebug.w("______________________The user gestured on the map._______________________");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            mReasonMapMoved = GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION;
            MZDebug.w("______________________The user TAPPED SOMETHING on the map._______________");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            mReasonMapMoved = GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION;
            MZDebug.w("_________________________________developer move the camera _______________");
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mCurrentLocationMaker)) {
            MZDebug.w("_____________User tapped on their location maker__________________________");
        }
        return true;
    }
}
