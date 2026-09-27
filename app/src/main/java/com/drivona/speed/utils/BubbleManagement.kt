package com.drivona.speed.utils

import android.util.SparseArray

/**
 * 气泡管理
 */
class BubbleManagement {
    private val textViews: SparseArray<String> = SparseArray()

    fun setBubble(index: Int, url: String) {
        textViews.put(index, url)
    }

    fun getBubble(index: Int): String? = textViews.get(index)

}