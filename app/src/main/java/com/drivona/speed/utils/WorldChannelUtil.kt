package com.drivona.speed.utils

//import com.example.analogtelephone.utils.Tool.Companion.WORLD_CHANNEL
//import com.tencent.imsdk.v2.V2TIMCallback
//import com.tencent.imsdk.v2.V2TIMManager
//import com.tencent.imsdk.v2.V2TIMValueCallback
//import com.tencent.liteav.trtcvoiceroom.model.impl.base.TRTCLogger
//
//class WorldChannelUtil {
//    companion object {
//        fun joinWorldChannel() {
//            V2TIMManager.getInstance().createGroup(V2TIMManager.GROUP_TYPE_AVCHATROOM, WORLD_CHANNEL, "世界喇叭", object :
//                V2TIMValueCallback<String> {
//                override fun onSuccess(p0: String?) {
//                    V2TIMManager.getInstance().joinGroup(Tool.WORLD_CHANNEL, "", object :
//                        V2TIMCallback {
//                        override fun onError(code: Int, msg: String) {
//                            TRTCLogger.e(Tool.WORLD_CHANNEL, "group has been created.join group failed, code:$code msg:$msg")
//                        }
//
//                        override fun onSuccess() {
//                            TRTCLogger.i(Tool.WORLD_CHANNEL, "group has been created.join group success.")
//                        }
//                    })
//                }
//
//                override fun onError(code: Int, p1: String?) {
//                    if (code == 10025 || code == 10021) {
//                        // 10025 indicates that the group owner is the local user, and the room is created successfully
//                        V2TIMManager.getInstance().joinGroup(Tool.WORLD_CHANNEL, "", object :
//                            V2TIMCallback {
//                            override fun onError(code: Int, msg: String) {
//                                TRTCLogger.e(Tool.WORLD_CHANNEL, "group has been created.join group failed, code:$code msg:$msg")
//                            }
//
//                            override fun onSuccess() {
//                                TRTCLogger.i(Tool.WORLD_CHANNEL, "group has been created.join group success.")
//                            }
//                        })
//                    }
//                }
//
//            })
//        }
//    }
//}