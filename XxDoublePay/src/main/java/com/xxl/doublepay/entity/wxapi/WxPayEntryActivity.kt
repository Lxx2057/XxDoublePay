package com.xxl.doublepay.entity.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.xxl.doublepay.XxDoublePay.Companion.intance

open class WxPayEntryActivity : Activity(), IWXAPIEventHandler {
    private var api: IWXAPI? = null
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {}
    override fun onResp(resp: BaseResp) {
        when (resp.errCode) {
            0 -> {
                //支付成功
                intance.getCallBack().onPaySuccess()
            }
            -1 -> {
                //支付失败
                intance.getCallBack().onPayFiale("支付失败")
            }
            -2 -> {
                //支付取消
                intance.getCallBack().onPayFiale("支付取消")
            }
            else -> {
                intance.getCallBack().onPayFiale("支付失败：${resp.errCode} ${resp.errStr}")
            }
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, intance.wxAppIDProvider!!.weChatAppID)
        api?.handleIntent(intent, this)
    }
}