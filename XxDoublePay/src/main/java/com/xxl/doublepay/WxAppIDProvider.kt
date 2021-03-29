package com.xx.anypay

/**
 * WxAppIDProvider
 * (。・∀・)ノ
 * Describe：微信AppID提供者
 * Created by 雷小星🍀 on 2017/7/18 9:42.
 */

interface WxAppIDProvider {

    /**
     * 获取微信AppID
     *
     * @return AppID
     */
    val weChatAppID: String
}
