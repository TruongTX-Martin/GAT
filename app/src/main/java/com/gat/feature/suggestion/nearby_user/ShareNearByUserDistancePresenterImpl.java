package com.gat.feature.suggestion.nearby_user;

import com.gat.common.util.MZDebug;
import com.gat.data.response.DataResultListResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistancePresenterImpl implements ShareNearByUserDistancePresenter {

    private static final int SIZE_OF_PAGE = 10;
    private int mCurrentPage;
    private boolean isCanLoadMore;
    private List<UserNearByDistance> mListUsers;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    UseCase<DataResultListResponse<UserNearByDistance>> useCaseUserNear;
    Subject<DataResultListResponse<UserNearByDistance>> resultUserNearSubject;
    Subject<DataResultListResponse<UserNearByDistance>> subjectLoadMoreSuccess;

    private final Subject<Boolean> subjectCanLoadMore;
    private final Subject<String> errorSubject;

    public ShareNearByUserDistancePresenterImpl (
            UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {

        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        subjectLoadMoreSuccess = PublishSubject.create();
        resultUserNearSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
        subjectCanLoadMore = PublishSubject.create();
    }

    @Override
    public void onCreate() {
        mCurrentPage = 1;
        mListUsers = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        this.userLocation = null;
        this.neLocation = null;
        this.wsLocation = null;
    }

    private LatLng userLocation;
    private LatLng neLocation;
    private LatLng wsLocation;
    @Override
    public void requestUserNearOnTheMap(LatLng userLocation, LatLng neLocation, LatLng wsLocation) {
        this.userLocation = userLocation;
        this.neLocation = neLocation;
        this.wsLocation = wsLocation;
        mCurrentPage = 1; // Mỗi lần lấy thì thì current page phải = 1

        useCaseUserNear = useCaseFactory.peopleNearByUser(userLocation, neLocation, wsLocation, mCurrentPage, SIZE_OF_PAGE);
        useCaseUserNear.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    resultUserNearSubject.onNext(data);
                    // nếu tổng mà lớn hơn trang hiện tại * size trang thì có thể load more
                    if (data.getTotalResult() > (mCurrentPage *SIZE_OF_PAGE)) {
                        subjectCanLoadMore.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("_______________________requestUserNearOnTheMap____onError_________");
                    errorSubject.onNext("Failed");
                }).execute();
    }

    @Override
    public Observable<DataResultListResponse<UserNearByDistance>> onPeopleNearByUserSuccess() {
        return resultUserNearSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void requestLoadMoreUser() {
        mCurrentPage += 1; // tang page len 1 khi load more
        MZDebug.w("Page = " + mCurrentPage);
        useCaseUserNear = useCaseFactory.peopleNearByUser(userLocation, neLocation, wsLocation, mCurrentPage, SIZE_OF_PAGE);
        useCaseUserNear.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    subjectLoadMoreSuccess.onNext(data);

                    // nếu tổng mà lớn hơn trang hiện tại * size trang thì có thể load more
                    if (data.getTotalResult() > (mCurrentPage *SIZE_OF_PAGE)) {
                        subjectCanLoadMore.onNext(true);
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("_______________________requestUserNearOnTheMap____onError_________");
                    errorSubject.onNext("Failed");
                }).execute();
    }

    @Override
    public Observable<DataResultListResponse<UserNearByDistance>> onLoadMoreUserSuccess() {
        return subjectLoadMoreSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<Boolean> onCanLoadMore() {
        return subjectCanLoadMore.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }
}
