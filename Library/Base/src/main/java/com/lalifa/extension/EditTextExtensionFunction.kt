package com.hjq.widget.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 扩展函数
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun EditText.onTextChanged(onTextChanged: (String, Int, Int, Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString(), start, before, count)

        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun EditText.beforeTextChanged(addTextChangedListener: (String, Int, Int, Int) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            addTextChangedListener.invoke(s.toString(), start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
fun com.lalifa.widget.SearchEditText.searchClearTouch(event: MotionEvent) {
    /*判断手指按下的x坐标*/
    val x = event.x

    /*获取自定义EditText宽度*/
    val width = this.width.toFloat()

    /*获取EditText右Padding值*/
    val totalPaddingRight = this.totalPaddingRight.toFloat()

    /*判断手指按下的区域是否在删除按钮宽高范围内*/
    if (event.action == MotionEvent.ACTION_UP) {
        if (x > width - totalPaddingRight && x < width && event.y < this.height) {
            val drawable = compoundDrawables[2]
            if ("" != text.toString().trim { it <= ' ' } && text.toString().trim { it <= ' ' }.isNotEmpty() && drawable != null) {
                this.setText("")
            }
        }
    }
}

var rootViewVisibleHeight = 0

fun EditText.softKeyIsShow(): Boolean? {
    val rect = Rect()
    rootView.getWindowVisibleDisplayFrame(rect)
    var visibleHeight = rect.height()

    if (rootViewVisibleHeight == 0) {
        //拷贝一份，用于比较值的改变
        rootViewVisibleHeight = visibleHeight
        return null
    }
    //软键盘显示／隐藏状态没有改变
    if (rootViewVisibleHeight == visibleHeight) {
        return null
    }

    if (rootViewVisibleHeight - visibleHeight > 200) {
        rootViewVisibleHeight = visibleHeight
        return true
    }

    if (visibleHeight - rootViewVisibleHeight > 200) {
        rootViewVisibleHeight = visibleHeight
        return false
    }
    return false
}

fun EditText.hideSoftInput(){
    val im = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (im != null && windowToken != null) {
        im.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}