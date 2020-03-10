package com.xing.myapplication.utils

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class CacheUtil {

    // 内容缓存
    private var mContentLruCache: LruCache<String, String>? = null
    // 图片缓存
    private var mBitmapLruCache: LruCache<String, Bitmap>? = null

    @Synchronized
    private fun getContentLruCacheInstance() {
        if (mContentLruCache != null) {
            return
        }
        val cacheSize = (Runtime.getRuntime().maxMemory() / 16).toInt()
        mContentLruCache = object : LruCache<String, String>(cacheSize) {
            override fun sizeOf(key: String?, value: String): Int {
                return value.toByteArray().size
            }
        }
    }

    @Synchronized
    private fun getBitmapCacheInstance() {
        if (mBitmapLruCache != null) {
            return
        }
        val cacheSize = (Runtime.getRuntime().maxMemory() / 16).toInt()
        mBitmapLruCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap): Int {
                return value.byteCount
            }
        }
    }

    fun putContentCache(url: String, content: String) {
        if (mContentLruCache == null) {
            getContentLruCacheInstance()
        }
        if (getContentFromCache(url) == null) {
            mContentLruCache!!.put(url, content)
        }
    }

    fun getContentFromCache(url: String): String? {
        if (mContentLruCache == null) {
            getContentLruCacheInstance()
        }
        return mContentLruCache!!.get(url)
    }

    fun putBitmapCache(url: String, bitmap: Bitmap) {
        if (mBitmapLruCache == null) {
            getBitmapCacheInstance()
        }
        if (getBitmapFromCache(url) == null) {
            mBitmapLruCache!!.put(url, bitmap)
        }
    }

    fun getBitmapFromCache(url: String): Bitmap? {
        if (mBitmapLruCache == null) {
            getBitmapCacheInstance()
        }
        return mBitmapLruCache!!.get(url)
    }

    companion object {
        @Volatile
        private var instance: CacheUtil? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: CacheUtil().also { instance = it }
        }
    }
}