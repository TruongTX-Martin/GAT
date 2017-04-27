package com.gat.feature.message;

import android.util.Log;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.UseCases;
import com.gat.domain.usecase.UseCase;
import com.gat.feature.message.item.GroupItemBuilder;
import com.gat.feature.message.item.ItemBuilder;
import com.gat.feature.message.item.LoadingMessage;
import com.gat.feature.message.item.MessageItemBuilder;
import com.gat.repository.entity.Group;
import com.gat.repository.entity.Message;
import com.gat.repository.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

/**
 * Created by ducbtsn on 3/27/17.
 */

public class MessagePresenterImpl implements MessagePresenter {
    private final String TAG = MessagePresenterImpl.class.getSimpleName();

    public @interface TYPE {
        int MESSAGE_LOAD_MORE = 1;
        int MESSAGE_REFRESH = 2;
    }
    private final int MESSAGE_LIST_SIZE = 15;
    private int pageCnt;
    private int userId;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;
    private final Scheduler worker;

    private Subject<LoadingEvent> loadingEventSubject;
    private Subject<ItemResult> itemResultSubject;


    private Subject<Integer> loadSubject;

    private UseCase<ItemResult> loadMessageUseCase;

    private UseCase<List<Message>> loadMoreMessageUseCase;

    private UseCase<List<Message>> getMessageUseCase;

    private Subject<String> sendMessageSubject;

    private Subject<Boolean> sendMessageResult;

    private UseCase<Boolean> sendMessageUseCase;

    private CompositeDisposable compositeDisposable;

    public MessagePresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        this.worker = this.schedulerFactory.single();

        this.loadingEventSubject = BehaviorSubject.create();
        this.itemResultSubject = BehaviorSubject.createDefault(ItemBuilder.defaultItems());
        this.loadSubject = BehaviorSubject.create();
        this.sendMessageSubject = BehaviorSubject.create();

        pageCnt = 1;

    }

    private void getMessage(boolean refresh, boolean clear) {
        Log.d(TAG, "getMessageList");
        if (loadMessageUseCase != null) {
            Log.d(TAG, "During load message...");
            return;
        }
        getMessageUseCase = UseCases.release(getMessageUseCase);

        pageCnt = refresh ? 1 : (pageCnt+1);

        showLoadingItem(refresh, clear, LoadingMessage.Message.LOADING);

        MessageItemBuilder itemBuilder = new MessageItemBuilder();

        getMessageUseCase = useCaseFactory.getMessageList(userId, pageCnt, MESSAGE_LIST_SIZE);
        ObservableTransformer<List<Message>, ItemResult> transformer =
                upstream -> upstream
                        .map(messages -> {
                            List<Item> items = itemResultSubject.blockingFirst().items();
                            Log.d(TAG, "Transformer:" + messages.size() + "," + items.size());
                            ItemResult result = itemBuilder.addList(items, messages,
                                    refresh,
                                    /*messages.size() >= MESSAGE_LIST_SIZE*/false);
                            itemResultSubject.onNext(result);
                            return result;
                        });

        loadMessageUseCase = useCaseFactory.transform(getMessageUseCase, transformer,worker)
                .executeOn(schedulerFactory.io())
                .onNext(list -> {
                    Log.d(TAG, "Loading message: has new messages");
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(false)
                            .status(LoadingEvent.Status.DONE)
                            .build();
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    Log.d(TAG, "Loading message: has error.");
                    showLoadingItem(true, true, LoadingMessage.Message.ERROR);
                })
                .onComplete(() -> {
                    Log.d(TAG, "Loading message stop");
                    showLoadingItem(refresh, clear, LoadingMessage.Message.COMPLETED);
                    getMessageUseCase = UseCases.release(getMessageUseCase);
                    loadMessageUseCase = UseCases.release(loadMessageUseCase);
                })
                .onStop(() -> {
                    Log.d(TAG, "Loading message stop");
                    showLoadingItem(refresh, clear, LoadingMessage.Message.COMPLETED);
                    getMessageUseCase = UseCases.release(getMessageUseCase);
                    loadMessageUseCase = UseCases.release(loadMessageUseCase);
                })
                .execute();
    }

    private void getMessage(int type) {
        switch (type) {
            case TYPE.MESSAGE_LOAD_MORE:
                getMessage(false, false);
                break;
            case TYPE.MESSAGE_REFRESH:
                getMessage(true, true);
                break;
            default:
                break;
        }
    }

    private void showLoadingItem(boolean refresh, boolean clear, @LoadingMessage.Message int message){
        Callable<ItemResult> job = () -> {
            List<Item> items = itemResultSubject.blockingFirst().items();
            Log.d(TAG, "show loading " + message);
            ItemResult result = ItemBuilder.showLoading(items, clear, !refresh || clear, message);
            itemResultSubject.onNext(result);
            return result;
        };

        useCaseFactory.doWork(job)
                .executeOn(worker)
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    int status = LoadingEvent.Status.BEGIN;
                    switch (message) {
                        case LoadingMessage.Message.LOADING:
                            status = LoadingEvent.Status.BEGIN;
                            break;
                        case LoadingMessage.Message.COMPLETED:
                            status = LoadingEvent.Status.DONE;
                            break;
                        default:
                            status = LoadingEvent.Status.ERROR;
                            break;
                    }
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(refresh)
                            .status(status)
                            .build();
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> {
                    Timber.d(throwable, "Error show loading.");
                    throwable.printStackTrace();
                })
                .execute();
    }
    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable(
                this.loadSubject.observeOn(schedulerFactory.main()).subscribe(this::getMessage),
                this.sendMessageSubject.observeOn(schedulerFactory.main()).subscribe(message -> send(message))
        );
    }

    @Override
    public void onDestroy() {
        loadMessageUseCase = UseCases.release(loadMessageUseCase);
        getMessageUseCase = UseCases.release(getMessageUseCase);
        loadMoreMessageUseCase = UseCases.release(loadMoreMessageUseCase);
        compositeDisposable.dispose();
    }

    @Override
    public void loadMoreMessageList(int userId) {
        Log.d(TAG, "loadMoreMessage");
        this.userId = userId;
        loadSubject.onNext(TYPE.MESSAGE_LOAD_MORE);
    }

    @Override
    public void refreshMessageList(int userId) {
        Log.d(TAG, "refreshMessage");
        this.userId = userId;
        loadSubject.onNext(TYPE.MESSAGE_REFRESH);
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
    public void sendMessage(String message) {
        sendMessageSubject.onNext(message);
    }

    @Override
    public Observable<Boolean> sendMessageResult() {
        return sendMessageResult.observeOn(schedulerFactory.main());
    }

    private void send(String message) {
        sendMessageUseCase = UseCases.release(sendMessageUseCase);

        sendMessageUseCase = useCaseFactory.sendMessage(userId, message);
        sendMessageUseCase.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    Log.d(TAG, result ? "SendOK":"NG");
                    sendMessageResult.onNext(result);
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    Log.d(TAG, throwable.getStackTrace().toString());
                })
                .onStop(() -> {
                    sendMessageUseCase = UseCases.release(sendMessageUseCase);
                })
                .execute();
    }

    @Override
    public Observable<User> loadUser() {
        return null;
    }
}
