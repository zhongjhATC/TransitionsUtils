package com.zhongjh.transitionsutils.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.zhongjh.transitionsutils.R
import com.zhongjh.transitionsutils.data.Data
import com.zhongjh.transitionsutils.data.Datas
import com.zhongjh.transitionsutils.ui.adapter.DetailAdapter
import kotlinx.android.synthetic.main.activity_detail.*
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.util.*

/**
 * @author zhongjh
 * @date 2022/2/22
 */
class DetailActivity : AppCompatActivity() {

    /**
     * 是否正在退出
     */
    private var isReturning = false
    private var mStartingPosition = 0
    private var mCurrentPosition = 0
    var mDetailAdapter: DetailAdapter? = null

    /**
     * 共享元素
     */
    var enterElementCallback: SharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (isReturning) {
                val view = mDetailAdapter?.getView(mCurrentPosition)
                // 当前元素已更改，转换共享单位
                if (mStartingPosition != mCurrentPosition) {
                    names.clear()
                    names.add(ViewCompat.getTransitionName(view!!).toString())
                    sharedElements.clear()
                    sharedElements[ViewCompat.getTransitionName(view).toString()] = view
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setupToolBar()
        // 暂时阻止启动共享元素Transition动画
        ActivityCompat.postponeEnterTransition(this)
        ActivityCompat.setEnterSharedElementCallback(this, enterElementCallback)
        if (intent != null) {
            val data: Data? = intent.getParcelableExtra(EXTRA_DATA)
            mStartingPosition = data?.position ?: 0
            mCurrentPosition = savedInstanceState?.getInt(SAVED_CURRENT_POSITION)
                    ?: mStartingPosition
            mDetailAdapter = DetailAdapter(this@DetailActivity, Datas.instance!!.datas, mCurrentPosition)
            viewPager.setAdapter(mDetailAdapter)
            viewPager.setCurrentItem(mCurrentPosition)
            viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    mCurrentPosition = position
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        // 这个只是用于记录界面因为内存不够等别的原因销毁后，重新创建时保存相关数值
        outState.putInt(SAVED_CURRENT_POSITION, mCurrentPosition)
    }

    override fun finishAfterTransition() {
        isReturning = true
        val intent = Intent()
        intent.putExtra(MainActivity.EXTRA_STARTING_POSITION, mStartingPosition)
        intent.putExtra(MainActivity.EXTRA_CURRENT_POSITION, mCurrentPosition)
        setResult(Activity.RESULT_OK, intent)
        super.finishAfterTransition()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return if (menuItem.itemId == android.R.id.home) {
            supportFinishAfterTransition()
            true
        } else {
            super.onOptionsItemSelected(menuItem)
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = ""
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.elevation = 0f
        }
    }

    override fun onDestroy() {
        removeActivityFromTransitionManager(this)
        super.onDestroy()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val SAVED_CURRENT_POSITION = "saved_current_position"

        /**
         * 防止内存泄漏
         */
        private fun removeActivityFromTransitionManager(activity: Activity) {
            val transitionManagerClass: Class<*> = android.transition.TransitionManager::class.java
            try {
                val runningTransitionsField: Field = transitionManagerClass.getDeclaredField("sRunningTransitions")
                runningTransitionsField.isAccessible = true
                @Suppress("UNCHECKED_CAST")
                val runningTransitions = runningTransitionsField.get(transitionManagerClass) as ThreadLocal<WeakReference<android.util.ArrayMap<ViewGroup, ArrayList<android.transition.Transition>>>>
                if (runningTransitions.get() == null || runningTransitions.get()?.get() == null) {
                    return
                }
                val map = runningTransitions.get()?.get()
                val decorView = activity.window.decorView
                if (map != null) {
                    if (map.containsKey(decorView)) {
                        map.remove(decorView)
                    }
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }
}