package com.gat.feature.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.common.listener.LoadMoreScrollListener;
import com.gat.common.util.Strings;
import com.gat.feature.message.adapter.GroupMessageAdapter;
import com.gat.feature.message.event.RecyclerItemClickListener;
import com.gat.feature.message.item.GroupItem;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 4/1/17.
 */

public class GroupMessageActivity extends ScreenActivity<MessageScreen, MessagePresenter> {
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
        compositeDisposable = new CompositeDisposable(
                getPresenter().itemsChanged().subscribe(this::onItemChanged),
                getPresenter().loadingEvents().subscribe(this::onLoadingEvent)

        );
        groupMessageAdapter = new GroupMessageAdapter();
        recyclerView.setAdapter(groupMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Get group list
        getPresenter().refreshGroupList();

        loadMoreScrollListener = new LoadMoreScrollListener(3, true, () -> {
            if (groupMessageAdapter.hasLoadMoreItem())
                getPresenter().loadMoreGroupList();
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Item item = groupMessageAdapter.getItemAt(position);
                if (!(item instanceof GroupItem))
                    return;
                start(getApplicationContext(), MessageActivity.class, MessageScreen.instance(((GroupItem)item).group().groupId()));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

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
                refreshLayout.setEnabled(!event.refresh() && groupMessageAdapter.getItemCount() > 1);
                break;
            case LoadingEvent.Status.DONE:
                loadMoreScrollListener.setEnable(groupMessageAdapter.hasLoadMoreItem());
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);
                if (event.refresh())
                    recyclerView.scrollToPosition(0);
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
    protected Class<MessagePresenter> getPresenterClass() {
        return MessagePresenter.class;
    }

    @Override
    protected MessageScreen getDefaultScreen() {
        return MessageScreen.instance(Strings.EMPTY);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.group_message_activity;
    }
}


