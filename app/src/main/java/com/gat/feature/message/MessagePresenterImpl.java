package com.gat.feature.message;

import android.support.design.widget.TabLayout;
import android.util.Log;

import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.common.util.Strings;
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

    private final int GROUP_LIST_SIZE = 10;
    private final int MESSAGE_LIST_SIZE = 10;
    private int pageCnt;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;
    private final Scheduler worker;

    private Subject<LoadingEvent> loadingEventSubject;
    private Subject<ItemResult> itemResultSubject;

    private UseCase<ItemResult> loadMessageUseCase;

    UseCase<List<Group>> getGroupUseCase;

    public MessagePresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        this.worker = this.schedulerFactory.single();

        this.loadingEventSubject = BehaviorSubject.create();
        this.itemResultSubject = BehaviorSubject.createDefault(ItemBuilder.defaultItems());

        pageCnt = 0;

    }

    private void getMessageList(int type, String groupId) {
        Log.d(TAG, "getMessageList");
        loadingEventSubject.onNext(
                LoadingEvent.builder()
                        .refresh(type == ACTION.MESSAGE_REFRESH ? true : false)
                        .status(LoadingEvent.Status.BEGIN).build()
        );
        MessageItemBuilder itemBuilder = new MessageItemBuilder();
        UseCase<List<Message>> getMessageUseCase = useCaseFactory.getMessageList(groupId);
        ObservableTransformer<List<Message>, ItemResult> transformer =
                upstream -> upstream
                        .filter(list -> list.size() > 0)
                        .map(messages -> {
                            List<Item> items = itemResultSubject.blockingLatest().iterator().next().items();
                            ItemResult result = itemBuilder.addList(items, messages,
                                    (type == ACTION.MESSAGE_REFRESH) ? true : false,
                                    messages.size() >= GROUP_LIST_SIZE);
                            itemResultSubject.onNext(result);
                            return result;
                        });

        loadMessageUseCase = useCaseFactory.transform(getMessageUseCase, transformer,worker)
                .executeOn(schedulerFactory.io())
                .onNext(list -> {
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(type == ACTION.MESSAGE_REFRESH ? true : false)
                            .status(LoadingEvent.Status.DONE)
                            .build();
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> {
                    showLoadingItem((type == ACTION.MESSAGE_REFRESH) ? true : false, true, LoadingMessage.Message.ERROR);
                    Timber.d(throwable, "Error loading message");
                })
                .onStop(() -> {
                    Log.d(TAG, "Message Stop");
                    UseCases.release(getMessageUseCase);
                    loadMessageUseCase = UseCases.release(loadMessageUseCase);
                })
                .execute();
    }

    private void getGroupList(Integer type) {
        Log.d(TAG, "getGroupList");
        loadingEventSubject.onNext(
                LoadingEvent.builder()
                        .refresh(type == ACTION.GROUP_REFRESH ? true : false)
                        .status(LoadingEvent.Status.BEGIN).build()
        );
        pageCnt = (type == ACTION.GROUP_REFRESH) ? 0 : pageCnt++;

        getGroupUseCase = UseCases.release(getGroupUseCase);
        getGroupUseCase = useCaseFactory.getGroupList();
        GroupItemBuilder itemBuilder = new GroupItemBuilder();
        ObservableTransformer<List<Group>, ItemResult> transformer =
                upstream -> upstream
                        .filter(list -> list.size() > 0)
                        .map(groups -> {
                            List<Item> items = itemResultSubject.blockingLatest().iterator().next().items();
                            Log.d(TAG, "ListSize:" + items.size());
                            ItemResult result = itemBuilder.addList(items, groups,
                                    (type == ACTION.GROUP_REFRESH) ? true : false,
                                    groups.size() >= GROUP_LIST_SIZE);
                            itemResultSubject.onNext(result);
                            return result;
                        });

        loadMessageUseCase = useCaseFactory.transform(getGroupUseCase, transformer,worker)
                .executeOn(schedulerFactory.io())
                .onNext(list -> {
                    LoadingEvent event = LoadingEvent.builder()
                            .refresh(type == ACTION.GROUP_REFRESH ? true : false)
                            .status(LoadingEvent.Status.DONE)
                            .build();
                    Log.d(TAG, "EventDone");
                    loadingEventSubject.onNext(event);
                })
                .onError(throwable -> {
                    showLoadingItem((type == ACTION.GROUP_REFRESH) ? true : false, true, LoadingMessage.Message.ERROR);
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
    }

    @Override
    public void onDestroy() {
        loadMessageUseCase = UseCases.release(loadMessageUseCase);
        getGroupUseCase = UseCases.release(getGroupUseCase);
    }

    @Override
    public void loadMoreMessageList(String groupId) {
        Log.d(TAG, "loadMoreMessageList");
        getMessageList(ACTION.MESSAGE_LOAD_MORE,groupId);
    }

    @Override
    public void refreshMessageList(String groupId) {
        Log.d(TAG, "refreshMessageList");
        getMessageList(ACTION.MESSAGE_REFRESH, groupId);
    }

    @Override
    public void refreshGroupList() {
        Log.d(TAG, "refreshGroupList");
        getGroupList(ACTION.GROUP_REFRESH);
    }

    @Override
    public void loadMoreGroupList() {
        Log.d(TAG, "loadMoreGroupList");
        getGroupList(ACTION.GROUP_LOAD_MORE);
    }

    @Override
    public Observable<ItemResult> hasNewItems() {
        return null;
    }

    @Override
    public Observable<LoadingEvent> loadingEvents() {
        return loadingEventSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<ItemResult> itemsChanged() {
        return itemResultSubject.observeOn(schedulerFactory.main());
    }
}
