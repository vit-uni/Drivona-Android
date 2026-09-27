package com.drivona.speed.utils

//import android.content.Context
//import androidx.appcompat.app.AppCompatActivity
//import com.blankj.utilcode.util.LogUtils
//import com.blankj.utilcode.util.SPUtils
//import com.blankj.utilcode.util.ToastUtils
//import com.drake.net.utils.scopeNet
//import com.drake.tooltip.toast
//import com.feijing.yypeiwan.R
//import com.feijing.yypeiwan.api.ColorDTO
//import com.feijing.yypeiwan.api.Expression
//import com.feijing.yypeiwan.api.GetMsgBean
//import com.feijing.yypeiwan.api.Gift
//import com.feijing.yypeiwan.api.RoomInfoBean
//import com.feijing.yypeiwan.api.RoomSet
//import com.feijing.yypeiwan.api.SenBarrage
//import com.feijing.yypeiwan.api.WolrdGift
//import com.feijing.yypeiwan.api.WorldChannelMessage
//import com.feijing.yypeiwan.api.messageSensitiveWordCheck
//import com.feijing.yypeiwan.tool.GiftInfo
//import com.feijing.yypeiwan.tool.Member
//import com.feijing.yypeiwan.tool.UserManager
//import com.feijing.yypeiwan.tool.showPublishBroadcastInputDialog
//import com.feijing.yypeiwan.tool.showRoomPeopleDialog
//import com.feijing.yypeiwan.tool.showShangMaiInteractiveDialog
//import com.feijing.yypeiwan.tool.showUpSeatDialog
//import com.example.analogtelephone.utils.Tool.Companion.IM_WORLD
//import com.example.analogtelephone.widght.GiftAnimatorLayout
//import com.example.analogtelephone.widght.InputBar
//import com.example.analogtelephone.widght.InputBarDialog
//import com.google.gson.Gson
//import com.hjq.permissions.Permission
//import com.hjq.permissions.XXPermissions
//import com.lalifa.ext.Tools
//import com.lalifa.extension.pk
//import com.lalifa.extension.toast
//import com.opensource.svgaplayer.SVGADrawable
//import com.opensource.svgaplayer.SVGAImageView
//import com.opensource.svgaplayer.SVGAParser
//import com.opensource.svgaplayer.SVGAVideoEntity
//import com.tencent.cloud.tuikit.engine.common.TUICommonDefine
//import com.tencent.cloud.tuikit.engine.room.TUIRoomDefine
//import com.tencent.cloud.tuikit.engine.room.TUIRoomEngine
//import com.tencent.imsdk.v2.V2TIMManager
//import com.tencent.imsdk.v2.V2TIMMessage
//import com.tencent.imsdk.v2.V2TIMSendCallback
//import com.tencent.liteav.debug.GenerateTestUserSig
//import com.tencent.liteav.trtcvoiceroom.model.TRTCVoiceRoom
//import com.tencent.liteav.trtcvoiceroom.model.TRTCVoiceRoomCallback
//import com.tencent.liteav.trtcvoiceroom.model.TRTCVoiceRoomDef
//import com.tencent.liteav.trtcvoiceroom.ui.base.MemberEntity
//import com.tencent.liteav.trtcvoiceroom.ui.room.TCConstants
//import com.tencent.liteav.trtcvoiceroom.ui.widget.msg.AudienceEntity
//import java.util.LinkedList
//
//class MUtils {
//
//    companion object {
//        //        fun loadSvg(view: SVGAImageView, path: String, callback: () -> Unit) {
////            Tools.parser.decodeFromAssets(
////                path,
////                object : SVGAParser.ParseCompletion {
////                    override fun onComplete(videoItem: SVGAVideoEntity) {
////                        val drawable = SVGADrawable(videoItem)
////                        view.setImageDrawable(drawable)
////                        view.startAnimation()
////                        callback()
////                    }
////
////                    override fun onError() {
////                    }
////                })
////        }
//        var roomsSet: RoomSet? = null
//        val mSeatUserMuteMap = HashMap<String, Boolean>()
//        val mMemberEntityMap = HashMap<String, MemberEntity>()
//        val mMemberEntityList = ArrayList<MemberEntity>()
//        var messageList = LinkedList<GetMsgBean>()
//        var worldChannelMessageList = LinkedList<GetMsgBean>()
//        var interactionMessageList = LinkedList<GetMsgBean>()
//        val mushedStateManagement = MushedStateManagement()
//
//        fun getMessageList(): List<GetMsgBean> {
//            return messageList
//        }
//
//        fun clearTalk() {
//            roomsSet = null
//            mSeatUserMuteMap.clear()
//            mMemberEntityMap.clear()
//            mMemberEntityList.clear()
//            worldChannelMessageList.clear()
//            interactionMessageList.clear()
//            messageList.clear()
//            mushedStateManagement.clear()
//        };
//        /**
//         * 自定义监听
//         */
//        fun setMUtilsListener(observer: MUtilsObserver?) {
//            mUtilsObserver = observer
//        }
//
//        public interface MUtilsObserver {
//            fun onLocalMsg(type: Int, msg: TUICommonDefine.Message)
//            fun onWorldChannelLocalMsg(type: Int, msg: TUICommonDefine.Message)
//        }
//
//        var mUtilsObserver: MUtilsObserver? = null
//
//        /**
//         * 加载动图
//         */
//        fun loadSvg(view: SVGAImageView, path: String, callback: () -> Unit) {
//            Tools.parser.decodeFromAssets(
//                path,
//                object : SVGAParser.ParseCompletion {
//                    override fun onComplete(videoItem: SVGAVideoEntity) {
//                        val drawable = SVGADrawable(videoItem)
//                        view.setImageDrawable(drawable)
//                        view.startAnimation()
//                        callback()
//                    }
//
//                    override fun onError() {
//                    }
//                })
//        }
//
//        var mTRTCVoiceRoom: TRTCVoiceRoom? = null
//
//        fun setTRTCVoiceRoom(voiceRoom: TRTCVoiceRoom) {
//            this.mTRTCVoiceRoom = voiceRoom
//        }
//
//        /**
//         * 获取房间用户
//         */
//        fun AppCompatActivity.getRoomUsers(page: Long) {
//            mTRTCVoiceRoom!!.getUserInfoList(null) { code, msg, list ->
//                if (code == 0) {
//                    android.util.Log.d(
//                        "",
//                        "getAudienceList list size:" + list.size
//                    )
//                    for (userInfo: TRTCVoiceRoomDef.UserInfo in list) {
//                        android.util.Log.d(
//                            "",
//                            "getAudienceList userInfo:$userInfo"
//                        )
//                        if (!mSeatUserMuteMap.containsKey(userInfo.userId)) {
//                            val audienceEntity = AudienceEntity()
//                            audienceEntity.userAvatar = userInfo.userAvatar
//                            audienceEntity.userId = userInfo.userId
////                            mAudienceListAdapter.addMember(audienceEntity)
//                        }
//                        if ((userInfo.userId == UserManager.get()?.user?.id.toString())) {
//                            continue
//                        }
//                        val memberEntity = MemberEntity()
//                        memberEntity.userId = userInfo.userId
//                        memberEntity.userAvatar = userInfo.userAvatar
//                        memberEntity.userName = userInfo.userName
//                        memberEntity.type = MemberEntity.TYPE_IDEL
//                        if (!mMemberEntityMap.containsKey(memberEntity.userId)) {
//                            mMemberEntityMap.put(memberEntity.userId, memberEntity)
//                            mMemberEntityList.add(memberEntity)
//                        }
//                    }
//                }
//            }
//
//        }
//
//        fun senLocalMessage(msg: String) {
//            val message = TUICommonDefine.Message()
//            message.message = msg
//            message.userId = UserManager.get()!!.user?.user_id
//            message.userName = UserManager.get()!!.user?.nickname
//            message.avatarUrl = UserManager.get()!!.user?.avatar
//            message.messageId = Tool.IM_GONGAO.toString()
//            mUtilsObserver!!.onLocalMsg(Tool.IM_GONGAO, message)
//        }
//
//        /**
//         * 加入房间
//         */
//        fun AppCompatActivity.enterRoom(room: RoomInfoBean?, callback: (result: Boolean) -> Unit) {
//            // 加入房间
//            TUIRoomEngine.login(this,
//                Tool.TxAppId, UserManager.get()!!.user?.user_id,
//                GenerateTestUserSig.genTestUserSig(UserManager.get()!!.user?.user_id),
//                object : TUIRoomDefine.ActionCallback {
//                    override fun onSuccess() {
//                        TUIRoomEngine.setSelfInfo(
//                            UserManager.get()!!.user?.nickname,
//                            Tools.FILE_PATH + UserManager.get()!!.user?.avatar, null
//                        )
//                        mTRTCVoiceRoom!!.enterRoom(room!!.roomid) { code, msg ->
//                            if (code == 0) {
//                                sendInMsg(room)
//                                callback(true)
//                            } else {
//                                callback(false)
//                                toast(msg!!)
//                            }
//                        }
//                    }
//
//                    override fun onError(
//                        error: TUICommonDefine.Error?,
//                        message: String?
//                    ) {
//                        callback(false)
//                        toast(message!!)
//                    }
//                }
//            )
//        }
//
//        private fun AppCompatActivity.sendInMsg(room: RoomInfoBean?) {
//            //发送默认消息
//            messageList.clear()
//            val message = TUICommonDefine.Message()
//            message.message = getString(R.string.room_welcome)
//            messageList.add(
//                GetMsgBean(
//                    Tool.IM_SYS,
//                    message,
//                    true
//                )
//            )
//            val message1 = TUICommonDefine.Message()
//            message1.message = room!!.screen.pk("欢迎来到直播间~")
//            messageList.add(
//                GetMsgBean(
//                    Tool.IM_SYS,
//                    message1,
//                    true
//                )
//            )
//        }
//
//        /**
//         * 听众调用上麦
//         * @param seatIndex 麦序
//         * */
//        fun AppCompatActivity.inSeat(seatIndex: Int, callback: (result: Boolean) -> Unit) {
//            mTRTCVoiceRoom!!.enterSeat(seatIndex) { code, msg ->
//                //                override fun onRejected(requestId: String?, userId: String?, message: String?) {
//                //                    toast(message.pk("上麦被拒"))
//                //                    callback(false)
//                //                }
//                //
//                //                override fun onCancelled(requestId: String?, userId: String?) {
//                //                    toast("上麦取消")
//                //                    callback(false)
//                //                }
//                //
//                //                override fun onTimeout(requestId: String?, userId: String?) {
//                //                    toast("上麦超时")
//                //                    callback(false)
//                //                }
//                //
//                //                override fun onError(
//                //                    requestId: String?,
//                //                    userId: String?,
//                //                    error: TUICommonDefine.Error?,
//                //                    message: String?
//                //                ) {
//                //                    toast(message.pk("上麦失败"))
//                //                    callback(false)
//                //                }
//                if (code == 0) {
//                    callback(true)
//                } else {
//                    toast(msg)
//                    callback(false)
//                }
//            }
//        }
//
//        /**
//         * 听众调用下麦
//         * */
//        fun AppCompatActivity.leaveSeat(callback: (result: Boolean) -> Unit) {
//            mTRTCVoiceRoom!!.leaveSeat { code, msg ->
//                if (code == 0) {
//                    callback(true)
//                } else {
//                    toast(msg.pk("下麦失败"))
//                    callback(false)
//                }
//            }
//        }
//
//        /**
//         * 房主抱人麦位
//         * @param seatIndex 麦序
//         * @param userId 用户ID
//         * */
//        fun upSeat(seatIndex: Int, userId: String, callback: (result: Boolean) -> Unit) {
//            mTRTCVoiceRoom!!.pickSeat(seatIndex, userId) { code, msg ->
//                if (code == 0) {
//                    callback(true)
//
//                } else {
//                    toast(msg.pk("抱麦失败"))
//                    callback(false)
//                }
//            }
//        }
//
//        /**
//         * 房主踢人下麦
//         * @param seatIndex 麦序
//         * */
//        fun AppCompatActivity.kickSeat(
//            seatIndex: Int,
//            userId: String,
//            callback: (result: Boolean) -> Unit
//        ) {
//            mTRTCVoiceRoom!!.kickSeat(seatIndex) { code, msg ->
//                if (code == 0) {
//                    callback(true)
//                } else {
//                    toast(msg)
//                    callback(false)
//                }
//            }
//        }
//
//
//        /**
//         * 房主锁麦
//         * @param seatIndex 麦序
//         * @param isClose 是否锁麦
//         * */
//        fun AppCompatActivity.clockSeat(
//            seatIndex: Int,
//            isClose: Boolean,
//            callback: (result: Boolean) -> Unit
//        ) {
//            val lockParams = TUIRoomDefine.SeatLockParams()
//            lockParams.lockSeat = isClose //锁定麦位
//            lockParams.lockAudio = isClose //锁定音频
//            lockParams.lockVideo = true //锁定视频
//            mTRTCVoiceRoom!!.closeSeat(seatIndex + 1,
//                isClose,
//                object : TRTCVoiceRoomCallback.ActionCallback {
//
//                    override fun onCallback(code: Int, msg: String?) {
//                        if (code == 0) {
//                            callback(true)
//                        } else {
//                            toast(msg.pk("操作失败"))
//                            callback(false)
//                        }
//                    }
//                })
//        }
//
//        /**
//         * 麦克风开启 关闭
//         * */
//        fun closeMic(isClose: Boolean) {
//            if (isClose) {
//                mTRTCVoiceRoom!!.stopMicrophone()
//            } else {
//                mTRTCVoiceRoom!!.startMicrophone()
//            }
//        }
//
//        /**
//         * 扬声器开启 关闭
//         * */
//        fun closeMute(
//            userId: String,
//            isClose: Boolean
//        ) {
//            mTRTCVoiceRoom!!.setSpeaker(isClose)
//        }
//
//        /**
//         * 麦位点击
//         * @param position 麦序
//         * @param seat 麦位信息
//         * @param ownerId 房主ID
//         * */
//        fun AppCompatActivity.seatClick(
//            isBoss: Boolean,
//            position: Int,
//            seat: TRTCVoiceRoomDef.SeatInfo,
//            ownerId: String,
//            callback: (type: Int, userId: String) -> Unit
//        ) {
//            val id = UserManager.get()!!.user?.user_id
//            if (isBoss) {
//                showRoomPeopleDialog(position, Member.getMember(seat.userId)!!, ownerId) {
//                    callback.invoke(it, seat.userId)
//                }
//            } else {
//                if (seat.userId.isNullOrEmpty()) {
//                    if (id == ownerId) {
//                        //房主抱人上麦
//                        showUpSeatDialog(position, seat.mute)
//                    } else {
////                        if (Member.getMember(id)!!.seatIndex != -1) {
////                            toast("您已在麦位上，不可重复申请")
////                            return
////                        }
//                        //用户上麦
////                        showInSeatDialog {
////                            inSeat(position) {
////                                Member.getMember(id)!!.seatIndex = position
////                            }
////                        }
//                        showShangMaiInteractiveDialog {
//                            val inviteId = mTRTCVoiceRoom!!.sendInvitation(
//                                TCConstants.CMD_REQUEST_TAKE_SEAT,
//                                ownerId,
//                                "请求上麦"
//                            ) { code, msg ->
//                                if (code == 0) {
//                                    ToastUtils.showShort(R.string.trtcvoiceroom_toast_application_has_been_sent_please_wait_for_processing)
//                                } else {
//                                    ToastUtils.showShort(
//                                        getString(
//                                            R.string.trtcvoiceroom_toast_failed_to_send_application,
//                                            msg
//                                        )
//                                    )
//                                }
//                            }
//                        }
//
//                    }
//                } else {
//                    showRoomPeopleDialog(position, Member.getMember(seat.userId)!!, ownerId) {
//                        callback.invoke(it, seat.userId)
//                    }
//                }
//            }
//        }
//
//        //发送礼物消息出去同时展示礼物动画和弹幕
//        fun AppCompatActivity.sendGift(
//            targetId: String,
//            giftInfo: Gift,
//            giftInfo2: GiftInfo,
//            giftView: GiftAnimatorLayout,
//            isShow: Int
//        ) {
////            giftView.show(giftInfo2)
//            //发送礼物
//            val gson = Gson()
//            val m = gson.toJson(
//                giftInfo
//            )
////            GiftOfSend(
////                giftInfo2.num,
////                UserManager.get()!!.user?.nickname,
////                giftInfo2.sendUserHeadIcon,
////                targetId,
////                giftInfo2.giftId,
////                giftInfo2.type,
////                giftInfo2.title,
////                giftInfo2.giftPicUrl,
////                giftInfo2.lottieUrl
////            )
//            val message = TUICommonDefine.Message()
//            message.message = m
//            message.userId = UserManager.get()!!.user?.user_id
//            message.userName = UserManager.get()!!.user?.nickname
//            message.avatarUrl = giftInfo2.sendUserHeadIcon
//            message.messageId = Tool.IM_GIFT.toString()
////            val m1 = gson.toJson(RoomMsgBean(Tool.IM_GIFT, m))
//            val m1 = gson.toJson(message)
//            LogUtils.d("发送礼物", m1)
//            if (mUtilsObserver != null) {
//                mTRTCVoiceRoom!!.sendRoomCustomMsg(Tool.CMD_IM_GIFT, m) { code, msg ->
//                    if (code == 0) {
//                        mUtilsObserver!!.onLocalMsg(Tool.IM_GIFT, message)
//                    }
//                }
//                if (isShow == 1) {
//                    giftInfo.showType = 1
//                    val roomName = SPUtils.getInstance().getString("roomName")
//                    var gift = WolrdGift(
//                        UserManager.get()!!.user?.avatar.pk(),
//                        giftInfo.thumb,
//                        giftInfo.id.toString(),
//                        giftInfo.name,
//                        giftInfo.giftnum,
//                        targetId,
//                        UserManager.get()!!.user?.nickname.pk(),
//                        giftInfo.roomId,
//                        roomName,
//                        "1",
//                        UserManager.get()!!.user?.id?.toString().pk(),
//                        giftInfo.animation_svga
//                    )
//                    mTRTCVoiceRoom!!.sendWorldChannelRoomCustomMsg(
//                        Tool.CMD_IM_GIFT,
//                        Gson().toJson(gift)
//                    ) { code, msg ->
//                        if (code == 0) {
//                            val message = TUICommonDefine.Message()
//                            message.message = Gson().toJson(gift)
//                            mUtilsObserver!!.onWorldChannelLocalMsg(Tool.IM_GIFT, message)
//                        }
//                    }
//                }
//            }
//        }
//
//        /**
//         * 离开房间
//         * */
//        fun leaveRoom(callback: (result: Boolean) -> Unit) {
//            mTRTCVoiceRoom!!.exitRoom { code, msg ->
//                if (code == 0) {
//                    Member.clear()
//                    clearTalk()
//                    callback(true)
//                } else {
//                    callback(false)
//                }
//            }
//        }
//
//        /**
//         * 语音房发送文字消息
//         * */
//        private var inputBarDialog: InputBarDialog? = null
//        fun Context.clickSend(roomId: Int) {
//            val mushedState = mushedStateManagement.getMushedState(UserManager.get()?.user?.id ?: 0)
//            if(mushedState == 1) {
//                ToastUtils.showShort("你已被禁言")
//                return
//            }
//            if (roomsSet?.room?.message_status1 == 0) {
//                ToastUtils.showShort("全员禁言中")
//                return
//            }
//            inputBarDialog = InputBarDialog(this, object : InputBar.InputBarListener {
//                override fun onClickSend(msg: String?) {
//                    if (msg!!.isEmpty()) {
//                        com.lalifa.utils.KToast.show("消息不能为空")
//                        return
//                    }
//                    val sharedInstance = TRTCVoiceRoom.sharedInstance(this@clickSend)
//                    scopeNet {
//                        val socialGetUserSet = messageSensitiveWordCheck(msg.pk())
//                        if(socialGetUserSet.code == 200 ) {
//                            sharedInstance.sendRoomTextMsg(msg) { code, msg ->
//                                if (code == 0) {
//                                    val message = TUICommonDefine.Message()
//                                    message.message = msg
//                                    message.userId = UserManager.get()!!.user?.user_id
//                                    message.userName = UserManager.get()!!.user?.nickname
//                                    message.avatarUrl = UserManager.get()!!.user?.avatar
//                                    message.messageId = Tool.IM_GIFT.toString()
//                                    mUtilsObserver!!.onLocalMsg(Tool.IM_TXT, message)
//                                } else {
//                                    LogUtils.e(msg!!)
//                                    toast(msg!!)
//                                }
//                            }
//                        } else {
//                            ToastUtils.showShort("发送的内容包含敏感内容")
//                        }
//                    }
//                }
//
//                override fun onClickBarrageSend(choseData: ColorDTO?, message: String?) {
//                    val senBarrage = SenBarrage(message.pk(), choseData?.color.pk(), UserManager.get()?.user?.id?.toString().pk(), Tool.IM_BARRAGE.toString(), UserManager.get()?.user?.avatar.pk(), UserManager.get()?.user?.nickname.pk())
//                    val message = TUICommonDefine.Message()
//                    message.message = Gson().toJson(senBarrage)
//                    message.userId = UserManager.get()?.user?.user_id
//                    message.userName = UserManager.get()?.user?.nickname
//                    message.avatarUrl = UserManager.get()?.user?.avatar
//                    message.messageId = Tool.IM_GIFT.toString()
////            val m1 = gson.toJson(RoomMsgBean(Tool.IM_GIFT, m))
//                    val m1 = Gson().toJson(message)
//                    LogUtils.d("发送礼物", m1)
//                    if (mUtilsObserver != null) {
//                        mTRTCVoiceRoom!!.sendRoomCustomMsg(Tool.CMD_BARRAGE, Gson().toJson(senBarrage)) { code, msg ->
//                            if (code == 0) {
//                                mUtilsObserver!!.onLocalMsg(Tool.IM_BARRAGE, message)
//                            }
//                        }
//                    }
//                }
//
//                override fun onClickSendExpression(message: String?) {
//                    val sharedInstance = TRTCVoiceRoom.sharedInstance(this@clickSend)
//                    val messageBean = TUICommonDefine.Message()
//                    val get = UserManager.get()
//                    val giftInfo = Expression(
//                        message.pk(),
//                        get?.user?.id?.toString().pk(),
//                        get?.user?.nickname.pk(),
//                        get?.user?.nickname.pk()
//                    )
//                    messageBean.message = Gson().toJson(giftInfo)
//                    mUtilsObserver!!.onLocalMsg(Tool.EXPRESSION, messageBean)
//                    sharedInstance.sendRoomCustomMsg(
//                        Tool.CMD_EXPRESSION,
//                        Gson().toJson(giftInfo)
//                    ) { code, msg -> }
//                }
//
//                override fun onWorldChannelSend() {
//                    inputBarDialog?.dismiss()
//                    (this@clickSend as AppCompatActivity).showPublishBroadcastInputDialog(l = 30) {
//
//                        val sharedInstance = TRTCVoiceRoom.sharedInstance(this@clickSend)
//                        val giftInfo = WorldChannelMessage(
//                            room_id = roomId.toString(),
//                            it.pk(),
//                            IM_WORLD.toString()
//                        )
//
//                        sharedInstance.sendWorldChannelRoomTextMsg(Gson().toJson(giftInfo)) { code, msg ->
//                            if (code == 0) {
//                                val message = TUICommonDefine.Message()
//                                message.message = msg
//                                message.userId = UserManager.get()!!.user?.user_id
//                                message.userName = UserManager.get()!!.user?.nickname
//                                message.avatarUrl = UserManager.get()!!.user?.avatar
//                                message.messageId = Tool.IM_GIFT.toString()
//                                mUtilsObserver!!.onWorldChannelLocalMsg(Tool.IM_TXT, message)
//                            } else {
//                                LogUtils.e(msg!!)
//                                toast(msg!!)
//                            }
//                        }
//                    }
//                }
//
//                override fun onClickEmoji(): Boolean {
//                    return false
//                }
//            })
//            inputBarDialog!!.show()
//        }
//
//        /**
//         * 展示悬浮窗
//         * */
//        fun AppCompatActivity.showMini(
//            callback: (canShow: Boolean) -> Unit
//        ) {
//            if (XXPermissions.isGranted(
//                    this,
//                    //所需危险权限可以在此处添加：
//                    Permission.SYSTEM_ALERT_WINDOW
//                )
//            ) {
//                callback(true)
//            } else {
//                callback(false)
//            }
//        }
//
//        fun sendMessage(
//            userId: String,
//            v2TIMMessage: V2TIMMessage,
//            callback: (result: Boolean) -> Unit
//        ) {
//            // 发送消息
//            V2TIMManager.getMessageManager().sendMessage(
//                v2TIMMessage,
//                userId,
//                null,
//                V2TIMMessage.V2TIM_PRIORITY_NORMAL,
//                false,
//                null,
//                object : V2TIMSendCallback<V2TIMMessage?> {
//                    override fun onProgress(progress: Int) {
//                        //文本消息不会回调进度
//                        LogUtils.i("=====>消息发送进度$progress")
//                    }
//
//
//                    override fun onError(code: Int, desc: String) {
//                        //消息发送失败
//                        callback(false)
//                        LogUtils.i("=====>$code 消息发送失败")
//                    }
//
//                    override fun onSuccess(p0: V2TIMMessage?) {
//                        //消息发送成功
//                        callback(true)
//                        LogUtils.i("=====>消息发送成功")
//                    }
//                })
//
//        }
//    }
//
//}