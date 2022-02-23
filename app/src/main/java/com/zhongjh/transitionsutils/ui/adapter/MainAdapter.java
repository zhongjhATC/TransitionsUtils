package com.zhongjh.transitionsutils.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zhongjh.transitionsutils.data.Data;

import java.util.List;

/**
 * @author zhongjh
 * @date 2022/2/22
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final List<Data> mDataList;
    private final OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, Data item);
    }

    public MainAdapter(List<Data> dataList, OnItemClickListener onItemClickListener) {
        this.mDataList = dataList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindViewHolder(mDataList.get(position), mOnItemClickListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(ViewGroup parent) {
            super(new ImageView(parent.getContext()));
        }

        public void onBindViewHolder(Data item, OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(view -> onItemClickListener.onClick(itemView, item));
            // 设置需要共享元素的view
            ViewCompat.setTransitionName(itemView, Data.transitionName(item.getPosition()));
            Glide.with(itemView.getContext())
                    .load(item.getUrl())
                    .into((ImageView) itemView);
        }

    }

}
