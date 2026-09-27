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
import com.drivona.speed.R
import com.drivona.speed.api.RankingBean
import com.lalifa.extension.load
import com.lalifa.extension.pk


open class RankTopView(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private var mContext: AppCompatActivity? = null
    private val mRootView: View

    var head1: ImageView? = null
    var head2: ImageView? = null
    var head3: ImageView? = null
    var name1: TextView? = null
    var name2: TextView? = null
    var name3: TextView? = null
    var tip1: TextView? = null
    var tip2: TextView? = null
    var tip3: TextView? = null
    var hc1: TextView? = null
    var hc2: TextView? = null
    var hc3: TextView? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        head1 = mRootView.findViewById(R.id.head1)
        head2 = mRootView.findViewById(R.id.head2)
        head3 = mRootView.findViewById(R.id.head3)
        name1 = mRootView.findViewById(R.id.name1)
        name2 = mRootView.findViewById(R.id.name2)
        name3 = mRootView.findViewById(R.id.name3)
        tip1 = mRootView.findViewById(R.id.tip1)
        tip2 = mRootView.findViewById(R.id.tip2)
        tip3 = mRootView.findViewById(R.id.tip3)
        hc1 = mRootView.findViewById(R.id.hc1)
        hc2 = mRootView.findViewById(R.id.hc2)
        hc3 = mRootView.findViewById(R.id.hc3)
    }

    init {
        mRootView = LayoutInflater.from(context).inflate(R.layout.rank_top_view, this)
        initView()
    }

    @SuppressLint("SetTextI18n")
    fun setData(list: List<RankingBean>) {
        when (list.size) {
            1 -> {
                head1!!.load(list[0]?.user?.avatar.pk())
                name1!!.text = list[0]?.user?.nickname.pk()
                hc1!!.text = "${list[0]?.total?:0}贡献"
            }
            2 -> {
                head1!!.load(list[0]?.user?.avatar.pk())
                name1!!.text = list[0]?.user?.nickname.pk()
                hc1!!.text = "${list[0]?.total?:0}贡献"
                head2!!.load(list[1]?.user?.avatar.pk())
                name2!!.text = list[1]?.user?.nickname
                hc2!!.text = "${list[1].total?:0}贡献"
            }
            3 -> {
                head1!!.load(list[0]?.user?.avatar.pk())
                name1!!.text = list[0]?.user?.nickname
                hc1!!.text = "${list[0]?.total?:0}贡献"
                head2!!.load(list[1]?.user?.avatar.pk())
                name2!!.text = list[1]?.user?.nickname
                hc2!!.text = "${list[1].total?:0}贡献"
                head3!!.load(list[2]?.user?.avatar.pk())
                name3!!.text = list[2]?.user?.nickname
                hc3!!.text = "${list[2]?.total?:0}贡献"
            }
        }
    }

}