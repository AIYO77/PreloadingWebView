package com.xing.myapplication.utils

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.xing.myapplication.model.GET
import com.xing.myapplication.model.JPG
import com.xing.myapplication.model.PNG
import com.xing.myapplication.net.LoadBitmapTask
import com.xing.myapplication.net.TaskDispatchManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SafeWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        view?.loadUrl(url)
        return true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
        view?.loadUrl(request.url.toString())
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest
    ): WebResourceResponse? {
        if (GET != request.method) {
            return super.shouldInterceptRequest(view, request)
        }
        val url = request.url.toString()
        if (url.endsWith(PNG) || url.endsWith(JPG)) {
            Log.d("xw", "url = $url")
            val bitmap = CacheUtil.getInstance().getBitmapFromCache(url)
            if (bitmap != null) {
                val mimeType = "image/png"
                val encoding = "utf-8"
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val inputStream = ByteArrayInputStream(outputStream.toByteArray())
                val response =
                    WebResourceResponse(mimeType, encoding, inputStream)
                try { //关闭流
                    outputStream.close()
                    inputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return response
            } else {
                TaskDispatchManager.getInstance().execute(LoadBitmapTask(url))
            }
            return super.shouldInterceptRequest(view, request)
        } else {
            return super.shouldInterceptRequest(view, request)
        }
    }

}