package com.xing.myapplication.net

import android.os.Handler
import android.os.Message
import com.xing.myapplication.callback.NetCallback
import com.xing.myapplication.model.CONNECT_TIMEOUT
import com.xing.myapplication.model.GET
import com.xing.myapplication.model.SUC_CODE
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class NetworkTask(private val mUrl: String, private val mNetCallback: NetCallback) : Runnable {

    private var mHandler: MyHandler

    init {
        mHandler = MyHandler(this)
    }

    override fun run() {
        val url = URL(mUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            connectTimeout = CONNECT_TIMEOUT
            requestMethod = GET
            connect()
        }
        if (connection.responseCode == SUC_CODE) {
            try {
                val inputStream = connection.inputStream
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    outputStream.write(buffer, 0, len)
                }
                val content = outputStream.toByteArray().toString(Charset.forName("utf-8"))
                val message = mHandler.obtainMessage().apply {
                    obj = content
                    what = ON_SUCCESS
                }
                mHandler.sendMessage(message)
                inputStream.close()
                outputStream.close()
            } catch (e: Exception) {
                val message = mHandler.obtainMessage().apply {
                    obj = e
                    what = ON_FAIL
                }
                mHandler.sendMessage(message)
            }
        }
    }

    companion object {
        private const val ON_FAIL = -1
        private const val ON_SUCCESS = 200

        private class MyHandler(networkTask: NetworkTask) : Handler() {
            private val mTaskRef: WeakReference<NetworkTask> = WeakReference(networkTask)
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    ON_FAIL -> {
                        mTaskRef.get()?.mNetCallback?.onFail(msg.obj as Exception)
                    }
                    ON_SUCCESS -> {
                        mTaskRef.get()?.mNetCallback?.onSuccess(
                            msg.obj.toString(),
                            mTaskRef.get()!!.mUrl
                        )
                    }
                }
            }
        }
    }
}