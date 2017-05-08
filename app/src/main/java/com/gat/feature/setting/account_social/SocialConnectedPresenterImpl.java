package com.gat.feature.setting.account_social;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;

/**
 * Created by mozaa on 05/05/2017.
 */

public class SocialConnectedPresenterImpl implements SocialConnectedPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    public SocialConnectedPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }
}
