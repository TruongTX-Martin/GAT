package com.gat.data.user;

import android.location.Address;
import android.util.Log;

import com.gat.common.util.Strings;
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
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.FirebasePassword;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
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
    private static final String EMAIL = "email";
    private static final String KEY_USER_LIST = "userList";

    private List<User> userList = new ArrayList<>();

    private final Book book = Paper.book(BOOK);

    @Override
    public Observable<User> loadUser() {
        return Observable.fromCallable(() -> book.read(KEY_USER, User.NONE));
    }

    @Override
    public void persitUser(User user) {
        if (user == null) {
            book.delete(KEY_USER);
        } else {
            book.write(KEY_USER, user);
        }
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
    public Observable<ServerResponse<ResetPasswordResponseData>> storeResetToken(String email, ServerResponse<ResetPasswordResponseData> data) {
        return Observable.fromCallable(() -> {
            book.write(KEY_RESET_TOKEN, data);
            book.write(EMAIL, email);
            return data;
        });
    }

    @Override
    public Observable<String> getEmailLogin() {
        return Observable.fromCallable(() -> book.read(EMAIL));
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
        if (loginToken != null)
            book.write(KEY_LOGIN_TOKEN, loginToken);
        else
            book.delete(KEY_LOGIN_TOKEN);
        return;
    }

    @Override
    public Observable<String> getLoginToken() {
        return Observable.fromCallable(() -> book.read(KEY_LOGIN_TOKEN, Strings.EMPTY));
    }

    @Override
    public Observable<Boolean> signOut() {
        return Observable.fromCallable(() -> {
            book.delete(KEY_USER);
            return true;
        });
    }

    @Override
    public Observable<Boolean> messageNotification(int receiver, String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Boolean> registerFirebaseToken(String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<List<User>> getListUserInfo(List<Integer> userIdList) {
        throw new UnsupportedOperationException();
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
    public Observable<DataResultListResponse<NotifyEntity>> getUserNotification(int page, int per_page) {
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
    public Observable<Data> requestBorrowBook(BorrowRequestInput input) {
        return null;
    }

    @Override
    public Observable<ServerResponse> unlinkSocialAccount(int socialType) {
        return null;
    }

    @Override
    public Observable<ServerResponse> linkSocialAccount(String socialID, String socialName, int socialType) {
        return null;
    }

    @Override
    public Observable<ServerResponse<FirebasePassword>> addEmailPassword(String email, String password) {
        return null;
    }

    @Override
    public Observable<ServerResponse> changeOldPassword(String newPassword, String oldPassword) {
        return null;
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
    public Observable<List<Keyword>> getUsersSearchedKeyword() {
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

    @Override
    public Observable<Data> getBookRequest(BookRequestInput instanceInput) {
        return null;
    }

    @Override
    public Observable<String> changeBookSharingStatus(BookChangeStatusInput input) {
        return null;
    }

    @Override
    public Observable<Data> getReadingBook(BookReadingInput input) {
        return null;
    }

    @Override
    public Observable<User> getUserInformation(int userId) {
        return null;
    }

}
