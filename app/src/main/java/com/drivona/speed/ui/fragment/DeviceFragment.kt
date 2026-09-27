package com.drivona.speed.ui.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import cn.com.heaton.blelibrary.ble.Ble
import com.drake.channel.receiveEvent
import com.drake.channel.receiveTag
import com.drake.channel.sendTag
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNet
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.drivona.speed.R
import com.drivona.speed.api.BleRssiDevice
import com.drivona.speed.api.ConnectIDMsg
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.DeviceDianInfo
import com.drivona.speed.api.DeviceTypeMsg
import com.drivona.speed.api.DisConnectIDMsg
import com.drivona.speed.api.delEquipment
import com.drivona.speed.api.getEquipmentList
import com.drivona.speed.api.setEquipmentInfo
import com.drivona.speed.databinding.FragmentDeviceBinding
import com.drivona.speed.ext.showChangeDeviceNameDialog
import com.drivona.speed.ext.showDeleteConfirmDialog
import com.drivona.speed.ui.activity.SearchDeviceActivity
import com.drivona.speed.utils.HexUtils
import com.drivona.speed.utils.HexUtils.Companion.swapMacFirstTwoBytesNoColon
import com.lalifa.base.BaseFragment
import com.lalifa.extension.globalUITask
import com.lalifa.extension.onClick
import com.lalifa.extension.pk
import com.lalifa.extension.start


class DeviceFragment : BaseFragment<FragmentDeviceBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentDeviceBinding.inflate(layoutInflater)

    lateinit var bean: DeviceData
    var isShow: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(bean: DeviceData, isShow: Boolean) = DeviceFragment().apply {
            arguments = Bundle().apply {
                putSerializable("data", bean)
                putBoolean("isShow", isShow)
            }
        }
    }

    var isFirst = true
    private var gattServices = arrayListOf<BluetoothGattService>()

    @SuppressLint("SetTextI18n")
    override fun initView() {

        bean = arguments?.getSerializable("data") as DeviceData
        isShow = arguments?.getBoolean("isShow") as Boolean
        setData(bean)
        binding.ivNext.isVisible = isShow
        binding.ivPre.isVisible = isShow
        receiveTag("Reconnect") {
            when (it) {
                "Reconnect" -> {
                    bean.connect = false
                    setData(bean)
                }
            }
        }
        receiveEvent<DisConnectIDMsg> {
            if (bean.uuid == swapMacFirstTwoBytesNoColon(it.uuid)) {
                bean.connect = false
                setData(bean)
            }

        }
        receiveEvent<ConnectIDMsg> {
            if (bean.uuid == HexUtils.swapMacFirstTwoBytesNoColon(it.bleAddress)) {
                bean.connect = true
                setData(bean)

            }

        }
        receiveEvent<DeviceDianInfo> {
            binding.apply {

                if (bean.connect) {
                    bean.dian = it.dian.toString()
                    tvDianDesc.text = bean.dian.pk("100%")
                    ivDianBg.isSelected = it.dian < 20
                    tvDianDesc.isSelected = it.dian < 20
                    if (it.dian >= 80) {
                        ivDianSmall.setBackgroundResource(R.drawable.ic_dian_100)
                    } else if (it.dian >= 60 && it.dian < 80) {
                        ivDianSmall.setBackgroundResource(R.drawable.ic_dian_80)
                    } else if (it.dian >= 40 && it.dian < 60) {
                        ivDianSmall.setBackgroundResource(R.drawable.ic_dian_60)
                    } else if (it.dian >= 20 && it.dian < 40) {
                        ivDianSmall.setBackgroundResource(R.drawable.ic_dian_40)
                    } else {
                        ivDianSmall.setBackgroundResource(R.drawable.ic_dian_20)
                    }
                }


            }
        }
        receiveEvent<DeviceTypeMsg> {
            binding.apply {

                if (bean.uuid == swapMacFirstTwoBytesNoColon(it.bleAddress)) {
                    if (it.deviceType == "02") {
                        bean.type = 2
                    } else {
                        bean.type = 1
                    }
                    setData(bean)
                    scopeNet {
                        setEquipmentInfo(bean.id, type = bean.type)
                    }
                }


            }
        }
    }

    var currentData: DeviceData? = null
    fun setData(bean: DeviceData) {
        Log.e("CurrentDeviceType", "setData___${bean.uuid}__${bean.connect}__${bean.type}")
        currentData = bean
        globalUITask {
            Log.e("CurrentDeviceType", "___${bean.uuid}__${bean.connect}__${bean.type}")
            binding.apply {
                tvDeviceName.text = bean.title
                ivDeleteNew.isVisible = !bean.connect
                if (bean.type == 1) {
                    tvDeviceNameDesc.text = "DRIVONA-S1"
                    imageView.setBackgroundResource(R.drawable.selector_device_icon01)
                } else if (bean.type == 2) {
                    tvDeviceNameDesc.text = "DRIVONA-S2"
                    imageView.setBackgroundResource(R.drawable.selector_device_icon)
                } else {
                    tvDeviceNameDesc.text = "DRIVONA"
                }

                imageView.isSelected = bean.connect
                ivDianBg.isVisible = bean.connect
                tvDianDesc.isVisible = bean.connect
                ivDianSmall.isVisible = bean.connect
                tvDianDesc.text = bean.dian.pk("100%")
                Log.e("Song", "更新数据——————${bean!!.connect}")


            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFirst) {
            scopeNetLife {
                var isconnect = bean.connect
                var dianLiang = bean.dian
                val data = getEquipmentList()
                data?.data?.let {
                    for (i in it) {
                        if (i.uuid == bean.uuid) {
                            bean = i
                            bean.connect = isconnect
                            bean.dian = dianLiang.pk()
                            setData(bean)
                        }
                    }
                }
            }
        }
        isFirst = false
    }


    override fun onClick() {
        super.onClick()
        binding.apply {
            tvDeviceName.onClick {
                requireActivity().showChangeDeviceNameDialog {
                    scopeNetLife {
                        setEquipmentInfo(bean.id, it)
                        bean.title = it
                        tvDeviceName.text = it
                    }
                }
            }



            ivAdd.onClick {
                requireActivity().start<SearchDeviceActivity> { }
            }
            ivPre.onClick {
                sendTag("pre")
            }
            ivNext.onClick {
                sendTag("next")
            }
            ivDeleteNew.onClick {
                requireActivity().showDeleteConfirmDialog(
                    getString(R.string.delete_device),
                    getString(R.string.confirm_delete_device)
                ) {
                    deleteDevice()
                }


            }


        }
    }

    fun disConnect() {
        bean.connect = false
        setData(bean)
    }

    var i = 0

    fun deleteDevice() {
        scopeDialog(BubbleDialog(requireContext(), "")) {
            delEquipment(bean.id)
            sendTag("delete")
            val ble = Ble.getInstance<BleRssiDevice>()
            val connectDevices = ble.connectedDevices

            if (connectDevices.isNotEmpty() && bean.connect) {
                for (i in connectDevices) {
                    ble.disconnect(i)
                }
            }

        }

    }

//    fun getCurrentData(): DeviceData {
//        return currentData
//    }

}