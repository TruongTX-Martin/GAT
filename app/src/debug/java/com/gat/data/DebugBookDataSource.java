package com.gat.data;

import com.gat.common.util.MZDebug;
import com.gat.data.api.GatApi;
import com.gat.data.exception.CommonException;
import com.gat.data.id.LongId;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ResultInfoList;
import com.gat.data.response.ResultInfoObject;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.BorrowResponse;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.dependency.DataComponent;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.entity.Author;
import com.gat.repository.entity.Book;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;


/**
 * Created by Rey on 2/14/2017.
 */

public class DebugBookDataSource implements BookDataSource {
    private final DataComponent dataComponent;

    public DebugBookDataSource(DataComponent dataComponent) {
        this.dataComponent = dataComponent;
    }

    private List<Book> listOfBooks(Book... books){
        ArrayList list = new ArrayList();
        if(books != null)
            Collections.addAll(list, books);
        return Collections.unmodifiableList(list);
    }

    private List<Author> listOfAuthors(String... names){
        ArrayList list = new ArrayList();
        if(names != null)
            for(String name : names)
                list.add(Author.instance(name));
        return Collections.unmodifiableList(list);
    }

    @Override
    public Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage) {
        return Observable.fromCallable(() -> {
            if(keyword.contains("empty"))
                return listOfBooks();

            int max = Integer.MAX_VALUE;
            if(keyword.contains("max"))
                max = Integer.parseInt(keyword.replaceAll("[^\\d.]", ""));

            int length = Math.max(0, Math.min(sizeOfPage, max - page * sizeOfPage));
            Book[] books = new Book[length];
            long baseId = keyword.hashCode() * 1000;
            Random rand = new Random();
            for (int i = 0; i < books.length; i++) {
                long id =  page * sizeOfPage + i;
                books[i] = Book.builder()
                        .id(LongId.instance(baseId + id))
                        .title("Book " + keyword + " " + id)
                        .publisher("NXB ABC")
                        .publishedDate(System.currentTimeMillis())
                        .pages(100)
                        .authors(listOfAuthors("Author " + id))
                        .rating(rand.nextFloat() * 5f)
                        .build();
            }
            return listOfBooks(books);
        }).delay(1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<Integer> searchBookByIsbn(String isbn) {
        MZDebug.w("______________ searchBookByIsbn______________________________________________");
        GatApi gatApi = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse>> bookResponse = gatApi.getBookByIsbn(isbn);
        return bookResponse.map(response -> {
            ServerResponse serverResponse = response.body();
            if (serverResponse != null) {
                MZDebug.w("____________________________________________________ searchBookByIsbn");
                LinkedTreeMap<String, Double> data = (LinkedTreeMap<String, Double>) serverResponse.data();
                if (data != null && data.get("editionId") != null)
                    return data.get("editionId").intValue();
                else
                    throw new CommonException("Bad response.");
            } else {
                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                if (jsonObject.has("message"))
                    throw new CommonException(jsonObject.getString("message"));
                else
                    throw new CommonException("Bad response.");
            }
        });
    }

    @Override
    public Observable<List<BookResponse>> suggestMostBorrowing() {
        MZDebug.i("_____________________________________ suggestMostBorrowing ___________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<ResultInfoList<BookResponse>>>> responseObservable;
        responseObservable = api.suggestMostBorrowing();
        return responseObservable.map(response -> {
            List<BookResponse> list = response.body().data().getResultInfo();

            MZDebug.i("__list most borrowing size: " + list.size());

            MZDebug.i("__item 0: " + list.get(0).toString());

            return list;

        });
    }

    @Override
    public Observable<List<BookResponse>> suggestBooksWithoutLogin() {
        MZDebug.i("____________________________ suggestBooksWithoutLogin ________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<ResultInfoList<BookResponse>>>> responseObservable;
        responseObservable = api.suggestWithoutLogin();

        return responseObservable.map( response -> {
            List<BookResponse> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<List<BookResponse>> suggestBooksAfterLogin() {
        MZDebug.i("____________________________ suggestBooksAfterLogin __________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<ResultInfoList<BookResponse>>>> responseObservable;
        responseObservable = api.suggestAfterLogin();

        return responseObservable.map( response -> {
            List<BookResponse> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> searchBookByTitle
            (String title, long userId, int page, int sizeOfPage) {
        MZDebug.i("____________________________ searchBookByTitle _______________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<DataResultListResponse<BookResponse>>>> responseObservable;
        responseObservable = api.searchBookByTitle(title, userId, page, sizeOfPage);

        return responseObservable.map( response -> {
            DataResultListResponse<BookResponse> data = response.body().data();
            return data;
        });
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> searchBookByAuthor
            (String author, long userId, int page, int sizeOfPage) {
        MZDebug.i("____________________________ searchBookByAuthor ______________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<DataResultListResponse<BookResponse>>>> responseObservable;
        responseObservable = api.searchBookByAuthor(author, userId, page, sizeOfPage);

        return responseObservable.map( response -> {
            DataResultListResponse<BookResponse> data = response.body().data();
            return data;
        });
    }

    @Override
    public Observable<List<Keyword>> getBooksSearchedKeyword() {
        MZDebug.i("________________________ getBooksSearchedKeyword _____________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<ResultInfoList<Keyword>>>> responseObservable;
        responseObservable = api.getBooksSearchedKeyword();

//        List<String> list = new ArrayList<String>();
//        list.add("book 1");
//        list.add("book 2");
//        list.add("book 3");
//        list.add("book 4");
//        list.add("book 5");

        return responseObservable.map( response -> {
            List<Keyword> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<List<Keyword>> getAuthorsSearchedKeyword() {
        MZDebug.i("________________________ getAuthorsSearchedKeyword ___________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<ResultInfoList<Keyword>>>> responseObservable;
        responseObservable = api.getAuthorsSearchedKeyword();

//        List<String> list = new ArrayList<String>();
//        list.add("author 1");
//        list.add("author 2");
//        list.add("author 3");
//        list.add("author 4");
//        list.add("author 5");

        return responseObservable.map( response -> {
            List<Keyword> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<BookInfo> getBookInfo(int editionId) {
        MZDebug.w("_____________________________________ getBookInfo ____________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<ResultInfoObject<BookInfo>>>> responseObservable;
        responseObservable = api.getBookInfo(editionId);

        return responseObservable.map( response -> {
            MZDebug.w("book info response: " + response.body().toString());

            return  response.body().data().getResultInfo();
        });
    }

    @Override
    public Observable<List<EvaluationItemResponse>> getBookEditionEvaluation(int editionId) {
        MZDebug.w("________________________ getBookEditionEvaluation ____________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<DataResultListResponse<EvaluationItemResponse>>>> responseObservable;
        responseObservable = api.getBookEditionEvaluation(editionId);

        return responseObservable.map(response -> {
            List<EvaluationItemResponse> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<BookReadingInfo> getReadingStatus(int editionId) {
        MZDebug.w("________________________________ getReadingStatus ____________________________");

//        BookReadingInfo bookReadingInfo = new BookReadingInfo();
//        bookReadingInfo.setReadingStatus(-1);
//        bookReadingInfo.setEditionId(12);
//
//        return Observable.fromCallable(() -> {
//            return bookReadingInfo;
//        }).delay(0, TimeUnit.MILLISECONDS);

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<BookReadingInfo>>> responseObservable;
        responseObservable = api.getReadingStatus(editionId);

        return responseObservable.map(response -> response.body().data());
    }

    @Override
    public Observable<EvaluationItemResponse> getBookEvaluationByUser(int editionId) {
        MZDebug.w("____________________________ getBookEvaluationByUser _________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<ResultInfoObject<EvaluationItemResponse>>>> responseObservable;
        responseObservable = api.getBookEvaluationByUser(editionId);

        return responseObservable.map(response -> response.body().data().getResultInfo());
    }

    @Override
    public Observable<List<UserResponse>> getEditionSharingUser(int editionId, Integer userId, Float latitude, Float longitude) {
        MZDebug.w("____________________________ getEditionSharingUser ___________________________");

        GatApi api = dataComponent.getPublicGatApi();
        Observable<Response<ServerResponse<DataResultListResponse<UserResponse>>>> responseObservable;
        responseObservable = api.getEditionSharingUser(editionId, userId, latitude, longitude);

        return responseObservable.map(response -> {
            List<UserResponse> list = response.body().data().getResultInfo();
            return list;
        });
    }

    @Override
    public Observable<ServerResponse> postComment(int editionId, int value, String review, boolean spoiler) {
        MZDebug.w("______________________________________ postComment ___________________________");

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.postComment(editionId, value, review, spoiler);

        return responseObservable.map(response -> response.body());
    }

    @Override
    public Observable<BookInstanceInfo> getSelfInstanceInfo(int editionId) {
        MZDebug.w("______________________________ getSelfInstanceInfo _________________ [ DEBUG ]");

//        BookInstanceInfo bookInstanceInfo = new BookInstanceInfo();
//        bookInstanceInfo.setBorrowingTotal(2);
//        bookInstanceInfo.setLostTotal(2);
//        bookInstanceInfo.setNotSharingToal(0);
//        bookInstanceInfo.setSharingTotal(2);
//
//        return Observable.fromCallable(() -> {
//            return bookInstanceInfo;
//        }).delay(0, TimeUnit.MILLISECONDS);

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<BookInstanceInfo>>> responseObservable;
        responseObservable = api.getSelfInstanceInfo(editionId);

        return responseObservable.map( response -> {

            MZDebug.w("response: " + response.body().data().toString());

            return response.body().data();
        });
    }

    @Override
    public Observable<ServerResponse> selfAddInstance(int editionId, int sharingStatus, int numberOfBook) {
        MZDebug.w("______________________________________ selfAddInstance _____________ [ DEBUG ]");

//        ServerResponse serverResponse = new ServerResponse("Success", 200, null);
//        return Observable.fromCallable(() -> {
//            return serverResponse;
//        }).delay(0, TimeUnit.MILLISECONDS);

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.selfAddInstance(editionId, sharingStatus, numberOfBook);

        return responseObservable.map(response -> {

            MZDebug.w("selfAddInstance: response :" + response.message());

            return response.body();
        });
    }

    @Override
    public Observable<ServerResponse> selfUpdateReadingStatus(int editionId, int readingStatus) {
        MZDebug.w("________________ selfUpdateReadingStatus :" + editionId + " = " + readingStatus);

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse>> responseObservable;
        responseObservable = api.selfUpdateReadingStatus(editionId, readingStatus);

        return responseObservable.map(response -> response.body());
    }

    @Override
    public Observable<BorrowResponse> requestBorrow(int editionId, int ownerId) {
        MZDebug.w("____________________________________ requestBorrow _________________ [ DEBUG ]");

//        BorrowResponse borrowResponse = new BorrowResponse();
//        borrowResponse.setRecordId(103);
//        borrowResponse.setBookId(150);
//        borrowResponse.setBorrowerId(156);
//        borrowResponse.setOwnerId(2);
//        borrowResponse.setRequestTime("2017-04-17 13:02:25");
//        borrowResponse.setRecordStatus(0);
//
//        return Observable.fromCallable(() -> {
//            return borrowResponse;
//        }).delay(1000, TimeUnit.MILLISECONDS);

        GatApi api = dataComponent.getPrivateGatApi();
        Observable<Response<ServerResponse<ResultInfoObject<BorrowResponse>>>> responseObservable;
        responseObservable = api.requestBorrow(editionId, ownerId);

        return responseObservable.map(response -> {
            BorrowResponse borrowResponse = response.body().data().getResultInfo();
            MZDebug.w("DEBUG: borrow response \n\r" + borrowResponse.toString());

            return borrowResponse;
        });

    }


}
