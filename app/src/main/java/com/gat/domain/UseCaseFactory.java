package com.gat.domain;

import android.support.annotation.Nullable;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.BorrowResponse;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.bookdetailsender.entity.ChangeStatusResponse;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personal.entity.RequestStatusInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.FirebasePassword;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.Message;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

/**
 * Created by Rey on 2/13/2017.
 */
public interface UseCaseFactory {

    UseCase<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage);

    UseCase<User> getUser();

    UseCase<User> login(LoginData data);

    UseCase<Boolean> loginFirebase();

    UseCase<User> register(LoginData data);

    UseCase<LoginData> getLoginData();

    UseCase<ServerResponse> sendRequestResetPassword(String email);

    UseCase<ServerResponse> verifyResetToken(String code);

    UseCase<ServerResponse> resetPassword(String password);

    UseCase<ServerResponse> updateLocation(String address, LatLng location);

    UseCase<ServerResponse> updateCategories(List<Integer> categories);

    UseCase<Integer> getBookByIsbn(String isbn);

    UseCase<List<Message>> getMessageList(int userId, int page, int size);

    UseCase<List<Group>> getGroupList(int page, int size);

    UseCase<Integer> getUnReadGroupMessageCnt();

    UseCase<Group> groupUpdate();

    UseCase<Message> messageUpdate(int userId);

    UseCase<Boolean> sendMessage(int toUserId, String message);

    UseCase<Boolean> sawMessage(String groupId, long timeStamp);

    UseCase<Boolean> makeNewGroupChat(int userId);

    <T, R> UseCase<R> transform(UseCase<T> useCase, ObservableTransformer<T, R> transformer, @Nullable Scheduler transformScheduler);

    <T> UseCase<T> doWork(Callable<T> callable);

    UseCase<List<BookResponse>> suggestMostBorrowing();

    UseCase<List<BookResponse>> suggestBooks();

    UseCase<List<BookResponse>> suggestBooksAfterLogin();

    UseCase<DataResultListResponse<UserNearByDistance>> peopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation, int page, int sizeOfPage);

    UseCase<DataResultListResponse<BookResponse>> searchBookByTitle(String title, long userId, int page, int sizeOfPage);

    UseCase<DataResultListResponse<BookResponse>> searchBookByAuthor(String author, long userId, int page, int sizeOfPage);

    UseCase<DataResultListResponse<UserResponse>> searchUser(String name, int page, int sizeOfPage);

    UseCase<List<Keyword>> getBooksSearchedKeyword();

    UseCase<List<Keyword>> getAuthorsSearchedKeyword();

    UseCase<List<Keyword>> getUsersSearchedKeyword();

    UseCase<Data> getBookInstance(BookInstanceInput input);

    UseCase<String> changeBookSharingStatus(BookChangeStatusInput input);

    UseCase<Data> getReadingBooks(BookReadingInput input);

    UseCase<Data> getBookRequest(BookRequestInput input);

    UseCase<Data<User>> getUserInfo();

    UseCase<String> updateInfo(EditInfoInput input);

    UseCase<Data> getBookUserSharing(BookSharingUserInput input);

    UseCase<Data> getBookDetail(Integer input);

    UseCase<BookInfo> getBookInfo (int editionId);

    UseCase<List<EvaluationItemResponse>> getBookEditionEvaluation (int editionId);

    UseCase<BookReadingInfo> getReadingStatus(int editionId);

    UseCase<EvaluationItemResponse> getBookEvaluationByUser (int editionId);

    UseCase<List<UserResponse>> getEditionSharingUser (int editionId);

    UseCase<ServerResponse> postComment(int editionId, int value, String review, boolean spoiler);

    UseCase <BookInstanceInfo> getSelfInstanceInfo (int editionId);

    UseCase<ServerResponse> selfAddInstance (int editionId, int sharingStatus, int numberOfBook);

    UseCase<ServerResponse> selfUpdateReadingStatus (int editionId, int readingStatus);

    UseCase<BorrowResponse> requestBorrow (int editionId, int ownerId);

    UseCase<DataResultListResponse<NotifyEntity>> getUserNotification (int page, int per_page);
    UseCase<ChangeStatusResponse> requestBookByBorrower(RequestStatusInput input);
    UseCase<ChangeStatusResponse> requestBookByOwner(RequestStatusInput input);

    UseCase<Data> requestBorrowBook(BorrowRequestInput input);

    UseCase<User> getVisitorInfor(int userId);

    UseCase<ServerResponse> unlinkSocialAccount (int socialType);

    UseCase<ServerResponse> linkSocialAccount (String socialID, String socialName , int socialType);

    UseCase<ServerResponse<FirebasePassword>> addEmailPassword (String email, String password);

    UseCase<ServerResponse> changeOldPassword(String newPassword, String oldPassword);

    UseCase<Boolean> signOut ();

    UseCase<String> removeBook (int instanceId);

}
