package com.gat.feature.notification.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ItemResult;
import com.gat.common.event.LoadingEvent;
import com.gat.common.listener.LoadMoreScrollListener;
import com.gat.repository.entity.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 3/27/17.
 */

public class MessageActivity extends ScreenActivity<MessageScreen, MessagePresenter> {

    @BindView(R.id.message_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.message_recycler)
    RecyclerView recyclerView;

    private LoadMoreScrollListener loadMoreScrollListener;
    private MessageAdapter messageAdapter;

    private CompositeDisposable compositeDisposable;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        compositeDisposable = new CompositeDisposable(
                getPresenter().itemsChanged().subscribe(this::onItemChanged),
                getPresenter().loadingEvents().subscribe(this::onLoadingEvent)

        );
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        loadMoreScrollListener = new LoadMoreScrollListener(3, true, () -> {
            if (messageAdapter.hasLoadMoreItem())
                getPresenter().loadMoreMessageList();
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);

        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.backgroundCard);
        refreshLayout.setOnRefreshListener(() -> getPresenter().refreshMessageList());

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
        super.onDestroy();
        recyclerView.clearOnScrollListeners();
        recyclerView.setAdapter(null);
        recyclerView.getRecycledViewPool().clear();
        messageAdapter = null;
        loadMoreScrollListener = null;
        unbinder.unbind();
        compositeDisposable.dispose();
    }

    @Override
    protected Class<MessagePresenter> getPresenterClass() {
        return MessagePresenter.class;
    }

    @Override
    protected MessageScreen getDefaultScreen() {
        return MessageScreen.instance();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.message_activity;
    }
}
