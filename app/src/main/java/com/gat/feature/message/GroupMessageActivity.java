package com.gat.feature.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.common.listener.LoadMoreScrollListener;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Strings;
import com.gat.feature.message.adapter.GroupMessageAdapter;
import com.gat.feature.message.event.RecyclerItemClickListener;
import com.gat.feature.message.item.GroupItem;
import com.gat.feature.message.presenter.GroupMessagePresenter;
import com.gat.feature.message.presenter.GroupMessageScreen;
import com.gat.repository.entity.Group;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class GroupMessageActivity extends ScreenActivity<GroupMessageScreen, GroupMessagePresenter> {
    private final String TAG = GroupMessageActivity.class.getSimpleName();

    @BindView(R.id.message_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.message_recycler)
    RecyclerView recyclerView;

    private LoadMoreScrollListener loadMoreScrollListener;
    private GroupMessageAdapter groupMessageAdapter;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClientUtils.context = getApplicationContext();  // TODO remove after
        compositeDisposable = new CompositeDisposable(
                getPresenter().itemsChanged().subscribe(this::onItemChanged),
                getPresenter().loadingEvents().subscribe(this::onLoadingEvent)

        );
        groupMessageAdapter = new GroupMessageAdapter();
        recyclerView.setAdapter(groupMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        loadMoreScrollListener = new LoadMoreScrollListener(3, false, () -> {
            //if (groupMessageAdapter.hasLoadMoreItem())
            //    getPresenter().loadMoreGroupList();
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                Item item = groupMessageAdapter.getItemAt(position);
                if (!(item instanceof GroupItem))
                    return;
                Log.d(TAG, "clicked:" + position);
                start(getApplicationContext(),
                        MessageActivity.class,
                        MessageScreen.instance(
                                ((GroupItem)item).group().userName(),
                                Integer.parseInt(((GroupItem)item).group().users().get(0))
                        )
                );

            }
        }));

        // Get group list
        getPresenter().refreshGroupList();

        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.backgroundCard);

        refreshLayout.setOnRefreshListener(() -> getPresenter().refreshGroupList());

    }

    private void onItemChanged(ItemResult result) {
        if (groupMessageAdapter.setItem(result.items()) && result.diffResult() != null)
            result.diffResult().dispatchUpdatesTo(groupMessageAdapter);
    }

    private void onLoadingEvent(LoadingEvent event) {
        switch (event.status()) {
            case LoadingEvent.Status.BEGIN:
                loadMoreScrollListener.setEnable(false);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
                break;
            case LoadingEvent.Status.DONE:
                loadMoreScrollListener.setEnable(groupMessageAdapter.hasLoadMoreItem());
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
                if (event.refresh())
                    recyclerView.scrollToPosition(0);
                break;
            case LoadingEvent.Status.COMPLETE:
                loadMoreScrollListener.setEnable(false);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
                if (event.refresh())
                    recyclerView.scrollToPosition(0);
                groupMessageAdapter.completeLoading();
                break;
            case LoadingEvent.Status.ERROR:
                loadMoreScrollListener.setEnable(false);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        recyclerView.clearOnScrollListeners();
        recyclerView.setAdapter(null);
        recyclerView.getRecycledViewPool().clear();
        groupMessageAdapter = null;
        loadMoreScrollListener = null;
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume");
        getPresenter().refreshGroupList();
    }

    @Override
    protected Class<GroupMessagePresenter> getPresenterClass() {
        return GroupMessagePresenter.class;
    }

    @Override
    protected GroupMessageScreen getDefaultScreen() {
        return GroupMessageScreen.instance();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.group_message_activity;
    }
}


