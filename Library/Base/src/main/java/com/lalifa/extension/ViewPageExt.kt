package com.lalifa.extension

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.drake.brv.BindingAdapter
import com.drake.net.utils.scopeNetLife
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lalifa.base.R
import com.lalifa.base.databinding.EmojiItemBinding
import com.lalifa.base.databinding.IteAddAccount2Binding
import com.lalifa.base.databinding.IteAddAccountBinding
import com.lalifa.base.databinding.IteDayInterestTagsBinding
import kotlin.math.abs

/**
 *
 * @ClassName ViewPageExt
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/11 12:46
 *
 */
fun ViewPager.fragmentAdapter(
    fm: FragmentManager,
    mTitles: ArrayList<String>,
    mFragment: ArrayList<Fragment>.() -> Unit,
): ViewPager {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    offscreenPageLimit = if (mTitles.size >= 2) mTitles.size - 1 else mTitles.size
    adapter = object : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
    return this
}

fun ViewPager.fragmentAdapter(
    fm: FragmentManager,
    mFragment: ArrayList<Fragment>.() -> Unit
): ViewPager {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    offscreenPageLimit = if (mFragments.size >= 2) mFragments.size - 1 else mFragments.size
    adapter = object : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return ""
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
    return this
}

fun ViewPager2.addAccountAdapter(
    callback: (type: Int, name: String, ali: String, carNum: String, carName: String, householdBank: String) -> Unit,
    function: (com.drake.brv.BindingAdapter) -> Unit
): com.drake.brv.BindingAdapter {
    var bindingAdapter = com.drake.brv.BindingAdapter().apply {
        addType<Int> {
            when (this) {
                2 -> {
                    R.layout.ite_add_account2
                }
                else -> {
                    R.layout.ite_add_account
                }
            }
        }

        onBind {
            when (itemViewType) {
                R.layout.ite_add_account2 -> {
                    getBinding<IteAddAccount2Binding>().apply {
                        next.onClick {
                            val name = name.text.toString()
                            val bankCard = bankCardNumber.text.toString()
                            val cardName = bankCardName.text.toString()
                            val mAccountBank = accountBank.text.toString()
                            callback.invoke(2, name, "", bankCard, cardName, mAccountBank)
                        }
                    }
                }
                else -> {
                    getBinding<IteAddAccountBinding>().apply {
                        next.onClick {
                            val name = name.text.toString()
                            val aliAccount = aliAccount.text.toString()
                            callback.invoke(1, name, aliAccount, "", "", "")
                        }
                    }
                }
            }
        }
    }
    adapter = bindingAdapter
    function.invoke(bindingAdapter)
    return bindingAdapter
}

fun ViewPager.fragmentStateAdapter(
    fm: FragmentManager,
    mTitles: ArrayList<String>,
    mFragment: ArrayList<Fragment>.() -> Unit
) {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    offscreenPageLimit = if (mTitles.size >= 2) mTitles.size - 1 else mTitles.size
    adapter = object : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
}

fun ViewPager2.fragmentAdapter(
    orientation: Int = ViewPager2.ORIENTATION_HORIZONTAL,
    mFragment: ArrayList<Fragment>.() -> Unit,
) {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    this.orientation = orientation
    adapter = object : FragmentStateAdapter(context as FragmentActivity) {
        override fun getItemCount(): Int {
            return mFragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return mFragments[position]
        }
    }
    offscreenPageLimit = 3
}

fun TabLayout.setViewPager(viewPager2: ViewPager2, mTitles: ArrayList<String>) {
    TabLayoutMediator(this, viewPager2) { tab, position ->
        tab.text = mTitles[position]
    }.attach()
}

/**
 * 切换动画
 * @receiver ViewPager
 */
fun ViewPager.zoomPageTransformer() {
    setPageTransformer(true) { page, position ->
        val MIN_ALPHA = 0.5f
        val MIN_SCALE = 0.85f
        page.apply {
            when {
                position < -1 -> alpha = 0f
                position <= 1 -> {
                    val scaleFactor = MIN_ALPHA.coerceAtLeast(1 - abs(position))
                    val vertMargin = height * (1 - scaleFactor) / 2
                    val horzMargin = width * (1 - scaleFactor) / 2
                    translationX =
                        if (position < 0) horzMargin - vertMargin / 2 else -horzMargin + vertMargin / 2
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha =
                        MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
                }
                else -> alpha = 0f
            }

        }
    }
}

fun ViewPager.pageChangedListener(
    onPageScrolled: (
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) -> Unit = { _, _, _ -> },
    onPageScrollStateChanged: (state: Int) -> Unit = { _ -> },
    onPageSelected: (position: Int) -> Unit = { _ -> },
) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }

    })
}

fun ViewPager.viewAdapter(
    mTitles: ArrayList<String>,
    views: ArrayList<View>.() -> Unit,
) {
    val list = arrayListOf<View>().apply(views)
    offscreenPageLimit = if (mTitles.size >= 2) mTitles.size - 1 else mTitles.size
    adapter = object : PagerAdapter() {
        override fun getCount(): Int = list.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(list[position])
            return list[position]
        }

        override fun getPageTitle(position: Int) = mTitles[position]
    }
}

fun ViewPager.viewAdapter(
    views: ArrayList<View>.() -> Unit,
): ViewPager {
    val list = arrayListOf<View>().apply(views)
    offscreenPageLimit = if (list.size >= 2) list.size - 1 else list.size
    adapter = object : PagerAdapter() {
        override fun getCount(): Int = list.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(list[position])
            return list[position]
        }
    }
    return this
}

fun ViewPager2.emojiAdapter2(clickeCallBack: (Int, String) -> Unit, callback: BindingAdapter.() -> Unit): ViewPager2 {
    var bindingAdapter = BindingAdapter().apply {
        addType<ArrayList<String>>(R.layout.emoji_item)
        onBind {
            val model = getModel<ArrayList<String>>()
            getBinding<EmojiItemBinding>().apply {
                recyclerView.emojiListAdapter().apply {
                    models = model
                    onClick(R.id.tv_content, R.id.iv_delete) {
                        val model1 = getModel<String>()
                        when (it) {
                            R.id.tv_content -> clickeCallBack.invoke(1, model1)
                            R.id.iv_delete -> clickeCallBack.invoke(2, "")
                        }
                    }
                }
            }
        }
    }
    adapter = bindingAdapter
    callback.invoke(bindingAdapter)
    return this
}

fun ViewPager2.pageChangedListener(
    onPageScrolled: (
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) -> Unit = { _, _, _ -> },
    onPageScrollStateChanged: (state: Int) -> Unit = { _ -> },
    onPageSelected: (position: Int) -> Unit = { _ -> },
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }

    })
}

fun ViewPager2.emojiAdapter(
    list: ArrayList<Int>,
    callback: (String) -> Unit
): com.drake.brv.BindingAdapter {
    var bindingAdapter = com.drake.brv.BindingAdapter().apply {
        addType<Int>(R.layout.ite_day_interest_tags)
        onBind {
            val model1 = getModel<Int>()
            getBinding<IteDayInterestTagsBinding>().apply {
                scopeNetLife {
//                    val roomsIcon = roomsIcon(model1)
                    recyclerView.rvEmojiAdapter(callback).apply {
//                        this.models = roomsIcon?.icon
                    }
                }
            }
        }
        models = list
    }
    adapter = bindingAdapter
    return bindingAdapter
}