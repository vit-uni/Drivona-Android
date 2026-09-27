package com.drivona.speed.utils

import android.util.SparseArray
import android.widget.TextView
import androidx.core.view.isVisible
import com.lalifa.extension.gone
import java.text.DecimalFormat

/**
 * 房间真爱值管理
 */
class TrueLoveValueManager {
    private val textViews: SparseArray<TextView> = SparseArray()
    private val trueLoveValues: SparseArray<Int> = SparseArray()
    val d = DecimalFormat("0.0")

    fun setTextViews(index: Int, textView: TextView) {
        textViews.put(index, textView)
    }

    fun noticeTrueLoveValue(index: Int, trueLoveValue: Int) {
        trueLoveValues.put(index, trueLoveValue)
        if(trueLoveValue > 0) {
            textViews.get(index)?.let {
                if(trueLoveValue > 1000) {
                    it.text = "${d.format(trueLoveValue / 1000f)}k"
                } else {
                    it.text = trueLoveValue.toString()
                }
                if(!it.isVisible) {
                    it.isVisible = true
                }
            }
        } else {
            textViews.get(index)?.gone()
        }
    }

//    fun noticeTrueLoveValue(index: Int) {
//
//    }
}