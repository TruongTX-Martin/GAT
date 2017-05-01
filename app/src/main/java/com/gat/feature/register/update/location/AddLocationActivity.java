package com.gat.feature.register.update.location;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Strings;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.user.UserAddressData;
import com.gat.feature.register.update.category.AddCategoryActivity;
import com.gat.feature.register.update.category.AddCategoryScreen;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

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

    // A default location is Hanoi city
    private final LatLng defaultLocation = new LatLng(21.022703,105.8194541);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean isLocationAccessGranted;

    // Keys for storing activity state.
    private static final String KEY_ADDRESS = "input_address";
    private static final String KEY_LOCATION = "select_location";

    private Marker marker;
    private Marker currentLocationMarker;

    // Used for selecting the current place.
    private String inputAddress = Strings.EMPTY;
    private LatLng selectedLocation = null;
    private Location lastKnownLocation = null;

    private Unbinder unbinder;
    private CompositeDisposable disposables;
    private ProgressDialog progressDialog;

    private Subject<String> getPlaceIdSubject;
    private Subject<List<String>> getLocationSubject;
    private Subject<LatLng> updateLocationSubject;
    private Subject<String> notFoundSubject;

    private String countryCode = "VN";


    @BindView(R.id.btn_add_location)
    Button addLocationBtn;

    @BindView(R.id.search_location_edit)
    EditText locationText;

    @BindView(R.id.search_location_btn)
    ImageButton searchBtn;

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

        getPlaceIdSubject = BehaviorSubject.create();
        getLocationSubject = BehaviorSubject.create();
        updateLocationSubject = BehaviorSubject.create();
        notFoundSubject = BehaviorSubject.create();

        // Disposable
        disposables = new CompositeDisposable(
                getPresenter().updateResult().subscribe(this::onUpdateSuccess),
                getPresenter().onError().subscribe(this::onUpdateError),
                getPlaceIdSubject.subscribe(this::getPlaceIdFromAddress),
                getLocationSubject.subscribe(this::getLocationFromPlaceId),
                updateLocationSubject.subscribe(this::onUpdateLocation),
                notFoundSubject.subscribe(this::placeNotFound)

        );

        progressDialog = new ProgressDialog(this);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            selectedLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            inputAddress = savedInstanceState.getParcelable(KEY_ADDRESS);
            updateLocationGUI(false);
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

        locationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    searchBtn.setClickable(true);
                    searchBtn.setEnabled(true);
                } else {
                    searchBtn.setClickable(false);
                    searchBtn.setEnabled(false);
                    selectedLocation = null;
                    inputAddress = Strings.EMPTY;
                    updateLocationGUI(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        locationText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String text = this.locationText.getText().toString();
                if (text != null && !text.trim().isEmpty()) {
                    getPlaceIdFromAddress(text);
                    InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(locationText.getWindowToken(), 0);
                }
            }
            return true;
        });

        searchBtn.setOnClickListener(view -> {
            String text = this.locationText.getText().toString();
            if (text != null && !text.trim().isEmpty())
                getPlaceIdFromAddress(text);
        });
    }

    private void onUpdateLocation(LatLng location) {
        selectedLocation = location;
        inputAddress = locationText.getText().toString();
        updateLocationGUI(false);
    }

    private void placeNotFound(String error) {
        //Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        selectedLocation = null;
        inputAddress = locationText.getText().toString();
        updateLocationGUI(true);
    }

    private void getPlaceIdFromAddress(String address)
    {
        //Southwest corner to Northeast corner.
        LatLngBounds bounds = new LatLngBounds( new LatLng( 39.906374, -105.122337 ), new LatLng( 39.949552, -105.068779 ) );
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry(countryCode)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                .build();
        Places.GeoDataApi.getAutocompletePredictions(googleApiClient, address, bounds, typeFilter)
                .setResultCallback(autocompletePredictions -> {
                    if (autocompletePredictions != null) {
                        if (autocompletePredictions.getStatus().isSuccess()) {
                            List<String> placeIds = new ArrayList<String>();
                            for (Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator(); iterator.hasNext();) {
                                placeIds.add(iterator.next().getPlaceId());
                            }
                            if (placeIds.isEmpty())
                                notFoundSubject.onNext(getString(R.string.no_address_found));
                            else
                                getLocationFromPlaceId(placeIds);
                        }
                        autocompletePredictions.release();
                    } else {
                        notFoundSubject.onNext(getString(R.string.no_address_found));
                    }
                });
    }

    private void getLocationFromPlaceId(List<String> placeIds) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeIds.get(0)).setResultCallback(places -> {
            if (places.getStatus().isSuccess()) {
                Place place = places.get(0);
                updateLocationSubject.onNext(place.getLatLng());
            } else {
                notFoundSubject.onNext(getString(R.string.no_address_found));
            }
        });
    }

    private void onUpdateError(String error) {
        onUpdating(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateMappingGUI();
    }

    private void updateLocationMarker(boolean cameraMove) {
        if (marker != null) marker.remove();

        if (selectedLocation != null && !Strings.isNullOrEmpty(inputAddress)) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(selectedLocation)
                    .visible(true);
            marker = googleMap.addMarker(markerOptions);

            if (cameraMove)
                // Move camera to selected position
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, DEFAULT_ZOOM));

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
        updateMappingGUI();
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateMappingGUI() {
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

        if (selectedLocation != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedLocation));
            locationText.setText(inputAddress);
        } else if (isLocationAccessGranted) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            lastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
        } else {
            // Cannot search
            selectedLocation = null;
            inputAddress = Strings.EMPTY;

            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            Log.d(TAG, "Cannot get device location");
        }
        updateLocationGUI(selectedLocation == null ? true : false);
    }

    private void changeButtonLable() {
        if (!Strings.isNullOrEmpty(inputAddress)) {
            addLocationBtn.setText(getString(R.string.btn_add_location));
        } else {
            addLocationBtn.setText(getString(R.string.btn_pass_over));
        }
    }

    private void addCurrentLocation(Location lastKnownLocation, boolean move) {
        if (lastKnownLocation != null) {
            // add current location = marker icon
            LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentLatLng).title("You're here").icon(icon);

            if (currentLocationMarker != null) currentLocationMarker.remove();

            currentLocationMarker = googleMap.addMarker(markerOptions);
            if (move) {
                // move camera
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM);
                googleMap.moveCamera(cameraUpdate);
            }
        }
    }

    private void updateLocationGUI(boolean add) {
        if (add && lastKnownLocation != null) {
            // Get current address to display on text edit
            List<Address> addresses = new ArrayList<>();
            try {
                addresses = new Geocoder(this, Locale.getDefault()).getFromLocation(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!addresses.isEmpty()) {
                String currentAddress = Strings.EMPTY;
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                    currentAddress += addresses.get(0).getAddressLine(i);
                }
                if (!Strings.isNullOrEmpty(currentAddress)) {
                    locationText.setText(currentAddress);
                    inputAddress = currentAddress;
                }
                countryCode = addresses.get(0).getCountryCode();
                Log.d(TAG, "CountryCode:"+countryCode);
            }

            if (!Strings.isNullOrEmpty(inputAddress)) {
                selectedLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                addCurrentLocation(lastKnownLocation, false);
            } else {
                addCurrentLocation(lastKnownLocation, true);
            }
        }

        changeButtonLable();
        updateLocationMarker(true);
    }
}