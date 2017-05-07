package com.gat.data.api;

import com.gat.data.response.BookResponse;
import com.gat.data.response.ResultInfoObject;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.SimpleResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.BorrowResponse;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.data.response.impl.ResetPasswordResponseData;
import com.gat.data.response.ResultInfoList;
import com.gat.data.response.impl.VerifyTokenResponseData;
import com.gat.data.response.DataResultListResponse;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;

import java.util.List;
//import rx.Observable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
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
    //@Multipart
    @POST("user/register_by_social")
    Observable<Response<ServerResponse<LoginResponseData>>> registerBySocial(
            //@Field("user_id") String userId,
            @Field("socialID") String socialID,
            @Field("socialType") String socialType,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("image") String image
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
    Observable<User> getPublicUserInfo();

    @FormUrlEncoded
    @POST("user/update_usually_location")
    Observable<Response<ServerResponse<SimpleResponse>>> updateLocation(
            @Field("address") String address,
            @Field("longitude") float longitude,
            @Field("latitude") float latitude
    );

    @FormUrlEncoded
    @POST("user/update_favorite_category")
    Observable<Response<ServerResponse<SimpleResponse>>> updateCategory(
            @Field("categories") List<Integer> categories
    );

    @GET("search/book_by_isbn")
    Observable<Response<ServerResponse>> getBookByIsbn(
            @Query("isbn") String isbn
    );

    @GET("suggestion/most_borrowing")
    Observable<Response<ServerResponse<ResultInfoList<BookResponse>>>> suggestMostBorrowing(
    );

    @GET("suggestion/book_without_login")
    Observable<Response<ServerResponse<ResultInfoList<BookResponse>>>> suggestWithoutLogin(
    );

    @GET("suggestion/book_after_login")
    Observable<Response<ServerResponse<ResultInfoList<BookResponse>>>> suggestAfterLogin(
    );

    @GET("search/nearby_user")
    Observable<Response<ServerResponse<ResultInfoList<UserNearByDistance>>>> getPeopleNearByUser(
            @Query("currentLat") float currentLatitude,
            @Query("currentLong") float currentLongitude,
            @Query("neLat") float neLatitude,
            @Query("neLong") float neLongitude,
            @Query("wsLat") float wsLatitude,
            @Query("wsLong") float wsLongitude,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @FormUrlEncoded
    @POST("search/book_by_title")
    Observable<Response<ServerResponse<DataResultListResponse<BookResponse>>>> searchBookByTitle(
            @Field("title") String title,
            @Field("userId") long userId,
            @Field("page") int page,
            @Field("per_page") int perPage
    );

    @FormUrlEncoded
    @POST("search/book_by_author")
    Observable<Response<ServerResponse<DataResultListResponse<BookResponse>>>> searchBookByAuthor(
            @Field("authorName") String author,
            @Field("userId") long userId,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @FormUrlEncoded
    @POST("search/user")
    Observable<Response<ServerResponse<DataResultListResponse<UserResponse>>>> searchUser (
            @Field("name") String title,
            @Query("page") int page,
            @Query("per_page") int per_page
            );

    @GET("share/get_book_record")
    Observable<Response<ServerResponse<Data>>> getBookRequest(
            @Query("sharingFilter") String sharingFilter,
            @Query("borrowingFilter") String borrowingFilter,
            @Query("page") int page,
            @Query("per_page") int per_page
    );


    @FormUrlEncoded
    @POST("book/selfchange_instance_stt")
    Observable<Response<ServerResponse<Data>>> changeBookSharingStatus(
            @Field("instanceId") int instanceId,
            @Field("sharingStatus") int sharingStatus
    );

    @GET("book/get_user_reading_editions")
    Observable<Response<ServerResponse<Data>>> getReadingBooks(
            @Query("userId") int userId,
            @Query("readingFilter") boolean readingFilter,
            @Query("toReadFilter") boolean toReadFitler,
            @Query("readFilter") boolean readFilter,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @GET("search/get_book_searched_keyword")
    Observable<Response<ServerResponse<ResultInfoList<Keyword>>>> getBooksSearchedKeyword(
    );

    @GET("search/get_author_searched_keyword")
    Observable<Response<ServerResponse<ResultInfoList<Keyword>>>> getAuthorsSearchedKeyword(
    );

    @GET("search/get_user_searched_keyword")
    Observable<Response<ServerResponse<ResultInfoList<Keyword>>>> getUsersSearchedKeyword(
    );

    @GET("user/get_user_private_info")
    Observable<Response<ServerResponse<Data<User>>>> getPersonalInformation();

    @GET("user/get_user_public_info")
    Observable<Response<ServerResponse<Data<User>>>> getUserPublicInfo(
            @Query("userId") int userId
    );

    @GET("book/selfget_book_instance")
    Observable<Response<ServerResponse<Data>>> getBookInstance(
            @Query("sharingFilter") boolean sharingFilter,
            @Query("notSharingFilter") boolean notSharingFilter,
            @Query("lostFilter") boolean lostFilter,
            @Query("page") int page,
            @Query("per_page") int per_page
    );

    @FormUrlEncoded
    @POST("user/update_user_info")
    Observable<Response<ServerResponse<Data>>> updateUserInfo(
            @Field("name") String name,
            @Field("image") String image,
            @Field("changeImageFlag") boolean changeImageFlag
    );

    @GET("book/get_user_sharing_editions")
    Observable<Response<ServerResponse<Data>>> getBookUserSharing(
            @Query("userId") int userId,
            @Query("ownerId") int ownerId,
            @Query("page") int page,
            @Query("per_page") int per_page
    );

    @GET("share/get_request_info")
    Observable<Response<ServerResponse<Data>>> getBookDetail(
            @Query("recordId") int recordId
    );

    @GET("book/get_book_info")
    Observable<Response<ServerResponse<ResultInfoObject<BookInfo>>>> getBookInfo (
            @Query("editionId") int editionId
    );

    @GET("book/get_edition_evaluation")
    Observable<Response<ServerResponse<DataResultListResponse<EvaluationItemResponse>>>> getBookEditionEvaluation (
            @Query("editionId") int editionId
    );

    @GET("book/selfget_reading_stt")
    Observable<Response<ServerResponse<BookReadingInfo>>> getReadingStatus (
            @Query("editionId") int editionId
    );


    @GET("book/selfget_book_evaluation")
    Observable<Response<ServerResponse<ResultInfoObject<EvaluationItemResponse>>>> getBookEvaluationByUser (
            @Query("editionId") int editionId
    );

    @GET("book/get_edition_sharing_user")
    Observable<Response<ServerResponse<DataResultListResponse<UserResponse>>>> getEditionSharingUser (
            @Query("editionId") int editionId
    );

    @FormUrlEncoded
    @POST("book/selfupdate_book_evaluation")
    Observable<Response<ServerResponse>> postComment (
            @Field("editionId") int editionId,
            @Field("value") int value,
            @Field("review") String review,
            @Field("spoiler") boolean spoiler
    );

    @GET("book/selfget_instance_info")
    Observable<Response<ServerResponse<BookInstanceInfo>>> getSelfInstanceInfo (
            @Query("editionId") int editionId
    );

    @FormUrlEncoded
    @POST("book/selfadd_instance")
    Observable<Response<ServerResponse>> selfAddInstance (
            @Field("editionId") int editionId,
            @Field("sharingStatus") int sharingStatus,
            @Field("numberOfBook") int numberOfBook
    );


    @FormUrlEncoded
    @POST("book/selfupdate_reading_stt")
    Observable<Response<ServerResponse>> selfUpdateReadingStatus (
            @Field("editionId") int editionId,
            @Field("readingStatus") int readingStatus
    );

    @FormUrlEncoded
    @POST("share/create_request")
    Observable<Response<ServerResponse<ResultInfoObject<BorrowResponse>>>> requestBorrow (
            @Field("editionId") int editionId,
            @Field("ownerId") int ownerId
    );

    @GET("user/get_user_notification")
    Observable<Response<ServerResponse<DataResultListResponse<NotifyEntity>>>> getUserNotification (
            @Query("page") int page,
            @Query("per_page") int per_page);

    @FormUrlEncoded
    @POST("share/update_request_by_borrower")
    Observable<Response<ServerResponse<Data>>> requestBookByBorrower(
            @Field("recordId") int recordId,
            @Field("currentStatus") int currentStatus,
            @Field("newStatus") int newStatus
    );


    @FormUrlEncoded
    @POST("share/update_request_by_owner")
    Observable<Response<ServerResponse<Data>>> requestBookByOwner(
            @Field("recordId") int recordId,
            @Field("currentStatus") int currentStatus,
            @Field("newStatus") int newStatus
    );


    @FormUrlEncoded
    @POST("user/firebase_token_register")
    Observable<Response<ServerResponse<Boolean>>> registerFirebaseToken (
            @Field("firebaseToken") String firebaseToken
    );

    @FormUrlEncoded
    @POST("user/push_message_notification")
    Observable<Response<ServerResponse<Boolean>>> messageNotification (
            @Field("receiverId") int receiverId,
            @Field("Message") String message
    );

    @FormUrlEncoded
    @POST("share/create_request")
    Observable<Response<ServerResponse<Data>>> requestBorrowBook (
            @Field("editionId") int editionId,
            @Field("ownerId") int ownerId
    );
}
