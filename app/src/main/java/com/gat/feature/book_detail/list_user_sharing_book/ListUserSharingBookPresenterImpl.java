package com.gat.feature.book_detail.list_user_sharing_book;

import com.gat.common.util.MZDebug;
import com.gat.data.response.UserResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;

import java.util.List;

/**
 * Created by mozaa on 21/04/2017.
 */

public class ListUserSharingBookPresenterImpl implements ListUserSharingBookPresenter {


    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private List<UserResponse> mListUser;

    public ListUserSharingBookPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
    }

    @Override
    public void onCreate() {
        MZDebug.e("++++++++++++++++++++++++++++ SCREEN onCreate +++++++++++++++++++++++++++++++++");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setListUser(List<UserResponse> list) {
        mListUser = list;
    }



}
