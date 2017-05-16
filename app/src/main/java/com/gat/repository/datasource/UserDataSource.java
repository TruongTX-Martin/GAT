package com.gat.repository.datasource;

import android.location.Address;

import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.FirebasePassword;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/23/2017.
 */

public interface UserDataSource {

    Observable<User> loadUser();
    void persitUser(User user);

    Observable<ServerResponse<LoginResponseData>> login(LoginData data);
    Observable<LoginData> loadLoginData();
    void saveLoginData(LoginData loginData);
    void storeLoginToken(String token);
    Observable<String> getLoginToken();

    Observable<Boolean> signOut();

    Observable<Boolean> messageNotification(int receiver, String message);
    Observable<Boolean> registerFirebaseToken(String token);

    Observable<ServerResponse<LoginResponseData>> register(LoginData data);


    Observable<ServerResponse<ResetPasswordResponseData>> sendRequestResetPassword(String email);

    Observable<ServerResponse<ResetPasswordResponseData>> storeResetToken(String email, ServerResponse<ResetPasswordResponseData> data);

    Observable<String> getEmailLogin();

    Observable<ServerResponse<ResetPasswordResponseData>> getResetToken();

    Observable<ServerResponse<VerifyTokenResponseData>> verifyToken(String code, String tokenReset);
    Observable<ServerResponse<VerifyTokenResponseData>> storeVerifyToken(ServerResponse<VerifyTokenResponseData> data);
    Observable<ServerResponse<VerifyTokenResponseData>> getVerifyToken();
    Observable<ServerResponse<LoginResponseData>> changePassword(String password, String tokenVerified);


    Observable<List<Address>> getAddress(LatLng location);

    Observable<ServerResponse> updateLocation(String address, float longitude, float latitude);
    Observable<ServerResponse> updateCategories(List<Integer> categories);

    Observable<DataResultListResponse<UserNearByDistance>> getPeopleNearByUserByDistance
            (float currentLongitude, float currentLatitude,
             float neLongitude, float neLatitude,
             float wsLongitude, float wsLatitude,
             int page, int sizeOfPage);

    Observable<DataResultListResponse<UserResponse>>
    searchUser (String name, int userId, int page, int sizeOfPage);

    Observable<List<Keyword>> getUsersSearchedKeyword();


    Observable<Data<User>> getPersonalInfo();
    Observable<Data> getBookInstance(BookInstanceInput instanceInput);
    Observable<Data> getBookRequest(BookRequestInput instanceInput);
    Observable<String> changeBookSharingStatus(BookChangeStatusInput input);
    Observable<Data> getReadingBook(BookReadingInput input);

    Observable<User> getUserInformation(int userId);
    Observable<List<User>> getListUserInfo(List<Integer> userIdList);
    Observable<String> updateUserInfo(EditInfoInput input);
    Observable<Data> getBookUserSharing(BookSharingUserInput input);
    Observable<Data> getBookDetail(Integer input);
    Observable<DataResultListResponse<NotifyEntity>> getUserNotification (int page, int per_page);
    Observable<ChangeStatusResponse> requestBookByBorrower(RequestStatusInput input);
    Observable<ChangeStatusResponse> requestBookByOwner(RequestStatusInput input);
    Observable<Data> requestBorrowBook(BorrowRequestInput input);

    Observable<ServerResponse> unlinkSocialAccount(int socialType);
    Observable<ServerResponse> linkSocialAccount (String socialID, String socialName , int socialType);
    Observable<ServerResponse<FirebasePassword>> addEmailPassword (String email, String password);
    Observable<ServerResponse> changeOldPassword(String newPassword, String oldPassword);
    Observable<String> removeBook(int instanceId);


}
