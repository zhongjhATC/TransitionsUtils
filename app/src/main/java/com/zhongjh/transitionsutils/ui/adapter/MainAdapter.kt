package com.zhongjh.transitionsutils.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zhongjh.transitionsutils.data.Data
import com.zhongjh.transitionsutils.data.Datas.Companion.transitionName

/**
 * @author zhongjh
 * @date 2022/2/22
 */
class MainAdapter(private val mDataList: List<Data>, private val mOnItemClickListener: OnItemClickListener) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(view: View?, item: Data?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindViewHolder(mDataList[position], mOnItemClickListener)
    }

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ImageView(parent.context)) {
        fun onBindViewHolder(item: Data, onItemClickListener: OnItemClickListener) {
            itemView.setOnClickListener { view: View? -> onItemClickListener.onClick(itemView, item) }
            // 设置需要共享元素的view
            ViewCompat.setTransitionName(itemView, transitionName(item.position))
            Glide.with(itemView.context)
                    .load(item.url)
                    .into((itemView as ImageView))
        }
    }

}