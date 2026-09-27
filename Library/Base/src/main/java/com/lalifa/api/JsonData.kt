package com.lalifa.api

import android.text.SpannableStringBuilder
import com.drake.brv.item.ItemHover
import java.io.Serializable


open class BaseBean<T> : Serializable {
    var code: Int = 0
    var message: String = ""
    var data: T? = null
    var time: Long = 0
}
data class GiftBean(
    val image: String,
    val name: String,
    val price: String
)

data class CityBean(
    val city: List<City>,
    val name: String
)

data class City(
    val area: List<String>,
    val name: String
)


/**
 * @Des 上传文件
 */
data class FileBean(
    val fullurl: String,
    val filepath: String
)

data class Price(
    val id: Int,
    val price: String
)

data class ChoseTab(
    val title: String,
    var chose: Boolean,
    val thumb: String = "",
    var linkagePosition: Int = 0,
    val id: String = "0"
)

data class HeaderTitleBean(
    val name: String,
    val chose: Boolean
) : ItemHover {
    override var itemHover: Boolean = true
}

data class HomeMoreClassBean(
    val id: Int,
    val name: String,
    val thumb: String
)

data class UserBean(
    val token: String,

    val user: Userinfo
)

data class Userinfo(
    val avatar: String,
    val createtime: Int,
    val expires_in: Int,
    val expiretime: Int,
    val id: Int,
    val mobile: String,
    val nickname: String,
    val score: Int,
    val token: String,
    val user_id: Int,
    val username: String,
    val birthday: String
)

data class DynamicList(
    val at_users: List<AtUser>,
    val city: String,
    val content: String,
    val created_at: String,
    val updated_at: String,
    val id: Int,
    //1是0否
    var is_like: Int,
    val medias: List<ReleaseDynamic>,
    var num_comment: Int,
    var num_like: Int,
    var is_recommend: Int,
    val topics: Any?,
    val user: DynamicUser,
    val user_id: Int,
    val user_live: UserLive?,
    var spannableStringBuilder: SpannableStringBuilder?,
    var isExpand: Boolean,
    val comment_list: ArrayList<DynamicComment>
)

data class DynamicUser(
    val age: Int,
    val avatar: String,
    val animation_svga: String?,
    val birthday: String,
    val user_id: Int,
    //是否关注1是0否
    var is_followed: Int,
    val nickname: String,
    val wealth_thumb: String?,
    val anchor_thumb: String?,
    val noble_thumb: String?,
    val sex: Int
)

data class AtUser(
    val text: String,
    val user_id: String
)

data class ReleaseDynamic(
    var type: String,
    var src: String,
    var cover: String,
    var duration: String,
)

data class UserLive(
    val id: Int,
    val name: String,
    val thumb: String
)

data class DynamicComment(
    val content: String,
    val topics: Any,
    val user: DynamicCommentUser,
    val user_id: Int,
    val at_users: List<AtUser>?,
    var num_like: Int,
    val comment_id: Int,
    var is_like: Int,
    val created_at: String
)

data class DynamicCommentUser(
    val age: Int,
    val avatar: String,
    val animation_svga: String?,
    val id: Int,
    val is_followed: Int,
    val nickname: String,
    val sex: Int
)