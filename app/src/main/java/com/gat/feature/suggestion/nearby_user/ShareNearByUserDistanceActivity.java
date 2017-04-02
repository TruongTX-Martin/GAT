package com.gat.feature.suggestion.nearby_user;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.app.screen.Screen;
import com.rey.mvp2.PresenterManager;
import com.rey.mvp2.impl.MvpActivity;

/**
 * Created by mozaa on 30/03/2017.
 */

public class ShareNearByUserDistanceActivity extends ScreenActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_share_near_by_user_distance;
    }

    @Override
    protected Class getPresenterClass() {
        return ShareNearByUserDistancePresenter.class;
    }






}
