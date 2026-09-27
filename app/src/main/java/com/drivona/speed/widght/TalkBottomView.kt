package com.drivona.speed.widght

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.DeviceUtils.getModel
import com.blankj.utilcode.util.ToastUtils
import com.drake.net.utils.scopeNetLife
import com.drivona.speed.R
import com.drivona.speed.api.Gift
import com.drivona.speed.databinding.FragmentListBinding
import com.drivona.speed.service.AppCache
import com.drivona.speed.ui.adapter.emojiAdapter
import com.drivona.speed.ui.adapter.roomGiftAdapter
import com.flyco.tablayout.SlidingTabLayout
import com.google.gson.Gson
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Tools
import com.lalifa.extension.*
import com.lalifa.widget.SoftKeyboardUtils


open class TalkBottomView(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {
    private var mContext: AppCompatActivity? = null
    private val mRootView: View

    //消息输入框
    var etInput: EditText? = null

    //消息发送按钮
    var send: TextView? = null

    //语音按钮
    var speak: TextView? = null

    //切换语音、输入按钮
    var changeInput: ImageView? = null

    //拍照按钮
    var pick: ImageView? = null

    //选择图片按钮
    var chooseIm: ImageView? = null

    //表情按钮
    var emoji: ImageView? = null

    //发送礼物按钮
    var gift: ImageView? = null

    //礼物弹框
    var giftCo: ConstraintLayout? = null

    //表情弹框
    var emojiList: RecyclerView? = null

    //礼物页
    var tabLayout: SlidingTabLayout? = null

    //礼物页
    var viewPager: ViewPager? = null

    //去充值
    var myMoney: TextView? = null

    //余额
    var money: TextView? = null
    var llNum: LinearLayoutCompat? = null
    var giftNumList: RecyclerView? = null
    var etGiftNum: TextView? = null
    var etNum: TextView? = null
    var btnBuy: TextView? = null
    var voice: ImageView? = null

    private var inputBarListener: InputBarListener? = null

    fun back() {
        if (emojiList != null && giftCo != null) {
            if (emojiList!!.isVisible) {
                emojiList!!.gone()
                emoji!!.isSelected = false
            } else {
                if (giftCo!!.isVisible) {
                    giftCo!!.gone()
                    gift!!.isSelected = false
                } else {
                    AppCache.getPlayService().quit()
                    mContext?.finish()
                }
            }
        } else {
            AppCache.getPlayService().quit()
            mContext!!.finish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        changeInput = mRootView.findViewById(R.id.changeInput)
        etInput = mRootView.findViewById(R.id.etMessage)
        pick = mRootView.findViewById(R.id.pick)
        send = mRootView.findViewById(R.id.send)
        speak = mRootView.findViewById(R.id.speak)
        chooseIm = mRootView.findViewById(R.id.chooseIm)
        giftCo = mRootView.findViewById(R.id.giftCo)
        emojiList = mRootView.findViewById(R.id.emojiList)
        gift = mRootView.findViewById(R.id.gift)
        emoji = mRootView.findViewById(R.id.emoji)
        tabLayout = mRootView.findViewById(R.id.tab_layout)
        viewPager = mRootView.findViewById(R.id.viewPager)
        myMoney = mRootView.findViewById(R.id.myMoney)
        money = mRootView.findViewById(R.id.money)
        llNum = mRootView.findViewById(R.id.llNum)
        giftNumList = mRootView.findViewById(R.id.giftNumList)
        etGiftNum = mRootView.findViewById(R.id.etGiftNum)
        etNum = mRootView.findViewById(R.id.etNum)
        btnBuy = mRootView.findViewById(R.id.btnBuy)
        voice = mRootView.findViewById(R.id.voice)
        voice?.setOnClickListener { speak?.isVisible = !speak?.isVisible!! }
        emojiList!!.emojiAdapter().apply {
            R.id.emoji.onClick {
                //发送表情
                val gson = Gson()
//                val m = gson.toJson(MsgOfSend(1, "$modelPosition"))
//                val msg = V2TIMManager.getMessageManager().createCustomMessage(m.toByteArray())
//                MUtils.sendMessage(targetId, msg) { s ->
//                    if (s) {
//                        emoji!!.isSelected = false
//                        emojiList!!.gone()
//                        if (inputBarListener != null) {
//                            inputBarListener!!.isSend()
//                        }
//                    }
//                }
            }
        }.models =
            arrayListOf(
                R.drawable.ic_bq_dx,
                R.drawable.ic_bq_dy,
                R.drawable.ic_bq_fd,
                R.drawable.ic_bq_jc,
                R.drawable.ic_bq_jx,
                R.drawable.ic_bq_jy,
                R.drawable.ic_bq_kn,
                R.drawable.ic_bq_kq,
                R.drawable.ic_bq_ku,
                R.drawable.ic_bq_kz,
                R.drawable.ic_bq_lh,
                R.drawable.ic_bq_qq,
                R.drawable.ic_bq_qs,
                R.drawable.ic_bq_sb,
                R.drawable.ic_bq_se,
                R.drawable.ic_bq_st,
                R.drawable.ic_bq_ts,
                R.drawable.ic_bq_wx,
                R.drawable.ic_bq_xk,
                R.drawable.ic_bq_yh
            )
        changeInput!!.onClick {
            if (changeInput!!.isSelected) {
                setBg()
                changeInput!!.isSelected = false
                speak!!.visibility = GONE
            } else {
                setBg()
                changeInput!!.isSelected = true
                speak!!.visibility = VISIBLE
                SoftKeyboardUtils.hideSoftKeyboard(etInput, 200)
            }
        }
        send!!.onClick { send() }
        etInput!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            send()
            false
        }
        myMoney!!.onClick {
//            mContext!!.start(PurseActivity::class.java)
        }
        etNum!!.onClick {
            llNum!!.isVisible = !llNum!!.isVisible
        }
        btnBuy!!.onClick {
            if (sendGiftId.isEmpty()) {
                mContext!!.toast("请选择要赠送的礼物")
                return@onClick
            }
            scopeNetLife {
//                val sendGift =
//                    sendGiftApi(sendGiftType, targetId, etNum!!.text.toString(), sendGiftId)
//                if (sendGift != null) {
//                    //发送礼物
//                    val gson = Gson()
//                    val m = gson.toJson(
//                        GiftOfSend(
//                            etNum!!.text.toString().toInt(),
//                            UserManager.get()!!.user?.nickname,
//                            Tools.FILE_PATH + UserManager.get()!!.user?.avatar,
//                            targetId,
//                            sendGiftId,
//                            0,
//                            sendGiftName,
//                            sendGiftPath,
//                            sendGiftLottiePath
//                        )
//                    )
//                    val m0 = gson.toJson(MsgOfSend(2, m))
//                    val msg = V2TIMManager.getMessageManager().createCustomMessage(m0.toByteArray())
//                    MUtils.sendMessage(targetId, msg) { s ->
//                        if (s) {
//                            emoji!!.isSelected = false
//                            emojiList!!.gone()
//                            if (inputBarListener != null) {
//                                inputBarListener!!.isSend()
//                            }
//                        }
//                    }
//                }
            }
        }
        emoji!!.onClick {
            if (emojiList!!.isVisible) {
                setBg()
                emojiList!!.gone()
            } else {
                setBg()
                emoji!!.isSelected = true
                emojiList!!.visible()
                if (giftCo!!.isVisible) {
                    giftCo!!.gone()
                }
            }
        }
        gift!!.onClick {
            if (giftCo!!.isVisible) {
                setBg()
                giftCo!!.gone()
            } else {
                setBg()
                gift!!.isSelected = true
                giftCo!!.visible()
                if (emojiList!!.isVisible) {
                    emojiList!!.gone()
                }
            }
        }

        chooseIm!!.onClick {
            if (mContext != null) {
                mContext!!.imagePick(false, 1) { it ->
                    if (it.size > 0) {
                        // 创建图片消息
//                        val v2TIMMessage =
//                            V2TIMManager.getMessageManager().createImageMessage(it[0].path)
//                        MUtils.sendMessage(targetId, v2TIMMessage) { s ->
//                            if (s) {
//                                if (inputBarListener != null) {
//                                    inputBarListener!!.isSend()
//                                }
//                            }
//                        }
                    }
                }
            }
        }
    }

    private fun setBg() {
        changeInput!!.isSelected = false
        pick!!.isSelected = false
        chooseIm!!.isSelected = false
        emoji!!.isSelected = false
        gift!!.isSelected = false
    }

    private var targetId = ""

    init {
        mRootView = LayoutInflater.from(context).inflate(R.layout.view_talk_bottom, this)
        initView()
    }

    private fun send() {
        var message = ""
        if (etInput!!.text != null) {
            message = etInput!!.text.toString().trim { it <= ' ' }
        }
        if (TextUtils.isEmpty(message)) {
            context.toast("消息不能为空")
            return
        }
        scopeNetLife {
//            val socialGetUserSet = messageSensitiveWordCheck(message)
//            if(socialGetUserSet.code == 200 ) {
//                etInput!!.setText("")
//                hideInputBar()
//                MUtils.sendMessage(targetId, V2TIMManager.getMessageManager().createTextMessage(message)) {
//                    if (it) {
//                        if (inputBarListener != null) {
//                            inputBarListener!!.isSend()
//                        }
//                    }
//                }
//            } else {
//                ToastUtils.showShort("发送的内容包含敏感内容")
//            }
        }

    }

    fun hideInputBar() {
//        etInput!!.clearFocus()
//        SoftKeyboardUtils.hideSoftKeyboard(etInput)
    }

    fun setInputBarListener(
        c: AppCompatActivity,
        inputBarListener: InputBarListener?,
        tId: String
    ) {
//        this.mContext = c
//        this.inputBarListener = inputBarListener
//        this.targetId = tId
//        scopeNetLife {
//            val roomGift = roomGiftApi()
//            if (roomGift != null) {
//                money!!.text = "${roomGift.total_price}"
//                giftNumList!!.giftNumAdapter().apply {
//                    R.id.co.onClick {
//                        val frequency = getModel<GiftFrequency>().frequency
//                        etNum!!.text = frequency
//                        llNum!!.gone()
//                    }
//                }.models = roomGift.gift_frequency
//                viewPager!!.fragmentAdapter(
//                    mContext!!.supportFragmentManager,
//                    arrayListOf("礼物", "背包")
//                ) {
//                    add(GiftFrag(1, roomGift.gift))
//                    add(GiftFrag(2, roomGift.knapsack))
//                }
//                tabLayout!!.setViewPager(viewPager)
//            }
//        }
    }

    interface InputBarListener {
        fun isSend()
    }

    companion object {
        var sendGiftId = ""
        var sendGiftName = ""
        var sendGiftPath = ""
        var sendGiftLottiePath = ""
        var sendGiftType = 1
    }

    class GiftFrag(val type: Int, val list: List<Gift>) : BaseFragment<FragmentListBinding>() {
        override fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
        ) = FragmentListBinding.inflate(layoutInflater)

        override fun initView() {
            binding.giftList.roomGiftAdapter(1).apply {
                R.id.im.onClick {
                    list.forEach {
                        it.choose = false
                    }
                    getModel<Gift>().choose = true
                    sendGiftId = getModel<Gift>().id.toString()
                    sendGiftName = getModel<Gift>().name
                    sendGiftPath = Tools.FILE_PATH + getModel<Gift>().thumb
                    sendGiftType = type
                    sendGiftLottiePath = Tools.FILE_PATH + getModel<Gift>().thumb
                    notifyDataSetChanged()
                }
            }.models = list
        }
    }
}

