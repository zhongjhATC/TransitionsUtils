package com.zhongjh.transitionsutils.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhongjh.transitionsutils.App;
import com.zhongjh.transitionsutils.data.Data;

import java.util.List;

/**
 * @author zhongjh
 * @date 2022/2/22
 */
public class DetailAdapter extends PagerAdapter {

    Activity activity;
    List<Data> items;
    int currentPosition;
    SparseArray<View> views;

    public DetailAdapter(Activity activity, List<Data> items, int currentPosition) {
        this.activity = activity;
        this.items = items;
        this.currentPosition = currentPosition;
        views = new SparseArray<>(items.size());
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Data data = items.get(position);
        ImageView imageView = new ImageView(App.getInstance());
        // 设置需要共享元素的view
        ViewCompat.setTransitionName(imageView, Data.transitionName(data.getPosition()));
        views.put(position, imageView);
        Glide.with(App.getInstance())
                .load(data.getOriginalUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // 重新恢复共享元素动画
                        ActivityCompat.startPostponedEnterTransition(activity);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // 当图片控件都测量完成并确定了框架后，重新恢复共享元素动画
                        if (position == currentPosition) {
                            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                                @Override
                                public boolean onPreDraw() {
                                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                                    // 重新恢复共享元素动画
                                    ActivityCompat.startPostponedEnterTransition(activity);
                                    return true;
                                }
                            });
                        }
                        return false;
                    }
                })
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        views.removeAt(position);
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public View getView(int position) {
        return views.get(position);
    }
}
