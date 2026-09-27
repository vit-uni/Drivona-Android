package com.drivona.speed.ui.adapter

import androidx.viewpager2.widget.ViewPager2
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.R
import com.lalifa.base.databinding.IteDayInterestTagsBinding

fun ViewPager2.emojiAdapter(
    list: ArrayList<Int>,
    callback: (String) -> Unit
): com.drake.brv.BindingAdapter {
    var bindingAdapter = com.drake.brv.BindingAdapter().apply {
        addType<Int>(R.layout.ite_day_interest_tags)
        onBind {
            val model1 = getModel<Int>()
            getBinding<IteDayInterestTagsBinding>().apply {
//                scopeNetLife {
//                    val roomsIcon = roomsIcon(model1)
//                    recyclerView.rvEmojiAdapter(callback).apply {
//                        this.models = roomsIcon?.icon
//                    }
//                }
            }
        }
        models = list
    }
    adapter = bindingAdapter
    return bindingAdapter
}