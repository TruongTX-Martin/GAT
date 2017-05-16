package com.gat.domain.usecase;

import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.UserResponse;
import com.gat.repository.UserRepository;

import io.reactivex.Observable;

/**
 * Created by mozaa on 11/04/2017.
 */

public class SearchUser extends UseCase<DataResultListResponse<UserResponse>> {

    private final UserRepository userRepository;
    private final String name;
    private final int userId;
    private final int page;
    private final int sizeOfPage;


    public SearchUser(UserRepository userRepository, String name, int userId, int page, int sizeOfPage) {
        this.userRepository = userRepository;
        this.name = name;
        this.userId = userId;
        this.page = page;
        this.sizeOfPage = sizeOfPage;
    }

    @Override
    protected Observable<DataResultListResponse<UserResponse>> createObservable() {
        return userRepository.searchUser(name, userId, page, sizeOfPage);
    }
}
