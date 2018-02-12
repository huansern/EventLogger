package com.github.huansern.eventlogger;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

@Deprecated
public abstract class EndlessScrollingListener extends RecyclerView.OnScrollListener {

    protected LinearLayoutManager mLayoutManager;

    private boolean mLoading = false;

    private int mPreviousItemCount = 0;

    private int mVisibleThreshold = 0;

    protected EndlessScrollingListener(LinearLayoutManager layoutManager, int visibleThreshold) {
        mLayoutManager = layoutManager;
        mVisibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        // Currently this implementation will never know if there is no more data or not
        // It will stuck at the end of the list thinking the list is still loading
        // until the list is refreshed where the previous item count will be more than the total

        int totalItemCount = mLayoutManager.getItemCount();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

        if(mPreviousItemCount > totalItemCount) {
            mLoading = false;
            mPreviousItemCount = totalItemCount;
        }

        if(mLoading && totalItemCount > mPreviousItemCount){
            mLoading = false;
            mPreviousItemCount = totalItemCount;
        }

        if(!mLoading && (lastVisibleItemPosition + mVisibleThreshold) >= totalItemCount){
            mLoading = true;
            loadMore();
        }

    }

    public abstract void loadMore();
}
