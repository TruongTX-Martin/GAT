package com.gat.feature.search;

import android.util.Log;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.common.util.Strings;
import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.search.item.LoadingItem;
import com.gat.repository.entity.Book;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 * Created by Rey on 2/14/2017.
 */

public class SearchPresenterImpl implements SearchPresenter {

    private final String TAG = SearchPresenterImpl.class.getSimpleName();

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;
    private final Scheduler workScheduler;
    private final SearchItemBuilder itemBuilder;

    private final Subject<String> keywordSubject;
    private final Subject<LoadingEvent> loadingEventSubject;
    private final Subject<ItemResult> itemResultSubject;

    private int pageCount;
    private UseCase<ItemResult> loadPageUseCase;

    private CompositeDisposable compositeDisposable;

    private final Subject<String> isbnSubject;
    private final Subject<Book> bookResultSubject;
    private final Subject<ServerResponse> errorSubject;
    private UseCase<Book> getByIsbnUseCase;

    public SearchPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory, SearchItemBuilder itemBuilder){
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        this.workScheduler = schedulerFactory.single();
        this.itemBuilder = itemBuilder;

        keywordSubject = BehaviorSubject.createDefault(Strings.EMPTY);
        loadingEventSubject = PublishSubject.create();
        itemResultSubject = BehaviorSubject.createDefault(itemBuilder.defaultItems());

        this.isbnSubject = BehaviorSubject.create();
        this.bookResultSubject = PublishSubject.create();
        this.errorSubject = PublishSubject.create();

        pageCount = 0;
    }

    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable(
                keywordChanged().filter(keyword -> !Strings.isNullOrEmpty(keyword)).subscribe(keyword -> loadPage(true, true)),
                isbnSubject.observeOn(schedulerFactory.main()).subscribe(isbn -> searchBookByIsbn(isbn))
        );
    }

    @Override
    public void onDestroy() {
        loadPageUseCase = UseCases.release(loadPageUseCase);
        getByIsbnUseCase = UseCases.release(getByIsbnUseCase);
        compositeDisposable.dispose();
    }

    @Override
    public Observable<String> keywordChanged() {
        return keywordSubject.distinctUntilChanged()
                .observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<LoadingEvent> loadingEvents() {
        return loadingEventSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ItemResult> itemsChanged() {
        return itemResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public void setKeyword(String keyword) {
        keywordSubject.onNext(keyword);
    }

    @Override
    public void loadMoreBooks() {
        loadPage(false, false);
    }

    @Override
    public void refreshBooks() {
        loadPage(true, false);
    }

    private void loadPage(boolean refresh, boolean clearBooks) {
        if(!refresh && loadPageUseCase != null)
            return;

        final String keyword = keywordSubject.blockingFirst();
        if(Strings.isNullOrEmpty(keyword)){
            showLoadingItem(refresh, clearBooks, LoadingItem.Message.DEFAULT);
            return;
        }

        showLoadingItem(refresh, clearBooks, LoadingItem.Message.LOADING);
        loadPageUseCase = UseCases.release(loadPageUseCase);

        final int page = refresh ? 0 : pageCount;
        final int size = 10;
        UseCase<List<Book>> searchBooks = useCaseFactory.searchBookByKeyword(keyword, page, size);
        ObservableTransformer<List<Book>, ItemResult> transformer =
                upstream -> upstream.map(books -> {
                    List<Item> items = itemResultSubject.blockingFirst().items();
                    ItemResult result = itemBuilder.addBooks(items, books, refresh, books.size() >= size);
                    itemResultSubject.onNext(result);
                    return result;
                });

        loadPageUseCase = useCaseFactory.transform(searchBooks, transformer, workScheduler)
                .executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(refresh)
                            .status(LoadingEvent.Status.DONE)
                            .build();
                    loadingEventSubject.onNext(event);
                    pageCount = page + 1;
                })
                .onError(throwable -> {
                    showLoadingItem(refresh, clearBooks, LoadingItem.Message.ERROR);
                    Timber.d(throwable, "Error load page.");
                })
                .onStop(() -> loadPageUseCase = UseCases.release(loadPageUseCase))
                .execute();
    }

    private void showLoadingItem(boolean refresh, boolean clearBooks, @LoadingItem.Message int message){
        Callable<ItemResult> work = () -> {
            List<Item> items = itemResultSubject.blockingFirst().items();
            ItemResult result = itemBuilder.showLoading(items, clearBooks, !refresh || clearBooks, message);
            itemResultSubject.onNext(result);
            return result;
        };

        useCaseFactory.doWork(work)
                .executeOn(workScheduler)
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(refresh)
                            .status(message == LoadingItem.Message.LOADING ? LoadingEvent.Status.BEGIN : LoadingEvent.Status.ERROR)
                            .build();
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> Timber.d(throwable, "Error show loading."))
                .execute();
    }

    @Override
    public void setIsbn(String isbn) {
        isbnSubject.onNext(isbn);
    }

    @Override
    public Observable<Book> getBookResult() {
        return bookResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ServerResponse> onError() {
        return errorSubject.observeOn(schedulerFactory.main());
    }

    private void searchBookByIsbn(String isbn) {
        Log.d(TAG, isbn);
        getByIsbnUseCase = UseCases.release(getByIsbnUseCase);

        getByIsbnUseCase = useCaseFactory.getBookByIsbn(isbn);

        getByIsbnUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(response -> {
                    bookResultSubject.onNext(response);
                })
                .onError(throwable -> {
                    errorSubject.onNext(ServerResponse.EXCEPTION);
                })
                .execute();
    }
}
