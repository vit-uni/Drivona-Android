package com.drivona.speed.ui.activity

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.model.ScanRecord
import com.drake.brv.utils.bindingAdapter
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.ext.Tools.Companion.DeviceID
import com.lalifa.ext.Tools.Companion.DeviceNAME
import com.lalifa.extension.globalUITask
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.visible
import com.drivona.speed.R
import com.drivona.speed.api.BleRssiDevice
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.DeviceIDMsg
import com.drivona.speed.api.DeviceInfo
import com.drivona.speed.api.addEquipment
import com.drivona.speed.api.checkEquipment
import com.drivona.speed.api.getEquipmentList
import com.drivona.speed.databinding.ActivitySearchDeviceBinding
import com.drivona.speed.ui.MainNewActivity
import com.drivona.speed.ui.adapter.SearchDeviceAdapter
import com.drivona.speed.ui.adapter.newDeviceList
import com.drivona.speed.utils.HexUtils
import com.drivona.speed.utils.HexUtils.Companion.parseManufacturerReverse
import com.lalifa.utils.SPUtil


class SearchDeviceActivity : BaseActivity<ActivitySearchDeviceBinding>() {


    override fun getViewBinding() = ActivitySearchDeviceBinding.inflate(layoutInflater)
    var mDevices = arrayListOf<BleRssiDevice>()
    var adapter: SearchDeviceAdapter? = null
    var address: String = ""
    val deviceList = arrayListOf<DeviceData>()
    private val ble: Ble<BleRssiDevice> = Ble.getInstance<BleRssiDevice?>()
    var currentPosition = 0

    private val scanCallback: BleScanCallback<BleRssiDevice> =
        object : BleScanCallback<BleRssiDevice>() {
            override fun onLeScan(device: BleRssiDevice, rssi: Int, scanRecord: ByteArray?) {
                synchronized(ble.getLocker()) {

                    device.setScanRecord(ScanRecord.parseFromBytes(scanRecord))
                    device.setRssi(rssi)
                    if (device.bleName != null && device.bleName.contains("SmarterDriving")) {
                        binding.tvEmpty.gone()
                        if (!mDevices.contains(device)) {
                            mDevices.add(device)
                            binding.ivPre.isVisible = mDevices.size > 1
                            binding.ivNext.isVisible = mDevices.size > 1
                            globalUITask {
                                binding.recyclerView.bindingAdapter.notifyDataSetChanged()
                            }
                        }

                    }


                }
            }

            override fun onStart() {
                super.onStart()

            }

            override fun onStop() {
                super.onStop()
                if (mDevices.isEmpty()) {
                    binding.clEmpty.visible()
                }

            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e(TAG, "onScanFailed: " + errorCode)
            }
        }


    var device1 = DeviceInfo("", "")

    override fun initView() {

        binding.apply {
            scopeNetLife {
                val data = getEquipmentList()
                data?.data?.let {
                    deviceList.addAll(it)

                }
            }
            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(recyclerView);
            recyclerView.setLayoutManager(
                LinearLayoutManager(
                    this@SearchDeviceActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            recyclerView.newDeviceList().apply {
                onFastClick(R.id.tv_connect) {

                    scopeNetLife {
                        var isBind = false
                        for (i in deviceList) {
                            if (i.uuid == HexUtils.swapMacFirstTwoBytesNoColon(getModel<BleRssiDevice>().bleAddress)) {
                                isBind = true
                            }
                        }

                        if (!isBind) {
                            checkEquipment(HexUtils.swapMacFirstTwoBytesNoColon(getModel<BleRssiDevice>().bleAddress))
                        }
                        connectDevice(getModel<BleRssiDevice>())
                    }

                }
                models = mDevices
            }
            //轮播图

            ivPre.onClick {
                if (currentPosition < mDevices.size - 1) {
                    recyclerView.smoothScrollToPosition(currentPosition++)
                }

            }
            ivNext.onClick {
                if (currentPosition > 0 && mDevices.isNotEmpty()) {
                    recyclerView.smoothScrollToPosition(currentPosition--)
                }


            }
            tvFinish.onClick {
                start<MainNewActivity> {
                    putExtra("device", device1)
                }
                sendTag("finishActivity")

                finish()
            }
            tvReconnect.onClick {
                clFail.gone()
            }
            ivBack1.onClick {
                finish()
            }
            tvReconnect2.onClick {
                clEmpty.gone()
                //扫描设备
                ble.startScan(scanCallback)
            }

        }


        //扫描设备
        ble.startScan(scanCallback)


    }


    override fun onClick() {
        binding.apply {
            ivBack.onClick { finish() }

        }
    }

    private val connectCallback: BleConnectCallback<BleRssiDevice> =
        object : BleConnectCallback<BleRssiDevice>() {
            override fun onConnectionChanged(device: BleRssiDevice) {
                Log.e(
                    "Song",
                    "onConnectionChanged: " + device.getConnectionState() + Thread.currentThread()
                        .getName()
                )
                if (device.isConnected()) {
                    Log.e("Song", "已连接")


                } else if (device.isConnecting()) {

                    Log.e("Song", "连接中")
                } else if (device.isDisconnected()) {

//                    Log.e("Song", "未连接")
//                    sendTag("Disconnected")
                }
            }

            override fun onConnectFailed(device: BleRssiDevice, errorCode: Int) {
                super.onConnectFailed(device, errorCode)
                Log.e("Song", "onConnectFailed___$errorCode")
                binding.clFail.visible()
            }

            override fun onConnectCancel(device: BleRssiDevice) {
                super.onConnectCancel(device)
                binding.clFail.visible()
                Log.e(
                    "Song",
                    "onConnectCancel: " + device.getBleName()
                )
            }

            override fun onServicesDiscovered(device: BleRssiDevice, gatt: BluetoothGatt) {
                super.onServicesDiscovered(device, gatt)
                Log.e(
                    "Song",
                    "onServicesDiscovered: " + device.getBleName()
                )
                ble.setMTU(device.bleAddress, 517, object : BleMtuCallback<BleRssiDevice>() {
                    override fun onMtuChanged(device: BleDevice, mtu: Int, status: Int) {
                        super.onMtuChanged(device, mtu, status)
                        Log.e("BLE", "onMtuChanged__${mtu} ___${status}")
                    }
                })

                SPUtil.set(DeviceNAME, device.bleName)
                SPUtil.set(DeviceID, device.bleAddress)



                scopeNetLife {
                    var isBind = false
                    var uid = ""

                    Log.e("Song", "uid__" + uid)
                    for (i in deviceList) {
                        if (i.uuid == HexUtils.swapMacFirstTwoBytesNoColon(device.bleAddress)) {
                            isBind = true
                        }

                    }
                    Log.e("Song", "uid__" + uid)
                    if (!isBind) {
                        addEquipment(
                            device.bleName,
                            HexUtils.swapMacFirstTwoBytesNoColon(device.bleAddress)
                        )
                    }

                    SPUtil.set(Tools.isHaveOpen, false)

                    device1 = DeviceInfo(device.bleAddress, device.bleName)
                    sendEvent(device1)
                    binding.clSuccess.visible()

                }

//                gattServices.addAll(gatt.services)
//                toast("链接成功")
//                SPUtil.set(DeviceNAME, device.bleName)
//                SPUtil.set(DeviceID, device.bleAddress)
//                val device1 = DeviceInfo(device.bleAddress, device.bleName)
//                sendEvent(device1)
//                scopeNetLife {
//                    addEquipment(device.bleName, device.bleAddress)
//                    start<MainNewActivity> {
//                        putExtra("device", device1)
//                        putExtra("gattServices", gattServices)
//                    }
//                }
            }

            override fun onReady(device: BleRssiDevice) {
                super.onReady(device)
                Log.e("Song", "_onReady__${device.bleName}")
                //连接成功后，设置通知

            }
        }

    private fun connectDevice(device: BleRssiDevice) {

        val ble = Ble.getInstance<BleRssiDevice>()
        val devices = ble.getConnectedDevices()
        if (devices.isNotEmpty()) {
            sendEvent(DeviceIDMsg("-1"))
            globalUITask(200) {
                for (i in devices) {
                    ble.disconnect(i)
                }

            }
            globalUITask(300) {
                ble.connect(device, connectCallback)
            }
        } else {
            ble.connect(device, connectCallback)
        }


    }


}