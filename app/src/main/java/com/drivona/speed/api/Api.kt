package com.drivona.speed.api

import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.component.Progress
import com.drake.net.interfaces.ProgressListener
import com.lalifa.api.BaseBean
import com.lalifa.api.UserBean
import com.lalifa.ext.Tools
import com.lalifa.extension.toJson
import kotlinx.coroutines.CoroutineScope
import java.io.File

/**
 * 文件上传
 * @param path String
 */
suspend fun CoroutineScope.uploadApi(file: File): String? {
    return Post<BaseBean<String>>("index/upload") {
        param("file", file)
    }.await().data!!
}


suspend fun CoroutineScope.getAddressDefaultList(): BaseBean<DefaultLocationData>? {
    return Post<BaseBean<DefaultLocationData>>("index/address_default_list").await()
}
suspend fun CoroutineScope.getPublicKey(): BaseBean<String> {
    return Post<BaseBean<String>>("index/publicKey").await()
}

suspend fun CoroutineScope.getAddressList(type: Int): BaseBean<List<LocationData>>? {
    return Post<BaseBean<List<LocationData>>>("index/address_list") {
        param("type", type)
    }.await()
}

suspend fun CoroutineScope.delAddressList(id: Int): BaseBean<Any>? {
    return Post<BaseBean<Any>>("index/address_del") {
        param("id", id)
    }.await()
}
suspend fun CoroutineScope.reportNewCamera(latitude: String,longitude:String,event_type:String): BaseBean<Any>? {
    return Post<BaseBean<Any>>("api/reportNewCamera") {
        param("latitude", latitude)
        param("longitude", longitude)
        param("event_type", event_type)
    }.await()
}
suspend fun CoroutineScope.updateCameraReport(latitude: String,longitude:String,event_id:String,update_type:String): BaseBean<Any>? {
    return Post<BaseBean<Any>>("api/updateCameraReport") {
        param("latitude", latitude)
        param("longitude", longitude)
        param("event_id", event_id)
        param("update_type", update_type)
    }.await()
}

suspend fun CoroutineScope.addEquipment(title: String, uuid: String): BaseBean<Any>? {
    return Post<BaseBean<Any>>("index/equipment_add") {
        param("title", title)
        param("uuid", uuid)
    }.await()
}

suspend fun CoroutineScope.checkEquipment(uuid: String): BaseBean<Any>? {
    return Post<BaseBean<Any>>("index/equipment_check") {
        param("uuid", uuid)
    }.await()
}

suspend fun CoroutineScope.delEquipment(id: Int): BaseBean<Any>? {
    return Post<BaseBean<Any>>("index/equipment_del") {
        param("id", id)
    }.await()
}

suspend fun CoroutineScope.setEquipmentInfo(
    id: Int,
    title: String = "",
    lighting: Int = -1,
    brightness: Int = -1,
    sound: Int = -1,
    mobile_speed: Int = -1,
    fixed_speed: Int = -1,
    traffic_light_camera: Int = -1,
    mobile_speed_phone: Int = -1,
    fixed_speed_phone: Int = -1,
    traffic_light_camera_phone: Int = -1,
    alarm_which: Int = -1,
    speed_alarm_device: Int = -1,
    speed_alarm_phone: Int = -1,
    road_alarm_device: Int = -1,
    road_alarm_phone: Int = -1,
    speed_unit: Int = -1,
    is_night: Int = -1,
    alarm_threshold: Int = -1,
    type: Int = -1,
): BaseBean<Any>? {
    return Post<BaseBean<Any>>("index/equipment_init") {
        param("id", id)
        if (title.isNotEmpty()) {
            param("title", title)
        }
        if (speed_unit != -1) {
            param("speed_unit", speed_unit)
        }
        if (type != -1) {
            param("type", type)
        }
        if (lighting != -1) {
            param("lighting", lighting)
        }
        if (brightness != -1) {
            param("brightness", brightness)
        }
        if (sound != -1) {
            param("sound", sound)
        }

        if (mobile_speed != -1) {
            param("mobile_speed", mobile_speed)
        }
        if (fixed_speed != -1) {
            param("fixed_speed", fixed_speed)
        }
        if (traffic_light_camera != -1) {
            param("traffic_light_camera", traffic_light_camera)
        }
        if (mobile_speed_phone != -1) {
            param("mobile_speed_phone", mobile_speed_phone)
        }
        if (fixed_speed_phone != -1) {
            param("fixed_speed_phone", fixed_speed_phone)
        }
        if (traffic_light_camera_phone != -1) {
            param("traffic_light_camera_phone", traffic_light_camera_phone)
        }
        if (alarm_which != -1) {
            param("alarm_which", alarm_which)
        }
        if (speed_alarm_device != -1) {
            param("speed_alarm_device", speed_alarm_device)
        }
        if (speed_alarm_phone != -1) {
            param("speed_alarm_phone", speed_alarm_phone)
        }
        if (road_alarm_device != -1) {
            param("road_alarm_device", road_alarm_device)
        }
        if (road_alarm_phone != -1) {
            param("road_alarm_phone", road_alarm_phone)
        }
        if (is_night != -1) {
            param("is_night", is_night)
        }
        if (alarm_threshold != -1) {
            param("alarm_threshold", alarm_threshold)
        }
    }.await()
}

suspend fun CoroutineScope.userEdit(

    avatar: String = "",
    nickname: String = "",
    distance: String = "",
    speed: String = "",

    ): BaseBean<Any>? {
    return Post<BaseBean<Any>>("index/useredit") {

        if (avatar.isNotEmpty()) {
            param("avatar", avatar)
        }
        if (nickname.isNotEmpty()) {
            param("nickname", nickname)
        }
        if (distance.isNotEmpty()) {
            param("distance", distance)
        }
        if (speed.isNotEmpty()) {
            param("speed", speed)
        }

    }.await()
}

suspend fun CoroutineScope.getEquipmentList(): BaseBean<List<DeviceData>>? {
    return Post<BaseBean<List<DeviceData>>>("index/equipment_list") {

    }.await()
}

/**
 * 添加地址
 *
 */
suspend fun CoroutineScope.addressAdd(
    lat: Double,
    long: Double,
    address: String,
    type: Int,
    title: String,
    place_id: String,
): BaseBean<Any>? {
    return Post<BaseBean<Any>>("index/address_add") {
        param("lat", lat)
        param("long", long)
        param("address", address)
        param("type", type)
        param("title", title)
        param("place_id", place_id)
    }.await()
}

/**
 * 协议内容
 * @param type 1用户协议2隐私政策3平台规则4信息规范5认证绑定6订单问题7充值提现8成为大神9充值协议10：8元活动，11：买二送一活动，12=“我要上推荐”活动
 */
suspend fun CoroutineScope.userRule(type: Int = 1): UserRuleBean? {
    return Post<BaseBean<UserRuleBean>>("user_rule") {
        param("type", type)
    }.await().data
}

/**
 * 注册
 */
suspend fun CoroutineScope.sign(email: String, code: String, password: String): Any? {
    return Post<BaseBean<UserRuleBean>>("index/sign") {
        param("email", email)
        param("code", code)
        param("password", password)
    }.await().data
}

/**
 * 注册
 * sign注册 forget忘记密码
 */
suspend fun CoroutineScope.sendMail(email: String, type: String = "sign"): Any? {
    return Post<BaseBean<Any>>("index/sendMail") {
        param("email", email)
        param("type", type)
    }.await().data
}

/**
 * 登录
 */
suspend fun CoroutineScope.loginByEmail(email: String, password: String): BaseBean<String> {
    return Post<BaseBean<String>>("index/login") {
        param("email", email)
        param("password", password)
    }.await()
}
suspend fun CoroutineScope.checkMail(email: String, code: String,type:String="1"): BaseBean<Int> {
    return Post<BaseBean<Int>>("index/checkMail") {
        param("email", email)
        param("code", code)
        param("type", type)
    }.await()
}
suspend fun CoroutineScope.changeMail(email: String, code: String): BaseBean<Int> {
    return Post<BaseBean<Int>>("index/change_email") {
        param("email", email)
        param("code", code)

    }.await()
}
suspend fun CoroutineScope.loginOff(): BaseBean<Int> {
    return Post<BaseBean<Int>>("index/login_off") {

    }.await()
}

/**
 * 登录
 */
suspend fun CoroutineScope.getUserinfo(): BaseBean<UserData> {
    return Post<BaseBean<UserData>>("index/userinfo") {

    }.await()
}

/**
 * 登录
 */
suspend fun CoroutineScope.forget(email: String, code: String, password: String): BaseBean<String> {
    return Post<BaseBean<String>>("index/forget") {
        param("email", email)
        param("password", password)
        param("code", code)
    }.await()
}
suspend fun CoroutineScope.feedback(name:String,mobile: String, content: String,): BaseBean<Any> {
    return Post<BaseBean<Any>>("index/feedback") {
        param("name", name)
        param("mobile", mobile)
        param("content", content)
    }.await()
}
suspend fun CoroutineScope.changePwd(password:String="",new_password:String="",step:String="1"): BaseBean<Any> {
    return Post<BaseBean<Any>>("index/change_pwd") {
        param("password", password)

        param("step", step)
        if (step=="2"){
            param("new_password", new_password)
        }

    }.await()
}

/**
 * 获取摄像头
 */
suspend fun CoroutineScope.getCamera(
    long: String,
    lat: String,
    direction: String,
    distance: String,
    street: String,
    route: String,
): BaseBean<List<CameraDeviceData>> {
    return Post<BaseBean<List<CameraDeviceData>>>("index/devices") {
        param("long", long)
        param("lat", lat)
        param("direction", direction)
        param("distance", distance)
        param("street", street)
        param("route", route)
    }.await()
}

/**
 * 获取事件
 */
suspend fun CoroutineScope.getMapEvent(
    long: String,
    lat: String,
    direction: String,
    distance: String,
    street: String,
    route: String,
): BaseBean<List<CameraDeviceData>> {
    return Post<BaseBean<List<CameraDeviceData>>>("index/events") {
        param("long", long)
        param("lat", lat)
        param("direction", direction)
        param("distance", distance)
        param("street", street)
        param("route", route)
    }.await()
}

/**
 * 账号密码登录
 */
suspend fun CoroutineScope.loginMake(phone: String, password: String): BaseBean<UserBean> {
    return Post<BaseBean<UserBean>>("login_make") {
        param("phone", phone)
        param("password", password)
    }.await()
}

/**
 * 三方登陆
 */
suspend fun CoroutineScope.checkAuthorizations(unionid: String, type: String): ThreePartyLogin? {
    return Post<BaseBean<ThreePartyLogin>>("check_authorizations") {
        param("unionid", unionid)
        param("type", type)
    }.await().data
}

/**
 * 图形验证码
 */
suspend fun CoroutineScope.sendImgCode(): ImageCodeBean? {
    return Post<BaseBean<ImageCodeBean>>("send_img_code") {}.await().data
}

/**
 * 获取手机验证码
 * @param type 1登录验证2绑定手机号3更换手机号验证身份4修改手机号5忘记密码
 */
suspend fun CoroutineScope.sendCode(phone: String, type: Int): BaseBean<Object>? {
    return Post<BaseBean<Object>>("send_code") {
        param("phone", phone)
        param("type", type)
    }.await()
}

/**
 * 忘记密码
 */
suspend fun CoroutineScope.forgetMake(
    phone: String,
    img_code: String,
    client_id: String,
    sms_code: String,
    password: String,
    password_confirmation: String,
): BaseBean<Object>? {

    return Post<BaseBean<Object>>("forget_make") {
        param("phone", phone)
        param("img_code", img_code)
        param("client_id", client_id)
        param("sms_code", sms_code)
        param("password", password)
        param("password_confirmation", password_confirmation)
    }.await()
}

/**
 * 三方登陆绑定手机号
 */
suspend fun CoroutineScope.oauthUserBind(
    phone: String,
    img_code: String,
    client_id: String,
    unionid: String,
    sms_code: String,
    type: String,
    nickname: String,
): BaseBean<UserBean> {
    return Post<BaseBean<UserBean>>("oauth_user_bind") {
        param("phone", phone)
        param("img_code", img_code)
        param("client_id", client_id)
        param("unionid", unionid)
        param("sms_code", sms_code)
        param("type", type)
        param("nickname", nickname)
    }.await()
}

/**
 * 获取路线
 */
suspend fun CoroutineScope.getRoute(
    origin: String,
    destination: String,
): BaseBean<Any> {
    return Get<BaseBean<Any>>("https://maps.googleapis.com/maps/api/directions/json") {
        param("origin", origin)
        param("destination", destination)
        param("mode", "driving")
        param("key", Tools.GoogleKey)

    }.await()
}

/**
 * 获取路线
 */
suspend fun CoroutineScope.getPlaceById(
    place_id: String,
): BaseBean<List<PlaceData>> {
    return Get<BaseBean<List<PlaceData>>>("https://maps.googleapis.com/maps/api/geocode/json") {
        param("place_id", place_id)
        param("key", Tools.GoogleKey)

    }.await()
}


/**
 * 下载文件
 */
suspend fun CoroutineScope.downloadFile(
    context: Context,
    name: String,
    url: String,
    callBack: (File) -> Unit,
) {
    if (url.isNullOrEmpty()) {
        return
    }
    var file: File? = null
    file = File("${context.filesDir.absolutePath}/${name}")
    if (file.exists()) {
        callBack(file)
        return
    }
    file =
        Get<File>(url) {
            setDownloadFileName(name)
            setDownloadDir(context.filesDir.absolutePath)
            setDownloadMd5Verify()
            addDownloadListener(object : ProgressListener() {
                override fun onProgress(p: Progress) {
                    LogUtils.e(
                        "下载进度: ${p.progress()}% 下载速度: ${p.speedSize()}     " +
                                "\n\n文件大小: ${p.totalSize()}  已下载: ${p.currentSize()}  剩余大小: ${p.remainSize()}" +
                                "\n\n已使用时间: ${p.useTime()}  剩余时间: ${p.remainTime()}"
                    )
                }
            })
        }.await()

    file?.let {
        callBack.invoke(it)
    }
}

/**
 *查询路线
 * @receiver CoroutineScope
 * @return String?
 */
suspend fun CoroutineScope.computeRoutes(
    entity: RoutesEntity,
): RoutesResult {
    return Post<RoutesResult>("https://routes.googleapis.com/directions/v2:computeRoutes") {
        addHeader("X-Goog-Api-Key", "AIzaSyCM7U1dQw4hMHK0iRJr2caNATHdMKboFrU")
        addHeader("X-Goog-FieldMask", "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline,routes.routeToken")
        body = CustomizerJSONBody(entity.toJson())
    }.await()
}