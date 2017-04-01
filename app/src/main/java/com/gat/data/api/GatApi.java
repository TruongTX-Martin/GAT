package com.gat.data.api;

import com.gat.common.util.Strings;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.User;

import java.util.List;

//import rx.Observable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.*;

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
    //@Multipart
    @POST("user/register_by_social")
    Observable<Response<ServerResponse<LoginResponseData>>> registerBySocial(
            //@Field("user_id") String userId,
            @Field("socialID") String socialID,
            @Field("socialType") String socialType,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password/*,
            @Field("image") RequestBody image*/
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

    @GET("suggestion/most_borrowing")
    Observable<Response<List<Book>>> suggestMostBorrowing (
    );


    // api v1
    @GET("search/nearby_user_by_distance")
    Observable<Response<ServerResponse>> getPeopleNearByUserV1 (
            @Query("longitude") float currentLongitude,
            @Query("latitude") float currentLatitude

    );

    // api v2
    @GET("search/nearby_user")
    Observable<Response<ServerResponse>> getPeopleNearByUser (
            @Query("currentLat") float currentLatitude,
            @Query("currentLong") float currentLongitude,
            @Query("neLat") float neLatitude,
            @Query("neLong") float neLongitude,
            @Query("wsLat") float wsLatitude,
            @Query("wsLong") float wsLongitude
    );

}
