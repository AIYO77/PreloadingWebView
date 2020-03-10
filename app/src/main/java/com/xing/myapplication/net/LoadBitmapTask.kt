package com.xing.myapplication.net

import android.graphics.BitmapFactory
import android.util.Log
import com.xing.myapplication.model.CONNECT_TIMEOUT
import com.xing.myapplication.model.GET
import com.xing.myapplication.model.SUC_CODE
import com.xing.myapplication.utils.CacheUtil
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class LoadBitmapTask(private val path: String) : Runnable {

    override fun run() {
        try {
            val url = URL(path)
            val connection = url.openConnection() as HttpsURLConnection
            connection.connectTimeout = CONNECT_TIMEOUT
            connection.requestMethod = GET
            connection.connect()
            if (connection.responseCode == SUC_CODE) {
                val inputStream = connection.inputStream
                Log.d("xw", "下载成功  url=$path")
                CacheUtil.getInstance()
                    .putBitmapCache(path, BitmapFactory.decodeStream(inputStream))
                inputStream.close()
            }
        } catch (e: Exception) {
            Log.e("xw", "下载失败 $e")
        }
    }
}