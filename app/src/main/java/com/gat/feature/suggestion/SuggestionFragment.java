package com.gat.feature.suggestion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.gat.data.response.BookResponse;
import com.gat.feature.main.MainActivity;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistanceActivity;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistanceScreen;
import com.gat.feature.suggestion.search.SuggestSearchActivity;
import com.gat.feature.suggestion.search.SuggestSearchScreen;
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
    private List<BookResponse> mListBookMostBorrowing;
    private List<BookResponse> mListBookSuggest;

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
        mListBookMostBorrowing = new ArrayList<>();
        mListBookSuggest = new ArrayList<>();
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

        if (mListBookMostBorrowing.isEmpty()) {
            getPresenter().suggestMostBorrowing();
        }
        if (mListBookSuggest.isEmpty()) {
            getPresenter().suggestBooks();
        }

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

    @OnClick(R.id.button_go_setting)
    void onButtonGoSettingToEnablePermissionTap() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @OnClick(R.id.image_button_search)
    void onSearchButtonTap() {
        MainActivity.start(mContext, SuggestSearchActivity.class, SuggestSearchScreen.instance());
    }

    @OnClick(R.id.button_more_sharing_near)
    void onMoreSharingNearTap() {

        MainActivity.start(mContext, ShareNearByUserDistanceActivity.class, ShareNearByUserDistanceScreen.instance());

//        getActivity().finish();
//
//        Intent intent = new Intent(mContext, ShareNearByUserDistanceActivity.class);
//        Bundle bundle = new Bundle();
//
//        intent.putExtra(EXTRA_SCREEN, new ParcelableScreen(ShareNearByUserDistanceScreen.instance()))
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        bundle.putParcelableArrayList(ShareNearByUserDistanceActivity.PASS_LIST_USER_DISTANCE,
//                (ArrayList<? extends Parcelable>) mListUserDistance);
//        bundle.putDouble(ShareNearByUserDistanceActivity.PASS_USER_LOCATION_LATITUDE, currentLatLng.latitude);
//        bundle.putDouble(ShareNearByUserDistanceActivity.PASS_USER_LOCATION_LONGITUDE, currentLatLng.longitude);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    void onPeopleNearByUserByDistanceSuccess(List<UserNearByDistance> list) {
        MZDebug.i("_________________________________________ onPeopleNearByUserByDistance Success");
        if (llUserNearSuggest.getChildCount() > 0) {
            return;
        }

        if (null == list || list.isEmpty()) {
            TextView textView = new TextView(mContext);
            textView.setText(mContext.getResources().getString(R.string.msg_no_user_near));
            textView.setWidth(llUserNearSuggest.getWidth());
            textView.setHeight(llUserNearSuggest.getHeight());

            llUserNearSuggest.addView(textView);
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
            if ( null != userItem.getImageId() && ! userItem.getImageId().isEmpty()) {
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

    void onTopBorrowingSuccess(List<BookResponse> list) {
        mListBookMostBorrowing.addAll(list);
        if (null == mMostBorrowingAdapter) {
            mMostBorrowingAdapter = new BookSuggestAdapter(mContext, mListBookMostBorrowing);
            mRecyclerViewMostBorrowing.setAdapter(mMostBorrowingAdapter);
            return;
        }
        mMostBorrowingAdapter.notifyDataSetChanged();
    }

    void onSuggestBooksSuccess(List<BookResponse> list) {
        mListBookSuggest.addAll(list);
        if (null == mBookSuggestAdapter) {
            mBookSuggestAdapter = new BookSuggestAdapter(getActivity(), mListBookSuggest);
            mRecyclerViewSuggestBooks.setAdapter(mBookSuggestAdapter);
            return;
        }
        mBookSuggestAdapter.notifyDataSetChanged();
    }

    void onError(String message) {
        MZDebug.e(message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(PERMISSION_ACCESS_COARSE_LOCATION)
    private void processLocationToUpdateUserShareNearByDistance() {
        MZDebug.i("_______________________________ processLocationToUpdateUserShareNearByDistance");
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {

            MZDebug.w("onPermissionsDenied -_- ");
            if (gps == null) {
                gps = new TrackGPS(getActivity());
            }
            if (gps.isGPSAvailable()) {
                // Have permission, remove button request permission
                llUserNearSuggest.removeAllViews();

                // request list user near
                processUserNearByDistance();
            }
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, mContext.getResources().getString(R.string.msg_allow_location),
                    PERMISSION_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        MZDebug.w("onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void processUserNearByDistance() {
        double longitude = gps.getLongitude();
        double latitude = gps.getLatitude();
        MZDebug.i("longitude: " + longitude + ", longitude: " + latitude);
        currentLatLng = new LatLng(latitude, longitude);

        LatLng neLocation = new LatLng(latitude - 20, longitude - 20);
        LatLng wsLocation = new LatLng(latitude + 20, longitude + 20);

        getPresenter().getPeopleNearByUser(currentLatLng, neLocation, wsLocation);
    }

}
