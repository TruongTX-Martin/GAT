package com.gat.feature.register.update.location;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.Strings;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.UserAddressData;
import com.gat.feature.register.RegisterPresenter;
import com.gat.feature.register.RegisterScreen;
import com.gat.feature.register.update.category.AddCategoryActivity;
import com.gat.feature.register.update.category.AddCategoryPresenter;
import com.gat.feature.register.update.category.AddCategoryScreen;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 2/28/17.
 */


public class AddLocationActivity  extends ScreenActivity<AddLocationScreen, AddLocationPresenter>
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = AddLocationActivity.class.getSimpleName();

    private GoogleMap googleMap;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient googleApiClient;

    // Auto complete
    PlaceAutocompleteFragment autocompleteFragment;

    // A default location is Hanoi city
    private final LatLng defaultLocation = new LatLng(21.022703,105.8194541);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean isLocationAccessGranted;

    // Keys for storing activity state.
    private static final String KEY_ADDRESS = "input_address";
    private static final String KEY_LOCATION = "select_location";

    private Marker marker;

    // Used for selecting the current place.
    private String inputAddress = Strings.EMPTY;
    private LatLng selectedLocation = null;
    private Location lastKnownLocation = null;

    private Unbinder unbinder;
    private CompositeDisposable disposables;
    private ProgressDialog progressDialog;

    @BindView(R.id.btn_add_location)
    Button addLocationBtn;

    @Override
    protected int getLayoutResource() {
        return R.layout.register_location_activity;
    }

    @Override
    protected AddLocationScreen getDefaultScreen() {
        return AddLocationScreen.instance();
    }

    @Override
    protected Class<AddLocationPresenter> getPresenterClass() {
        return AddLocationPresenter.class;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unbinder = ButterKnife.bind(findViewById(R.id.register_location_view));
        // Disposable
        disposables = new CompositeDisposable(
                getPresenter().updateResult().subscribe(this::onUpdateSuccess),
                getPresenter().onError().subscribe(this::onUpdateError)
        );

        progressDialog = new ProgressDialog(this);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            selectedLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            inputAddress = savedInstanceState.getParcelable(KEY_ADDRESS);
            addMarker(true);
        }

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();

        // Active place auto complete
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        /**
         * When a place was selected
         */
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "Place: " + place.getName());
                inputAddress = place.getAddress().toString();
                selectedLocation = place.getLatLng();
                changeButtonLable();
                // Add marker to this place
                addMarker(true);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d(TAG, "An error occurred: " + status);
                Toast.makeText(getApplicationContext(), getString(R.string.error_auto_place), Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * When clear button is pushed
         */
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setOnClickListener(view -> {
            TextView textView = (TextView) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
            inputAddress = Strings.EMPTY;
            textView.setText(inputAddress);
            selectedLocation = null;
            removeMarker();
            changeButtonLable();
        });

        /**
         * When search button is pushed
         */
        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setOnClickListener(view -> {
            TextView textView = (TextView) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
            inputAddress = textView.getText().toString();
            // TODO get location by text
            getDeviceLocation();
            if (lastKnownLocation != null) {
                selectedLocation = new LatLng(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
                addMarker(true);
                changeButtonLable();
            }
        });

        /**
         * Override key listener to catch search key
         */
        //autocompleteFragment.getView().setFocusableInTouchMode(true);
        //autocompleteFragment.getView().requestFocus();
        autocompleteFragment.getView().setOnKeyListener((v, keyCode, event) -> {
            switch (keyCode) {
                case KeyEvent.KEYCODE_SEARCH:
                    autocompleteFragment.getActivity().onBackPressed();
                    return true;
                default:
                    return super.onKeyUp(keyCode, event);
            }
        });

        addLocationBtn.setOnClickListener(view -> {
            if (!Strings.isNullOrEmpty(inputAddress) && selectedLocation != null) {
                onUpdating(true);
                getPresenter().setLocation(UserAddressData.instance(inputAddress, selectedLocation));
            } else {
                // Start add category
                start(this, AddCategoryActivity.class, AddCategoryScreen.instance());
                finish();
            }
        });

    }

    private void onUpdateError(ServerResponse<ResponseData> error) {
        onUpdating(false);
        //Toast.makeText(this, getString(R.string.update_location_failed), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, error.message(), Toast.LENGTH_SHORT).show();
    }

    private void onUpdateSuccess(ServerResponse<ResponseData> response) {
        onUpdating(false);
        start(this, AddCategoryActivity.class, AddCategoryScreen.instance());
        finish();
    }

    private void onUpdating(boolean enter) {
        if (enter) {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.updating));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        disposables.dispose();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null && !Strings.isNullOrEmpty(inputAddress) && selectedLocation != null) {
            outState.putParcelable(KEY_ADDRESS, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, selectedLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        Toast.makeText(this, getString(R.string.map_connection_failed), Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout)findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                // Always set title by input address
                title.setText(inputAddress);

                return infoWindow;
            }
        });

        /**
         * Handling drag map move
         */
        googleMap.setOnCameraMoveListener(() -> {
            LatLng center = googleMap.getCameraPosition().target;
            if (marker != null) {
                // Update selected position
                selectedLocation = center;
                // Move marker to center of map
                marker.setPosition(center);
                marker.showInfoWindow();
            }
        });

        /**
         * Handling click event
         */
        googleMap.setOnMapClickListener(location -> {
            if (marker != null) {
                // Prevent info window is disappeared
                marker.showInfoWindow();
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void addMarker(boolean cameraMove) {
        if (marker != null) marker.remove();

        MarkerOptions markerOptions = new MarkerOptions()
                .position(selectedLocation)
                .title(inputAddress)
                .snippet(selectedLocation.toString())
                //.icon(BitmapDescriptorFactory.fromBitmap(drawBmp))
                //.icon(BitmapDescriptorFactory.fromBitmap(drawBmp))
                // .anchor(0.5f, 1)
                //.draggable(true)
                .visible(true);
        marker = googleMap.addMarker(markerOptions);
        marker.showInfoWindow();

        if (cameraMove)
            // Move camera to selected position
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, DEFAULT_ZOOM));

    }

    private void removeMarker() {
        if (marker != null) marker.remove();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationAccessGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        // Set the map's camera position to the current location of the device.
        if (selectedLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation));
        } else if (isLocationAccessGranted) {
            lastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
            if (lastKnownLocation != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Cannot get device location");
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        isLocationAccessGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Access granted");
                    isLocationAccessGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationAccessGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (isLocationAccessGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void changeButtonLable() {
        if (!Strings.isNullOrEmpty(inputAddress)) {
            addLocationBtn.setText(getString(R.string.btn_add_location));
        } else {
            addLocationBtn.setText(getString(R.string.btn_pass_over));
        }
    }
}