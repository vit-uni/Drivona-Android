package com.drivona.speed.widght

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.drivona.speed.R

class HeadLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){
    private val mRootView: View = LayoutInflater.from(getContext()).inflate(R.layout.head_layout, this)
    var header: ImageView? = null
    init {
        initView()
    }

    private fun initView() {
        header = mRootView.findViewById(R.id.header)
        val layoutParams = header?.layoutParams
    }
}