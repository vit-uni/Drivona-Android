package com.drivona.speed.widght

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.StringUtils
import com.drivona.speed.R
import com.drivona.speed.ui.adapter.medalList

class NameLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    val medal = ArrayList<String>()
    private val mRootView: View = LayoutInflater.from(getContext()).inflate(R.layout.name_layout, this)
    var name: TextView? = null
    var rvMedal: RecyclerView? = null
    var sex: ImageView? = null
    var nameColor: Int = Color.parseColor("#333333")

    init {
        val obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.NameLayout)
        nameColor = obtainStyledAttributes.getColor(R.styleable.NameLayout_nl_name_color, Color.parseColor("#333333"))
        initView()
    }

    private fun initView() {
        name = mRootView.findViewById(R.id.tv_name)
        rvMedal = mRootView.findViewById(R.id.rv_medal)
        sex = mRootView.findViewById(R.id.sex)
        name?.setTextColor(nameColor)
    }

    fun setName(name: String) {
        this.name?.text = name
    }

    fun setMedal(data: List<String>) {
        medal.clear()
        data.forEachIndexed { index, s ->
            if(!StringUtils.isEmpty(s)){
                medal.add(s)
            }
        }
        rvMedal?.medalList().apply {

        }?.models = medal
    }

    fun setSexImg(isGirl: Boolean) {
        sex?.isSelected = isGirl
        sex?.isVisible = isGirl
    }
}