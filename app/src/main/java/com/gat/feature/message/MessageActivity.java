package com.gat.feature.message;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.common.listener.LoadMoreScrollListener;
import com.gat.common.util.Strings;
import com.gat.feature.message.adapter.MessageAdapter;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 3/27/17.
 */

public class MessageActivity extends ScreenActivity<MessageScreen, MessagePresenter> {
    private final String GROUP_ID = "group_id";

    @BindView(R.id.message_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.message_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.message_edit)
    EditText messageEdit;

    @BindView(R.id.message_send)
    Button messageSendBtn;

    private LoadMoreScrollListener loadMoreScrollListener;
    private MessageAdapter messageAdapter;

    private CompositeDisposable compositeDisposable;

    private String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            groupId = getScreen().groupId();
        else
            groupId = savedInstanceState.getString(GROUP_ID);

        compositeDisposable = new CompositeDisposable(
                getPresenter().itemsChanged().subscribe(this::onItemChanged),
                getPresenter().loadingEvents().subscribe(this::onLoadingEvent)

        );
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        loadMoreScrollListener = new LoadMoreScrollListener(3, true, () -> {
            if (messageAdapter.hasLoadMoreItem())
                getPresenter().loadMoreMessageList(groupId);
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);

        getPresenter().refreshMessageList(groupId);

        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.backgroundCard);
        refreshLayout.setOnRefreshListener(() -> getPresenter().refreshMessageList(groupId));

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(GROUP_ID, groupId);
    }

    private void onItemChanged(ItemResult result) {
        if (messageAdapter.setItem(result.items()) && result.diffResult() != null)
            result.diffResult().dispatchUpdatesTo(messageAdapter);
    }

    private void onLoadingEvent(LoadingEvent event) {
        switch (event.status()) {
            case LoadingEvent.Status.BEGIN:
                loadMoreScrollListener.setEnable(false);
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(!event.refresh() && messageAdapter.getItemCount() > 1);
                break;
            case LoadingEvent.Status.DONE:
                loadMoreScrollListener.setEnable(messageAdapter.hasLoadMoreItem());
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
        messageAdapter = null;
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
        return R.layout.message_activity;
    }
}
