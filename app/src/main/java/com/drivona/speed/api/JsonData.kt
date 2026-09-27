package com.drivona.speed.api

import android.annotation.SuppressLint
import android.text.TextUtils
import com.drake.brv.item.ItemHover
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.time.LocalDate
import java.time.Period

data class AppSetting(
    val permissions: Permissions,
)


data class Permissions(
    val created_at: String,
    val id: Int,
    //消息提醒
    val set1: Int,
    //动态房间背景
    val set10: Int,
    //礼物横幅
    val set11: Int,
    //房间弹幕
    val set12: Int,
    //全屏广播
    val set13: Int,
    //切换房间弹窗确认
    val set14: Int,
    //消息免打扰
    val set2: Int,
    //个性化推荐
    val set3: Int,
    //进房间隐身
    val set4: Int,
    //全站隐身
    val set5: Int,
    //魅力隐身
    val set6: Int,
    //财富隐身
    val set7: Int,
    //高级礼物特效
    val set8: Int,
    //进场特效
    val set9: Int,
    val updated_at: String,
    val user_id: Int,
)

data class ColorBean(
    val list: List<ColorDTO>,
)

data class ColorDTO(
    val color: String,
    val color_note: String,
    val id: Int,
    val note: String,
    val price: String,
    val price_barrage: String,
    val status: Int,
    var choose: Boolean = false,
)

data class RankingBean(
    var itemType: Int,
    val id: Int,
    val noble_thumb: String,
    val total: String,
    var user: RankingUser,
    val info: RankingUser,
    val user_id: Int,
    val wealth_thumb: String,
    val gift_top: GiftTopBean,
    val top: Any,
    var topDto: TopBean,
    val name: String,
)


data class TopBean(
    val id: Int,
    val noble_thumb: Any,
    val user: GiftListUser,
    val user_id: Int,
    val wealth_thumb: Any,
)

data class GiftListUser(
    val avatar: String,
    val id: Int,
    val nickname: String,
)

data class GiftTopBean(
    val gift_id: Int,
    val num: Int,
    val user_id: Int,
)

data class RankingUser(
    val avatar: String,
    val id: Int,
    val nickname: String,
)

data class Gift(
    val thumb: String,
    val id: Int,
    val image: String,
    val name: String,
    val price: String,
    val type_list: Int = 0,
    var choose: Boolean = false,
    var giftnum: String,
    var sendName: String,
    var sendHeader: String,
    var sendUserId: Int,//发送者id
    var roomId: String,
    var is_show: Int,
    var animation_svga: String,
    var showType: Int = 0,
    var num: Int = 0,
)

data class RoomUser(
    val avatar: String,
    val birthday: String,
    val id: Int,
    val nickname: String,
    val sex: Int,
    val user_id: Int,
) {
    @SuppressLint("NewApi")
    fun getAge(): String {
        if (!TextUtils.isEmpty(birthday)) {
            val split = birthday?.split("-")
            if (split != null && split.size >= 3) {
                val birthDate = LocalDate.of(split[0].toInt(), split[1].toInt(), split[2].toInt())
                val age = calculateAge(birthDate)
                return age.toString()
            }
        }
        return ""
    }

    @SuppressLint("NewApi")
    fun calculateAge(birthDate: LocalDate): Int {
        val currentDate = LocalDate.now()
        return Period.between(birthDate, currentDate).years
    }
}

data class WolrdGift(
    val avatar: String,
    val giftAvatar: String,
    val giftID: String,
    val giftName: String,
    val giftNum: String,
    val liveUserName: String,
    val nickname: String,
    val roomId: String,
    val roomName: String,
    val showType: String,
    val userId: String,
    var animation_svga: String,

    )

data class UserRuleBean(
    val text: Text,
)

data class Text(
    val content: String,
    val created_at: String,
    val id: Int,
    val thumb: String,
    val title: String,
    val type: Int,
    val updated_at: String,
)

data class ThreePartyLogin(
    val token: String,
    val user: User,
    val unionid: String,
)

data class User(
    var authentication: String,
    var avatar: String,
    val id: Int,
    val fans: Int,
    var id_number: String,
    var nickname: String,
    var phone: String,
    var serial_number: String,
    var truename: String,
    var birthday: String,
    var city: String,
    var like_content: ArrayList<String>,
    var like_voice: ArrayList<String>,
    var photo_wall: ArrayList<String>,
    var province: String,
    var qq_num: String,
    var sex: String,
    var sign_name: String,
    var specific_tags: ArrayList<String>,
    var photo_wall_check: ArrayList<String>,
    val user_id: String,
    val video: String,
    var video_check: String,
    val concern: Int,
    val black: Int,
    val age: Int,
    val constellation: String,
    val level_wealth: String,
    val level_wealth_thumb: String,
    val anchor: String,
    val anchor_thumb: String,
    val level_noble: String,
    val level_noble_thumb: String,
    val avatar_check: String,
    val avatar_check_status: Int,
    val check_status: Int,
    var voice_check: String,
    var voice: String,
    var god_status: Int,
    var recommend: Int,
    var reg_type: Int,
    var reg_nickname: String,
    var created_at: String,
    var ip_name: String,
    var ip_text: String,
) : Serializable {

    fun getConcernString(): String {
        return if (concern == 1) "已关注" else "关注"
    }

    fun getSexString(): String {
        return if (sex == "1") "男" else "女"
    }

    @SuppressLint("NewApi")
    fun getAge(): String {
        if (!TextUtils.isEmpty(birthday)) {
            val split = birthday?.split("-")
            if (split != null && split.size >= 3) {
                val birthDate = LocalDate.of(split[0].toInt(), split[1].toInt(), split[2].toInt())
                val age = calculateAge(birthDate)
                return age.toString()
            }
        }
        return ""
    }

    @SuppressLint("NewApi")
    fun calculateAge(birthDate: LocalDate): Int {
        val currentDate = LocalDate.now()
        return Period.between(birthDate, currentDate).years
    }
}

data class ImageCodeBean(
    val captcha: String,
    val client_id: String,
    val code: String,
)

data class UiModeBean(
    val mode: Int,
)

data class PlaceData(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val navigation_points: List<NavigationPoint>,
    val place_id: String,
    val types: List<String>,
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>,
)

data class Geometry(
    val location: Location,
    val location_type: String,
    val viewport: Viewport,
)

data class NavigationPoint(
    val location: LocationX,
)

data class Location(
    val lat: Double,
    val lng: Double,
)

data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest,
)


data class LocationX(
    val latitude: Double,
    val longitude: Double,
)


data class RoutesData(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String,
)

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>,
)

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>,
    val overview_polyline: OverviewPolyline,
    val summary: String,
    val warnings: List<Any?>,
    val waypoint_order: List<Any?>,
)

data class Bounds(
    val northeast: Northeast,
    val southwest: Southwest,
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val end_location: EndLocation,
    val start_address: String,
    val start_location: StartLocation,
    val steps: List<Step>,
    val traffic_speed_entry: List<Any?>,
    val via_waypoint: List<Any?>,
)

data class OverviewPolyline(
    val points: String,
)

data class Northeast(
    val lat: Double,
    val lng: Double,
)

data class Southwest(
    val lat: Double,
    val lng: Double,
)

data class Distance(
    val text: String,
    val value: Int,
)

data class Duration(
    val text: String,
    val value: Int,
)

data class EndLocation(
    val lat: Double,
    val lng: Double,
)

data class StartLocation(
    val lat: Double,
    val lng: Double,
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: EndLocation,
    val html_instructions: String,
    val maneuver: String,
    val polyline: Polyline,
    val start_location: StartLocationX,
    val travel_mode: String,
)

data class Polyline(
    val points: String,
)

data class StartLocationX(
    val lat: Double,
    val lng: Double,

    )

data class DeviceInfo(
    val address: String,
    val name: String,
    var dian: String = "",
    var connect: Boolean = false,

    ) : Serializable

data class DeviceDianInfo(
    val dian: Int,
)

data class CMDMsg(
    val cmd: String,
)

data class DeviceIDMsg(
    val uuid: String,
)
data class DeviceTypeMsg(
    val bleAddress: String,
    val deviceType: String,
)
data class DisConnectIDMsg(
    val uuid: String,
)
data class ConnectIDMsg(
    val bleAddress: String,
)
data class ConnectWithIDMsg(
    val uuid: String,
)

data class UserData(
    val avatar: String,
    val email: String,
    val id: Int,
    val nickname: String,
    val status: Int,
    val token: String,
    val speed: String,
    val distance: String,
)

data class SearchPlaceData(
    val placeId: String,
    val placeName: String,
    val placeDesc: String,
    val latitude: Double,
    val longitude: Double,
)

data class SearchPlaceOtherData(
    val placeId: String,
    val placeName: String,
    val placeDesc: String,
    val isFirst: Boolean = false,
    val position: Int = 0,
)


data class CameraDeviceData(
    val id: Int,
    val camera_id: Int,
    val event_id: Int,
    val direction: Int,
    val dirtype: Int,
    val distance: Double,
    val latitude: Double,
    val longitude: Double,
    var speed: Int,
    val type: String,
)

data class RoutesEntity(
    val computeAlternativeRoutes: Boolean,
    val destination: Destination,

    val languageCode: String,
    val origin: Origin,
    val routeModifiers: RouteModifiers,
    val routingPreference: String,
    val travelMode: String,
    val units: String,
    val intermediates: List<Destination>,//途径点
)

data class Destination(
    val location: LocationG,
)


data class Origin(
    val location: LocationG,
)

data class RouteModifiers(
    val avoidFerries: Boolean,
    val avoidHighways: Boolean,
    val avoidTolls: Boolean,
)

data class LocationG(
    val latLng: LatLngG,
)

data class LatLngG(
    var latitude: Double,
    var longitude: Double,
)

data class RoutesResult(
    val routes: List<RouteResult>,
)

data class RouteResult(
    val distanceMeters: Int,
    val duration: String,
    val routeToken: String,
    val polyline: PolylineResult,
    val legs: List<RouteLgeResult>,
    var select: Boolean = false,
)

data class RouteLgeResult(
    val distanceMeters: Int,
    val duration: String,
    val routeToken: String,
    val polyline: PolylineResult,
    val steps: List<StepsData>,
)

data class PolylineResult(
    val encodedPolyline: String,
)

data class StepsData(
    val polyline: PolylineResult,
    val startLocation: StartLocationData,
    val endLocation: StartLocationData,
)

data class StartLocationData(
    val latLng: LatLng,
)

data class MiddlePoint(
    var placeId: String,
    var latitude: Double,
    var longitude: Double,
    var placeName: String,
    var placeDesc: String,
) : Serializable

data class DeviceState(
    val connect: Boolean,
)

data class DistanceData(
    var distance: Int,
    var street: String,
)


data class DefaultLocationData(
    val company: LocationData,
    val favorite: LocationData,
    val home: LocationData,
)

data class LocationData(
    val address: String,
    val createtime: Int,
    val id: Int,
    val is_default: Int,
    val lat: String,
    val long: String,
    val place_id: String,
    val title: String,
    val type: Int,
    val user_id: Int,
)
data class LocationDataCar(
    val lat: String,
    val long: String,
    val place_id: String,

)

data class DeviceData(
//    var alarms: Int,
    var brightness: Int,
    val created_at: Any,
    val createtime: Int,
    val id: Int,
    var lighting: Int,
    var sound: Int,
    var title: String,
    val updated_at: Any,
    val user_id: Int,
    val uuid: String,
    var dian: String = "",
    var type: Int = 1,
    var speed_unit: Int = 1,
    var alarm_which: Int = -1,
    var is_night: Int = -1,
    var mobile_speed: Int = -1,
    var fixed_speed: Int = -1,
    var traffic_light_camera: Int = -1,
    var mobile_speed_phone: Int = -1,
    var fixed_speed_phone: Int = -1,
    var traffic_light_camera_phone: Int = -1,
    var speed_alarm_device: Int = -1,
    var speed_alarm_phone: Int = -1,
    var road_alarm_device: Int = -1,
    var road_alarm_phone: Int = -1,
    var alarm_threshold: Int = 0,
    var connect: Boolean = false,
) : Serializable

data class ApiLocation(
    var lat: Double,
    var lng: Double,

)
data class CarLocation(
    var lat: Double,
    var lng: Double,

)
data class CarRouteInfo(
    var displayDistance: Double,
    var displayTime: Int,
)
data class LoginBean(
    var email: String,
    var password: String
)
data class IsChao(
    var chao: Boolean,

)