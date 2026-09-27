package com.lalifa.api

import android.content.Context
import android.text.TextUtils
import com.drake.channel.sendTag
import com.drake.net.utils.TipUtils
import com.lalifa.ext.Tools
import com.lalifa.extension.pk
import com.lalifa.tools.UserManager
import com.lalifa.utils.SPUtil


class InitNet {
    companion object {
        /**
         * 初始化网络请求，全局配置
         */
        fun initNetHttp(context: Context, token: String) {
            NetHttp.init(
                context, Tools.HOST, JsonHttpConverter(),
                block = {
                    //全局请求头/参数
                    //addHeader("Content-Type","application/json; charset=utf-8")
                    if (!TextUtils.isEmpty(token)) {
                        addHeader("token", token)
                    } else {
                        val token = SPUtil.get(Tools.Token)
                        addHeader("token", token.pk())

                    }
                    val language = SPUtil.get("language")
                    if (language.isNullOrEmpty()) {
                        addHeader("language", "en")
                    } else {
                        addHeader("language", language)
                    }

                }, error = {
                    TipUtils.toast(it.message)
                    when (it.code) {
                        Tools.LOGIN_INVALID -> {
                            //token失效
                            sendTag("HttpLogout")
                        }
                    }
                })
        }
    }
}