package com.lalifa.extension

import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.lalifa.base.R
import com.lalifa.base.databinding.EmojiListItemBinding
import com.lalifa.base.databinding.EmojiTabLayoutBinding
import com.lalifa.base.databinding.ItemEmojiLayout2Binding

fun RecyclerView.emojiListAdapter(): BindingAdapter {
    return grid(8).setup {
        addType<String>(R.layout.emoji_list_item)
        onBind {
            val model = getModel<String>()
            getBinding<EmojiListItemBinding>().apply {
                tvContent.text = model
                if(modelPosition == 23) {
                    ivDelete.visible()
                    tvContent.gone()
                } else {
                    ivDelete.gone()
                    tvContent.visible()
                }
            }
        }
    }
}

fun RecyclerView.emojiTab(): BindingAdapter {
    return grid(4, scrollEnabled = false).setup {
        addType<Int>(R.layout.emoji_tab_layout)
        onBind {
            val bean = getModel<Int>()
            getBinding<EmojiTabLayoutBinding>().apply {
                thumb.setImageResource(bean)
            }
        }
    }
}

fun RecyclerView.rvEmojiAdapter(callback: (String) -> Unit): BindingAdapter {
    return grid(5).setup {
        addType<String>(R.layout.item_emoji_layout2)
        onBind {
            val bean = getModel<String>()
            getBinding<ItemEmojiLayout2Binding>().apply {
                thumb.load(bean.pk())
                thumb.onClick { callback.invoke(bean) }
            }
        }
    }
}
