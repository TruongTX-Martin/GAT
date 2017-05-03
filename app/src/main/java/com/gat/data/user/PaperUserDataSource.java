package com.gat.data.user;

import android.location.Address;
import android.util.Log;

import com.gat.common.util.Strings;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.LoginResponseData;
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
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;
import io.reactivex.Observable;

/**
 * Created by Rey on 2/23/2017.
 * An implementation of UserDataSource that use PaperDb as local database.
 */
public class PaperUserDataSource implements UserDataSource {
    private static final String TAG = PaperUserDataSource.class.getSimpleName();

    private static final String BOOK        = "user";
    private static final String KEY_USER    = "user";
    private static final String KEY_LOGINDATA = "loginData";
    private static final String KEY_RESET_TOKEN = "resetToken";
    private static final String KEY_VERIFY_TOKEN = "verifiedToken";
    private static final String KEY_LOGIN_TOKEN = "loginToken";
    private final Book book = Paper.book(BOOK);

    @Override
    public Observable<User> loadUser() {
        return Observable.fromCallable(() -> book.read(KEY_USER, User.NONE));
    }

    @Override
    public Observable<User> persitUser(User user) {
        return Observable.fromCallable(() -> {
            book.write(KEY_USER, user);
            return user;
        });
    }

    @Override
    public Observable<LoginData> loadLoginData() {
        Log.d(TAG, "loadLoginData");
        return Observable.fromCallable(()-> {
            LoginData loginData = book.read(KEY_LOGINDATA, LoginData.EMPTY);
            return loginData;
        });
    }

    @Override
    public void saveLoginData(LoginData loginData) {
        book.write(KEY_LOGINDATA, loginData);
        return;
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> storeResetToken(ServerResponse<ResetPasswordResponseData> data) {
        return Observable.fromCallable(() -> {
            book.write(KEY_RESET_TOKEN, data);
            return data;
        });
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> getResetToken() {
        return Observable.fromCallable(() -> book.read(KEY_RESET_TOKEN));
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> storeVerifyToken(ServerResponse<VerifyTokenResponseData> data) {
        return Observable.fromCallable(() -> {
            book.write(KEY_VERIFY_TOKEN, data);
            return data;
        });
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> getVerifyToken() {
        return Observable.fromCallable(() -> book.read(KEY_VERIFY_TOKEN));
    }

    @Override
    public void storeLoginToken(String loginToken) {
        book.write(KEY_LOGIN_TOKEN, loginToken);
        return;
    }

    @Override
    public Observable<String> getLoginToken() {
        return Observable.fromCallable(() -> book.read(KEY_LOGIN_TOKEN, Strings.EMPTY));
    }


    @Override
    public Observable<User> getUserInformation(int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<User>> getListUserInfo(List<Integer> userIdList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> login(LoginData data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> register(LoginData loginData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<ResetPasswordResponseData>> sendRequestResetPassword(String email) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<VerifyTokenResponseData>> verifyToken(String code, String tokenReset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse<LoginResponseData>> changePassword(String password, String tokenVerified) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<Address>> getAddress(LatLng location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse> updateLocation(String address, float longitude, float latitude) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<ServerResponse> updateCategories(List<Integer> categories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<UserNearByDistance>> getPeopleNearByUserByDistance(float currentLongitude, float currentLatitude, float neLongitude, float neLatitude, float wsLongitude, float wsLatitude, int page, int size_of_page) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<DataResultListResponse<UserResponse>> searchUser(String name, int page, int sizeOfPage) {
        return null;
    }
    @Override
    public Observable<Data> getBookRequest(BookRequestInput instanceInput) {
        return null;
    }

    @Override
    public Observable<Data> changeBookSharingStatus(BookChangeStatusInput input) {
        return null;
    }

    @Override
    public Observable<Data> getReadingBook(BookReadingInput input) {
        return null;
    }

    @Override
    public Observable<String> updateUserInfo(EditInfoInput input) {
        return null;
    }

    @Override
    public Observable<Data> getBookUserSharing(BookSharingUserInput input) {
        return null;
    }

    @Override
    public Observable<Data> getBookDetail(Integer input) {
        return null;
    }

    @Override
    public Observable<ChangeStatusResponse> requestBookByBorrower(RequestStatusInput input) {
        return null;
    }

    @Override
    public Observable<ChangeStatusResponse> requestBookByOwner(RequestStatusInput input) {
        return null;
    }

    @Override
    public Observable<List<String>> getUsersSearchedKeyword() {
        return null;
    }

    @Override
    public Observable<Data<User>> getPersonalInfo() {
        return null;
    }

    @Override
    public Observable<Data> getBookInstance(BookInstanceInput instanceInput) {
        return null;
    }
}
