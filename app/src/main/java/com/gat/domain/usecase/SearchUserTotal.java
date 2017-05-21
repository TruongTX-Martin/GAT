package com.gat.domain.usecase;

import com.gat.data.response.DataResultListResponse;
import com.gat.repository.UserRepository;
import io.reactivex.Observable;

/**
 * Created by mryit on 5/21/2017.
 */

public class SearchUserTotal  extends UseCase<DataResultListResponse> {

    private final UserRepository userRepository;
    private final String name;
    private final int userId;

    public SearchUserTotal(UserRepository userRepository, String name, int userId) {
        this.userRepository = userRepository;
        this.name = name;
        this.userId = userId;
    }

    @Override
    protected Observable<DataResultListResponse> createObservable() {
        return userRepository.searchUserTotal(name, userId);
    }


}
