package com.gat.domain.impl;

import android.support.annotation.Nullable;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.GetGroupList;
import com.gat.domain.usecase.*;
import com.gat.domain.usecase.ChangeBookSharingStatus;
import com.gat.domain.usecase.GetBookInstance;
import com.gat.domain.usecase.GetBookRequest;
import com.gat.domain.usecase.GetLoginData;
import com.gat.domain.usecase.GetMessageList;
import com.gat.domain.usecase.GetReadingBooks;
import com.gat.domain.usecase.GetUser;
import com.gat.domain.usecase.LoadMoreGroup;
import com.gat.domain.usecase.LoadMoreMessage;
import com.gat.domain.usecase.Login;
import com.gat.domain.usecase.GetPersonalData;
import com.gat.domain.usecase.Register;
import com.gat.domain.usecase.ResetPassword;
import com.gat.domain.usecase.SearchBookByIsbn;
import com.gat.domain.usecase.SearchBookByKeyword;
import com.gat.domain.usecase.SendMessage;
import com.gat.domain.usecase.SendRequestResetPassword;
import com.gat.domain.usecase.TransformUseCase;
import com.gat.domain.usecase.UpdateCategory;
import com.gat.domain.usecase.UpdateLocation;
import com.gat.domain.usecase.UseCase;
import com.gat.domain.usecase.VerifyResetToken;
import com.gat.domain.usecase.WorkUseCase;
import com.gat.feature.personal.entity.BookChangeStatusInput;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personal.entity.BookRequestInput;
import com.gat.repository.BookRepository;
import com.gat.repository.MessageRepository;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.Message;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.Callable;

import dagger.Lazy;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

/**
 * Created by Rey on 2/13/2017.
 */

public class UseCaseFactoryImpl implements UseCaseFactory {

    private final Lazy<BookRepository> bookRepositoryLazy;
    private final Lazy<UserRepository> userRepositoryLazy;
    private final Lazy<MessageRepository> messageRepositoryLazy;

    public UseCaseFactoryImpl(Lazy<BookRepository> bookRepositoryLazy,
                              Lazy<UserRepository> userRepositoryLazy,
                              Lazy<MessageRepository> messageRepositoryLazy){
        this.bookRepositoryLazy = bookRepositoryLazy;
        this.userRepositoryLazy = userRepositoryLazy;
        this.messageRepositoryLazy = messageRepositoryLazy;
    }

    @Override
    public UseCase<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage) {
        return new SearchBookByKeyword(bookRepositoryLazy.get(), keyword, page, sizeOfPage);
    }

    @Override
    public UseCase<Book> getBookByIsbn(String isbn) {
        return new SearchBookByIsbn(bookRepositoryLazy.get(), isbn);
    }

    @Override
    public UseCase<List<Message>> getMessageList(String userId, int page, int size) {
        return new GetMessageList(messageRepositoryLazy.get(), userId, page, size);
    }

    @Override
    public UseCase<List<Group>> getGroupList(int page, int size) {
        return new GetGroupList(messageRepositoryLazy.get(), page, size);
    }

    @Override
    public UseCase<Group> groupUpdate() {
        return new GroupUpdate(messageRepositoryLazy.get());
    }

    @Override
    public UseCase<Boolean> sendMessage(String toUserId, String message) {
        return new SendMessage(messageRepositoryLazy.get(), message, toUserId);
    }

    @Override
    public UseCase<User> getUser() {
        return new GetUser(userRepositoryLazy.get());
    }

    @Override
    public UseCase<User> login(LoginData data) {
        return new Login(userRepositoryLazy.get(), data);
    }

    @Override
    public UseCase<LoginData> getLoginData() {
        return new GetLoginData(userRepositoryLazy.get());
    }

    @Override
    public UseCase<ServerResponse> sendRequestResetPassword(String email) {
        return new SendRequestResetPassword(userRepositoryLazy.get(), email);
    }

    @Override
    public UseCase<ServerResponse> verifyResetToken(String code) {
        return new VerifyResetToken(userRepositoryLazy.get(), code);
    }

    @Override
    public UseCase<ServerResponse> resetPassword(String password) {
        return new ResetPassword(userRepositoryLazy.get(), password);
    }

    @Override
    public UseCase<User> register(LoginData data) {
        return new Register(userRepositoryLazy.get(), data);
    }

    @Override
    public UseCase<ServerResponse> updateLocation(String address, LatLng location) {
        return new UpdateLocation(userRepositoryLazy.get(), address, location);
    }

    @Override
    public UseCase<ServerResponse> updateCategories(List<Integer> categories) {
        return new UpdateCategory(userRepositoryLazy.get(), categories);
    }


    @Override
    public <T, R> UseCase<R> transform(UseCase<T> useCase, ObservableTransformer<T, R> transformer, @Nullable Scheduler transformScheduler) {
        TransformUseCase transformUseCase = new TransformUseCase<>(useCase, transformer);
        if (transformScheduler != null)
            transformUseCase.transformOn(transformScheduler);
        return transformUseCase;
    }

    @Override
    public <T> UseCase<T> doWork(Callable<T> callable) {
        return new WorkUseCase<>(callable);
    }

    @Override
    public UseCase<List<BookResponse>> suggestMostBorrowing() {
        return new SuggestMostBorrowing(bookRepositoryLazy.get());
    }

    @Override
    public UseCase<List<BookResponse>> suggestBooks() {
        return new SuggestBooks(bookRepositoryLazy.get());
    }

    @Override
    public UseCase<List<BookResponse>> suggestBooksAfterLogin() {
        return new SuggestBooksAfterLogin(bookRepositoryLazy.get());
    }

    @Override
    public UseCase<List<UserNearByDistance>> peopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation, int page, int sizeOfPage) {
        return new PeopleNearByUser(userRepositoryLazy.get(), userLocation, neLocation, wsLocation, page, sizeOfPage);
    }

    @Override
    public UseCase<DataResultListResponse<BookResponse>> searchBookByTitle(String title, long userId, int page, int sizeOfPage) {
        return new SearchBookByTitle(bookRepositoryLazy.get(), title, userId, page, sizeOfPage);
    }

    @Override
    public UseCase<DataResultListResponse<BookResponse>> searchBookByAuthor(String author, long userId, int page, int sizeOfPage) {
        return new SearchBookByAuthor(bookRepositoryLazy.get(), author, userId, page, sizeOfPage);
    }

    @Override
    public UseCase<DataResultListResponse<UserResponse>> searchUser(String name, int page, int sizeOfPage) {
        return new SearchUser(userRepositoryLazy.get(), name, page, sizeOfPage);
    }

    @Override
    public UseCase<List<String>> getBooksSearchedKeyword() {
        return new GetBooksSearchedKeyword(bookRepositoryLazy.get());
    }

    @Override
    public UseCase<List<String>> getAuthorsSearchedKeyword() {
        return new GetAuthorsSearchedKeyword(bookRepositoryLazy.get());
    }

    @Override
    public UseCase<List<String>> getUsersSearchedKeyword() {
        return new GetUsersSearchedKeyword(userRepositoryLazy.get());
    }

    @Override
    public UseCase<Data> getBookInstance(BookInstanceInput input) {
        return new GetBookInstance(userRepositoryLazy.get(), input);
    }

    @Override
    public UseCase<Data> changeBookSharingStatus(BookChangeStatusInput input) {
        return new ChangeBookSharingStatus(userRepositoryLazy.get(), input);
    }

    @Override
    public UseCase<Data> getReadingBooks(BookReadingInput input) {
        return new GetReadingBooks(userRepositoryLazy.get(), input);
    }

    @Override
    public UseCase<Data> getBookRequest(BookRequestInput input) {
        return new GetBookRequest(userRepositoryLazy.get(), input);
    }

    @Override
    public UseCase<Data<User>> getUserInfo() {
        return new GetPersonalData(userRepositoryLazy.get());
    }
}
