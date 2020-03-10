package com.xing.myapplication

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import com.xing.myapplication.utils.SafeWebViewClient
import kotlin.properties.Delegates

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        webView = WebView(this).apply {
            webSettings(settings)
            webViewClient = SafeWebViewClient()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webSettings(webSettings: WebSettings) {
        webSettings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            setAppCacheEnabled(true)
        }
    }

    companion object {
        var webView: WebView by Delegates.notNull()
        var context: Context by Delegates.notNull()
    }
}