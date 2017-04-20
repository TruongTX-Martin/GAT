package com.gat.repository;

import android.location.Address;

import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/23/2017.
 */

public interface UserRepository {

    Observable<User> getUser();

    Observable<User> login(LoginData data);

    Observable<User> login();

    Observable<LoginData> getLoginData();

    Observable<User> register(LoginData data);

    Observable<ServerResponse> sendRequestResetPassword(String email);

    Observable<ServerResponse> verifyToken(String code);

    Observable<ServerResponse> changePassword(String password);

    Observable<List<Address>> getAddress(LatLng location);

    Observable<ServerResponse> updateLocation(String address, LatLng location);
    Observable<ServerResponse> updateCategories(List<Integer> categories);

    Observable<List<UserNearByDistance>>
            getPeopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation, int page, int sizeOfPage);

    Observable<DataResultListResponse<UserResponse>>
            searchUser (String name, int page, int sizeOfPage);

    Observable<List<String>> getUsersSearchedKeyword();

    Observable<Data> getPersonalData();
    Observable<Data> getBookRequest(BookRequestInput input);
    Observable<Data> changeBookSharingStatus(BookChangeStatusInput input);
    Observable<Data> getReadingBooks(BookReadingInput input);
    Observable<Data> getBookInstance(BookInstanceInput input);
    Observable<Data> updateUserInfo(EditInfoInput input);

    Observable<Data> getBookUserSharing(BookSharingUserInput input);

}
