package com.drivona.speed.ext

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.drake.channel.sendEvent
import com.drake.net.utils.scopeNet
import com.drake.net.utils.scopeNetLife
import com.lalifa.ext.Tools.Companion.BLE_DengLiang
import com.lalifa.extension.format
import com.lalifa.extension.globalUITask
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.pk
import com.lalifa.extension.text
import com.lalifa.extension.visible
import com.drivona.speed.MApplication
import com.drivona.speed.R
import com.drivona.speed.api.CMDMsg
import com.drivona.speed.api.ColorDTO
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.setEquipmentInfo
import com.drivona.speed.databinding.BannerDeviceItemBinding
import com.drivona.speed.databinding.PopAgreeConfirmBinding
import com.drivona.speed.databinding.PopChangeDeviceNameBinding
import com.drivona.speed.databinding.PopDeleteConfirmBinding
import com.drivona.speed.databinding.PopDeviceNoticeBinding
import com.drivona.speed.databinding.PopDeviceSetBinding
import com.drivona.speed.databinding.PopEmailDeleteConfirmBinding
import com.drivona.speed.databinding.PopExitBinding
import com.drivona.speed.databinding.PopExitConfirmBinding
import com.drivona.speed.utils.HexUtils
import com.drivona.speed.utils.HexUtils.Companion.decimalToLittleEndianHex
import com.lalifa.activity.WebActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.appendSpan
import com.lalifa.extension.start
import per.goweii.layer.core.ktx.onClickToDismiss
import per.goweii.layer.core.ktx.onDismiss
import per.goweii.layer.core.ktx.onInitialize
import per.goweii.layer.core.widget.SwipeLayout
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.backgroundDimDefault
import per.goweii.layer.dialog.ktx.cancelableOnTouchOutside
import per.goweii.layer.dialog.ktx.contentView
import per.goweii.layer.dialog.ktx.gravity
import per.goweii.layer.dialog.ktx.swipeDismiss


/**
 * 弹幕收费
 */
fun Activity.showBarrageChargesDialog(
    colorDTO: ColorDTO?,
    callback: DialogLayer.(type: View) -> Unit,
) {
    DialogLayer(this)
        .contentView(R.layout.pop_barrage_charges)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.BOTTOM)
        .backgroundDimDefault()
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
//        .onClickToDismiss(R.id.close)
        .addInputMethodCompat(true)
        .onInitialize {
//            val diamonds = requireViewById<TextView>(R.id.diamonds)
//            val close = requireViewById<TextView>(R.id.close)
//            val drill = requireViewById<TextView>(R.id.drill)
//            val go_to_top_up = requireViewById<TextView>(R.id.go_to_top_up)
//            diamonds.text = "${colorDTO?.price_barrage}/个"
//            scopeNet {
//                val centerIndex = centerIndex()
//                drill.text = centerIndex?.wallet?.diamond
//                val wallet = centerIndex?.wallet
//                var price = colorDTO?.price_barrage?.toInt() ?: 0
//                val diamond = wallet?.diamond?.toDouble()!!
//                if (price > diamond) {
//                    close.setText("余额不足，无法购买")
//                    close.isEnabled = false
//                }
//            }
//
//            go_to_top_up.onClick {
//                if (this@showBarrageChargesDialog is RoomActivity) {
//                    showRechargeDialog { view, bean, rechargeRue ->
//                        when (view.id) {
//                            R.id.wechat_pay, R.id.ali_pay -> {
//                                placeOrder(this, if (view.id == R.id.ali_pay) 2 else 1, bean)
//                            }
//                        }
//                    }
//                    dismiss()
//                }
//            }
//            close.onClick {
//                callback.invoke(this, it)
////                if (this@showBarrageChargesDialog is RoomActivity) {
////                    dismiss()
////                    this@showBarrageChargesDialog.showRechargeDialog { view, bean ->
////                        when (view.id) {
////                            R.id.wechat_pay, R.id.ali_pay -> {
////                                placeOrder(this, if (view.id == R.id.ali_pay) 2 else 1, bean)
////                            }
////                        }
////                    }
////                }
//            }
        }.show()
}

/**
 * 修改昵称
 */
fun Activity.showEditDialog(

    callback: (type: String) -> Unit,
) {
    DialogLayer(this)
        .contentView(R.layout.pop_edit_name)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.CENTER)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
//        .onClickToDismiss(R.id.close)
        .addInputMethodCompat(true)
        .onInitialize {

            val et_input = requireViewById<EditText>(R.id.et_input)
            val tv_sure = requireViewById<TextView>(R.id.tv_sure)

            tv_sure.onClick {
                val name = et_input.text()
                if (name.isNotEmpty()) {
                    callback.invoke(name)
                    dismiss()
                }
            }

        }.show()
}

fun Activity.showEditDeviceNameDialog(

    callback: (type: String) -> Unit,
) {
    DialogLayer(this)
        .contentView(R.layout.pop_edit_device_name)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.CENTER)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
//        .onClickToDismiss(R.id.close)
        .addInputMethodCompat(true)
        .onInitialize {

            val et_input = requireViewById<EditText>(R.id.et_input)
            val tv_sure = requireViewById<TextView>(R.id.tv_sure)

            tv_sure.onClick {
                val name = et_input.text()
                if (name.isNotEmpty()) {
                    callback.invoke(name)
                    dismiss()
                }
            }

        }.show()
}

/**
 * 公共弹窗
 */
fun Activity.showTipsDialog(
    title: String = "Cancel the account",
    content: String = "Cancel the account",
    leftText: String = "Continue to cancel",
    rightText: String = "Reconsider",
    callback: () -> Unit,
) {
    DialogLayer(this)
        .contentView(R.layout.pop_common_tips)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.CENTER)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
//        .onClickToDismiss(R.id.close)
        .addInputMethodCompat(true)
        .onInitialize {

            val tvTitle = requireViewById<TextView>(R.id.tv_title)
            val tvTips = requireViewById<TextView>(R.id.tv_tips)
            val tvSure = requireViewById<TextView>(R.id.tv_sure)
            val tvCancel = requireViewById<TextView>(R.id.tv_cancel)
            tvTitle.text = title
            tvTips.text = content
            tvSure.text = leftText
            tvCancel.text = rightText
            tvSure.onClick {
                callback.invoke()
                dismiss()
            }
            tvCancel.onClick {
                dismiss()
            }
        }.show()
}

fun Activity.showAddDescDialog(

    callback: (Int) -> Unit,
) {
    DialogLayer(this)
        .contentView(R.layout.pop_add_desc)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.BOTTOM)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
//        .onClickToDismiss(R.id.close)
        .addInputMethodCompat(true)
        .onInitialize {


            val iv_close = requireViewById<ImageView>(R.id.iv_close)
            val cl_favorite = requireViewById<ConstraintLayout>(R.id.cl_favorite)
            val cl_home = requireViewById<ConstraintLayout>(R.id.cl_home)
            val cl_company = requireViewById<ConstraintLayout>(R.id.cl_company)
            iv_close.onClick {
                dismiss()
            }
            cl_favorite.onClick {
                callback.invoke(3)
                dismiss()
            }
            cl_home.onClick {
                callback.invoke(1)
                dismiss()
            }
            cl_company.onClick {
                callback.invoke(2)
                dismiss()
            }

        }.show()
}

fun Activity.showDeviceNightModeSuccessDialog(

    callback: (Int) -> Unit,
) {
    DialogLayer(this)
        .contentView(R.layout.pop_device_night_mode)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.CENTER)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
        .onClickToDismiss(R.id.cl_location_detail)
        .addInputMethodCompat(true)
        .onInitialize {

            globalUITask(1000) {
                dismiss()
            }

        }.show()
}

fun Activity.showReconnectDialog(): DialogLayer {
   return DialogLayer(this)
        .contentView(R.layout.pop_device_reconnecting)
        .cancelableOnTouchOutside(false)
        .gravity(Gravity.CENTER)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
        .onClickToDismiss(R.id.cl_location_detail)
        .addInputMethodCompat(true)
        .onInitialize {

        }
}

/**
 * 到达目的地
 */
fun Activity.showArrivedDialog(
    callback: (Int) -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(false)
        .contentView(R.layout.pop_arrived)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)

        .onInitialize {
            val tvFeedBack = requireViewById<TextView>(R.id.tv_feedback)
            val tv_done = requireViewById<TextView>(R.id.tv_done)
            tvFeedBack.onClick {
                callback.invoke(1)
                dismiss()
            }
            tv_done.onClick {
                callback.invoke(2)
                dismiss()
            }
        }.show()
}

fun Activity.showEmailDialog(
    callback: (Int) -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_email)
        .backgroundDimDefault()
        .gravity(Gravity.CENTER)
        .onClickToDismiss(R.id.cl_email)
        .onInitialize {

        }.show()
}

fun Activity.showDeleteConfirmDialog(
    title: String = getString(R.string.remove_location),
    desc: String = getString(R.string.remove_this_location),
    callback: (Int) -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_delete_confirm)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)
        .onInitialize {
            PopDeleteConfirmBinding.bind(requireViewById<ConstraintLayout>(R.id.pop_delete_confirm))
                .apply {
                    tv0.text = title
                    tv1.text = desc
                    tvCancel.onClick {
                        callback.invoke(0)
                        dismiss()
                    }
                    tvDone.onClick {
                        dismiss()
                    }
                }
        }.show()
}

fun Activity.showChangeDeviceNameDialog(

    callback: (String) -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_change_device_name)
        .backgroundDimDefault()
        .gravity(Gravity.CENTER)
        .onInitialize {
            PopChangeDeviceNameBinding.bind(requireViewById<ConstraintLayout>(R.id.pop_change_device_name))
                .apply {


                    tvDone.onClick {
                        val text = etName.text()
                        if (text.isNullOrEmpty()) {
                            return@onClick
                        }
                        callback.invoke(text)
                        dismiss()
                    }
                }
        }.show()
}

fun Activity.showAgreeConfirmDialog(

    callback: (Int) -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_agree_confirm)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)
        .onInitialize {
            PopAgreeConfirmBinding.bind(requireViewById<ConstraintLayout>(R.id.pop_agree_confirm))
                .apply {
                    tv1.apply {
                        text = context.getString(R.string.please_review_and_accept_the)
                        append("  ")
                        appendSpan(
                            context.getString(R.string.user_agreement),
                            color = Color.parseColor("#ED5700")
                        ) {
                            start(WebActivity::class.java) {
                                putExtra("title", context.getString(R.string.user_agreement))
                                putExtra("url", "${Tools.HOST}terms-of-service.html")
                            }

                        }
                        append("  ")
                        append(context.getString(R.string.and))
                        append("  ")
                        appendSpan(
                            context.getString(R.string.privacy_policy),
                            color = Color.parseColor("#ED5700")
                        ) {
                            start<WebActivity> {
                                putExtra("title", context.getString(R.string.privacy_policy))
                                putExtra("url", "${Tools.HOST}privacy-policy.html")
                            }
                        }
                        append("  ")
                        append(context.getString(R.string.to_continue))
                    }
                    tvCancel.onClick {

                        dismiss()
                    }
                    tvDone.onClick {
                        callback.invoke(0)
                        dismiss()
                    }
                }
        }.show()
}

fun Activity.showExitConfirmDialog(

    callback: () -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_exit_confirm)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)
        .onInitialize {
            PopExitConfirmBinding.bind(requireViewById<ConstraintLayout>(R.id.pop_exit_confirm))
                .apply {

                    tvCancel.onClick {
                        callback.invoke()
                        dismiss()
                    }
                    tvDone.onClick {
                        dismiss()
                    }
                }
        }.show()
}

fun Activity.showDeviceSetDialog(
    bean: DeviceData,
    callback: (currentDevice: DeviceData) -> Unit,
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_device_set)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)

        .onInitialize {
            PopDeviceSetBinding.bind(requireViewById<ConstraintLayout>(R.id.pop_device_set)).apply {
                if (bean.type == 1) {
                    tvDeviceNameDesc.text = "DRIVONA-S1"
                } else if (bean.type == 2) {
                    tvDeviceNameDesc.text = "DRIVONA-S2"
                } else {
                    tvDeviceNameDesc.text = "DRIVONA"
                }

                tvDeviceName.text = bean.title
                if (bean.type == 1) {
                    clDevice02.gone()
                }
                cl01.isVisible = bean.type == 1
                ivLight.isVisible = bean.type != 1
                ivLightIcon.isVisible = bean.type != 1

//                    tvDianDesc.text = bean.dian.pk("100%")
                Log.e("Song", "更新数据——————${bean!!.connect}")

                seekbar0.setProgress(bean!!.brightness)
                seekbar1.setProgress(bean!!.lighting)
                seekbar11.setProgress(bean!!.lighting)
                seekbar2.setProgress(bean.sound)
                tvSeekbar0.text = bean!!.brightness.toString()
                tvSeekbar1.text = bean!!.lighting.toString()
                tvSeekbar11.text = bean!!.lighting.toString()
                tvSeekbar2.text = bean!!.sound.toString()
                ivLight.isSelected = bean!!.is_night == 1
                ivLight.onClick {
                    scopeNet {
                        bean.apply {
                            if (is_night != 1) {
                                is_night = 1
                                sendEvent(CMDMsg("55AA 04 00 01 05 02 01"))

                                showDeviceNightModeSuccessDialog {
                                }
                            } else {
                                is_night = 0
                                sendEvent(CMDMsg("55AA 04 00 01 05 02 00"))
                            }
                            setEquipmentInfo(this.id, is_night = this.is_night)
                            ivLight.isSelected = is_night == 1
                            callback.invoke(this)
                        }

                    }
                }
                ivNotice.onClick {
                    showDeviceNoticeDialog(bean) {

                    }
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
                        scopeNet {
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
                        scopeNet {
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
                seekbar11.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean,
                    ) {
                        tvSeekbar11.text = progress.toString()

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        scopeNet {
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
                        scopeNet {
                            bean?.apply {
                                setEquipmentInfo(id, sound = seekBar.progress)
                                bean.sound = seekBar.progress
                                val cmd = CMDMsg(
                                    "55 AA 05 00 01 02 0${type} 01 ${
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
        }.show()
}

fun Activity.showDeviceNoticeDialog(
    currentDevice: DeviceData,
    callback: (currentDevice: DeviceData) -> Unit,
) {
    DialogLayer(this)
        .contentView(R.layout.pop_device_notice)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.BOTTOM)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
//        .onClickToDismiss(R.id.close)
        .addInputMethodCompat(true)
        .onInitialize {
            PopDeviceNoticeBinding.bind(requireViewById<NestedScrollView>(R.id.pop_device_notice))
                .apply {
                    tvThreshold0.isSelected = currentDevice.speed_unit != 2
                    tvThreshold1.isSelected = currentDevice.speed_unit == 2
                    if (currentDevice.speed_unit == 2) {
                        tvType.text = "%"
                    } else {
                        tvType.text = "km/h (mph)"
                    }
                    tvThreshold0.onClick {
                        tvThreshold0.isSelected = true
                        tvThreshold1.isSelected = false
                        tvType.text = "km/h (mph)"
                        scopeNet {
                            setEquipmentInfo(currentDevice.id, speed_unit = 1)
                            currentDevice.speed_unit = 1
                            val cmd = CMDMsg(
                                "55 AA 06 00 03 04 0${currentDevice.type} FF 01 ${
                                    decimalToLittleEndianHex(
                                        seekTime.progress,
                                        1
                                    )
                                }"
                            )
                            sendEvent(cmd)
                        }

                    }
                    seekTime.progress = currentDevice.alarm_threshold
                    tvNumber.text = "${currentDevice.alarm_threshold}"

                    tvThreshold1.onClick {
                        tvThreshold0.isSelected = false
                        tvThreshold1.isSelected = true
                        tvType.text = "%"
                        scopeNet {
                            setEquipmentInfo(currentDevice.id, speed_unit = 2)
                            currentDevice.speed_unit = 2
                            val cmd = CMDMsg(
                                "55 AA 06 00 03 04 0${currentDevice.type} FF 02 ${
                                    decimalToLittleEndianHex(
                                        seekTime.progress,
                                        1
                                    )
                                }"
                            )
                            sendEvent(cmd)
                        }

                    }
                    swSpeedAlarmDevice.setChecked(currentDevice.speed_alarm_device == 1, false)
                    swSpeedAlarmPhone.setChecked(currentDevice.speed_alarm_phone == 1, false)
                    swRoadAlarmDevice.setChecked(currentDevice.road_alarm_device == 1, false)
                    swRoadAlarmPhone.setChecked(currentDevice.road_alarm_phone == 1, false)
                    cl02.isSelected = currentDevice.alarm_which != 2
                    cl12.isSelected = currentDevice.alarm_which == 2
                    cl02.onClick {
                        cl02.isSelected = true
                        cl12.isSelected = false
//                        currentDevice.alarm_which = 1
//                        scopeNet {
//                            setEquipmentInfo(currentDevice.id, alarm_which = 1)
//                        }
//                        val cmd = CMDMsg("55AA0500 0002 0${currentDevice.type} FF01")
//                        sendEvent(cmd)
                        swMobileSpeed.setChecked(currentDevice.mobile_speed == 1, false)
                        swFixedSpeed.setChecked(currentDevice.fixed_speed == 1, false)
                        swTrafficLightCamera.setChecked(
                            currentDevice.traffic_light_camera == 1,
                            false
                        )
                    }
                    cl12.onClick {
                        cl02.isSelected = false
                        cl12.isSelected = true
//                        currentDevice.alarm_which = 2
//                        scopeNet {
//                            setEquipmentInfo(currentDevice.id, alarm_which = 2)
//                        }
//                        val cmd = CMDMsg("55AA0500 0002 0${currentDevice.type} FF00")
//                        sendEvent(cmd)
                        swMobileSpeed.setChecked(currentDevice.mobile_speed_phone == 1, false)
                        swFixedSpeed.setChecked(currentDevice.fixed_speed_phone == 1, false)
                        swTrafficLightCamera.setChecked(
                            currentDevice.traffic_light_camera_phone == 1,
                            false
                        )
                    }
                    swMobileSpeed.setChecked(currentDevice.mobile_speed == 1, false)
                    swFixedSpeed.setChecked(currentDevice.fixed_speed == 1, false)
                    swTrafficLightCamera.setChecked(currentDevice.traffic_light_camera == 1, false)
                    swSpeedAlarmDevice.setOnCheckedChangeListener { button, checked ->
                        if (checked) {
                            currentDevice.speed_alarm_device = 1
                            val cmd = CMDMsg("55 AA 05 00 00 04 0${currentDevice.type} FF 01")
                            sendEvent(cmd)
                        } else {
                            currentDevice.speed_alarm_device = 0
                            val cmd = CMDMsg("55 AA 05 00 00 04 0${currentDevice.type} FF 00")
                            sendEvent(cmd)
                        }
                        scopeNet {
                            setEquipmentInfo(
                                currentDevice.id,
                                speed_alarm_device = currentDevice.speed_alarm_device
                            )
                        }
                    }
                    swSpeedAlarmPhone.setOnCheckedChangeListener { button, checked ->
                        if (checked) {
                            currentDevice.speed_alarm_phone = 1
                        } else {
                            currentDevice.speed_alarm_phone = 0
                        }
                        scopeNet {
                            setEquipmentInfo(
                                currentDevice.id,
                                speed_alarm_phone = currentDevice.speed_alarm_phone
                            )
                        }
                    }
                    swRoadAlarmDevice.setOnCheckedChangeListener { button, checked ->
                        if (checked) {
                            currentDevice.road_alarm_device = 1
                            val cmd = CMDMsg("55 AA 05 00 00 03 0${currentDevice.type} FF 01")
                            sendEvent(cmd)
                        } else {
                            currentDevice.road_alarm_device = 0
                            val cmd = CMDMsg("55 AA 05 00 00 03 0${currentDevice.type} FF 00")
                            sendEvent(cmd)
                        }
                        scopeNet {
                            setEquipmentInfo(
                                currentDevice.id,
                                road_alarm_device = currentDevice.road_alarm_device
                            )
                        }
                    }
                    swRoadAlarmPhone.setOnCheckedChangeListener { button, checked ->
                        if (checked) {
                            currentDevice.road_alarm_phone = 1
                        } else {
                            currentDevice.road_alarm_phone = 0
                        }
                        scopeNet {
                            setEquipmentInfo(
                                currentDevice.id,
                                road_alarm_phone = currentDevice.road_alarm_phone
                            )
                        }
                    }
                    swMobileSpeed.setOnCheckedChangeListener { button, checked ->
                        if (checked) {
                            if (cl02.isSelected) {
                                currentDevice.mobile_speed = 1
                            } else {
                                currentDevice.mobile_speed_phone = 1
                            }

                        } else {
                            if (cl02.isSelected) {
                                currentDevice.mobile_speed = 0
                            } else {
                                currentDevice.mobile_speed_phone = 0
                            }

                        }
                        if (cl02.isSelected) {
                            //0x2：移动超速摄像头告警
                            val cmd =
                                CMDMsg("55AA 0500 0002 0${currentDevice.type} 02 0${currentDevice.mobile_speed}")
                            sendEvent(cmd)
                            scopeNet {
                                setEquipmentInfo(
                                    currentDevice.id,
                                    mobile_speed = currentDevice.mobile_speed
                                )
                            }
                        } else {
                            scopeNet {
                                setEquipmentInfo(
                                    currentDevice.id,
                                    mobile_speed_phone = currentDevice.mobile_speed_phone
                                )
                            }
                        }


                    }
                    swFixedSpeed.setOnCheckedChangeListener { button, checked ->
                        if (cl02.isSelected) {
                            if (checked) {
                                currentDevice.fixed_speed = 1
                            } else {
                                currentDevice.fixed_speed = 0
                            }
                            //固定摄像头
                            val cmd =
                                CMDMsg("55AA050000020${currentDevice.type} 01 0${currentDevice.fixed_speed}")
                            sendEvent(cmd)
                            scopeNet {
                                setEquipmentInfo(
                                    currentDevice.id,
                                    fixed_speed = currentDevice.fixed_speed
                                )
                            }
                        } else {
                            if (checked) {
                                currentDevice.fixed_speed_phone = 1
                            } else {
                                currentDevice.fixed_speed_phone = 0
                            }
                            //固定摄像头

                            scopeNet {
                                setEquipmentInfo(
                                    currentDevice.id,
                                    fixed_speed_phone = currentDevice.fixed_speed_phone
                                )
                            }
                        }

                    }
                    swTrafficLightCamera.setOnCheckedChangeListener { button, checked ->
                        if (cl02.isSelected) {
                            if (checked) {
                                currentDevice.traffic_light_camera = 1
                            } else {
                                currentDevice.traffic_light_camera = 0
                            }
                            //交通灯
                            val cmd =
                                CMDMsg("55AA050000020${currentDevice.type} 03 0${currentDevice.traffic_light_camera}")
                            sendEvent(cmd)
                            scopeNet {
                                setEquipmentInfo(
                                    currentDevice.id,
                                    traffic_light_camera = currentDevice.traffic_light_camera
                                )
                            }
                        } else {
                            if (checked) {
                                currentDevice.traffic_light_camera_phone = 1
                            } else {
                                currentDevice.traffic_light_camera_phone = 0
                            }

                            scopeNet {
                                setEquipmentInfo(
                                    currentDevice.id,
                                    traffic_light_camera_phone = currentDevice.traffic_light_camera_phone
                                )
                            }
                        }

                    }
                    speedTestPhone.onClick {

                        MApplication.get().playChaosu()


                    }
                    speedTestDevice.onClick {
                        val cmd = CMDMsg("55AA0400 0B02 0${currentDevice.type} 03")
                        sendEvent(cmd)

                    }
                    roadTestDevice.onClick {
                        val cmd = CMDMsg("55AA0400 0B02 0${currentDevice.type} 02")
                        sendEvent(cmd)
                    }
                    roadTestPhone.onClick {

                        MApplication.get().playWeixian()

                    }
                    speedWarnTestDevice.onClick {
                        val cmd = CMDMsg("55AA0400 0B02 0${currentDevice.type} 01")
                        sendEvent(cmd)
                    }
                    speedWarnTestPhone.onClick {

                        MApplication.get().playShexiangtou()


                    }
                    seekTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean,
                        ) {
                            ivDotStart.isVisible = progress != 0
                            ivDotEnd.isVisible = progress != 30
                            tvNumber.text = progress.toString()

                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar) {

                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            scopeNet {
                                currentDevice.alarm_threshold = seekBar.progress
                                setEquipmentInfo(
                                    currentDevice.id,
                                    alarm_threshold = currentDevice.alarm_threshold
                                )
                                if (tvThreshold1.isSelected) {
                                    val cmd = CMDMsg(
                                        "55 AA 06 00 03 04 0${currentDevice.type} FF 02 ${
                                            decimalToLittleEndianHex(
                                                seekTime.progress,
                                                1
                                            )
                                        }"
                                    )
                                    sendEvent(cmd)
                                } else {
                                    val cmd = CMDMsg(
                                        "55 AA 06 00 03 04 0${currentDevice.type} FF 01 ${
                                            decimalToLittleEndianHex(
                                                seekTime.progress,
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
            val cl_top_0 = requireViewById<ConstraintLayout>(R.id.cl_top_0)
            val cl_top_1 = requireViewById<ConstraintLayout>(R.id.cl_top_1)
            val cl_top_2 = requireViewById<ConstraintLayout>(R.id.cl_top_2)
            val cl_parent = requireViewById<ConstraintLayout>(R.id.cl_parent)
            val cl_notice_0 = requireViewById<ConstraintLayout>(R.id.cl_notice_0)
            val cl_notice_1 = requireViewById<ConstraintLayout>(R.id.cl_notice_1)
            val cl_notice_2 = requireViewById<ConstraintLayout>(R.id.cl_notice_2)
            cl_top_0.isSelected = true

            cl_top_0.onClick {
                cl_top_0.isSelected = true
                cl_top_1.isSelected = false
                cl_top_2.isSelected = false
                cl_parent.setBackgroundResource(R.drawable.bg_notice_0)
                cl_notice_0.visible()
                cl_notice_1.gone()
                cl_notice_2.gone()
            }
            cl_top_1.onClick {
                cl_top_0.isSelected = false
                cl_top_1.isSelected = true
                cl_top_2.isSelected = false
                cl_parent.setBackgroundResource(R.drawable.bg_notice_1)
                cl_notice_0.gone()
                cl_notice_1.visible()
                cl_notice_2.gone()
            }
            cl_top_2.onClick {
                cl_top_0.isSelected = false
                cl_top_1.isSelected = false
                cl_top_2.isSelected = true
                cl_parent.setBackgroundResource(R.drawable.bg_notice_2)
                cl_notice_0.gone()
                cl_notice_1.gone()
                cl_notice_2.visible()
            }

            onDismiss {
                callback.invoke(currentDevice)
            }
        }.show()
}

fun Activity.showEmailDeleteConfirmDialog(
    callback: (Int) -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_email_delete_confirm)
        .backgroundDimDefault()
        .gravity(Gravity.BOTTOM)
        .onInitialize {
            PopEmailDeleteConfirmBinding.bind(requireViewById<ConstraintLayout>(R.id.pop_email_delete_confirm))
                .apply {
                    tvCancel.onClick {
                        callback.invoke(0)
                        dismiss()
                    }
                    tvDone.onClick {
                        dismiss()
                    }
                }
        }.show()
}

fun Activity.showNoticeDialog(

) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_notice)
        .backgroundDimDefault()
        .onClickToDismiss(R.id.pop_notice)
        .gravity(Gravity.CENTER)
        .onInitialize {

        }.show()
}

fun Activity.showSetSuccessDialog(

) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_set_success)
        .backgroundDimDefault()
        .onClickToDismiss(R.id.pop_notice)
        .gravity(Gravity.CENTER)
        .onInitialize {

        }.show()
}


fun Activity.showExitDialog(
    callback: () -> Unit = {},
) {
    DialogLayer(this)
        .cancelableOnTouchOutside(true)
        .contentView(R.layout.pop_exit)
        .backgroundDimDefault()
        .gravity(Gravity.CENTER)
        .onInitialize {
            PopExitBinding.bind(requireViewById<ConstraintLayout>(R.id.pop_exit))
                .apply {
                    tvCancel.onClick {

                        dismiss()
                    }
                    tvDone.onClick {
                        callback.invoke()
                        dismiss()
                    }
                }
        }.show()
}

