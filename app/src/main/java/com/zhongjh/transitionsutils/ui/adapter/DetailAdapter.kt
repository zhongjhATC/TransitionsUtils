package com.zhongjh.transitionsutils.ui.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zhongjh.transitionsutils.data.Data
import com.zhongjh.transitionsutils.data.Datas.Companion.transitionName

/**
 * @author zhongjh
 * @date 2022/2/22
 */
class DetailAdapter(var activity: Activity, var items: List<Data>, var currentPosition: Int) : PagerAdapter() {

    var views: SparseArray<View> = SparseArray(items.size)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val data = items[position]
        val imageView = ImageView(container.context)
        // 设置需要共享元素的view
        ViewCompat.setTransitionName(imageView, transitionName(data.position))
        views.put(position, imageView)
        Glide.with(container.context)
                .load(data.originalUrl)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        // 重新恢复共享元素动画
                        ActivityCompat.startPostponedEnterTransition(activity)
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        // 当图片控件都测量完成并确定了框架后，重新恢复共享元素动画
                        if (position == currentPosition) {
                            imageView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                                override fun onPreDraw(): Boolean {
                                    imageView.viewTreeObserver.removeOnPreDrawListener(this)
                                    // 重新恢复共享元素动画
                                    ActivityCompat.startPostponedEnterTransition(activity)
                                    return true
                                }
                            })
                        }
                        return false
                    }
                })
                .into(imageView)
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        views.removeAt(position)
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun getView(position: Int): View {
        return views[position]
    }

}