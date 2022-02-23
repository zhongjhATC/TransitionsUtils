package com.zhongjh.transitionsutils.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import com.zhongjh.transitionsutils.R;
import com.zhongjh.transitionsutils.data.Data;
import com.zhongjh.transitionsutils.data.Datas;
import com.zhongjh.transitionsutils.ui.adapter.DetailAdapter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zhongjh.transitionsutils.ui.MainActivity.EXTRA_CURRENT_POSITION;
import static com.zhongjh.transitionsutils.ui.MainActivity.EXTRA_STARTING_POSITION;

/**
 * @author zhongjh
 * @date 2022/2/22
 */
public class DetailActivity extends AppCompatActivity {

    final static String EXTRA_DATA = "extra_data";
    final static String SAVED_CURRENT_POSITION = "saved_current_position";

    Toolbar mToolbar;
    ViewPager mViewPager;

    /**
     * 是否正在退出
     */
    private boolean isReturning = false;
    private int mStartingPosition = 0;
    private int mCurrentPosition = 0;
    DetailAdapter mDetailAdapter;

    /**
     * 共享元素
     */
    SharedElementCallback enterElementCallback = new SharedElementCallback() {

        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (isReturning) {
                View view = mDetailAdapter.getView(mCurrentPosition);
                // 当前元素已更改，转换共享单位
                if (mStartingPosition != mCurrentPosition) {
                    names.clear();
                    names.add(ViewCompat.getTransitionName(view));
                    sharedElements.clear();
                    sharedElements.put(ViewCompat.getTransitionName(view), view);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mToolbar = findViewById(R.id.toolbar);
        mViewPager = findViewById(R.id.viewPager);
        setupToolBar();
        // 暂时阻止启动共享元素Transition动画
        ActivityCompat.postponeEnterTransition(this);
        ActivityCompat.setEnterSharedElementCallback(this, enterElementCallback);

        if (getIntent() != null) {
            Data data = getIntent().getParcelableExtra(EXTRA_DATA);
            mStartingPosition = data.getPosition();
            if (savedInstanceState == null) {
                mCurrentPosition = mStartingPosition;
            } else {
                mCurrentPosition = savedInstanceState.getInt(SAVED_CURRENT_POSITION);
            }
            mDetailAdapter = new DetailAdapter(DetailActivity.this, Datas.getInstance().getDatas(), mCurrentPosition);
            mViewPager.setAdapter(mDetailAdapter);
            mViewPager.setCurrentItem(mCurrentPosition);
            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    mCurrentPosition = position;
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // 这个只是用于记录界面因为内存不够等别的原因销毁后，重新创建时保存相关数值
        outState.putInt(SAVED_CURRENT_POSITION, mCurrentPosition);
    }

    @Override
    public void finishAfterTransition() {
        isReturning = true;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STARTING_POSITION, mStartingPosition);
        intent.putExtra(EXTRA_CURRENT_POSITION, mCurrentPosition);
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    protected void onDestroy() {
        removeActivityFromTransitionManager(this);
        super.onDestroy();
    }

    /**
     * 防止内存泄漏
     */
    private static void removeActivityFromTransitionManager(Activity activity) {
        Class<TransitionManager> transitionManagerClass = TransitionManager.class;
        try {
            Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            //noinspection unchecked
            ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> runningTransitions
                    = (ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>)
                    runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions == null) {
                return;
            }
            if (runningTransitions.get() == null) {
                return;
            }
            if (runningTransitions.get().get() == null) {
                return;
            }
            ArrayMap<ViewGroup, ArrayList<Transition>> map = runningTransitions.get().get();
            View decorView = activity.getWindow().getDecorView();
            if (map.containsKey(decorView)) {
                map.remove(decorView);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
