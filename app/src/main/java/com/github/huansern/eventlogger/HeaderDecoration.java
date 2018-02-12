package com.github.huansern.eventlogger;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class HeaderDecoration extends RecyclerView.ItemDecoration {

    private final Map<Long, ViewHolder> mCache;
    private final HeaderAdapter mAdapter;

    @SuppressLint("UseSparseArrays")
    public HeaderDecoration(HeaderAdapter adapter) {
        mCache = new HashMap<>();
        mAdapter = adapter;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int headerHeight = 0;

        if(position != RecyclerView.NO_POSITION && mAdapter.hasHeader(position)) {
            View header = getHeaderViewHolder(parent, position).itemView;
            headerHeight = header.getHeight();
        }

        outRect.set(0, headerHeight, 0, 0);
    }

    private ViewHolder getHeaderViewHolder(RecyclerView parent, int position) {
        final long key = mAdapter.getHeaderId(position);

        if(mCache.containsKey(key)) {
            return mCache.get(key);
        } else {
            final ViewHolder holder = mAdapter.onCreateHeaderViewHolder(parent);
            final View header = holder.itemView;

            mAdapter.onBindHeaderViewHolder(holder, position);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft()
                            + parent.getPaddingRight(), header.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop()
                            + parent.getPaddingBottom(), header.getLayoutParams().height);

            header.measure(childWidth, childHeight);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());

            mCache.put(key, holder);

            return holder;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int visibleViewCount = parent.getChildCount();

        for(int layoutPosition = 0; layoutPosition < visibleViewCount; layoutPosition++) {
            final View child = parent.getChildAt(layoutPosition);
            final int position = parent.getChildAdapterPosition(child);

            if(mAdapter.hasHeader(position)) {
                View header = getHeaderViewHolder(parent, position).itemView;
                c.save();
                final int left = child.getLeft();
                final int top = (int) child.getY() - header.getHeight();
                c.translate(left, top);
                header.draw(c);
                c.restore();
            }
        }

    }

    public interface HeaderAdapter<T extends ViewHolder> {

        boolean hasHeader(int position);

        long getHeaderId(int position);

        T onCreateHeaderViewHolder(ViewGroup parent);

        void onBindHeaderViewHolder(T viewHolder, int position);

    }

}
