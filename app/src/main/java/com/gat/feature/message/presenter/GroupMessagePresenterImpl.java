package com.gat.feature.message.presenter;

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
import com.gat.repository.entity.Group;

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
 * Created by ducbtsn on 4/24/17.
 */

public class GroupMessagePresenterImpl implements GroupMessagePresenter {
    private static final String TAG = GroupMessagePresenterImpl.class.getSimpleName();

    public @interface TYPE {
        int REFRESH = 1;
        int LOAD_MORE = 2;
    }

    private static final int GROUP_SIZE = 20;

    // Factory
    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;
    private final Scheduler worker;

    // Result observable
    private Subject<LoadingEvent> loadingEventSubject;
    private Subject<ItemResult> itemResultSubject;

    // Start load observer
    private Subject<Integer> loadSubject;

    private UseCase<List<Group>> getGroupUseCase;
    //private UseCase<List<Group>> loadMoreGroupUseCase;
    private UseCase<ItemResult> loadingUseCase;

    private CompositeDisposable compositeDisposable;

    private int loadingPageCnt;

    public GroupMessagePresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        this.worker = schedulerFactory.single();

        this.loadingEventSubject = BehaviorSubject.create();
        this.itemResultSubject = BehaviorSubject.createDefault(ItemBuilder.defaultItems());
        this.loadSubject = BehaviorSubject.create();

        this.loadingPageCnt = 0;
    }

    /**
     * Get group list from firebase
     * @param refresh
     * @param clear
     */
    private void getGroupList (boolean refresh, boolean clear) {
        Log.d(TAG, "getGroupList:" + refresh + "," + clear);

        // during loading
        if (loadingUseCase != null) {
            Log.d(TAG, "group is loading.");
            return;
        }

        loadingPageCnt = refresh ? 1 : loadingPageCnt + 1;

        getGroupUseCase = UseCases.release(getGroupUseCase);

        getGroupUseCase = useCaseFactory.getGroupList(loadingPageCnt, GROUP_SIZE);

        ObservableTransformer<List<Group>, ItemResult> transformer =
                upstream -> upstream
                        .map( groupList -> {
                            List<Item> items = itemResultSubject.blockingFirst().items();
                            Log.d(TAG, "Transformer:" + groupList.size() + "," + items.size());
                            ItemResult itemResult = new GroupItemBuilder().addList(items, groupList, refresh, groupList.size() >= GROUP_SIZE);
                            itemResultSubject.onNext(itemResult);
                            return itemResult;
                        });
        loadingUseCase = useCaseFactory.transform(getGroupUseCase, transformer, worker)
                .executeOn(schedulerFactory.io())
                .onNext(itemList -> {
                    Log.d(TAG, "Loading has new item.");
                    loadingEventSubject.onNext(LoadingEvent.builder()
                            .refresh(refresh)
                            .status(LoadingEvent.Status.DONE)
                            .build()
                    );
                })
                .onError(throwable -> {
                    Log.d(TAG, "Loading error.");
                    showLoadingItem(refresh, clear, LoadingMessage.Message.ERROR);
                    // Release all use case
                    loadingUseCase = UseCases.release(loadingUseCase);
                    getGroupUseCase = UseCases.release(getGroupUseCase);
                })
                .onComplete(() -> {
                    Log.d(TAG, "Loading completed.");
                    showLoadingItem(refresh, clear, LoadingMessage.Message.COMPLETED);
                    // Release all use case
                    loadingUseCase = UseCases.release(loadingUseCase);
                    getGroupUseCase = UseCases.release(getGroupUseCase);
                })
                .onStop(() -> {
                    Log.d(TAG, "Loading stopped.");
                    showLoadingItem(refresh, clear, LoadingMessage.Message.COMPLETED);
                    // Release all use case
                    loadingUseCase = UseCases.release(loadingUseCase);
                    getGroupUseCase = UseCases.release(getGroupUseCase);
                })
                .execute();
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
                .onError(throwable -> Timber.d(throwable, "Error show loading."))
                .execute();
    }

    private void getGroupList(int type) {
        switch (type) {
            case TYPE.REFRESH:
                getGroupList(true, true);
                break;
            case TYPE.LOAD_MORE:
                getGroupList(false, true);
                break;
            default:
                break;
        }
    }

    /*private void groupUpdate() {
        groupUpdateUseCase = UseCases.release(groupUpdateUseCase);

        groupUpdateUseCase = useCaseFactory.groupUpdate();
        GroupItemBuilder itemBuilder = new GroupItemBuilder();
        ObservableTransformer<Group, ItemResult> transformer =
                upstream -> upstream
                        .map(group -> {
                            List<Item> items = itemResultSubject.blockingFirst().items();
                            Log.d(TAG, "ListSize:" + items.size());
                            ItemResult result = itemBuilder.updateList(items, group);
                            itemResultSubject.onNext(result);
                            return result;}
                        );

        UseCase<ItemResult> updateItemUseCase = useCaseFactory.transform(groupUpdateUseCase, transformer, worker)
                .executeOn(schedulerFactory.io())
                .onNext(list -> {
                })
                .onError(throwable -> {
                    showLoadingItem(true, true, LoadingMessage.Message.ERROR);
                    Timber.d(throwable, "Error loading message");
                })
                .onComplete(() -> {
                })
                .onStop(() -> {
                    groupUpdateUseCase = UseCases.release(groupUpdateUseCase);
                })
                .execute();

    }*/

    @Override
    public void refreshGroupList() {
        loadSubject.onNext(TYPE.REFRESH);
    }

    @Override
    public void loadMoreGroupList() {
        loadSubject.onNext(TYPE.LOAD_MORE);
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
    public void onCreate() {
        compositeDisposable = new CompositeDisposable(
                loadSubject.observeOn(schedulerFactory.main()).subscribe(this::getGroupList)
        );
    }

    @Override
    public void onDestroy() {
        getGroupUseCase = UseCases.release(getGroupUseCase);
        loadingUseCase = UseCases.release(loadingUseCase);

        compositeDisposable.dispose();
    }
}
