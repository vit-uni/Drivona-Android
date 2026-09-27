package com.drivona.speed.utils

//import com.blankj.utilcode.util.JsonUtils
//import com.blankj.utilcode.util.LogUtils
//import com.feijing.yypeiwan.api.ShareUsernameSlice
//import com.feijing.yypeiwan.api.UserSkillPay
//import com.google.gson.Gson
//import com.tencent.imsdk.v2.V2TIMCustomElem
//import com.tencent.imsdk.v2.V2TIMFriendOperationResult
//import com.tencent.imsdk.v2.V2TIMManager
//import com.tencent.imsdk.v2.V2TIMMessage
//import com.tencent.imsdk.v2.V2TIMValueCallback
//
//
//class IMUtils {
//    companion object {
//        fun sendTextMessage(msg: String, receiver_userID: String, callback: (msg: V2TIMMessage?) -> Unit): String? {
//            return V2TIMManager.getInstance().sendC2CTextMessage(
//                msg,
//                receiver_userID,
//                object : V2TIMValueCallback<V2TIMMessage?> {
//                    override fun onSuccess(message: V2TIMMessage?) {
//                        // 发送单聊文本消息成功
//                        callback.invoke(message)
//                    }
//
//                    override fun onError(code: Int, desc: String) {
//                        // 发送单聊文本消息失败
//                    }
//                })
//
//        }
//        fun sendCustomMessage(msg: String, receiver_userID: Int, callback: (msg: String) -> Unit): String {
//            return V2TIMManager.getInstance()
//                .sendC2CCustomMessage(msg.toByteArray(), receiver_userID.toString(),
//                    object : V2TIMValueCallback<V2TIMMessage?> {
//                        override fun onSuccess(message: V2TIMMessage?) {
//                            message?.customElem?.description
//                            // 发送单聊自定义消息成功
//                            V2TIMCustomElem().description
//
//                            callback.invoke(msg)
//                        }
//
//                        override fun onError(code: Int, desc: String) {
//                            // 发送单聊自定义消息失败
//                            LogUtils.e(code.toString(), desc)
//                        }
//                    })
//        }
//
//        fun sendOrderNessage(v2TIMMessage: V2TIMMessage, status: String, callback: (msg: String) -> Unit) {
//            v2TIMMessage.customElem.description = status
//            V2TIMManager.getMessageManager().modifyMessage(v2TIMMessage) { code, desc, message ->
//                // 修改消息完成，message 为修改之后的消息对象
//                callback.invoke(desc)
//            }
//        }
//
//        fun sendModifyMessage(id: Int, status: String, callback: (msg: String) -> Unit) {
//            V2TIMManager.getMessageManager().getC2CHistoryMessageList("", 100, null, object : V2TIMValueCallback<List<V2TIMMessage>> {
//                    override fun onSuccess(p0: List<V2TIMMessage>?) {
//                        p0?.forEachIndexed { index, v2TIMMessage ->
//                            if (v2TIMMessage.elemType == 2) {
//                                val data = String(v2TIMMessage.customElem.data)
//                                val businessID = JsonUtils.getString(data, "businessID")
//                                val jsData = if (businessID == "userSkillPay") Gson().fromJson(data, UserSkillPay::class.java) else Gson().fromJson(data, ShareUsernameSlice::class.java)
//                                businessID?.let { jsData.type = if (it == "shareLive") 4 else if (it == "userSkillPay") 5 else 3 }
//                                if (jsData.type == 5) {
//                                    if(id.toString() == (jsData as UserSkillPay).orderId) {
//                                        sendOrderNessage(v2TIMMessage, status) {
//                                            callback.invoke(it)
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onError(p0: Int, p1: String?) {
//                    }
//                })
//        }
//
//        fun toBlackList(userIDList: List<String>) {
//            V2TIMManager.getFriendshipManager().addToBlackList(
//                userIDList,
//                object : V2TIMValueCallback<List<V2TIMFriendOperationResult?>?> {
//                    override fun onSuccess(v2TIMFriendOperationResults: List<V2TIMFriendOperationResult?>?) {
//                        // 拉黑成功
//                    }
//
//                    override fun onError(code: Int, desc: String) {
//                        // 拉黑失败
//                    }
//                })
//
//        }
//
//        fun deleteBlackList(userIDList: List<String>) {
//            V2TIMManager.getFriendshipManager().deleteFromBlackList(
//                userIDList,
//                object : V2TIMValueCallback<List<V2TIMFriendOperationResult?>?> {
//                    override fun onSuccess(v2TIMFriendOperationResults: List<V2TIMFriendOperationResult?>?) {
//                        // 解除拉黑成功
//                    }
//
//                    override fun onError(code: Int, desc: String) {
//                        // 解除拉黑失败
//                    }
//                })
//        }
//    }
//}