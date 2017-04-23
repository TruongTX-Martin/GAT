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
import com.gat.domain.usecase.UseCase;
import com.gat.feature.editinfo.entity.EditInfoInput;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.LoginData;
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

    UseCase<User> register(LoginData data);

    UseCase<LoginData> getLoginData();

    UseCase<ServerResponse> sendRequestResetPassword(String email);

    UseCase<ServerResponse> verifyResetToken(String code);

    UseCase<ServerResponse> resetPassword(String password);

    UseCase<ServerResponse> updateLocation(String address, LatLng location);

    UseCase<ServerResponse> updateCategories(List<Integer> categories);

    UseCase<Book> getBookByIsbn(String isbn);

    <T, R> UseCase<R> transform(UseCase<T> useCase, ObservableTransformer<T, R> transformer, @Nullable Scheduler transformScheduler);

    <T> UseCase<T> doWork(Callable<T> callable);

    UseCase<List<BookResponse>> suggestMostBorrowing();

    UseCase<List<BookResponse>> suggestBooks();

    UseCase<List<BookResponse>> suggestBooksAfterLogin();

    UseCase<List<UserNearByDistance>> peopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation, int page, int sizeOfPage);

    UseCase<DataResultListResponse<BookResponse>> searchBookByTitle(String title, long userId, int page, int sizeOfPage);

    UseCase<DataResultListResponse<BookResponse>> searchBookByAuthor(String author, long userId, int page, int sizeOfPage);

    UseCase<DataResultListResponse<UserResponse>> searchUser(String name, int page, int sizeOfPage);

    UseCase<List<String>> getBooksSearchedKeyword();

    UseCase<List<String>> getAuthorsSearchedKeyword();

    UseCase<List<String>> getUsersSearchedKeyword();

    UseCase<Data> getBookInstance(BookInstanceInput input);

    UseCase<Data> changeBookSharingStatus(BookChangeStatusInput input);

    UseCase<Data> getReadingBooks(BookReadingInput input);

    UseCase<Data> getBookRequest(BookRequestInput input);

    UseCase<Data<User>> getUserInfo();

    UseCase<Data> updateInfo(EditInfoInput input);

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


}
