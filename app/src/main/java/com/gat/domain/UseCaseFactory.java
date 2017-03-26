package com.gat.domain;

import android.support.annotation.Nullable;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.Callable;

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
}
