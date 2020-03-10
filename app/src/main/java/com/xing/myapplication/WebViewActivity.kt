package com.xing.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.xing.myapplication.utils.CacheUtil
import kotlinx.android.synthetic.main.activity_webview.*


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        container.addView(BaseApplication.webView)

        val url = intent.getStringExtra(URL)
        if (url.isNullOrEmpty().not()) {
            val contentFromCache = CacheUtil.getInstance().getContentFromCache(url!!)
            if (contentFromCache != null) {
                BaseApplication.webView.loadDataWithBaseURL(
                    url,
                    contentFromCache,
                    "text/html",
                    "utf-8",
                    null
                )
            } else {
                BaseApplication.webView.loadUrl(url)
            }
        } else {
            BaseApplication.webView.loadUrl("about:blank")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.webView.stopLoading()
        BaseApplication.webView.clearHistory()
        BaseApplication.webView.loadUrl("about:blank")
        if (container.childCount > 0) {
            (BaseApplication.webView.parent as ViewGroup).removeView(BaseApplication.webView)
        }
    }

    companion object {
        private const val URL = "url"
        fun run(context: Context, url: String) = context.apply {
            startActivity(Intent(this, WebViewActivity::class.java).apply {
                putExtra(URL, url)
            })
        }
    }
}