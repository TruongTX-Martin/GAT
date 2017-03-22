package com.gat.common.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Rey on 7/1/2015.
 * A scroll listener class that help determine when to load more content.
 */
public class LoadMoreScrollListener extends RecyclerView.OnScrollListener{

    private int threshold;
    private Runnable loadMoreAction;
    private boolean enable;

    /**
     * @param threshold The max number of item below the last visible position before start loading more.
     * @param action The action will run when scroll pass threshold.
     */
    public LoadMoreScrollListener(int threshold, boolean enable, Runnable action){
        this.threshold = threshold;
        this.enable = enable;
        loadMoreAction = action;
    }

    public void setEnable(boolean enable){
        this.enable = enable;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if(!enable)
            return;

        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int lastVisiblePosition = findLastVisibleItemPosition(recyclerView);

        if(lastVisiblePosition < 0 || totalItemCount == 0)
            return;

        if(lastVisiblePosition >= totalItemCount - threshold)
            loadMoreAction.run();
    }

    private int findLastVisibleItemPosition(RecyclerView recyclerView){
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if(layoutManager instanceof LinearLayoutManager)
            return ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

        return recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
    }
}
