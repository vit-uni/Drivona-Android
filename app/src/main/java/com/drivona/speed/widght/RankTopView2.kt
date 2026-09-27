package com.drivona.speed.widght

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.drivona.speed.R
import com.drivona.speed.api.RankingBean
import com.drivona.speed.ui.adapter.medalList
import com.lalifa.extension.load


open class RankTopView2(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private var mContext: AppCompatActivity? = null
    private val mRootView: View

    var head1: ImageView? = null
    var head2: ImageView? = null
    var head3: ImageView? = null
    var name1: TextView? = null
    var name2: TextView? = null
    var name3: TextView? = null
    var medal1: RecyclerView? = null
    var medal2: RecyclerView? = null
    var medal3: RecyclerView? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        head1 = mRootView.findViewById(R.id.header1)
        head2 = mRootView.findViewById(R.id.header2)
        head3 = mRootView.findViewById(R.id.header3)
        name1 = mRootView.findViewById(R.id.name1)
        name2 = mRootView.findViewById(R.id.name2)
        name3 = mRootView.findViewById(R.id.name3)
        medal1 = mRootView.findViewById(R.id.medal1)
        medal2 = mRootView.findViewById(R.id.medal2)
        medal3 = mRootView.findViewById(R.id.medal3)
    }

    init {
        mRootView = LayoutInflater.from(context).inflate(R.layout.rank_top_view2, this)
        initView()
    }

    @SuppressLint("SetTextI18n")
    fun setData(list: List<RankingBean>) {
        when (list.size) {
            1 -> {
                head1!!.load(list[0]?.user?.avatar)
                name1!!.text = list[0]?.user?.nickname
                medal1?.medalList()
            }
            2 -> {
                head1!!.load(list[0]?.user?.avatar)
                name1!!.text = list[0]?.user?.nickname
                head2!!.load(list[1]?.user?.avatar)
                name2!!.text = list[1]?.user?.nickname
                medal1?.medalList()
                medal2?.medalList()
            }
            3 -> {
                head1!!.load(list[0]?.user?.avatar)
                name1!!.text = list[0]?.user?.nickname
                head2!!.load(list[1]?.user?.avatar)
                name2!!.text = list[1]?.user?.nickname
                head3!!.load(list[2]?.user?.avatar)
                name3!!.text = list[2]?.user?.nickname
                medal1?.medalList()
                medal2?.medalList()
                medal3?.medalList()
            }
        }
    }

}