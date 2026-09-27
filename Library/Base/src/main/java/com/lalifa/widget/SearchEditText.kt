package com.lalifa.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.hjq.widget.view.afterTextChanged
import com.hjq.widget.view.hideSoftInput
import com.hjq.widget.view.searchClearTouch
import com.lalifa.base.R

class SearchEditText(context: Context?, attrs: AttributeSet?) : AppCompatEditText(context!!, attrs),
    TextView.OnEditorActionListener, View.OnFocusChangeListener,
    ViewTreeObserver.OnGlobalLayoutListener {

    constructor(context: Context?) : this(context, null)

    var rootViewVisibleHeight = 0
    var clear_icon = 0
    var logo = 0
    var logo_location = 0
    var show_clears = true

    private var rightClearDrawable: Drawable? = null
    private var drawable: Drawable? = null
    private var searchDrawable: Drawable? = null
    private var mOnEditorActionListener: MyOnEditorActionListener? = null
    private var mOnFocusChangeListener: MyOnFocusChangeListener? = null
    private var onSoftKeyShowListener: OnSoftKeyShowListener? = null

    init {
        isSingleLine = true
        val obtainStyledAttributes =
            context?.obtainStyledAttributes(attrs, R.styleable.SearchEditText)
        clear_icon = obtainStyledAttributes?.getResourceId(
            R.styleable.SearchEditText_clears_icon,
            R.drawable.input_delete_ic
        )!!
        logo = obtainStyledAttributes.getResourceId(R.styleable.SearchEditText_logo, -1)
        logo_location =
            obtainStyledAttributes.getInteger(R.styleable.SearchEditText_logo_location, 2)
        show_clears =
            obtainStyledAttributes.getBoolean(R.styleable.SearchEditText_show_clears, true)
        obtainStyledAttributes.recycle()

        /*获取删除按钮图片的Drawable对象*/
        drawable = ContextCompat.getDrawable(
            context!!,
            if (clear_icon == 0) R.drawable.input_delete_ic else clear_icon
        )
        searchDrawable = ContextCompat.getDrawable(
            context!!,
            if (logo == -1) R.drawable.input_delete_ic else logo
        )

        /*设置图片的范围*/
        drawable!!.setBounds(0, 0, convertDpToPixel(13f).toInt(), convertDpToPixel(13f).toInt())
        searchDrawable!!.setBounds(
            0,
            0,
            convertDpToPixel(15f).toInt(),
            convertDpToPixel(15f).toInt()
        )

        /*设置EditText和删除按钮图片的间距*/
        compoundDrawablePadding = context.resources.getDimensionPixelSize(R.dimen.dp8)

        /*输入框内容监听*/
        afterTextChanged {
            /*判断输入框有没有内容，设置是否显示删除按钮*/
            if ("" != text.toString().trim { it <= ' ' } && text.toString().trim { it <= ' ' }
                    .isNotEmpty()) {
                setHideClearDrawable(true)
            } else {
                setHideClearDrawable(false)
            }
        }

        /*设置是否显示删除按钮*/
        setHideClearDrawable(false)

        if (logo_location == 1) {
            setCompoundDrawables(
                searchDrawable,
                compoundDrawables[1],
                rightClearDrawable,
                compoundDrawables[3]
            )
        }

        setOnEditorActionListener(this)

        onFocusChangeListener = this

        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    private fun setHideClearDrawable(isVisible: Boolean) {
        /*是否显示删除按钮*/
        rightClearDrawable = if (isVisible) {
            drawable
        } else {
            if (logo_location == 2) searchDrawable else null
        }

        /*给EditText左，上，右，下设置图片*/
        setCompoundDrawables(
            compoundDrawables[0],
            compoundDrawables[1],
            rightClearDrawable,
            compoundDrawables[3]
        )
    }

    /**
     * 监听点击事件是否点击到清空按钮
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        searchClearTouch(event)
        return super.onTouchEvent(event)
    }

    /**
     * 监听回车时间
     */
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            clearFocus()
            if (!TextUtils.isEmpty(text)) {
                setCompoundDrawables(
                    compoundDrawables[0],
                    compoundDrawables[1],
                    null,
                    compoundDrawables[3]
                )
                hideSoftInput()
            }
        }
        mOnEditorActionListener?.let {
            it.onEditorAction(v, actionId, event)
        }
        return false
    }

    /**
     * 监听是否获取焦点
     */
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            // 此处为得到焦点时的处理内容
            if (!TextUtils.isEmpty(text)) {
                setHideClearDrawable(true)
            } else {
                setHideClearDrawable(false)
            }
        } else {
            // 此处为失去焦点时的处理内容
            hideSoftInput()
            if (!TextUtils.isEmpty(text)) {
                setCompoundDrawables(
                    compoundDrawables[0], compoundDrawables[1],
                    null, compoundDrawables[3]
                )
            }
        }
        mOnFocusChangeListener?.onFocusChange(v, hasFocus)
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        var visibleHeight = rect.height()

        if (rootViewVisibleHeight == 0) {
            //拷贝一份，用于比较值的改变
            rootViewVisibleHeight = visibleHeight
            return
        }
        //软键盘显示／隐藏状态没有改变
        if (rootViewVisibleHeight == visibleHeight) {
            return
        }

        if (rootViewVisibleHeight - visibleHeight > 200) {
            onSoftKeyShowListener?.onSoftKeyShow(true)
            rootViewVisibleHeight = visibleHeight
            return
        }

        if (visibleHeight - rootViewVisibleHeight > 200) {
            clearFocus()
            onSoftKeyShowListener?.onSoftKeyShow(false)
            rootViewVisibleHeight = visibleHeight
            return
        }
    }

    fun convertDpToPixel(dp: Float): Float {
        val metrics = context.resources.displayMetrics
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun setOnSoftKeyShowListener(onSoftKeyShowListener: OnSoftKeyShowListener) {
        this.onSoftKeyShowListener = onSoftKeyShowListener
    }

    interface OnSoftKeyShowListener {
        fun onSoftKeyShow(isShow: Boolean)
    }

    fun setMyOnEditorActionListener(l: MyOnEditorActionListener?) {
        mOnEditorActionListener = l
    }

    interface MyOnEditorActionListener {
        fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?)
    }

    fun setMyOnFocusChangeListener(f: MyOnFocusChangeListener) {
        mOnFocusChangeListener = f;
    }

    interface MyOnFocusChangeListener {
        fun onFocusChange(v: View?, hasFocus: Boolean)
    }
}