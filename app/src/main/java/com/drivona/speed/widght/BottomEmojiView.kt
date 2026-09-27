package com.drivona.speed.widght

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.drivona.speed.R
import com.drivona.speed.ui.adapter.emojiAdapter
import com.drivona.speed.ui.adapter.emojiTab

class BottomEmojiView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var bottomEmojiListener: BottomEmojiListener? = null
    private val mRootView: View =
        LayoutInflater.from(getContext()).inflate(R.layout.emoji_layout, this)
    var rvEmojiTab: RecyclerView? = null
    var viewPager: ViewPager2? = null

    init {
        initView()
    }

    private fun initView() {
        rvEmojiTab = mRootView.findViewById(R.id.rv_emoji_tab)
        viewPager = mRootView.findViewById(R.id.view_pager)
        rvEmojiTab?.emojiTab()?.apply {
            models = arrayListOf()
            onClick(R.id.thumb) {
                viewPager?.currentItem = modelPosition
            }
        }

        viewPager?.emojiAdapter(arrayListOf(1, 2, 3)) {
            bottomEmojiListener?.onClickSend(it)
        }
    }

    fun setBottomEmojiListener(bottomEmojiListener: BottomEmojiListener) {
        this.bottomEmojiListener = bottomEmojiListener;
    }
    interface BottomEmojiListener {
        fun onClickSend(message: String?)
    }
}
