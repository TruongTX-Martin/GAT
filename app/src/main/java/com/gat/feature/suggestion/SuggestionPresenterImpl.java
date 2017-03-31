package com.gat.feature.suggestion;

import com.gat.data.response.ServerResponse;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.Book;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ducbtsn on 3/25/17.
 */

public class SuggestionPresenterImpl implements SuggestionPresenter {

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private final Subject<List<Book>> resultSubject;
    private final Subject<String> errorSubject;

    private UseCase<List<Book>> suggestionUseCase;
    public SuggestionPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;

        resultSubject = PublishSubject.create();
        errorSubject = PublishSubject.create();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }


    @Override
    public Observable<List<Book>> onTopBorrowingSuccess() {
        return resultSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<List<Book>> onBookSuggestSuccess() {
        return resultSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return errorSubject.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void suggestMostBorrowing() {
        suggestionUseCase = useCaseFactory.suggestMostBorrowing();
        suggestionUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(listBook -> {
                    resultSubject.onNext(listBook);
                })
                .onError(throwable -> {
                    errorSubject.onNext("bắt được rồi");
                })
                .execute();
    }

    @Override
    public void suggestBooks() {

    }
}
