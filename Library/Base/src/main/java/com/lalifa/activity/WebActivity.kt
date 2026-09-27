package com.lalifa.activity

import android.content.Context
import android.webkit.*
import com.lalifa.base.BaseTitleActivity
import com.lalifa.base.databinding.ActivityWebUrlBinding
import com.lalifa.extension.getIntentString
import com.lalifa.extension.gone
import com.lalifa.extension.start

/**
 *
 * @ClassName WebUrlActivity
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/4/6 17:34
 * @Des
 */
class WebActivity : BaseTitleActivity<ActivityWebUrlBinding>() {
    override fun darkMode() = false
    override fun getViewBinding() = ActivityWebUrlBinding.inflate(layoutInflater)

    companion object {
        fun forward(context: Context, title: String = "", url: String) {
            context.start(WebActivity::class.java) {
                putExtra("title", title)
                putExtra("url", url)
            }
        }
    }

    override fun initView() {
        var title = getIntentString("title")
        var url = getIntentString("url")
        if (title.isNotEmpty()) setTitle(title)
        if (url.isEmpty()) return
        binding.webView.apply {
            settings.apply {
                // 网页内容的宽度是否可大于WebView控件的宽度
                loadWithOverviewMode = false
                // 是否应该支持使用其屏幕缩放控件和手势缩放
                setSupportZoom(false)
                //  页面加载好以后，再放开图片
                blockNetworkImage = false
                // 排版适应屏幕
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                //设置字体大小
                textZoom = 80
                javaScriptEnabled = true
//                // 是否阻止网络图像
//                blockNetworkImage = false
//                // 是否阻止网络请求
//                blockNetworkLoads = false
//                // 是否加载JS
//                javaScriptEnabled = true
//                javaScriptCanOpenWindowsAutomatically = true
//                //覆盖方式启动缓存
//                cacheMode = WebSettings.LOAD_DEFAULT
//                // 使用广泛视窗
//                useWideViewPort = true
//                loadWithOverviewMode = true
//                domStorageEnabled = true
//                //是否支持缩放
//                builtInZoomControls = false
//                setSupportZoom(false)
//                //不显示缩放按钮
//                displayZoomControls = false
//                allowFileAccess = true
//                databaseEnabled = true
//                binding.webView.setVerticalScrollbarOverlay(false) //不出现指定的垂直滚动条有叠加样式
//
//                useWideViewPort = true //设定支持viewport
//
//                builtInZoomControls = true //设置出现缩放工具
//
//                displayZoomControls = false //设置缩放工具隐藏
//
//                setSupportZoom(true) //设定支持缩放
//
//                //缓存相关
//                setAppCacheEnabled(true)
//                domStorageEnabled = true
//                databaseEnabled = true
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (url.startsWith("http")) {
                        view!!.loadUrl(url)
                    } else {
                        view?.loadData(url, "text/html", "UTF-8")
                    }
                    return true
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) binding.progress.gone()
                }

                override fun onReceivedTitle(view: WebView?, title2: String?) {
                    super.onReceivedTitle(view, title2)
                    if(title.isNullOrEmpty()) {
                        setTitle(title2 ?: "")
                    }
                }
            }
            if (url.startsWith("http")) {
                loadUrl(url)
            } else {
                loadData(url, "text/html" , "utf-8")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webView.onPause()
    }

}