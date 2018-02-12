package com.github.huansern.eventlogger.home;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.huansern.eventlogger.BaseRecyclerViewAdapter;
import com.github.huansern.eventlogger.HeaderDecoration;
import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.data.EventLog;

import java.util.Arrays;
import java.util.Calendar;

import javax.annotation.Nonnull;

public class EventLogsAdapter extends BaseRecyclerViewAdapter
        implements HeaderDecoration.HeaderAdapter<EventLogsAdapter.HeaderViewHolder> {

    private ObservableList<EventLog> mEventLogs;

    private static final String categoryService = "SERVICE", categorySession = "SESSION",
            actionSessionStart = "EL.session.start", actionSessionEnd = "EL.session.end";

    private ObservableList.OnListChangedCallback mCallback =
            new ObservableList.OnListChangedCallback() {
                @Override
                public void onChanged(ObservableList observableList) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList observableList, int positionStart, int itemCount) {
                    notifyItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeInserted(ObservableList observableList, int positionStart, int itemCount) {
                    notifyItemRangeInserted(positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(ObservableList observableList, int fromPosition, int toPosition, int itemCount) {
                    notifyItemRangeRemoved(fromPosition, itemCount);
                    notifyItemRangeInserted(toPosition, itemCount);
                }

                @Override
                public void onItemRangeRemoved(ObservableList observableList, int positionStart, int itemCount) {
                    notifyItemRangeRemoved(positionStart, itemCount);
                }
            };

    private Calendar mCalendar;

    EventLogsAdapter(ObservableList<EventLog> EventLogs){
        mCalendar = Calendar.getInstance();
        mEventLogs = EventLogs;
        mEventLogs.addOnListChangedCallback(mCallback);
        this.hasStableIds();
    }

    @Override
    public int getItemCount() {
        return mEventLogs != null ? mEventLogs.size() : 0;
    }

    @Override
    protected Object getObjectForPosition(int position) {
        return mEventLogs.get(position);
    }

    @Override
    protected int getLayoutForPosition(int position) {
        switch (mEventLogs.get(position).getCategory()) {
            case categoryService:
                return R.layout.event_log_service_label;
            case categorySession:
                if(actionSessionStart.equals(mEventLogs.get(position).getAction())) {
                    return R.layout.event_log_session_start;
                } else if(actionSessionEnd.equals(mEventLogs.get(position).getAction())){
                    return R.layout.event_log_session_end;
                }
            default:
                return R.layout.event_log_item;
        }
    }

    @Override
    public long getItemId(int position) {
        return mEventLogs.get(position).getId();
    }

    String getHeaderText(int position) {
        if(mEventLogs.size() == 0) return "";
        if(position < 0 || position >= mEventLogs.size()) return "";
        return mEventLogs.get(position).getDateText();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding mBinding;

        HeaderViewHolder(@Nonnull ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(String date) {
            mBinding.setVariable(com.github.huansern.eventlogger.BR.date, date);
            mBinding.executePendingBindings();
        }
    }

    @Override
    public boolean hasHeader(int position) {
        return !(position < 0 || position >= mEventLogs.size())
                && (position == 0 || getHeaderId(position - 1) != getHeaderId(position));
    }

    @Override
    public long getHeaderId(int position) {
        mCalendar.setTime(mEventLogs.get(position).getDate());
        return Arrays.hashCode(new int[]{mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.DAY_OF_YEAR)});
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.generic_header, parent, false);
        return new HeaderViewHolder(binding);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        viewHolder.bind(getHeaderText(position));
    }
}
