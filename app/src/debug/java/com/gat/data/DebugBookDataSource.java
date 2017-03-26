package com.gat.data;

import com.gat.data.api.GatApi;
import com.gat.data.id.LongId;
import com.gat.data.response.ServerResponse;
import com.gat.dependency.DataComponent;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.entity.Author;
import com.gat.repository.entity.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
    public Observable<Book> searchBookByIsbn(String isbn) {
        GatApi gatApi = dataComponent.getPublicGatApi();

        Observable<Response<ServerResponse<Book>>> bookResponse = gatApi.getBookByIsbn(isbn);
        return bookResponse.map(response -> {
            ServerResponse<Book> serverResponse = response.body();
            if (serverResponse != null) {
                return serverResponse.data();
            } else {
                throw new RuntimeException();
            }
        });
    }
}
