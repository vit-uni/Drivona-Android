package com.drivona.speed.utils

import android.util.SparseArray
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible

/**
 * 房间真爱值管理
 */
class SeatMuteManager {
    private val textViews: SparseArray<AppCompatImageView> = SparseArray()

    fun setMutes(index: Int, imageView: AppCompatImageView) {
        textViews.put(index, imageView)
    }

    fun getMutes(index: Int): AppCompatImageView = textViews.get(index)

    fun noticeSeatMute(index: Int, isMute: Boolean) {
        textViews.get(index).isVisible = !isMute
    }
}