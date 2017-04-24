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

    public @interface ACTION {
        int MESSAGE_LOAD_MORE = 1;
        int MESSAGE_REFRESH = 2;
        int GROUP_LOAD_MORE = 3;
        int GROUP_REFRESH = 4;
    }

    private final int GROUP_LIST_SIZE = 15;
    private final int MESSAGE_LIST_SIZE = 15;
    private int pageCnt;
    private String userId;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;
    private final Scheduler worker;

    private Subject<LoadingEvent> loadingEventSubject;
    private Subject<ItemResult> itemResultSubject;

    private Subject<Integer> loadSubject;

    private UseCase<ItemResult> loadMessageUseCase;

    private UseCase<List<Group>> loadMoreGroupUseCase;

    private UseCase<List<Message>> loadMoreMessageUseCase;

    private UseCase<List<Group>> getGroupUseCase;

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

    private void refreshMessage(boolean refresh, boolean clear) {
        Log.d(TAG, "getMessageList");
        getMessageUseCase = UseCases.release(getMessageUseCase);

        pageCnt = refresh ? 1 : (pageCnt+1);

        showLoadingItem(refresh, clear, LoadingMessage.Message.LOADING);

        MessageItemBuilder itemBuilder = new MessageItemBuilder();
        getMessageUseCase = useCaseFactory.getMessageList(userId, pageCnt, MESSAGE_LIST_SIZE);
        ObservableTransformer<List<Message>, ItemResult> transformer =
                upstream -> upstream
                        .filter(list -> list.size() > 0)
                        .map(messages -> {
                            List<Item> items = itemResultSubject.blockingLatest().iterator().next().items();
                            ItemResult result = itemBuilder.addList(items, messages,
                                    refresh ? true : false,
                                    messages.size() >= GROUP_LIST_SIZE);
                            itemResultSubject.onNext(result);
                            return result;
                        });

        loadMessageUseCase = useCaseFactory.transform(getMessageUseCase, transformer,worker)
                .executeOn(schedulerFactory.io())
                .onNext(list -> {
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(true)
                            .status(LoadingEvent.Status.DONE)
                            .build();
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> {
                    showLoadingItem(true, true, LoadingMessage.Message.ERROR);
                    Timber.d(throwable, "Error loading message");
                })
                .onStop(() -> {
                    Log.d(TAG, "Message Stop");
                    UseCases.release(getMessageUseCase);
                    loadMessageUseCase = UseCases.release(loadMessageUseCase);
                })
                .execute();
    }

    private void refreshGroup(boolean refresh, boolean clear) {
        Log.d(TAG, "getGroupList");

        getGroupUseCase = UseCases.release(getGroupUseCase);

        pageCnt = refresh ? 1 : (pageCnt+1);

        showLoadingItem(refresh, clear, LoadingMessage.Message.LOADING);

        getGroupUseCase = useCaseFactory.getGroupList(pageCnt, GROUP_LIST_SIZE);
        GroupItemBuilder itemBuilder = new GroupItemBuilder();
        ObservableTransformer<List<Group>, ItemResult> transformer =
                upstream -> upstream
                        .map(groups -> {
                            List<Item> items = itemResultSubject.blockingFirst().items();
                            Log.d(TAG, "ListSize:" + items.size() + ", GroupSize:" + groups.size());
                            ItemResult result = itemBuilder.addList(items, groups,
                                    refresh ? true : false,
                                    groups.size() >= GROUP_LIST_SIZE);
                            itemResultSubject.onNext(result);
                            return result;
                        });

        loadMessageUseCase = useCaseFactory.transform(getGroupUseCase, transformer, worker)
                .executeOn(schedulerFactory.io())
                .onNext(list -> {
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(true)
                            .status(LoadingEvent.Status.DONE)
                            .build();
                    Log.d(TAG, "EventDone");
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> {
                    showLoadingItem(true, true, LoadingMessage.Message.ERROR);
                    Timber.d(throwable, "Error loading message");
                })
                .onStop(() -> {
                    Log.d(TAG, "OnStop");
                    getGroupUseCase = UseCases.release(getGroupUseCase);
                    loadMessageUseCase = UseCases.release(loadMessageUseCase);
                })
                .execute();
    }

    private void showLoadingItem(boolean refresh, boolean clear, @LoadingMessage.Message int message){
        Callable<ItemResult> job = () -> {
            List<Item> items = itemResultSubject.blockingFirst().items();
            Log.d(TAG, "do job");
            ItemResult result = ItemBuilder.showLoading(items, clear, !refresh || clear, message);
            itemResultSubject.onNext(result);
            return result;
        };

        useCaseFactory.doWork(job)
                .executeOn(worker)
                .returnOn(schedulerFactory.main())
                .onNext(result -> {
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(refresh)
                            .status(message == LoadingMessage.Message.LOADING ? LoadingEvent.Status.BEGIN : LoadingEvent.Status.ERROR)
                            .build();
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> Timber.d(throwable, "Error show loading."))
                .execute();
    }
    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable(
                this.loadSubject.observeOn(schedulerFactory.main()).subscribe(type -> {
                    switch (type) {
                        case ACTION.GROUP_LOAD_MORE:
                        case ACTION.GROUP_REFRESH:
                            refreshGroup(type == ACTION.GROUP_REFRESH ? true : false, false);
                            break;
                        case ACTION.MESSAGE_LOAD_MORE:
                        case ACTION.MESSAGE_REFRESH:
                            refreshMessage(type == ACTION.MESSAGE_REFRESH ? true : false, false);
                            break;
                    }
                }),
                this.sendMessageSubject.observeOn(schedulerFactory.main()).subscribe(message -> send(message))
        );
    }

    @Override
    public void onDestroy() {
        loadMessageUseCase = UseCases.release(loadMessageUseCase);
        getGroupUseCase = UseCases.release(getGroupUseCase);
        getMessageUseCase = UseCases.release(getMessageUseCase);
        loadMoreGroupUseCase = UseCases.release(loadMoreGroupUseCase);
        loadMoreMessageUseCase = UseCases.release(loadMoreMessageUseCase);
        compositeDisposable.dispose();
    }

    @Override
    public void loadMoreMessageList(String userId) {
        Log.d(TAG, "loadMoreMessageList");
        this.userId = userId;
        loadSubject.onNext(ACTION.MESSAGE_LOAD_MORE);
    }

    @Override
    public void refreshMessageList(String userId) {
        Log.d(TAG, "refreshMessageList");
        this.userId = userId;
        loadSubject.onNext(ACTION.MESSAGE_REFRESH);
    }

    @Override
    public void refreshGroupList() {
        Log.d(TAG, "refreshGroupList");
        loadSubject.onNext(ACTION.GROUP_REFRESH);
    }

    @Override
    public void loadMoreGroupList() {
        Log.d(TAG, "loadMoreGroupList");
        loadSubject.onNext(ACTION.GROUP_LOAD_MORE);
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
                    Log.d(TAG, throwable.getStackTrace().toString());
                })
                .onStop(() -> {
                    sendMessageUseCase = UseCases.release(sendMessageUseCase);
                })
                .execute();
    }

}
