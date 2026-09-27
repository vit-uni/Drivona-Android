package com.drivona.speed.widght

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.drivona.speed.R
import com.lalifa.extension.onClick
import com.lalifa.extension.start

class  RoomBottomView(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private var mContext: AppCompatActivity? = null
    private val mRootView: View =
        LayoutInflater.from(context).inflate(R.layout.view_room_bottom, this)

    //话筒按钮
    var yy: ImageView? = null

    //声音按钮
    var lb: ImageView? = null

    //消息按钮
    var msg: ImageView? = null

    //礼物按钮
    var gift: ImageView? = null

    //菜单按钮
    var cd: ImageView? = null

    //话筒按钮
    var talk: TextView? = null
    var placeOrder: TextView? = null

    var unread: ImageView? = null
    private var bottomClickListener: BottomClickListener? = null

//    val v2TIMAdvancedMsgListener = object : V2TIMAdvancedMsgListener() {
//        //收到新消息
//        override fun onRecvNewMessage(msg: V2TIMMessage) {
//            if(msg?.isRead == true) {
//                unread?.isVisible = true
//            }
//        }
//    }

    interface BottomClickListener {
        fun canSend(type: Int)
        fun showMessageDialog()
        fun placeOrder()
    }

    var v1 = false
    var v2 = false
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        yy = mRootView.findViewById(R.id.yy)
        lb = mRootView.findViewById(R.id.lb)
        msg = mRootView.findViewById(R.id.msg)
        gift = mRootView.findViewById(R.id.gift)
        cd = mRootView.findViewById(R.id.cd)
        talk = mRootView.findViewById(R.id.tvTalk)
        unread = mRootView.findViewById(R.id.unread)
        placeOrder = mRootView.findViewById(R.id.place_order)
//        talk!!.onClick { mContext!!.clickSend(roomId) }
        placeOrder?.onClick { bottomClickListener?.placeOrder() }
//        V2TIMManager.getMessageManager()
//            .addAdvancedMsgListener(v2TIMAdvancedMsgListener)
        msg?.onClick {
            if (bottomClickListener != null) {
                unread?.isVisible = false
                bottomClickListener!!.showMessageDialog()
            }
        }
        gift!!.onClick {
            if (bottomClickListener != null) {
                bottomClickListener!!.canSend(4)
            }
        }
        yy!!.onClick {
            if (v1) {
//                yy!!.setBackgroundResource(R.drawable.ic_mic_switch_on)
            } else {
//                yy!!.setBackgroundResource(R.drawable.ic_mic_switch_off)
            }
//            closeMic(!v1)
            v1 = !v1
        }
        lb!!.onClick {
            if(v2){
//                lb!!.setBackgroundResource(R.drawable.ic_room_setting_unmute)
            }else{
//                lb!!.setBackgroundResource(R.drawable.ic_room_setting_mute)
            }
//            closeMute(UserManager.get()!!.user?.user_id,!v2)
            v2 = !v2
        }
        cd!!.onClick {
//            RoomSettingDialog(mContext!!) { it1 ->
//                when (it1) {
//                    0 -> {
//                        //房间特效
//                        showRoomSpecialEffectsPop()
//                    }
//                    2 -> {
//                        context?.start(ReportActivity::class.java) {
//                            putExtra("id", roomId)
//                            putExtra("type", 2)
//                        }
//                    }
//                    3 -> {
//                        //管理员
//                        dismiss()
//                        (context as Activity).showAdministratorList((mContext as RoomActivity).roomId, (mContext as RoomActivity).anchorId, (mContext as RoomActivity).onSeatList, (mContext as RoomActivity).mVoiceRoomSeatEntityList){}
//                    }
//                    4 -> {
//                        //公屏管理
//                        dismiss()
//                        (context as Activity).showPublicScreenManagement(roomId) {
//                            when (it.id) {
//                                R.id.sb_speak_public_screen -> {
//                                    MUtils.messageList.clear()
//                                    MUtils.worldChannelMessageList.clear()
//                                    MUtils.interactionMessageList.clear()
//                                    (mContext as RoomActivity).msgAdapter?.models = if((mContext as RoomActivity).mssgType == 1) MUtils.messageList else if((mContext as RoomActivity).mssgType == 2) MUtils.worldChannelMessageList else  MUtils.interactionMessageList
//                                    (mContext as RoomActivity).msgAdapter?.notifyDataSetChanged()
//                                }
//                            }
//                        }
//                    }
//                }
//            }.show(mContext?.supportFragmentManager!!, "dialog")
//            mContext!!.showRoomSetDialog { it ->
//                when (it) {
//                    1 -> {
//
//                    }
//                    2 -> {
//                        //PK
//
//                    }
//                    3 -> {
//                        //贵族特权
//                        mContext!!.start(VipActivity::class.java) {
//                            putExtra("roomId", roomId)
//                        }
//                    }
//                    4 -> {
//                        //装扮中心
////                        mContext!!.start(ShopActivity::class.java) {
////                            putExtra("roomId", roomId)
////                        }
//                    }
//                    5 -> {
//                        //房间信息
//                        mContext!!.start(RoomSettingActivity::class.java) {
//                            putExtra("roomId", roomId)
//                        }
//                    }
//                    6 -> {
//                        //主持人
//
//                    }
//                    7 -> {
//                        //清空消息
//                        mContext!!.showTipDialog("确定清空聊天列表？") {
//                            if (bottomClickListener != null) {
//                                bottomClickListener!!.canSend(17)
//                            }
//                        }
//                    }
//                    8 -> {
//                        //清空魅力值
//                        mContext!!.showTipDialog("确定清空所有麦位魅力值？") {
//                            if (bottomClickListener != null) {
//                                bottomClickListener!!.canSend(18)
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    /**
     * 房间特效
     */
    private fun showRoomSpecialEffectsPop() {
//        RoomSpecialEffectsDIalog(mContext!!) {
//
//        }.show(mContext?.supportFragmentManager!!, "dialog")
    }

    private var roomId: Int = 0

    init {
        initView()
    }

    fun setRoomId(context: AppCompatActivity, inputBarListener: BottomClickListener?, roomId: Int) {
        this.roomId = roomId
        mContext = context
        this.bottomClickListener = inputBarListener
    }

    fun setGiftVisible(isVisible: Boolean) {
        gift?.isVisible = isVisible
    }

    fun showPlaceOrder() {
        placeOrder?.isVisible = true
    }

    override fun onDetachedFromWindow() {
//        V2TIMManager.getMessageManager().removeAdvancedMsgListener(v2TIMAdvancedMsgListener)
        super.onDetachedFromWindow()
    }
}