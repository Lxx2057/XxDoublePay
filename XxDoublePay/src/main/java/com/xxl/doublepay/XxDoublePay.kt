package com.xxl.doublepay

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.widget.Toast
import com.alipay.sdk.app.PayTask
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.xx.anypay.WxAppIDProvider
import com.xx.anypay.XxAnyPayResultCallBack
import com.xxl.doublepay.entity.AliPayResultEntity
import org.json.JSONException
import org.json.JSONObject


/**
 * XxAnyPay
 * (。・∀・)ノ
 * Describe：支付集成SDK{支付包，微信}
 * Created by 雷小星🍀 on 2017/7/18 9:44.
 */

class XxDoublePay private constructor() {
    private var callBack: XxAnyPayResultCallBack? = null
    private var msgApi: IWXAPI? = null
    private var context: Context? = null

    /**
     * 设置支付信息提供者
     */
    var wxAppIDProvider: WxAppIDProvider? = null
        set(wxAppIDProvider) {
            if (wxAppIDProvider == null || TextUtils.isEmpty(wxAppIDProvider.weChatAppID)) {
                callBack?.onPayFiale("微信信息提供者未设置或设置内容为空")
                return
            }
            field = wxAppIDProvider
            msgApi = WXAPIFactory.createWXAPI(context, wxAppIDProvider.weChatAppID)
            msgApi!!.registerApp(wxAppIDProvider.weChatAppID)
        }

    /**
     * 是否安装并且支持微信API
     *
     * @return 是否支持
     */
    private val isWXAppInstalledAndSupported: Boolean
        get() = msgApi!!.isWXAppInstalled

    /**
     * 检查支付宝是否安装
     */
    @SuppressLint("QueryPermissionsNeeded")
    private fun checkAliPayInstalled(): Boolean {
        val uri = Uri.parse("alipays://platformapi/startApp")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val componentName = intent.resolveActivity(context!!.packageManager)
        return componentName != null
    }

    /**
     * 获取支付结果回调
     *
     * @return 支付结果回调
     */
    fun getCallBack(): XxAnyPayResultCallBack {
        return if (callBack != null)
            callBack as XxAnyPayResultCallBack
        else
            object : XxAnyPayResultCallBack {
                override fun onPaySuccess() {
                    Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show()
                }

                override fun onPayFiale(error: String) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * 步骤一
     * 步骤二[.setWxAppIDProvider]
     * 初始化SDK
     *
     * @param context 内容提供者，建议放在应用入口处
     */
    fun init(context: Context) {
        this.context = context
        if (context is Application) {
            context.registerActivityLifecycleCallbacks(getActivityLifecycleCallback())
        } else {
            (context.applicationContext as Application).registerActivityLifecycleCallbacks(
                getActivityLifecycleCallback()
            )
        }
    }

    private var mActivityLifecycleCallbacks: Application.ActivityLifecycleCallbacks? = null
    private var mActivityList: ArrayList<Activity>? = null
    private fun getActivityLifecycleCallback(): Application.ActivityLifecycleCallbacks {
        if (mActivityLifecycleCallbacks == null) {
            mActivityList = ArrayList()
            mActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                }

                override fun onActivityStarted(activity: Activity) {
                }

                override fun onActivityResumed(activity: Activity) {
                    //Activity激活时执行一下操作
                    //如果已经存在该Activity,移除它，并将它重新添加到顶部
                    if (mActivityList!!.contains(activity)) {
                        mActivityList?.remove(activity)
                    }
                    mActivityList?.add(activity)
                }

                override fun onActivityPaused(activity: Activity) {
                }

                override fun onActivityStopped(activity: Activity) {

                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                    //Activity销毁时移除
                    if (mActivityList!!.contains(activity)) {
                        mActivityList?.remove(activity)
                    }
                }
            }
        }
        return mActivityLifecycleCallbacks!!
    }

    /**
     * 获取最顶上激活状态的Activity
     */
    private fun getTopActivity(): Activity {
        return mActivityList!![0]
    }

    /**
     * 支付宝支付
     * 使用支付宝支付前,需使用Activity对SDK初始化一次
     *
     * @param aliPayInfo 参数信息
     */
    fun openAliPay(aliPayInfo: String, callBack: XxAnyPayResultCallBack?) {
        this.callBack = callBack
        if (checkAliPayInstalled()) {
            startAliPay(aliPayInfo)
        } else {
            //没有安装
            callBack?.onPayFiale("请先安装支付宝")
        }
    }

    /**
     * 启动支付宝支付
     */
    private fun startAliPay(aliPayInfo: String) {
        val payRunnable = Runnable {
            val alipay = PayTask(getTopActivity())
            val result = alipay.payV2(aliPayInfo, true)
            val msg = Message()
            msg.what = XXPAY_ALI
            msg.obj = result
            mHandler.sendMessage(msg)
        }
        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    /**
     * 打开指定类型支付
     *
     * @param xxPayType SDK支付类型{@value XXPAY_ALI}{@value XXPAY_WX}
     * @param payInfo   支付信息,微信支付时需要使用GSON序列化字符串
     * @param callBack  支付结果回调
     */
    fun openPay(xxPayType: Int, payInfo: String, callBack: XxAnyPayResultCallBack) {
        if (xxPayType == XXPAY_ALI) {
            openAliPay(payInfo, callBack)
        } else if (xxPayType == XXPAY_WX) {
            openWxPay(payInfo, callBack)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                XXPAY_ALI -> {
                    val resultStatus =
                        AliPayResultEntity(msg.obj as Map<String, String>?).resultStatus
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (callBack != null) {
                            callBack!!.onPaySuccess()
                        }
                    } else {
                        if (callBack != null) {
                            callBack!!.onPayFiale("支付失败")
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * 微信字符串解析为PayReq实体
     * 适配两种解析方式
     *
     * @param wxJsonStr 封装字符串
     */
    @Throws(JSONException::class)
    private fun jsonStrToPayReq(wxJsonStr: String): PayReq {
        //解析Java后台返回数据
        val wxJsonObj = JSONObject(wxJsonStr)
        var appId = wxJsonObj.optString("appId")
        var partnerId = wxJsonObj.optString("partnerId")
        var prepayId = wxJsonObj.optString("prepayId")
        var nonceStr = wxJsonObj.optString("nonceStr")
        var timeStamp = wxJsonObj.optString("timeStamp")
        var packageValue = wxJsonObj.optString("packageValue", "Sign=WXPay")
        var sign = wxJsonObj.optString("sign")

        //驼峰字段解析为空时,尝试使用-连接符字段进行解析
        if (TextUtils.isEmpty(appId)) {
            //解析Php后台返回数据,字段以_连接
            appId = wxJsonObj.optString("appid")
            partnerId = wxJsonObj.optString("mch_id")
            prepayId = wxJsonObj.optString("prepay_id")
            nonceStr = wxJsonObj.optString("nonce_str")
            timeStamp = wxJsonObj.optString("time")
            packageValue = wxJsonObj.optString("package", "Sign=WXPay")
            sign = wxJsonObj.optString("sign")
            //如果以上内容解析还为空,交给PayReq内部检查去判断了
        }

        //创建PayReq实体对象并赋值
        val payReq = PayReq()
        payReq.appId = appId
        payReq.partnerId = partnerId
        payReq.prepayId = prepayId
        payReq.nonceStr = nonceStr
        payReq.timeStamp = timeStamp
        payReq.packageValue = packageValue
        payReq.sign = sign
        return payReq
    }

    /**
     * 开始微信支付
     *
     * @param wxPayInfo 微信支付信息
     */
    fun openWxPay(wxPayInfo: String, callBack: XxAnyPayResultCallBack?) {
        this.callBack = callBack
        //检查手机上是否安装了微信
        if (!isWXAppInstalledAndSupported) {
            callBack?.onPayFiale("请先安装微信")
            return
        }
        val request: PayReq?
        try {
            request = jsonStrToPayReq(wxPayInfo)
        } catch (e: JSONException) {
            e.printStackTrace()
            callBack?.onPayFiale("json格式错误")
            return
        }

        if (msgApi == null) {
            callBack?.onPayFiale("请先设置微信信息提供者")
            return
        }

        //前置步骤正常,调起微信支付
        msgApi?.sendReq(request)
    }


    companion object {

        const val XXPAY_ALI = 0x00000000//支付宝支付
        const val XXPAY_WX = 0x00000001//微信支付

        @SuppressLint("StaticFieldLeak")
        private var paySDK: XxDoublePay? = null

        /**
         * 单例获取SDK实例
         */
        val intance: XxDoublePay
            get() {
                if (paySDK == null) {
                    synchronized(XxDoublePay::class.java) {
                        if (paySDK == null) {
                            paySDK = XxDoublePay()
                        }
                    }
                }
                return paySDK!!
            }
    }
}
