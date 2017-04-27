package com.gat.domain.usecase;

import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by mryit on 4/27/2017.
 */

public class GetUserNotifications extends UseCase<DataResultListResponse<NotifyEntity>> {

    private final UserRepository userRepository;
    private final int page;
    private final int perPage;

    public GetUserNotifications(UserRepository userRepository, int page, int perPage) {
        this.userRepository = userRepository;
        this.page = page;
        this.perPage = perPage;
    }

    @Override
    protected Observable<DataResultListResponse<NotifyEntity>> createObservable() {
        return userRepository.getUserNotification(page,perPage);
    }
}
