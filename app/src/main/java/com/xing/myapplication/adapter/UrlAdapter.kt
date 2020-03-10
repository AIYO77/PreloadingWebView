package com.xing.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.xing.myapplication.R
import com.xing.myapplication.WebViewActivity
import com.xing.myapplication.model.urls
import com.xing.myapplication.utils.CacheUtil

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class UrlAdapter(private val callback: () -> Unit) :
    RecyclerView.Adapter<UrlAdapter.UrlViewHolder>() {

    private var mHasRecorded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlViewHolder {
        return UrlViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_url,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UrlViewHolder, position: Int) {
        if (position == 0 && mHasRecorded.not()) {
            mHasRecorded = true
            holder.layout.viewTreeObserver
                .addOnPreDrawListener(object : OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        holder.layout.viewTreeObserver.removeOnPreDrawListener(this)
                        callback.invoke()
                        return true
                    }
                })
        }

        with(holder) {
            val url = urls[position]
            urlTv.text = url
            successIv.visibility =
                if (CacheUtil.getInstance().getContentFromCache(url) == null) View.INVISIBLE else View.VISIBLE
            layout.setOnClickListener {
                WebViewActivity.run(
                    it.context,
                    urls[position]
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return urls.size
    }

    class UrlViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urlTv: AppCompatTextView = view.findViewById(R.id.urlTv)
        val successIv: AppCompatImageView = view.findViewById(R.id.successIv)
        val layout: FrameLayout = view.findViewById(R.id.flOut)
    }

}