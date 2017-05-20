package com.gat.dependency;

import com.gat.common.util.Strings;
import com.gat.data.api.GatApi;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ServerResponse;
import com.gat.repository.datasource.UserDataSource;
import com.gat.repository.entity.LoginData;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ducbtsn on 3/13/17.
 */

@Module
public class DataModule {

    private static final int TIME_OUT_SECONDS = 20;

    private UserDataSource userDataSource;
    private String language;
    private String url;
    GsonConverterFactory gsonConverterFactory;
    public DataModule(String url, String language, UserDataSource userDataSource) {
        this.userDataSource = userDataSource;
        this.language = language;
        this.url = url;
        gsonConverterFactory = GsonConverterFactory.create(
                new GsonBuilder()
                        .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                        .create());
    }
    @Provides
    @Singleton
    @Named("private")
    OkHttpClient providePrivateOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        try {
                            Request origin = chain.request();
                            Request.Builder requestBuilder = origin.newBuilder();
                            String loginToken = userDataSource.getLoginToken().blockingFirst();
                            if (!Strings.isNullOrEmpty(loginToken))
                                    requestBuilder.header("Authorization", loginToken);
                            if (!Strings.isNullOrEmpty(language))
                                requestBuilder.addHeader("Accept-Language", language);
                            Request request = requestBuilder.build();
                            okhttp3.Response response = chain.proceed(request);
                            if (response.code() == ServerResponse.HTTP_CODE.TOKEN) {
                                userDataSource.persitUser(null);
                                userDataSource.storeLoginToken(null);
                                throw new LoginException(ServerResponse.TOKEN_CHANGED);
                            }
                            return response;
                        } catch (SocketTimeoutException socketEx) {
                            socketEx.printStackTrace();
                            throw CommonException.SOCKET_TIMEOUT;
                        }
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    @Named("public")
    OkHttpClient providePublicOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        try {
                            Request origin = chain.request();
                            Request.Builder requestBuilder = origin.newBuilder();
                            if (!Strings.isNullOrEmpty(language))
                                requestBuilder.addHeader("Accept-Language", language);
                            Request request = requestBuilder.build();
                            Response response = chain.proceed(request);
                            return response;
                        } catch (SocketTimeoutException socketEx) {
                            throw CommonException.SOCKET_TIMEOUT;
                        }
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    @Named("public")
    Retrofit providePublicRetrofit(@Named("public") OkHttpClient client) {

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    @Provides
    @Singleton
    @Named("private")
    Retrofit providePrivateRetrofit(@Named("private") OkHttpClient client) {

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    @Provides
    @Singleton
    @Named("public")
    GatApi providePublicGatApi(@Named("public") Retrofit retrofit) {
        return retrofit.create(GatApi.class);
    }

    @Provides
    @Singleton
    @Named("private")
    GatApi providePrivateGatApi(@Named("private") Retrofit retrofit) {
        return retrofit.create(GatApi.class);
    }
}
