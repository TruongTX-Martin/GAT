package com.gat.feature.suggestion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.MZDebug;
import com.gat.common.util.TrackGPS;
import com.gat.feature.main.MainActivity;
import com.gat.feature.register.update.location.AddLocationActivity;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistanceActivity;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by mryit on 3/26/2017.
 */

public class SuggestionFragment extends ScreenFragment<SuggestionScreen, SuggestionPresenter>
        implements EasyPermissions.PermissionCallbacks {
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1986;

    @BindView(R.id.image_button_search)
    ImageButton textViewTitle;

    @BindView(R.id.recycler_view_most_borrowing)
    RecyclerView mRecyclerViewMostBorrowing;

    @BindView(R.id.recycler_view_suggest_books)
    RecyclerView mRecyclerViewSuggestBooks;

    @BindView(R.id.ll_user_near_suggest)
    LinearLayout llUserNearSuggest;

    private CompositeDisposable disposables;
    private BookSuggestAdapter mMostBorrowingAdapter;
    private BookSuggestAdapter mBookSuggestAdapter;

    private TrackGPS gps;
    private List<UserNearByDistance> mListUserDistance;
    private LatLng currentLatLng;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_suggestion;
    }

    @Override
    protected SuggestionScreen getDefaultScreen() {
        return SuggestionScreen.instance();
    }

    @Override
    protected Class<SuggestionPresenter> getPresenterClass() {
        return SuggestionPresenter.class;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // setup recycler view linear
        mRecyclerViewMostBorrowing.setHasFixedSize(true);
        mRecyclerViewMostBorrowing.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewMostBorrowing.setNestedScrollingEnabled(false);

        mRecyclerViewSuggestBooks.setHasFixedSize(true);
        mRecyclerViewSuggestBooks.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewSuggestBooks.setNestedScrollingEnabled(false);

        disposables = new CompositeDisposable(
                getPresenter().onTopBorrowingSuccess().subscribe(this::onTopBorrowingSuccess),
                getPresenter().onBookSuggestSuccess().subscribe(this::onSuggestBooksSuccess),
                getPresenter().onPeopleNearByUserSuccess().subscribe(this::onPeopleNearByUserByDistanceSuccess),
                getPresenter().onError().subscribe(this::onError)
        );

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        getPresenter().suggestMostBorrowing();
        getPresenter().suggestBooks();
        processLocationToUpdateUserShareNearByDistance();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }


    @OnClick(R.id.image_button_search)
    void onSearchButtonTap() {
        // TODO start search activity
    }

    @OnClick(R.id.button_more_sharing_near)
    void onMoreSharingNearTap() {
        Intent intent = new Intent(mContext, ShareNearByUserDistanceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ShareNearByUserDistanceActivity.PASS_LIST_USER_DISTANCE,
                (ArrayList<? extends Parcelable>) mListUserDistance);
        bundle.putDouble(ShareNearByUserDistanceActivity.PASS_USER_LOCATION_LATITUDE, currentLatLng.latitude);
        bundle.putDouble(ShareNearByUserDistanceActivity.PASS_USER_LOCATION_LONGITUDE, currentLatLng.longitude);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    void onPeopleNearByUserByDistanceSuccess(List<UserNearByDistance> list) {
        MZDebug.d("onPeopleNearByUserByDistance Success");

        if (llUserNearSuggest.getChildCount() > 0) {
            return;
        }

        mListUserDistance = list; // use this to pass along to ShareNearByUserDistanceActivity
        View viewItem;
        UserNearByDistance userItem;
        int size = list.size() < 5 ? list.size() : 5;

        for (int i = 0; i < size; i++) {
            userItem = list.get(i);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewItem = inflater.inflate(R.layout.item_user_near_suggest, llUserNearSuggest, false);

            // init view
            ImageView imageViewAvatar = (ImageView) viewItem.findViewById(R.id.iv_people_near_suggest_avatar);
            TextView textViewName = (TextView) viewItem.findViewById(R.id.tv_people_near_suggest_name);

            // set data
            if ( ! userItem.getImageId().isEmpty()) {
                Glide.with(getActivity()).
                        load("http://gatbook-api-v1.azurewebsites.net/api/common/get_image/"
                                + userItem.getImageId() + "?size=t").into(imageViewAvatar);
            }
            textViewName.setText(userItem.getName());
            imageViewAvatar.setTag(userItem); // pass object -> tag

            imageViewAvatar.setOnClickListener(view -> {
                UserNearByDistance item = (UserNearByDistance) view.getTag();
                Toast.makeText(getActivity(), "User id: " + item.getUserId(), Toast.LENGTH_SHORT).show();
            });

            // add child view
            llUserNearSuggest.addView(viewItem);
        }
    }

    void onTopBorrowingSuccess(List<Book> list) {
        // setup adapter
        if (list == null) {
            MZDebug.w("LIST onTopBorrowingSuccess = NULL");
            return;
        }

        mMostBorrowingAdapter = new BookSuggestAdapter(mContext, list);
        mRecyclerViewMostBorrowing.setAdapter(mMostBorrowingAdapter);
    }

    void onSuggestBooksSuccess(List<Book> list) {
        // setup adapter
        mBookSuggestAdapter = new BookSuggestAdapter(getActivity(), list);
        mRecyclerViewSuggestBooks.setAdapter(mBookSuggestAdapter);
    }

    void onError(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(PERMISSION_ACCESS_COARSE_LOCATION)
    private void processLocationToUpdateUserShareNearByDistance() {
        MZDebug.w("processLocationToUpdateUserShareNearByDistance");
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Have permission, do the thing!
            processUserNearByDistance();
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, "Location permission is required! Ops! Ops!",
                    PERMISSION_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        MZDebug.w("onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        MZDebug.w("onPermissionsDenied");
    }

    private void processUserNearByDistance() {
        if (gps == null) {
            gps = new TrackGPS(getActivity());
        }
        if (!gps.canGetLocation()) {
            Toast.makeText(mContext, "Please, Enable location.", Toast.LENGTH_SHORT).show();
            MZDebug.w("gps can not get location");
            return;
        }
        double longitude = gps.getLongitude();
        double latitude = gps.getLatitude();
        MZDebug.i("longitude: " + longitude + ", longitude: " + latitude);
        currentLatLng = new LatLng(latitude, longitude);

        LatLng neLocation = new LatLng(latitude - 20, longitude - 20);
        LatLng wsLocation = new LatLng(latitude + 20, longitude + 20);

        getPresenter().getPeopleNearByUser(currentLatLng, neLocation, wsLocation);
    }

}
