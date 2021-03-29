package com.xxl.doublepay.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xx.anypay.WxAppIDProvider
import com.xx.anypay.XxAnyPayResultCallBack
import com.xxl.doublepay.XxDoublePay
import com.xxl.doublepay.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //1.初始化
        XxDoublePay.intance.init(this)
        //2.微信支付设置APPID
        XxDoublePay.intance.wxAppIDProvider = object : WxAppIDProvider {
            override val weChatAppID: String
                get() = "wx53fe2facdc1df93f"
        }
        //3.发起支付
        binding.btnWechat.setOnClickListener {
            XxDoublePay.intance.openPay(
                XxDoublePay.XXPAY_WX,
                weChatPayInfo,
                object : XxAnyPayResultCallBack {
                    override fun onPaySuccess() {
                        Toast.makeText(this@MainActivity, "支付成功", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPayFiale(error: String) {
                        Toast.makeText(this@MainActivity, "支付失败", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        binding.btnAlipay.setOnClickListener {
            XxDoublePay.intance.openPay(
                XxDoublePay.XXPAY_ALI,
                aliPayInfo,
                object : XxAnyPayResultCallBack {
                    override fun onPaySuccess() {
                        Toast.makeText(this@MainActivity, "支付成功", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPayFiale(error: String) {
                        Toast.makeText(this@MainActivity, "支付失败", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


    companion object {
        private const val aliPayInfo =
            "app_id=2017060807447897&biz_content={\"timeout_express\":\"30m\",\"seller_id\":\"\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"15.00\",\"subject\":\"\\u5065\\u8eabAPP\\u8ba2\\u5355\\u652f\\u4ed8\",\"body\":\"\\u5065\\u8eabAPP\\u8ba2\\u5355\\u652f\\u4ed8\",\"out_trade_no\":\"8150028639797\"}&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http://112.74.44.235/Api/AlipayNotify/index&sign_type=RSA&timestamp=2017-07-18 11:55:16&version=1.0&sign=oSrpaswC6t1HgxM3tf4u1Rg88FkerDCY0diuRgdQtbTIszAuEUaAfAiAlH4%2Ful6gdLX9sRdEZsTKfmy93cHAY32pIIV%2BTPsWg1Ljif%2FmTOZHpWbbAOr%2BCvur3ak4Tkk89yAK%2BjlwH5UhfrhSbT%2BzWFLu7uxGTRnRv0nqS7se%2B2w%3D&sign_type=RSA"
        private const val weChatPayInfo = "{\n" +
                "    \"return_code\": \"SUCCESS\", \n" +
                "    \"return_msg\": \"OK\", \n" +
                "    \"appid\": \"wx53fe2facdc1df93f\", \n" +
                "    \"mch_id\": \"1483111212\", \n" +
                "    \"nonce_str\": \"test\", \n" +
                "    \"sign\": \"A155B7DC36549BF9E31A641AD160BE10\", \n" +
                "    \"result_code\": \"SUCCESS\", \n" +
                "    \"prepay_id\": \"wx20170718110737bdde12b65f0982531828\", \n" +
                "    \"trade_type\": \"APP\", \n" +
                "    \"time\": 1500347257, \n" +
                "    \"package\": \"Sign=WXPay\"\n" +
                "}"
    }
}