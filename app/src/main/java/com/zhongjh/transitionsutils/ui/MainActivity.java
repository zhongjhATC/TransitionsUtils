package com.zhongjh.transitionsutils.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.zhongjh.transitionsutils.R;
import com.zhongjh.transitionsutils.data.Data;
import com.zhongjh.transitionsutils.data.Datas;
import com.zhongjh.transitionsutils.ui.adapter.MainAdapter;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnItemClickListener {

    final static String EXTRA_STARTING_POSITION = "extra_starting_position";
    final static String EXTRA_CURRENT_POSITION = "extra_current_position";

    Toolbar mToolbar;
    RecyclerView mRvImage;

    Bundle mReenterBundle;

    /**
     * 这是从DetailActivity跳回到MainActivity时改变共享元素
     */
    SharedElementCallback exitElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mReenterBundle != null) {
                super.onMapSharedElements(names, sharedElements);
                int startingPosition = mReenterBundle.getInt(EXTRA_STARTING_POSITION);
                int currentPosition = mReenterBundle.getInt(EXTRA_CURRENT_POSITION);
                // 当前元素已更改，需要重写以前的退出转换的共享单位
                if (startingPosition != currentPosition) {
                    String newTransitionName = Data.transitionName(Datas.getInstance().getDatas().get(currentPosition).getPosition());
                    View view = mRvImage.findViewWithTag(newTransitionName);
                    if (view != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, view);
                    }
                }
                mReenterBundle = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        mRvImage = findViewById(R.id.rvImage);
        setSupportActionBar(mToolbar);
        ActivityCompat.setExitSharedElementCallback(this, exitElementCallback);

        mRvImage.setHasFixedSize(true);
        mRvImage.setLayoutManager(new GridLayoutManager(this, 2));
        mRvImage.setAdapter(new MainAdapter(Datas.getInstance().getDatas(), this));
        mRvImage.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRvImage.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
    }

    /**
     * 如果用到共享元素，那么返回的时候会多调用到这个方法，类似于onActivityResult
     */
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mReenterBundle = new Bundle(data.getExtras());
        int startingPosition = mReenterBundle.getInt(EXTRA_STARTING_POSITION);
        int currentPosition = mReenterBundle.getInt(EXTRA_CURRENT_POSITION);
        // 当前元素已更改，就滑动列表到当前的position
        if (startingPosition != currentPosition) {
            mRvImage.scrollToPosition(currentPosition);
        }
        // 暂时阻止启动共享元素Transition动画
        ActivityCompat.postponeEnterTransition(this);
        // 当列表所有的视图都测量完成并确定了框架后重新恢复共享元素动画
        mRvImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRvImage.getViewTreeObserver().removeOnPreDrawListener(this);
                // 重新恢复共享元素动画
                ActivityCompat.startPostponedEnterTransition(MainActivity.this);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view, Data item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_DATA, item);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String name = ViewCompat.getTransitionName(view);
        Bundle bundle = new Bundle();
        if (name != null) {
            bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, name).toBundle();
        }
        startActivity(intent, bundle);
    }
}