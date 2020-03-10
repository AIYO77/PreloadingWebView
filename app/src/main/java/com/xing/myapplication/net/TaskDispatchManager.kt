package com.xing.myapplication.net

import android.widget.Toast
import com.xing.myapplication.BaseApplication
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class TaskDispatchManager {

    // 线程池
    private val executor: ThreadPoolExecutor = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        CORE_POOL_SIZE,
        1,
        TimeUnit.HOURS,
        LinkedBlockingQueue(),
        Executors.defaultThreadFactory(),
        ThreadPoolExecutor.AbortPolicy()
    )

    private val activeList = mutableListOf<Runnable>()

    fun execute(runnable: Runnable?) {
        if (runnable == null) return
        executor.execute(runnable)
    }

    fun addRunnable(runnable: Runnable?) {
        if (runnable == null) return
        activeList.add(runnable)
    }

    fun removeAllRunnable() {
        activeList.forEach {
            executor.remove(it)
        }
        activeList.clear()
    }

    fun run() {
        if (activeList.isNullOrEmpty()) return
        Toast.makeText(BaseApplication.context, "开始缓存", Toast.LENGTH_SHORT).show()
        activeList.forEach {
            execute(it)
        }
    }

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        //线程数量
        val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))

        @Volatile
        private var instance: TaskDispatchManager? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: TaskDispatchManager().also { instance = it }
        }
    }
}