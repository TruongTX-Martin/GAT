package com.gat.domain.usecase;

import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Data;

import io.reactivex.Observable;

/**
 * Created by root on 19/04/2017.
 */

public class EditInfo extends UseCase<String> {

    private UserRepository userRepository;
    private EditInfoInput input;
    public EditInfo(UserRepository userRepository, EditInfoInput input) {
        this.userRepository = userRepository;
        this.input = input;
    }

    @Override
    protected Observable<String> createObservable() {
        return  userRepository.updateUserInfo(input);
    }



}
