package com.zhongjh.transitionsutils.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zhongjh.transitionsutils.R
import com.zhongjh.transitionsutils.data.Data
import com.zhongjh.transitionsutils.data.Datas
import com.zhongjh.transitionsutils.data.Datas.Companion.transitionName
import com.zhongjh.transitionsutils.ui.MainActivity
import com.zhongjh.transitionsutils.ui.adapter.MainAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainAdapter.OnItemClickListener {
    var mReenterBundle: Bundle? = null

    /**
     * 这是从DetailActivity跳回到MainActivity时改变共享元素
     */
    var exitElementCallback: SharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (mReenterBundle != null) {
                super.onMapSharedElements(names, sharedElements)
                val startingPosition = mReenterBundle!!.getInt(EXTRA_STARTING_POSITION)
                val currentPosition = mReenterBundle!!.getInt(EXTRA_CURRENT_POSITION)
                // 当前元素已更改，需要重写以前的退出转换的共享单位
                if (startingPosition != currentPosition) {
                    val newTransitionName = transitionName(Datas.instance!!.datas[currentPosition].position)
                    val view = rvImage!!.findViewWithTag<View>(newTransitionName)
                    if (view != null) {
                        names.clear()
                        names.add(newTransitionName)
                        sharedElements.clear()
                        sharedElements[newTransitionName] = view
                    }
                }
                mReenterBundle = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        ActivityCompat.setExitSharedElementCallback(this, exitElementCallback)
        rvImage.setHasFixedSize(true)
        rvImage.setLayoutManager(GridLayoutManager(this, 2))
        rvImage.setAdapter(MainAdapter(Datas.instance!!.datas, this))
        rvImage.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rvImage.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
    }

    /**
     * 如果用到共享元素，那么返回的时候会多调用到这个方法，类似于onActivityResult
     */
    override fun onActivityReenter(resultCode: Int, data: Intent) {
        super.onActivityReenter(resultCode, data)
        mReenterBundle = Bundle(data.extras)
        val startingPosition = mReenterBundle!!.getInt(EXTRA_STARTING_POSITION)
        val currentPosition = mReenterBundle!!.getInt(EXTRA_CURRENT_POSITION)
        // 当前元素已更改，就滑动列表到当前的position
        if (startingPosition != currentPosition) {
            rvImage!!.scrollToPosition(currentPosition)
        }
        // 暂时阻止启动共享元素Transition动画
        ActivityCompat.postponeEnterTransition(this)
        // 当列表所有的视图都测量完成并确定了框架后重新恢复共享元素动画
        rvImage!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                rvImage!!.viewTreeObserver.removeOnPreDrawListener(this)
                // 重新恢复共享元素动画
                ActivityCompat.startPostponedEnterTransition(this@MainActivity)
                return true
            }
        })
    }

    override fun onClick(view: View?, item: Data?) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.Companion.EXTRA_DATA, item)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val name = ViewCompat.getTransitionName(view!!)
        var bundle: Bundle? = Bundle()
        if (name != null) {
            bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, name).toBundle()
        }
        startActivity(intent, bundle)
    }

    companion object {
        const val EXTRA_STARTING_POSITION = "extra_starting_position"
        const val EXTRA_CURRENT_POSITION = "extra_current_position"
    }
}