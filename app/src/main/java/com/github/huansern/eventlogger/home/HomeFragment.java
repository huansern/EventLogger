package com.github.huansern.eventlogger.home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.huansern.eventlogger.HeaderDecoration;
import com.github.huansern.eventlogger.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;

    private FragmentHomeBinding mBinding;

    private EventLogsAdapter mEventLogsAdapter;

    private int mVisibleThreshold = 10;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);

        mViewModel = HomeActivity.obtainViewModel(getActivity());

        mBinding.setViewModel(mViewModel);

        mBinding.setFragment(this);

        mBinding.setDateHeader(mViewModel.dateHeaderText);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRecyclerViewAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start();
    }

    private void setupRecyclerViewAdapter() {
        RecyclerView recyclerView = mBinding.homeRecyclerView;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new HomeScrollListener(linearLayoutManager, mVisibleThreshold));

        mEventLogsAdapter = new EventLogsAdapter(mViewModel.eventLogsData);

        recyclerView.setAdapter(mEventLogsAdapter);

        recyclerView.addItemDecoration(new HeaderDecoration(mEventLogsAdapter));
    }

    private class HomeScrollListener extends RecyclerView.OnScrollListener {

        private LinearLayoutManager mLayoutManager;

        private int mVisibleThreshold = 0;

        private int mLastPositionId = 0;

        HomeScrollListener(LinearLayoutManager layoutManager, int visibleThreshold) {
            mLayoutManager = layoutManager;
            mVisibleThreshold = visibleThreshold;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            int positionId = mLayoutManager.findFirstVisibleItemPosition();

            if(mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                mViewModel.scrollToTopButtonVisibility.set(View.GONE);
            } else {
                mViewModel.scrollToTopButtonVisibility.set(View.VISIBLE);
            }

            if(mLastPositionId != positionId) {
                mViewModel.dateHeaderText.set(mEventLogsAdapter.getHeaderText(positionId));
            }

            mLastPositionId = positionId;

            // If user is not scrolling up(revealing the view below, return)
            if(!(dy > 0)) {
                return;
            }

            int totalItemCount = mLayoutManager.getItemCount();
            int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

            if(!mViewModel.dataLoading.get() && !mViewModel.endOfList.get()
                    && (lastVisibleItemPosition + mVisibleThreshold) >= totalItemCount){
                mViewModel.loadMore();
            }

        }

    }

    public void scrollToTop() {
        mBinding.homeRecyclerView.smoothScrollToPosition(0);
    }

}
