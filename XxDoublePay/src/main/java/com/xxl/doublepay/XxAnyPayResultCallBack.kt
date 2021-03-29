package com.xx.anypay

/**
 * XxAnyPayResultCallBack
 * (。・∀・)ノ
 * Describe：支付结果信息同步回调
 * Created by 雷小星🍀 on 2017/7/18 10:17.
 */

interface XxAnyPayResultCallBack {

    /**
     * 支付成功回调
     */
    fun onPaySuccess()

    /**
     * 支付失败回调
     *
     * @param error 错误描述
     */
    fun onPayFiale(error: String)
}
