package com.xxl.doublepay

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.xx.anypay.XxAnyPayResultCallBack
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.xxl.xxdoublepay.test", appContext.packageName)
    }

    @Test
    fun openApliPay() {
        val aliPayInfo =
            "app_id=2017060807447897&biz_content={\"timeout_express\":\"30m\",\"seller_id\":\"\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"15.00\",\"subject\":\"\\u5065\\u8eabAPP\\u8ba2\\u5355\\u652f\\u4ed8\",\"body\":\"\\u5065\\u8eabAPP\\u8ba2\\u5355\\u652f\\u4ed8\",\"out_trade_no\":\"8150028639797\"}&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http://112.74.44.235/Api/AlipayNotify/index&sign_type=RSA&timestamp=2017-07-18 11:55:16&version=1.0&sign=oSrpaswC6t1HgxM3tf4u1Rg88FkerDCY0diuRgdQtbTIszAuEUaAfAiAlH4%2Ful6gdLX9sRdEZsTKfmy93cHAY32pIIV%2BTPsWg1Ljif%2FmTOZHpWbbAOr%2BCvur3ak4Tkk89yAK%2BjlwH5UhfrhSbT%2BzWFLu7uxGTRnRv0nqS7se%2B2w%3D&sign_type=RSA"
        XxDoublePay.intance.init(InstrumentationRegistry.getInstrumentation().context)
        XxDoublePay.intance.openAliPay(aliPayInfo, object : XxAnyPayResultCallBack {
            override fun onPaySuccess() {
            }

            override fun onPayFiale(error: String) {
            }
        })
    }
}