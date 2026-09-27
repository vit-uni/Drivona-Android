package com.drivona.speed.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.channel.sendEvent
import com.drake.net.utils.scopeNetLife
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.lalifa.ext.Tools.Companion.BLE_DengLiang
import com.drivona.speed.R
import com.drivona.speed.api.Gift
import com.drivona.speed.databinding.EmojiTabLayoutBinding
import com.drivona.speed.databinding.ItemEmojiBinding
import com.drivona.speed.databinding.ItemGiftTalkBinding
import com.drivona.speed.databinding.ItemMedalBinding
import com.lalifa.extension.dp
import com.lalifa.extension.gone
import com.lalifa.extension.invisible
import com.lalifa.extension.load
import com.lalifa.extension.loadLocal
import com.lalifa.extension.pk
import com.lalifa.extension.visible
import com.drivona.speed.api.BleRssiDevice
import com.drivona.speed.api.CMDMsg
import com.drivona.speed.api.DefaultLocationData
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.DeviceInfo
import com.drivona.speed.api.LocationData
import com.drivona.speed.api.RouteResult
import com.drivona.speed.api.SearchPlaceData
import com.drivona.speed.api.setEquipmentInfo
import com.drivona.speed.databinding.BannerDeviceItemBinding
import com.drivona.speed.databinding.BannerItemLayoutBinding
import com.drivona.speed.databinding.DeviceInfoItemBinding
import com.drivona.speed.databinding.ItemDeviceBinding
import com.drivona.speed.databinding.ItemDeviceConnectBinding
import com.drivona.speed.databinding.ItemLocationBinding
import com.drivona.speed.databinding.ItemLocationRouteResultBinding
import com.drivona.speed.databinding.ItemSearchDestBinding
import com.drivona.speed.databinding.ItemSearchHistoryBinding
import com.drivona.speed.databinding.ItemUserLocationBinding
import com.drivona.speed.databinding.PlacePredictionItemBinding
import com.drivona.speed.utils.HexUtils
import com.drivona.speed.utils.map.NavInfoBottomDisplayFragment.DistanceUnit
import com.drivona.speed.utils.map.mToFt
import com.drivona.speed.utils.map.mToKm
import com.drivona.speed.utils.map.mToMi
import okhttp3.internal.notify

/**
 * 勋章
 */
fun RecyclerView.medalList(): BindingAdapter {
    return linear(orientation = LinearLayout.HORIZONTAL).setup {
        addType<String>(R.layout.item_medal)
        onBind {
            val bean = getModel<String>()
            getBinding<ItemMedalBinding>().apply {
                imageView39.load(bean)
            }
        }
    }
}

/**
 * 语音房礼物列表
 */
fun RecyclerView.roomGiftAdapter(inType: Int): BindingAdapter {
    return grid(4).divider {
        setDivider(3.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(5.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<Gift> { R.layout.item_gift_talk }
        onBind {
            val bean = getModel<Gift>()
            getBinding<ItemGiftTalkBinding>().apply {
                im.load(bean.thumb)
                name.text = bean.name
                price.text = bean.price
                if (bean.choose) {
                    bg.visible()
                } else {
                    bg.gone()
                }
                tvNum.isVisible = inType == -1
                price.isVisible = inType != -1
                if (inType == -1) {
                    tvNum.text = "x${bean.num}"
                }
                when (bean.id) {
                    1 -> {
                        type.text = "普通"
                    }

                    2 -> {
                        type.text = "中特效"
                    }

                    3 -> {
                        type.text = "大特效"
                    }

                    else -> {
                        type.text = "普通"
                    }
                }
            }
        }
    }
}

/**
 * 表情列表
 */
fun RecyclerView.emojiAdapter(): BindingAdapter {
    return grid(4).divider {
        setDivider(8.dp)
        orientation = DividerOrientation.VERTICAL
    }.divider {
        setDivider(8.dp)
        orientation = DividerOrientation.HORIZONTAL
    }.setup {
        addType<Int>(R.layout.item_emoji)
        onBind {
            val bean = getModel<Int>()
            getBinding<ItemEmojiBinding>().apply {
                emoji.loadLocal(bean)
            }
        }
    }
}

/**
 * 个人中心功能列表
 * @receiver RecyclerView
 * @return BindingAdapter
 */
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

/**
 * 派对顶部推荐
 */
fun RecyclerView.loginBindPhoneTip(): BindingAdapter {
    return linear().divider {
        setDivider(18.dp)
        orientation = DividerOrientation.VERTICAL
    }.setup {
//        addType<String>(R.layout.item_login_bind_phone_tip)
//        onBind {
//            val bean = getModel<String>()
//            getBinding<ItemLoginBindPhoneTipBinding>().apply {
//
//            }
//        }
    }
}

/**
 * 设备列表
 */
fun RecyclerView.deviceList(): BindingAdapter {
    return linear().setup {
        addType<DeviceInfo>(R.layout.item_device)
        onBind {
            val bean = getModel<DeviceInfo>()
            getBinding<ItemDeviceBinding>().apply {
                tvName.text = bean.name
            }
        }
    }
}

/**
 * 已连设备列表
 */
fun RecyclerView.connectDeviceList(): BindingAdapter {
    return linear().setup {
        addType<BleRssiDevice>(R.layout.item_device_connect)
        onBind {
            val bean = getModel<BleRssiDevice>()
            getBinding<ItemDeviceConnectBinding>().apply {
                tvName.text = bean.bleName
            }
        }
    }
}

/**
 * 设备列表
 */
fun RecyclerView.newDeviceList(): BindingAdapter {
    return setup {
        addType<BleRssiDevice>(R.layout.banner_item_layout)
        onBind {
            val bean = getModel<BleRssiDevice>()
            getBinding<BannerItemLayoutBinding>().apply {
                tvDeviceName.text = bean.bleName
                tvDeviceAddress.text = HexUtils.swapMacFirstTwoBytesNoColon(bean.bleAddress)
            }
        }
    }
}

/**
 * 设备列表
 */
@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.deviceNewList(): BindingAdapter {
    return setup {
        addType<DeviceData>(R.layout.banner_device_item)
        onBind {
            val bean = getModel<DeviceData>()
            getBinding<BannerDeviceItemBinding>().apply {
                tvDeviceName.text = bean.title
                tvDeviceNameDesc.text = bean.uuid
                if (bean.type == 1) {
                    imageView.setBackgroundResource(R.drawable.selector_device_icon01)
                } else {
                    imageView.setBackgroundResource(R.drawable.selector_device_icon)
                }
                ivLight.isVisible = bean.type != 1
                ivLightIcon.isVisible = bean.type != 1
                clDevice02.isVisible = bean.type != 1
                imageView.isSelected = bean.connect
                ivDianBg.isVisible = bean.connect
                tvDianDesc.isVisible = bean.connect
                ivDianSmall.isVisible = bean.connect
                tvDianDesc.text = bean.dian.pk("100%")
                Log.e("Song", "更新数据——————${bean!!.connect}")
                if (bean!!.connect) {
                    clSet.visible()
                    tvReconnect.gone()
                } else {
                    clSet.gone()
                    tvReconnect.visible()
                }
                seekbar0.setProgress(bean!!.brightness)
                seekbar1.setProgress(bean!!.lighting)
                seekbar2.setProgress(bean!!.sound)
                tvSeekbar0.text = bean!!.brightness.toString()
                tvSeekbar1.text = bean!!.lighting.toString()
                tvSeekbar2.text = bean!!.sound.toString()
                ivLight.isSelected = bean!!.is_night == 1
                seekbar0.setOnTouchListener { v, event ->
                    val parent = v.parent as ViewGroup
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // 按下时，禁止父容器拦截触摸事件
                            parent.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            // 抬手后恢复拦截
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    false // 不消费事件，继续交给SeekBar处理拖动
                }
                seekbar1.setOnTouchListener { v, event ->
                    val parent = v.parent as ViewGroup
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // 按下时，禁止父容器拦截触摸事件
                            parent.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            // 抬手后恢复拦截
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    false // 不消费事件，继续交给SeekBar处理拖动
                }
                seekbar2.setOnTouchListener { v, event ->
                    val parent = v.parent as ViewGroup
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // 按下时，禁止父容器拦截触摸事件
                            parent.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            // 抬手后恢复拦截
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    false // 不消费事件，继续交给SeekBar处理拖动
                }
                seekbar0.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean,
                    ) {
                        tvSeekbar0.text = progress.toString()

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        scopeNetLife {
                            bean?.apply {
                                setEquipmentInfo(id, brightness = seekBar.progress)
                                bean.brightness = seekBar.progress
                                //向设备发送速度
                                val cmd = CMDMsg(
                                    "55 AA 04 00 00 05 02 ${
                                        HexUtils.decimalToLittleEndianHex(
                                            seekBar.progress,
                                            1
                                        )
                                    }"
                                )
                                sendEvent(cmd)


                            }

                        }

                    }

                })
                seekbar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean,
                    ) {
                        tvSeekbar1.text = progress.toString()

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        scopeNetLife {
                            bean?.apply {
                                setEquipmentInfo(id, lighting = seekBar.progress)
                                bean.lighting = seekBar.progress
                                val cmd = CMDMsg(
                                    BLE_DengLiang + HexUtils.decimalToLittleEndianHex(
                                        seekBar.progress,
                                        1
                                    )
                                )
                                sendEvent(cmd)


                            }

                        }

                    }

                })
                seekbar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean,
                    ) {
                        tvSeekbar2.text = progress.toString()
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        scopeNetLife {
                            bean?.apply {
                                setEquipmentInfo(id, sound = seekBar.progress)
                                bean.sound = seekBar.progress
                                val cmd = CMDMsg(
                                    "55 AA 05 00 01 02 02 01 ${
                                        HexUtils.decimalToLittleEndianHex(
                                            seekBar.progress,
                                            1
                                        )
                                    }"
                                )
                                sendEvent(cmd)


                            }

                        }

                    }

                })
            }
        }
    }
}

/**
 * 设备列表
 */
fun RecyclerView.deviceInfoList(): BindingAdapter {
    return linear(RecyclerView.HORIZONTAL).setup {
        addType<Int>(R.layout.device_info_item)
        onBind {
            val bean = getModel<Int>()
            getBinding<DeviceInfoItemBinding>().apply {
                if (bean == 0) {
                    iv0.setBackgroundResource(R.mipmap.ic_edit_info_0)
                    tvName.text = "S1"
                } else {
                    iv0.setBackgroundResource(R.mipmap.ic_edit_info_1)
                    tvName.text = "S2"
                }
                tvDesc.text = context.getString(R.string.product_manual)
            }
        }
    }
}

/**
 * 搜索记录
 */
fun RecyclerView.searchHistoryList(): BindingAdapter {
    return linear().setup {
        addType<SearchPlaceData>(R.layout.place_prediction_item)
        onBind {
            val bean = getModel<SearchPlaceData>()
            getBinding<PlacePredictionItemBinding>().apply {
                textViewTitle.text = bean.placeName
                textViewAddress.text = bean.placeDesc
            }
        }
    }
}

/**
 * 搜索结果
 */
fun RecyclerView.searchResultList(): BindingAdapter {
    return linear().setup {
        addType<AutocompletePrediction>(R.layout.item_search_history)
        onBind {
            val bean = getModel<AutocompletePrediction>()
            getBinding<ItemSearchHistoryBinding>().apply {
//                tvName.text = bean.getPrimaryText(null)
//                tvNameDesc.text = bean.getSecondaryText(null)
            }
        }
    }
}

/**
 * 路线
 */
fun RecyclerView.destList(): BindingAdapter {
    return linear(RecyclerView.HORIZONTAL).setup {
        addType<String>(R.layout.item_search_dest)
        singleMode = true
        onBind {
            val bean = getModel<String>()
            getBinding<ItemSearchDestBinding>().apply {
                item.isSelected = modelPosition == 0
            }
        }
    }
}

/**
 * 路线
 */
fun RecyclerView.locationList(): BindingAdapter {
    return linear().setup {
        addType<String>(R.layout.item_location)
        singleMode = true
        onBind {
            val bean = getModel<String>()
            getBinding<ItemLocationBinding>().apply {
                item.isSelected = modelPosition == 0
                tvName.text = bean
                line.isVisible = modelPosition != itemCount - 1
                if (modelPosition == 0) {
                    ivAdd.visible()
                    ivAdd.setBackgroundResource(R.drawable.ic_route_change)
//                } else if (modelPosition == 1) {
//                    ivAdd.visible()
//                    ivAdd.setBackgroundResource(R.drawable.ic_route_change)
                } else {
                    ivAdd.invisible()
                }
                if (modelPosition == 0) {
                    lineTop.gone()
                    lineBottom.visible()
                    ivDotStart.setBackgroundResource(R.drawable.bg_dot_528bff)
                } else if (modelPosition == itemCount - 1) {
                    ivDotStart.setBackgroundResource(R.drawable.bg_dot_ff3333)
                    lineBottom.gone()
                    lineTop.visible()
                } else {
                    ivDotStart.setBackgroundResource(R.drawable.ic_dot_a)
                    lineBottom.visible()
                    lineTop.visible()
                }
            }
        }
    }
}

/**
 * 路线
 */
fun RecyclerView.locationRouteList(danwei: String): BindingAdapter {
    return linear(RecyclerView.HORIZONTAL).setup {
        addType<RouteResult>(R.layout.item_location_route_result)
        singleMode = true
        onBind {
            val bean = getModel<RouteResult>()
            getBinding<ItemLocationRouteResultBinding>().apply {
                item.isSelected = bean.select
                tvName.text = getDisplayDistance(bean.distanceMeters.toDouble(), danwei != "mi")
                tvNameDesc.text = formatSecondToHM(bean.duration)
                tvIndex.text = "Route ${position + 1}"
            }
        }
    }
}

fun RecyclerView.userLocationList(): BindingAdapter {
    return linear().setup {
        addType<LocationData>(R.layout.item_user_location)
        singleMode = true
        onBind {
            val bean = getModel<LocationData>()
            getBinding<ItemUserLocationBinding>().apply {
                tvName.text = bean.title
                tvNameDesc.text = bean.address
            }
        }
    }
}

/**
 * 秒数转 时:分（舍弃秒）
 */
fun formatSecondToHM(seconds: String): String {
    val totalSeconds = seconds.replace("s", "").toLong()
    val hour = totalSeconds / 3600
    val minute = (totalSeconds % 3600) / 60
    if (hour.toInt() == 0) {
        return String.format("%01dmin", minute)
    } else {
        return String.format("%01dh%01dmin", hour, minute)
    }

}

/**
 * 米转千米，保留1位小数 例：123123m → 123.1 km
 */
fun meterToKmOne(meter: Number): String {
    val km = meter.toDouble() / 1000.0
    return String.format("%.1f", km)
}

fun getDisplayDistance(meters: Double, isMetric: Boolean): String {
    return if (isMetric) {
        // 公制 km/m
        if (meters < 1000) "${meters.toInt()} m"
        else "${formatDistance(meters, DistanceUnit.KM, 2)} km"
    } else {
        meterToMiOrYd(meters)
//            // 英制 mi/ft
//            if (meters < M_PER_MI) "${formatDistance(meters, DistanceUnit.FT, 0)} ft"
//            else "${formatDistance(meters, DistanceUnit.MI, 1)} mi"
    }
}

fun formatDistance(meters: Double, unit: DistanceUnit, decimal: Int = 2): String {
    val value = when (unit) {
        DistanceUnit.KM -> meters.mToKm()
        DistanceUnit.M -> meters
        DistanceUnit.MI -> meters.mToMi()
        DistanceUnit.FT -> meters.mToFt()
    }
    return "%.${decimal}f".format(value)
}

/**
 * 米 转为 英里/码 文本
 * @param meter 距离：米
 * @param decimal 保留小数位数，默认2位
 * @return 格式化距离字符串
 */
fun meterToMiOrYd(meter: Double, decimal: Int = 2): String {
    val mileStandard = 1609.344
    return if (meter >= mileStandard) {
        // 换算英里
        val mile = meter / mileStandard
        String.format("%.${decimal}f mi", mile)
    } else {
        // 换算码
        val yard = meter * 1.0936133
        "${yard.toInt()}yd"
    }
}
