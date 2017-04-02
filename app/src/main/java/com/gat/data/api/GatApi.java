package com.gat.data.api;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;

import java.util.List;

//import rx.Observable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by ducbtsn on 2/25/17.
 */

public interface GatApi {
    @FormUrlEncoded
    @POST("user/login_by_email")
    Observable<Response<ServerResponse<LoginResponseData>>> loginByEmail(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("user/register_by_email")
    Observable<Response<ServerResponse<LoginResponseData>>> registerByEmail(
            @Field("email") String email,
            @Field("password") String password,
            @Field("name") String name
            //@Field("image") String image
    );

    @FormUrlEncoded
    @POST("user/login_by_social")
    Observable<Response<ServerResponse<LoginResponseData>>> loginBySocial(
            //@Field("user_id") String userId,
            @Field("socialID") String socialID,
            @Field("socialType") String socialType);

    @FormUrlEncoded
    @Multipart
    @POST("user/register_by_social")
    Observable<Response<ServerResponse<LoginResponseData>>> registerBySocial(
            //@Field("user_id") String userId,
            @Field("socialID") String socialID,
            @Field("socialType") String socialType,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Part("image") RequestBody image
    );

    @FormUrlEncoded
    @POST("user/request_reset_password")
    Observable<Response<ServerResponse<ResetPasswordResponseData>>> requestResetPassword(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("user/verify_reset_token")
    Observable<Response<ServerResponse<VerifyTokenResponseData>>> verifyResetToken(
            @Field("code") String code,
            @Field("tokenResetPassword") String token
    );

    @FormUrlEncoded
    @POST("user/reset_password")
    Observable<Response<ServerResponse<LoginResponseData>>> resetPassword(
            @Field("newPassword") String password,
            @Field("tokenVerify") String tokenVerify
    );

    @GET("user/get_user_information")
    Observable<User> getUserInformation();

    @FormUrlEncoded
    @POST("user/update_usually_location")
    Observable<Response<ServerResponse>> updateLocation(
            @Field("address") String address,
            @Field("longitude") float longitude,
            @Field("latitude") float latitude
    );

    @FormUrlEncoded
    @POST("user/update_favourite_category")
    Observable<Response<ServerResponse>> updateCategory(
            @Field("categories")List<Integer> categories
    );

    @GET("search/book_by_isbn")
    Observable<Response<ServerResponse<Book>>> getBookByIsbn(
        @Field("isbn") String isbn
    );

    @GET("user/get_user_private_info")
    Observable<Response<ServerResponse<Data>>> getPersonalInformation();

    @GET("book/selfget_book_instance")
    Observable<Response<ServerResponse<Data>>> getBookInstance(
            @Query("sharingFilter") boolean sharingFilter,
            @Query("notSharingFilter") boolean notSharingFilter,
            @Query("lostFilter") boolean lostFilter
    );

}
