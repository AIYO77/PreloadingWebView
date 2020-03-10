package com.xing.myapplication.callback

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface NetCallback {
    fun onSuccess(content:String,url:String)

    fun onFail(e:Exception)
}