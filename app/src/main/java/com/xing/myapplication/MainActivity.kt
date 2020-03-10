package com.xing.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xing.myapplication.adapter.UrlAdapter
import com.xing.myapplication.callback.NetCallback
import com.xing.myapplication.model.urls
import com.xing.myapplication.net.NetworkTask
import com.xing.myapplication.net.TaskDispatchManager
import com.xing.myapplication.utils.CacheUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var urlAdapter: UrlAdapter

    private var mFirstPosition = 0
    private var mLastPosition = -1

    private var isFirst: Boolean = true

    private val mHandler by lazy {
        MyHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        urlAdapter = UrlAdapter {
            //第一个feed显示就开始预加载
            Log.d("xw", "第一个feed显示")
            startHandler()
        }
        rv.adapter = urlAdapter
        urlAdapter.notifyDataSetChanged()

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // 静止
                        startHandler()
                    }
                    else -> {
                        // 滚动
                        stopHandler()
                    }
                }
            }
        })
    }

    private fun startLoadUrl() {
        val layoutManager = rv.layoutManager
        if (layoutManager is LinearLayoutManager) {
            mFirstPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            mLastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
            Log.d("xw", "mFirstPosition =$mFirstPosition mLastPosition = $mLastPosition")
        }
        val dispatchManager = TaskDispatchManager.getInstance()
        val cacheUtil = CacheUtil.getInstance()
        urls.subList(mFirstPosition, mLastPosition + 1).forEach {
            val cache = cacheUtil.getContentFromCache(it)
            if (cache == null) {
                dispatchManager.addRunnable(NetworkTask(it, object : NetCallback {
                    override fun onSuccess(content: String, url: String) {
                        cacheUtil.putContentCache(url, content)
                        //刷新界面
                        val position = urls.indexOf(url)
                        val viewHolder =
                            rv.findViewHolderForAdapterPosition(position)
                        if (viewHolder is UrlAdapter.UrlViewHolder){
                            viewHolder.successIv.visibility = View.VISIBLE
                        }
//                        urlAdapter.notifyItemChanged(position)
                    }

                    override fun onFail(e: Exception) {
                        Log.e("xw", e.toString())
                    }

                }))
            }
        }
        dispatchManager.run()
    }

    private fun stopLoadUrl() {
        TaskDispatchManager.getInstance().removeAllRunnable()
    }

    private fun startHandler() {
        mHandler.removeCallbacks(startRunnable)
        // 延迟三秒
        mHandler.postDelayed(startRunnable, 3000)
    }

    private fun stopHandler() {
        mHandler.removeCallbacks(startRunnable)
        mHandler.post(stopRunnable)
    }

    override fun onPause() {
        super.onPause()
        stopHandler()
    }

    override fun onResume() {
        super.onResume()
        if (isFirst.not()) {
            startHandler()
        }
        isFirst = false
    }

    private val stopRunnable = Runnable { mHandler.obtainMessage(MSG_STOP).sendToTarget() }
    private val startRunnable = Runnable { mHandler.obtainMessage(MSG_SCROLL).sendToTarget() }

    companion object {
        private const val MSG_STOP = 0x01
        private const val MSG_SCROLL = 0x02

        private class MyHandler(activity: MainActivity) : Handler() {
            private val mActivity: WeakReference<MainActivity> = WeakReference(activity)

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_STOP -> {
                        mActivity.get()?.stopLoadUrl()
                    }
                    MSG_SCROLL -> {
                        mActivity.get()?.startLoadUrl()
                    }
                }
            }
        }

    }

}
