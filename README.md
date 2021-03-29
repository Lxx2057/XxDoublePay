# XxDoublePay For Kotlin[![](https://jitpack.io/v/Lxx2057/XxDoublePay.svg)](https://jitpack.io/#Lxx2057/XxDoublePay)

#### 集合支付宝支付,微信支付的一个工具库,简化了直接对接支付SDK的操作
### 如何使用
1.项目根结点build.gradle文件中增加如下内容

    allprojects {
        	repositories {
        			...
        			maven { url 'https://jitpack.io' }
        		}
    }
2.项目主module的build.gradle文件中增加依赖
      	
    dependencies {
	        implementation 'com.github.Lxx2057:XxDoublePay:Tag'
	}

### 配置

#### 配置微信回调Activity
在包名根目录下,新建wxapi文件夹,新建WXPayEntryActiv
内容如下

        import com.xxl.doublepay.wxapi.WxPayEntryActivity
        
        class WXPayEntryActivity : WxPayEntryActivity()

Manifest文件中添加如下代码
        
        <activity
                    android:name=".wxapi.WXPayEntryActivity"
                    android:exported="true"
                    android:launchMode="singleTop"
                    android:theme="@android:style/Theme.Translucent" />
## 使用
### 第一步，初始化
        XxDoublePay.intance.init(this)

### 第二步,根据支付需求进行配置
#### 使用微信支付时，设置微信信息提供者，设置微信AppID,可以放到Application中
        XxDoublePay.intance.wxAppIDProvider = object : WxAppIDProvider {
                    override val weChatAppID: String
                        get() = "wx53fe2facdc1df93f"
                }

### 第三步，发起支付,传入标识参数: XXPAY_ALI or XXPAY_WX
        XxDoublePay.intance
                        .openPay(payType, payInfo, object : XxAnyPayResultCallBack {
                            override fun onPaySuccess() {
                                Toast.makeText(this@MainActivity, "支付成功", Toast.LENGTH_SHORT).show()
                            }
        
                            override fun onPayFiale(error: String) {
                                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                            }
                        })
                
####  单独调用
    XxDoublePay.intance
                .openAliPay(payInfo, object : XxAnyPayResultCallBack {
                            override fun onPaySuccess() {
                                Toast.makeText(this@MainActivity, "支付成功", Toast.LENGTH_SHORT).show()
                            }
        
                            override fun onPayFiale(error: String) {
                                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                            }
                        })
                        
     XxDoublePay.intance
                .openWeChatPay(payInfo, object : XxAnyPayResultCallBack {
                            override fun onPaySuccess() {
                                Toast.makeText(this@MainActivity, "支付成功", Toast.LENGTH_SHORT).show()
                            }
        
                            override fun onPayFiale(error: String) {
                                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                            }
                        })                    