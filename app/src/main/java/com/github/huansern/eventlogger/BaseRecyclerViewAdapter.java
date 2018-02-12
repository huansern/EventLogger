package com.github.huansern.eventlogger;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import javax.annotation.Nonnull;

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {

    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
        return new BaseViewHolder(binding);
    }

    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Object data = getObjectForPosition(position);
        holder.bind(data);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutForPosition(position);
    }

    protected abstract Object getObjectForPosition(int position);

    protected abstract int getLayoutForPosition(int position);

    class BaseViewHolder extends RecyclerView.ViewHolder{
        private final ViewDataBinding mBinding;

        BaseViewHolder(@Nonnull ViewDataBinding binding){
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(@Nonnull Object data){
            mBinding.setVariable(com.github.huansern.eventlogger.BR.data, data);
            mBinding.executePendingBindings();
        }
    }
}
