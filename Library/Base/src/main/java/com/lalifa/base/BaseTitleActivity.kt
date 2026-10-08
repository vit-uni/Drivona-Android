package com.lalifa.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.drake.statusbar.immersive
import com.gyf.immersionbar.ImmersionBar
import com.lalifa.base.databinding.CommonLayoutBinding
import com.lalifa.base.databinding.CommonTopBarBinding
import com.lalifa.extension.applyVisible
import com.lalifa.extension.onClick
import com.lalifa.widget.loading.LoadTag

/**
 *
 * @ClassName BaseActivity
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/6/30 16:24
 * @Des
 */
abstract class BaseTitleActivity<T : ViewBinding>() : AppCompatActivity() {
    lateinit var mainBinding: CommonLayoutBinding
    lateinit var binding: T
    public var TAG = "=======>"

    //标题文本
    open fun title(): String = ""
    open fun rightText(): String = ""
    open fun isCanExit(): Boolean = false
    open fun rightClick() {}
    open fun rightIconClick() {}
    open fun leftClick() {}

    /**
     * 状态栏字体深色模式
     */
    protected open fun isStatusBarDarkFont(): Boolean {
        return false
    }

    /**
     * 是否使用沉浸式状态栏
     */
    protected open fun isStatusBarEnabled(): Boolean {
        return true
    }

    //空数据返回
    open fun String?.pk(def: String = ""): String {
        return if (this.isNullOrEmpty()) {
            def
        } else {
            this
        }
    }

    //是否隐藏标题栏  false 显示  true 隐藏
    open fun topBarHide(): Boolean = false

    //深色状态栏 true 深色 false 浅色
    open fun darkMode(): Boolean = true

    open fun topBar(): CommonTopBarBinding = mainBinding.topBar

    var mContext: Context? = null
    var loadTag: LoadTag? = null
    var savedInstanceStat: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceStat = savedInstanceState
        loadTag = LoadTag(
            this, getString(
                R.string.text_loading
            )
        )
        mContext = this
        mainBinding = CommonLayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(mainBinding.root)
        binding = getViewBinding()
        mainBinding.content.addView(binding.root)
        initTitleBar()
        initView()
        initViewBundle(savedInstanceState)
        onClick()
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            ImmersionBar.with(this) // 默认状态栏字体颜色为黑色
                .statusBarColor(R.color.transparent) // 指定导航栏背景颜色
                .statusBarDarkFont(isStatusBarDarkFont())
                .titleBar(mainBinding.topBar.tBar)
                .navigationBarColor(R.color.white) // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
                .init()
        }
    }

    protected fun getRootView() = mainBinding.root

    private fun initTitleBar() {
        mainBinding.topBar.root.applyVisible(!topBarHide())
        mainBinding.topBar.leftIcon.onClick {
            leftClick()
            finishAfterTransition()
        }
        mainBinding.topBar.rightText.onClick { rightClick() }
        if (getRightIcon() != null) {
            mainBinding.topBar.rightIcon.setImageDrawable(getRightIcon()!!)
            mainBinding.topBar.rightIcon.onClick { rightIconClick() }
        }
        mainBinding.topBar.title.text = title()
        mainBinding.topBar.rightText.text = rightText()
        immersive(darkMode = darkMode())
    }

    open fun setTitle(title: String) {
        mainBinding.topBar.title.text = title
    }

    open fun getRightIcon(): Drawable? = null
    abstract fun getViewBinding(): T
    abstract fun initView()
    open fun initViewBundle(savedInstanceState: Bundle?) {}
    open fun onClick() {}

    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (isCanExit()) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(
                        applicationContext,
                        "再按一次退出",
                        Toast.LENGTH_SHORT
                    ).show()
                    exitTime = System.currentTimeMillis()
                } else {
                    BaseApplication.get().exit()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}