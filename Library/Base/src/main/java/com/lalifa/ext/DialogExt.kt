package com.lalifa.ext

import android.app.Activity
import android.content.Context
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView
import com.drake.net.component.Progress
import com.lalifa.api.Price
import com.lalifa.base.R
import com.lalifa.extension.*
import per.goweii.layer.core.anim.AnimStyle
import per.goweii.layer.core.anim.NullAnimatorCreator
import per.goweii.layer.core.ktx.onClickToDismiss
import per.goweii.layer.core.ktx.onInitialize
import per.goweii.layer.core.ktx.onPreDismiss
import per.goweii.layer.core.ktx.onPreShow
import per.goweii.layer.core.widget.SwipeLayout
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.*


//

/**
 * 通用提示框
 * @receiver Activity
 * @param content String
 * @param title String
 * @param isShowCancelBtn Boolean
 * @param callback Function0<Unit>
 */
fun Activity.showTipDialog(
    content: String, title: String = "提示",
    isShowCancelBtn: Boolean = true,
    cancelText: String = "取消",
    sureText: String = "确定",
    callback: (view: View) -> Unit = {}
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.dialog_tips)
        .backgroundDimDefault()
        .gravity(Gravity.CENTER)
        .onClickToDismiss(R.id.sure_btn) { callback.invoke(it) }
        .onClickToDismiss(R.id.cancel_btn) { callback.invoke(it) }
        .onInitialize {
            findViewById<TextView>(R.id.tip)?.text = title
            findViewById<TextView>(R.id.sure_btn)?.text = sureText
            findViewById<TextView>(R.id.cancel_btn)?.text = cancelText
            findViewById<TextView>(R.id.content)?.text = content
            findViewById<TextView>(R.id.cancel_btn)?.applyVisible(isShowCancelBtn)
            findViewById<ImageView>(R.id.line)?.applyVisible(isShowCancelBtn)
        }.show()
}

fun Fragment.showTipDialog(
    content: String, title: String = "温馨提示",
    isShowCancelBtn: Boolean = true,
    cancelText: String = "取消", sureText: String = "确定",
    callback: (view: View) -> Unit = {}
) {
    requireActivity().showTipDialog(content, title, isShowCancelBtn, cancelText, sureText, callback)
}

fun Activity.showHeaderTipDialog(callback: () -> Unit = {}) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.dialog_header_tips)
        .backgroundDimDefault()
        .gravity(Gravity.CENTER)
        .onInitialize {
            findViewById<View>(R.id.bt)?.onClick {
                callback.invoke()
                dismiss()
            }
        }.show()
}


//
///**
// * 显示一个带取消按钮的list弹框
// * @receiver Activity
// * @param contents ArrayList<String>
// * @param sureText String
// * @param callback Function1<Int, Unit>
// */
//fun Activity.showListDialog(
//    contents: ArrayList<String>,
//    sureText: String = "完成",
//    callback: DialogLayer.(Int) -> Unit = {}
//) {
//    DialogLayer(this)
//        .cancelableOnTouchOutside(true)
//        .contentView(R.layout.dialog_content)
//        .backgroundDimDefault()
//        .gravity(Gravity.BOTTOM)
//        .animStyle(AnimStyle.BOTTOM)
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
//        .onClickToDismiss(R.id.sure_btn)
//        .onInitialize {
//            requireViewById<RecyclerView>(R.id.list).linear().setup {
//                addType<String>(R.layout.item_dialog_text)
//                onBind {
//                    getBinding<ItemDialogTextBinding>().content.text = getModel()
//                }
//                R.id.content.onClick {
//                    callback.invoke(this@onInitialize, modelPosition)
//                }
//            }.models = contents
//            requireViewById<TextView>(R.id.sure_btn).text = sureText
//        }.show()
//}
//
//fun Fragment.showListDialog(
//    contents: ArrayList<String>,
//    sureText: String = "完成",
//    callback: DialogLayer.(Int) -> Unit = {}
//) {
//    requireActivity().showListDialog(contents, sureText, callback)
//}
//
/**
 * 显示一个通用List弹框
 * @receiver Activity
 * @param title String
 * @param block [@kotlin.ExtensionFunctionType] Function1<RecyclerView, Unit>
 */
fun Activity.showCommonListDialog(title: String = "", block: RecyclerView.(DialogLayer) -> Unit, dialog: DialogLayer.(DialogLayer) -> Unit = {}) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.dialog_parameter)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)
        .animStyle(AnimStyle.BOTTOM)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
        .onClickToDismiss(R.id.close_btn)
        .onInitialize {
            this.dialog(this)
            requireViewById<RecyclerView>(R.id.list).apply { block.invoke(this, this@onInitialize) }
            requireViewById<TextView>(R.id.title).text = title
        }.show()
}

/**
 * 单价规则说明
 * @receiver Activity
 * @param title String
 * @param block [@kotlin.ExtensionFunctionType] Function1<RecyclerView, Unit>
 */
fun Activity.showDescriptionPriceRuleDialog(url: String = "", title: String = "", block: RecyclerView.(DialogLayer) -> Unit) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.dialog_price_rule)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)
        .animStyle(AnimStyle.BOTTOM)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
        .onClickToDismiss(R.id.close_btn)
        .onInitialize {
//            requireViewById<RecyclerView>(R.id.list).apply { block.invoke(this, this@onInitialize) }
//            requireViewById<TextView>(R.id.title).text = title
            val webView = requireViewById<WebView>(R.id.web_view)
            val progress = requireViewById<ProgressBar>(R.id.progress)
            webView.apply {
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
                        if (newProgress == 100) progress.gone()
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

        }.show()
}

/**
 * 单价
 * @receiver Activity
 * @param title String
 * @param block [@kotlin.ExtensionFunctionType] Function1<RecyclerView, Unit>
 */
fun Activity.priceOrderDialog(price: List<Price>, callback: (Price?, View) -> Unit) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.dialog_price_order)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)
        .animStyle(AnimStyle.BOTTOM)
//        .swipeDismiss(SwipeLayout.Direction.BOTTOM)//设置拖拽dismiss弹框的方向
        .onClickToDismiss(R.id.cancel)
        .onInitialize {
            val arrayList = ArrayList<String>()
            var mPrice: Price = price[0]
            price.forEachIndexed { index, price ->
                arrayList.add((price?.price?:0).toString())
            }
//            val sexList = listOf("10/半小时", "15/半小时", "20/半小时", "25/半小时", "30/半小时")
            var selectTitle: String? = null
            requireViewById<TextView>(R.id.cancel).onClick { dismiss() }
            val wheelView = requireViewById<WheelView>(R.id.wheel_view)

            wheelView.setAdapter(ArrayWheelAdapter<Any?>(arrayList.toList()))
            wheelView.setOnItemSelectedListener {
                selectTitle = arrayList[it]
                mPrice = price[it]
            }
            requireViewById<TextView>(R.id.sure).onClick {
                callback.invoke(mPrice, it)
                dismiss()
            }

            requireViewById<TextView>(R.id.close_btn).onClick {
                callback.invoke(mPrice, it)
                dismiss()
            }
        }.show()
}

/**
 * 显示一个在键盘上方的弹框
 * @receiver Activity
 * @param hint String
 * @param l int 可输入长度
 * @param callback Function1<[@kotlin.ParameterName] String, Unit>
 */
fun Activity.showInputDialog(
    hint: String = "请输入",
    content: String = "",
    l: Int,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    callback: (content: String) -> Unit
) {
    DialogLayer(this)
        .contentView(R.layout.dialog_input)
        .cancelableOnTouchOutside(true)
        .contentAnimator(NullAnimatorCreator())
        .backgroundDimDefault()
        .addInputMethodCompat(true)
        .onPreShow {
            val et = requireViewById<EditText>(R.id.et_input)
            et.requestFocus()
            et.inputType = inputType
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et, InputMethodManager.SHOW_FORCED)
        }.onPreDismiss {
            val et = requireViewById<EditText>(R.id.et_input)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(et.windowToken, 0)
        }.onInitialize {
            val inputEt = requireViewById<EditText>(R.id.et_input).apply {
                setHint(hint)
                maxEms = l
                setText(content)
            }
            requireViewById<TextView>(R.id.sure).onClick {
                if (inputEt.text().isEmpty()) {
                    toast("请输入内容")
                    return@onClick
                }
                callback.invoke(inputEt.text())
                dismiss()
            }
        }.show()
}

