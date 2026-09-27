package com.lalifa.api

import com.lalifa.ext.Tools
import com.lalifa.utils.SPUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 演示如何自动刷新token令牌
 */
class RefreshTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request) // 如果token失效

        return synchronized(RefreshTokenInterceptor::class.java) {
            val isLogin = SPUtil.getBoolean(Tools.IS_LOGIN, false)
            if (response.code == 403 && isLogin && !request.url.pathSegments.contains("token")) {
                chain.proceed(request)
            } else {
                response
            }
        }
    }
}