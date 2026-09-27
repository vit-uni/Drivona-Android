package com.drivona.speed.ui

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.car.app.connection.CarConnection
import androidx.car.app.model.Distance
import androidx.car.app.navigation.model.TravelEstimate
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback
import cn.com.heaton.blelibrary.ble.callback.BleMtuCallback
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.model.ScanRecord
import cn.com.heaton.blelibrary.ble.utils.ByteUtils
import com.blankj.utilcode.util.ActivityUtils
import com.drake.channel.receiveEvent
import com.drake.channel.receiveTag
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNet
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.drivona.speed.MApplication.Companion.placesClient
import com.drivona.speed.R
import com.drivona.speed.api.ApiLocation
import com.drivona.speed.api.BleRssiDevice
import com.drivona.speed.api.CMDMsg
import com.drivona.speed.api.CarRouteInfo
import com.drivona.speed.api.ConnectIDMsg
import com.drivona.speed.api.ConnectWithIDMsg
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.DeviceDianInfo
import com.drivona.speed.api.DeviceIDMsg
import com.drivona.speed.api.DeviceInfo
import com.drivona.speed.api.DeviceState
import com.drivona.speed.api.DeviceTypeMsg
import com.drivona.speed.api.DisConnectIDMsg
import com.drivona.speed.api.LocationDataCar
import com.drivona.speed.api.MiddlePoint
import com.drivona.speed.api.UserInfoManager
import com.drivona.speed.api.delEquipment
import com.drivona.speed.api.downloadFile
import com.drivona.speed.api.getAddressDefaultList
import com.drivona.speed.api.getEquipmentList
import com.drivona.speed.api.getUserinfo
import com.drivona.speed.api.reportNewCamera
import com.drivona.speed.api.setEquipmentInfo
import com.drivona.speed.api.updateCameraReport
import com.drivona.speed.car.NavigationService
import com.drivona.speed.car.model.DemoScripts
import com.drivona.speed.databinding.ActivityMainNewBinding
import com.drivona.speed.ext.showDeleteConfirmDialog
import com.drivona.speed.ext.showDeviceNightModeSuccessDialog
import com.drivona.speed.ext.showDeviceNoticeDialog
import com.drivona.speed.ext.showReconnectDialog
import com.drivona.speed.ui.activity.AddDeviceActivity
import com.drivona.speed.ui.activity.SearchMapActivity
import com.drivona.speed.ui.activity.help.HelpFirstActivity
import com.drivona.speed.ui.activity.login.LoginActivity
import com.drivona.speed.ui.activity.map.MapMainActivity
import com.drivona.speed.ui.adapter.ViewPageAdapter
import com.drivona.speed.ui.fragment.DeviceFragment
import com.drivona.speed.utils.HexUtils
import com.drivona.speed.utils.HexUtils.Companion.swapMacFirstTwoBytesNoColon
import com.drivona.speed.utils.LanguageUtil
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavInfo
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.ext.Tools
import com.lalifa.ext.Tools.Companion.Avatar_Path
import com.lalifa.ext.Tools.Companion.BASE_URL
import com.lalifa.ext.Tools.Companion.BLE_DengLiang
import com.lalifa.ext.Tools.Companion.BLE_DianLiang
import com.lalifa.ext.Tools.Companion.BLE_LianJie
import com.lalifa.ext.Tools.Companion.BLE_Read_ID
import com.lalifa.ext.Tools.Companion.BLE_SERVICE_ID
import com.lalifa.ext.Tools.Companion.BLE_Write_ID
import com.lalifa.ext.Tools.Companion.CurrentDeviceType
import com.lalifa.extension.dp
import com.lalifa.extension.getIntentSerializable
import com.lalifa.extension.globalUITask
import com.lalifa.extension.gone
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener
import com.lalifa.extension.pk
import com.lalifa.extension.start
import com.lalifa.extension.toJson
import com.lalifa.extension.visible
import com.lalifa.utils.SPUtil
import per.goweii.layer.dialog.DialogLayer
import java.util.Locale
import java.util.UUID


class MainNewActivity : BaseActivity<ActivityMainNewBinding>() {

    var currentLatLng: ApiLocation? = null
    var currentDeviceType = 2

    // A reference to the navigation service used to get location updates and routing.
    var mService: NavigationService? = null

    // Tracks the bound state of the navigation service.
    var mIsBound: Boolean = false
    var connectingDialog: DialogLayer? = null

    // Monitors the state of the connection to the navigation service.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(
                NavigationService.TAG,
                "In onServiceConnected() component:" + name
            )
            val binder = service as NavigationService.LocalBinder
            mService = binder.service
            mIsBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(
                NavigationService.TAG,
                "In onServiceDisconnected() component:" + name
            )
            mService = null
            mIsBound = false
        }
    }


    var deviceId = ""
    private val ble = Ble.getInstance<BleRssiDevice>()

    private var gattServices = arrayListOf<BluetoothGattService>()
    override fun getViewBinding() = ActivityMainNewBinding.inflate(layoutInflater)
    val deviceList = arrayListOf<DeviceData>()
    var currentDevice: DeviceData? = null

    val MSG_Heart = 1001
    var tagDeviceId = ""
    var currentItem = 0
    var pagerAdapter: ViewPageAdapter? = null
    val fragments = arrayListOf<DeviceFragment>()

    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_Heart -> {
                    Log.e("Song", "电量")
                    sendToBle(BLE_DianLiang)
                    if (ble.connectedDevices.size > 0) {
                        sendEmptyMessageDelayed(MSG_Heart, 20000)
                    }


                }
            }
        }
    }

    //    // 正确写法：参数是 Context，不能加 ?
    override fun attachBaseContext(newBase: Context) {
        Log.e("Song", "attachBaseContext---------------")
        super.attachBaseContext(wrapContext(newBase))
    }

    override fun initView() {
        val device = getIntentSerializable<DeviceInfo>("device")
        val uidList = getIntentSerializable<List<BluetoothGattService>>("gattServices")
        if (uidList?.isNotEmpty() == true) {
            gattServices.addAll(uidList)
        }
        binding.apply {
            tvReconnect.onClick {
                if (connectingDialog == null) {
                    connectingDialog = showReconnectDialog()
                }

                if (ble.isScanning) {
                    ble.stopScan()
                }
                if (connectingDialog!!.isShown) {
                    connectingDialog!!.dismiss()
                }
                connectingDialog!!.show()
                sendTag("Reconnect")
                globalUITask(100) {
                    tvReconnect.text = getString(R.string.reconnecting)
                    tvReconnect.isEnabled = false
                    sendEvent(DeviceIDMsg(currentDevice!!.uuid))
                }


            }

            ivBack.onClick {


                currentDevice?.apply {
                    start<MapMainActivity> {
                        Log.e("device", currentDevice!!.toJson())
                        putExtra("device", currentDevice)
                    }
                }

            }
            ivAvatar.onClick {

                start<UserSettingActivity> { }
            }
            ivLight.onClick {

                scopeNetLife {
                    currentDevice?.apply {
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
                    }

                }
            }
            ivNotice.onClick {
                currentDevice?.apply {
                    showDeviceNoticeDialog(this) {

                    }
                }
            }
            ivDelete.onClick {
                showDeleteConfirmDialog(
                    getString(R.string.delete_device),
                    getString(R.string.confirm_delete_device)
                ) {
                    deleteDevice()
                }


            }
            scopeNetLife {
                val data = getEquipmentList()
                data?.data?.let {
                    deviceList.clear()
                    deviceList.addAll(it)
                    currentDevice = deviceList[0]
                }

                fragments.clear()

                for (i in deviceList) {

                    if (device != null && i.uuid == HexUtils.swapMacFirstTwoBytesNoColon(device.address)) {
                        i.connect = true
                        currentDevice = i
                    }
                    fragments.add(DeviceFragment.newInstance(i, deviceList.size > 1))

                }
                pagerAdapter = ViewPageAdapter(supportFragmentManager, fragments)
                viewPager.adapter = pagerAdapter
                viewPager.offscreenPageLimit = fragments.size
                indicatorView.setPageSize(fragments.size)
                indicatorView.apply {
                    val density = context.resources.displayMetrics.density
                    fun Int.dp(): Int = (this * density + 0.5f).toInt()

                    setIndicatorGap(6.dp())
                    setIndicatorDrawable(
                        R.drawable.banner_indicator_nornal,
                        R.drawable.banner_indicator_focus
                    )
                    setIndicatorSize(
                        8.dp(),
                        8.dp(),
                        30.dp(),
                        8.dp()
                    )
                    setupWithViewPager(binding.viewPager)
                }
                viewPager.pageChangedListener({
                        position,
                        positionOffset,
                        positionOffsetPixels,
                    ->
                    indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }, {}, {
                    currentItem = it
                    currentDevice = deviceList[it]
                    setData(currentDevice!!)
                    indicatorView.onPageSelected(it)
                })


                if (device != null) {
                    for (i in deviceList) {
                        if (i.uuid == HexUtils.swapMacFirstTwoBytesNoColon(device.address)) {
                            i.connect = true
                            currentDevice = i

                        }
                    }
                    connectedDevice(device)

                } else {
                    ble.startScan(scanCallback)
                }
            }

        }

        receiveEvent<CMDMsg> {
            sendToBle(it.cmd.replace(" ", ""))
        }
        receiveEvent<ApiLocation> {
            currentLatLng = it
        }
        receiveEvent<DeviceDianInfo> {
            device?.apply {
                dian = it.dian.toString()
            }
        }
        receiveEvent<DeviceIDMsg> {
            tagDeviceId = it.uuid
            Log.e("Song", "tagDeviceId__${tagDeviceId}")

            if (tagDeviceId != "-1") {
                val devices = ble.getConnectedDevices()
                if (devices.isNotEmpty()) {
                    globalUITask(200) {
                        for (i in devices) {
                            ble.disconnect(i)
                        }

                    }

                } else {
                    ble.startScan(scanCallback)

                }
            }


        }
        receiveTag(
            "HttpLogout",
            "ReConnect",
            "Disconnected",
            "pre",
            "next",
            "delete",
            "MSG_Heart_remove",
            "MSG_Heart",
            "changeLanguage",
            Tools.Start_Navigation,
            Tools.Stop_Navigation,
            Tools.Car_APP_CHECK
        ) {
            when (it) {
                "changeLanguage" -> {
                    changeLanguage()
                }

                "Reconnect" -> {
                    for (i in fragments) {
                        i.disConnect()
                    }
                }

                "MSG_Heart_remove" -> {
                    mHandler.removeMessages(MSG_Heart)
                }

                "MSG_Heart" -> {
                    mHandler.sendEmptyMessageDelayed(MSG_Heart, 2000)
                }

                "pre" -> {
                    if (currentItem != 0) {
                        binding.viewPager.currentItem = currentItem - 1
                    }
                }

                "next" -> {
                    val total = binding.viewPager.childCount
                    if (currentItem < total - 1) {
                        binding.viewPager.currentItem = currentItem + 1
                    }
                }

                "delete" -> {

                    resetFragment()


                }

                "Disconnected" -> {
                    Log.e("Song", "收到____Disconnected")
                    val devices = ble.getConnectedDevices()
                    if (devices.isNotEmpty()) {
                        return@receiveTag
                    }
                    mHandler.removeMessages(MSG_Heart)
                    if (ble.isScanning) {
                        return@receiveTag
                    }

                    Log.e("Song", "收到____Disconnected__${tagDeviceId}")
                    if (tagDeviceId.isNotEmpty() && tagDeviceId != "-1" || deviceList.size > 0) {
                        Log.e("Song", "收到____Disconnected__重新扫描")
                        ble.startScan(scanCallback)
                    }


                }

                "HttpLogout" -> {
//                    ToastUtils.showShort("登录已失效，请重新登录")
                    SPUtil.set(Tools.IS_LOGIN, false)
                    SPUtil.set(Tools.Token, "")
                    scopeNetLife {
                        ActivityManager.getInstance().finishAllActivities()
                        val intent = Intent(this@MainNewActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        ActivityUtils.finishOtherActivities(LoginActivity::class.java)
                    }
                }

                Tools.Start_Navigation -> {
                    Log.e(NavigationService.TAG, "Start_Navigation")
                    if (mService != null) {
                        Log.e(NavigationService.TAG, "Start_Navigation__mService != null")
                        mService!!.startNavigation()
                    }
                }

                Tools.Stop_Navigation -> {
                    if (mService != null) {
                        mService!!.stopNavigation()


                    }
                }

                Tools.Car_APP_CHECK -> {
                    sendTag(Tools.Car_APP_CHECK_RESULT)
                }
            }
        }
        receiveEvent<ConnectWithIDMsg> {
            ble.connect(it.uuid, connectCallback)
        }
        receiveEvent<NavInfo> {
            Log.e(NavigationService.TAG, "收到导航信息")
            if (mService != null) {
                var trave: TravelEstimate? = null
                if (it.distanceToFinalDestinationMeters != null) {
                    trave = TravelEstimate.Builder(
                        Distance.create(
                            it.distanceToFinalDestinationMeters.toDouble(),
                            Distance.UNIT_METERS
                        ),
                        DemoScripts.getCurrentDateTimeZoneWithOffset(it.timeToFinalDestinationSeconds)
                    )
                        .setRemainingTimeSeconds(
                            it.timeToFinalDestinationSeconds.toLong()
                        )
                        .build()
                }

                mService!!.updateInfo(
                    trave,  /* isRerouting= */
                    it

                )

            }
        }
        receiveEvent<CarRouteInfo> {

        }
        receiveEvent<DeviceInfo> {
            deviceList.clear()
            Log.e("Fragment", "收到更新")
            val device = it
            scopeNetLife {
                val data = getEquipmentList()
                data?.data?.let {
                    deviceList.addAll(it)

                }
                fragments.clear()
                var index = 0
                for (i in deviceList.indices) {
                    if (device != null && deviceList[i].uuid == HexUtils.swapMacFirstTwoBytesNoColon(
                            device.address
                        )
                    ) {
                        deviceList[i].connect = true
                        index = i
                        currentDevice = deviceList[i]

                    }
                    fragments.add(DeviceFragment.newInstance(deviceList[i], deviceList.size > 1))
                }
                binding.apply {
                    pagerAdapter = ViewPageAdapter(supportFragmentManager, fragments)
                    viewPager.adapter = pagerAdapter
                    viewPager.offscreenPageLimit = fragments.size
                    indicatorView.apply {
                        val density = context.resources.displayMetrics.density
                        fun Int.dp(): Int = (this * density + 0.5f).toInt()
                        setIndicatorGap(6.dp())
                        setIndicatorDrawable(
                            R.drawable.banner_indicator_nornal,
                            R.drawable.banner_indicator_focus
                        )
                        setIndicatorSize(
                            8.dp(),
                            8.dp(),
                            30.dp(),
                            8.dp()
                        )
                        setupWithViewPager(binding.viewPager)
                    }
                    indicatorView.setPageSize(fragments.size)

                    viewPager.pageChangedListener({
                            position,
                            positionOffset,
                            positionOffsetPixels,
                        ->
                        indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    }, {}, {
                        currentItem = it
                        currentDevice = deviceList[it]
                        setData(currentDevice!!)
                        indicatorView.onPageSelected(it)
                    })
                    viewPager.currentItem = index
                    indicatorView.onPageSelected(index)

                }
            }
            val devices = ble.getConnectedDevices()
            if (devices.isNotEmpty()) {
                val device1 = devices[0]
                //连接成功后，设置通知
                setNoticListerner(device1)


            }

        }

        receiveEvent<ConnectIDMsg> {
            for (i in fragments.indices) {
                if (fragments[i].currentData?.uuid == swapMacFirstTwoBytesNoColon(it.bleAddress)) {
                    binding.viewPager.currentItem = i
                }
            }
            for (i in deviceList) {
                if (i.uuid == swapMacFirstTwoBytesNoColon(it.bleAddress)) {
                    i.connect = true
                }
            }
            if (currentDevice!!.uuid == swapMacFirstTwoBytesNoColon(it.bleAddress)) {
                currentDevice!!.connect = true
                setData(currentDevice!!)

            }
        }
        receiveEvent<DisConnectIDMsg> {
            Log.e("DisConnect", "______${it.toJson()}")
            Log.e(
                "DisConnect",
                "_swapMacFirstTwoBytesNoColon(it.uuid)_____${swapMacFirstTwoBytesNoColon(it.uuid)}"
            )
            for (i in deviceList) {
                Log.e("DisConnect", "__i.uuid____${i.uuid}")
                if (i.uuid == swapMacFirstTwoBytesNoColon(it.uuid)) {
                    i.connect = false
                }
            }
            if (currentDevice!!.uuid == swapMacFirstTwoBytesNoColon(it.uuid)) {
                currentDevice!!.connect = false
                setData(currentDevice!!)
            }


        }
        receiveEvent<LocationDataCar> {
            if (it.long.isNullOrEmpty()) {
                fetchPlaceDetails(it.place_id)
            } else {
                //终点
                var tagPoint: MiddlePoint =
                    MiddlePoint(it.place_id, it.lat.toDouble(), it.long.toDouble(), "", "")

                start<SearchMapActivity> {
                    putExtra("device", currentDevice)
                    putExtra("placeId", tagPoint)

//                putExtra("middlePointPlaceId", middlePointPlaceId)
//                            putExtra("orangePointPlaceId", originPointPlaceId)
                }
            }


        }

        CarConnection(this).type.observe(
            this,
            Observer { connectionState: Int -> this.onConnectionStateUpdate(connectionState) })
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, NavigationService::class.java),
            mServiceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onStop() {


        super.onStop()
    }

    private fun onConnectionStateUpdate(connectionState: Int) {
//        val message = if (connectionState > CarConnection.CONNECTION_TYPE_NOT_CONNECTED)
//            "Connected to a car head unit"
//        else
//            "Not Connected to a car head unit"
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private val connectCallback: BleConnectCallback<BleRssiDevice> =
        object : BleConnectCallback<BleRssiDevice>() {
            override fun onConnectionChanged(device: BleRssiDevice) {
                Log.e(
                    "Song",
                    "onConnectionChanged: " + device.connectionState + Thread.currentThread()
                        .name
                )
                if (device.isConnected) {

                    Log.e("Song", "已连接")
                    sendEvent(ConnectIDMsg(device.bleAddress))
                } else if (device.isConnecting) {

                    Log.e("Song", "连接中")
                } else if (device.isDisconnected) {
//                    toast("连接失败")


                    sendEvent(DeviceState(false))
                    Log.e("Song", "未连接")
                    sendTag("Disconnected")
                    mHandler.removeMessages(MSG_Heart)
                    sendEvent(DisConnectIDMsg(device.bleAddress))


                }
            }


            override fun onConnectFailed(device: BleRssiDevice, errorCode: Int) {
                super.onConnectFailed(device, errorCode)
                Log.e(
                    "Song",
                    "onConnectFailed: " + device.bleName + "___" + device.bleAddress
                )

            }

            override fun onConnectCancel(device: BleRssiDevice) {
                super.onConnectCancel(device)
                Log.e(
                    "Song",
                    "onConnectCancel: " + device.bleName
                )
            }

            override fun onServicesDiscovered(device: BleRssiDevice, gatt: BluetoothGatt) {
                super.onServicesDiscovered(device, gatt)
                tagDeviceId = ""
                gattServices.clear()
                gattServices.addAll(gatt.getServices())
                Log.e(
                    "Song",
                    "onServicesDiscovered: " + device.bleName
                )
                setNoticListerner(device)
                sendEvent(ConnectIDMsg(device.bleAddress))
                for (i in deviceList) {
                    if (i.uuid == HexUtils.swapMacFirstTwoBytesNoColon(device.bleAddress)) {
                        i.connect = true
                        currentDevice = i

                    }
                }

                ble.setMTU(device.bleAddress, 517, object : BleMtuCallback<BleRssiDevice>() {
                    override fun onMtuChanged(device: BleDevice, mtu: Int, status: Int) {
                        super.onMtuChanged(device, mtu, status)
                        Log.e("BLE", "onMtuChanged__${mtu} ___${status}")
                    }
                })


            }

            override fun onReady(device: BleRssiDevice) {
                super.onReady(device)

//                toast("链接成功")
                //连接成功后，设置通知

            }
        }
    var startScan = false
    private val scanCallback: BleScanCallback<BleRssiDevice> =
        object : BleScanCallback<BleRssiDevice>() {
            override fun onLeScan(device: BleRssiDevice, rssi: Int, scanRecord: ByteArray?) {
                synchronized(ble.locker) {
                    device.scanRecord = ScanRecord.parseFromBytes(scanRecord)
                    device.rssi = rssi


                    if (device.bleName != null && device.bleName.contains("SmarterDriving")) {

                        Log.e(
                            "SOng",
                            "扫描————${device.bleName}__${device.bleAddress}"
                        )
                        Log.e("SOng", "扫描——tagDeviceId——${tagDeviceId}")
                        if (tagDeviceId != "-1" && tagDeviceId.isNotEmpty()) {
                            if (tagDeviceId == HexUtils.swapMacFirstTwoBytesNoColon(device.bleAddress)) {
                                ble.connect(device, connectCallback)
                                ble.stopScan()
                            }
                        } else {
                            for (i in deviceList) {
                                Log.e(
                                    "SOng",
                                    "扫描——对比——${i.uuid}_———${device.bleName}_${device.bleAddress}————${
                                        HexUtils.swapMacFirstTwoBytesNoColon(
                                            device.bleAddress
                                        )
                                    }"
                                )
                                if (i.uuid == HexUtils.swapMacFirstTwoBytesNoColon(device.bleAddress)) {
                                    ble.connect(device, connectCallback)
                                    ble.stopScan()
                                    return
                                }
                            }

                        }


                    }


                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e(TAG, "onScanFailed: " + errorCode)
                if (connectingDialog?.isShown == true) {
                    connectingDialog!!.dismiss()
                }
            }

            override fun onStop() {
                super.onStop()
                startScan = false
                binding.tvReconnect.text = getString(R.string.reconnect)
                binding.tvReconnect.isEnabled = true
                if (connectingDialog?.isShown == true) {
                    connectingDialog!!.dismiss()
                }
            }

            override fun onStart() {
                super.onStart()
                startScan = true
            }
        }

    fun connectedDevice(device: DeviceInfo) {
        binding.apply {
            deviceId = device.address
        }

        val devices = ble.getConnectedDevices()
        if (devices.isNotEmpty()) {
            val device1 = devices[0]
            //连接成功后，设置通知
            setNoticListerner(device1)


        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e(
            "SOng",
            "onConfigurationChanged____________${newConfig.uiMode}____${Configuration.UI_MODE_NIGHT_YES}"
        )

    }

    /**
     * 连接成功发送默认设置
     */
    private fun sendDefaultSet(data: DeviceData) {
        data.apply {
            Log.e("Song", "同步设置命令")
            mHandler.sendEmptyMessageDelayed(MSG_Heart, 2000)
            sendToBle(BLE_LianJie)
            globalUITask(500) {
                sendToBle(BLE_DianLiang)
            }

            //是否是夜间模式
            if (is_night == 1) {
                //brightness
                sendEvent(CMDMsg("55AA040001050201"))
            } else {
                sendEvent(CMDMsg("55AA040001050200"))
            }

            //brightness
            val cmd = CMDMsg("55 AA 04 00 00 05 02 0${brightness}")
            sendEvent(cmd)
            //lighting
            val cmdLight = CMDMsg(
                BLE_DengLiang + HexUtils.decimalToLittleEndianHex(
                    lighting,
                    1
                )
            )
            sendEvent(cmdLight)

            //setEquipmentInfo(id, alarms = seekBar.progress)
            val cmdalarms = CMDMsg(
                "55 AA 05 00 01 02 0${type} 01 ${
                    HexUtils.decimalToLittleEndianHex(
                        sound,
                        1
                    )
                }"
            )
            sendEvent(cmdalarms)

            //currentDevice.speed_alarm_device
            sendEvent(CMDMsg("55 AA 05 00 00 04 0${type} FF 0${speed_alarm_device}"))
            //限速
            val alarm_threshold = CMDMsg(
                "55 AA 06 00 03 04 0${type} FF 02 ${
                    HexUtils.decimalToLittleEndianHex(
                        alarm_threshold,
                        1
                    )
                }"
            )
            sendEvent(alarm_threshold)

            //道路告警
            sendEvent(CMDMsg("55 AA 05 00 00 03 0${type} FF 0${road_alarm_device}"))

            if (alarm_which == 1) {
                val cmd = CMDMsg("55AA0500 0002 0${type} FF01")
                sendEvent(cmd)
            } else {
                val cmd = CMDMsg("55AA0500 0002 0${type} FF00")
                sendEvent(cmd)
            }
            if (speed_alarm_device == 1) {
                val cmd = CMDMsg("55 AA 05 00 00 04 0${type} FF 01")
                sendEvent(cmd)
            } else {
                val cmd = CMDMsg("55 AA 05 00 00 04 0${type} FF 00")
                sendEvent(cmd)
            }
            //移动摄像头
            val cmdYidong = CMDMsg("55AA050000020${type} 02 0${mobile_speed}")
            sendEvent(cmdYidong)
            //固定摄像头
            val cmdGuding = CMDMsg("55AA050000020${type} 01 0${fixed_speed}")
            sendEvent(cmdGuding)
            //交通灯
            val cmdDeng = CMDMsg("55AA050000020${type} 03 0${traffic_light_camera}")
            sendEvent(cmdDeng)
        }


    }

    override fun onDestroy() {
        Log.e(
            NavigationService.TAG,
            "In onStop(). bound__" + mIsBound
        )
        if (mIsBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mIsBound = false
            mService = null
        }

        val connectDevices = ble.connectedDevices
        if (connectDevices.isNotEmpty()) {
            for (i in connectDevices) {
                ble.disconnect(i)
                ble.cancelCallback(connectCallback)
            }
        }
        mHandler.removeMessages(MSG_Heart)
        super.onDestroy()
    }


    fun sendToBle(cmd: String) {
        if (cmd.contains(BLE_DianLiang) || cmd.contains(BLE_LianJie)) {
            val data = sendData(cmd)
            val devices = Ble.getInstance<BleDevice>().getConnectedDevices()
            if (devices.isNotEmpty()) {
                writeChar(devices[0], data)
            }
        } else {
            val data = sendData(cmd)
            val devices = Ble.getInstance<BleDevice>().getConnectedDevices()
            if (devices.isNotEmpty()) {
                writeCharType(devices[0], data)
            }
        }


    }

    private fun writeChar(
        bleDevice: BleDevice,
        bytes: ByteArray,
        serviceUuid: UUID = UUID.fromString(BLE_SERVICE_ID),
        characteristicUuid: UUID = UUID.fromString(BLE_Write_ID),
    ) {
        Ble.getInstance<BleDevice?>().writeByUuid(
            bleDevice,
            bytes,
            serviceUuid,
            characteristicUuid,
            object : BleWriteCallback<BleDevice?>() {
                override fun onWriteSuccess(
                    device: BleDevice?,
                    characteristic: BluetoothGattCharacteristic?,
                ) {
                    Log.e("CMD", "写入特征成功")
                }

                override fun onWriteFailed(device: BleDevice?, failedCode: Int) {
                    super.onWriteFailed(device, failedCode)
                    Log.e("CMD", "写入特征失败:" + failedCode)
                }
            })
    }

    private fun writeCharType(
        bleDevice: BleDevice,
        bytes: ByteArray,
        serviceUuid: UUID = UUID.fromString(BLE_SERVICE_ID),
        characteristicUuid: UUID = UUID.fromString(BLE_Write_ID),
    ) {
        Ble.getInstance<BleDevice?>().writeByUuid(
            bleDevice,
            bytes,
            serviceUuid,
            characteristicUuid,
            BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE,
            object : BleWriteCallback<BleDevice?>() {
                override fun onWriteSuccess(
                    device: BleDevice?,
                    characteristic: BluetoothGattCharacteristic?,
                ) {
                    Log.e("CMD", "写入特征成功")
                }

                override fun onWriteFailed(device: BleDevice?, failedCode: Int) {
                    super.onWriteFailed(device, failedCode)
                    Log.e("Song", "写入特征失败:" + failedCode)
                }
            })
    }

    private fun sendData(data: String): ByteArray {
        Log.e("CMD", "原始数据1__$data")
        val array = toByteArray(data)
        val length = jimiDataChecksum(array, array.size)
        Log.e("CMD", "原始crc2__$length")
        var crc = Integer.toHexString(length)
        if (crc.length == 1) {
            crc = "0${crc}"
        }
        val realData = data + crc + "5A"
        Log.e("CMD", "发送数据__$realData")
        val send_buff = toByteArray(realData)
        return send_buff
    }

    /**
     * 将String转化为byte[]数组
     *
     * @param arg
     * 需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private fun toByteArray(arg: String): ByteArray {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            val NewArray = CharArray(1000)
            val array = arg.toCharArray()
            var length = 0
            for (i in array.indices) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i]
                    length++
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            val EvenLength = if ((length % 2 == 0)) length else length + 1
            if (EvenLength != 0) {
                val data = IntArray(EvenLength)
                data[EvenLength - 1] = 0
                for (i in 0 until length) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i].code - '0'.code
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i].code - 'a'.code + 10
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i].code - 'A'.code + 10
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                val byteArray = ByteArray(EvenLength / 2)
                for (i in 0 until EvenLength / 2) {
                    byteArray[i] = (data[i * 2] * 16 + data[i * 2 + 1]).toByte()
                }
                return byteArray
            }
        }
        return byteArrayOf()
    }


    fun jimiDataChecksum(pBuf: ByteArray, len: Int): Int {
        var checksum = 0 // 使用int存储累加和，避免溢出问题
        for (i in 0 until len) {
            // 将字节转为无符号整数 (0-255)
            val unsignedByte = pBuf[i].toInt() and 0xFF
            checksum += unsignedByte
        }
        // 计算校验和: 256 - (checksum % 256)
        val result = 256 - (checksum % 256)


        // 确保结果在0-255范围内
        return result and 0xFF
    }

    override fun onResume() {
        super.onResume()
        scopeNetLife {
            val data = getUserinfo()
            data.data?.apply {
                binding.apply {
                    ivAvatar.load(avatar)
                }
                downloadFile(this@MainNewActivity, "${id}.png", BASE_URL + avatar) {
                    SPUtil.set(Avatar_Path, it.absolutePath)

                }
                UserInfoManager.save(this)


            }


        }
        scopeNetLife {
            val data = getAddressDefaultList()
            data?.data?.apply {
                SPUtil.set("defaultAddress", this.toJson())

            }
        }
        scopeNetLife {
            val data = getEquipmentList()
            data?.data?.let {
                for (i in it) {
                    if (i.uuid == currentDevice?.uuid) {

                        i.connect = currentDevice!!.connect
                        i.dian = currentDevice!!.dian
                        currentDevice = i
                    }
                }
            }
        }
    }

    var i = 0
    fun setNoticListerner(device: BleRssiDevice) {


        globalUITask(1000) {

            var serviceUuid: UUID = UUID.fromString(BLE_SERVICE_ID)
            var characteristicUuid: UUID = UUID.fromString(BLE_Read_ID)
            Ble.getInstance<BleRssiDevice>().enableNotifyByUuid(
                device,
                true,
                serviceUuid,
                characteristicUuid,
                object : BleNotifyCallback<BleRssiDevice>() {
                    override fun onChanged(
                        device: BleRssiDevice,
                        characteristic: BluetoothGattCharacteristic,
                    ) {
                        val msg = ByteUtils.toHexString(characteristic.value)
                        Log.e("onChanged", "onChanged==data:$msg")
                        if (msg != "55aa0300020100fb5a") {
                            Log.e("onChanged", "onChanged==data:$msg")

                        }
                        if (msg.pk().startsWith("55aa03000101")) {
                            val dianMsg = msg.pk().subSequence(12, 14).toString()
                            val dian = dianMsg.toInt(16).toString()
                            Log.e("SOng", "电量____$dian")
                            sendEvent(DeviceDianInfo(dianMsg.toInt(16)))
                        }

                        //设备类型0100
                        if (msg.pk().startsWith("55aa03000001")) {
                            Log.e("CurrentDeviceType", msg)
                            val deviceType = msg.pk().subSequence(12, 14).toString()
//                            val deviceType = "01"
                            Log.e("CurrentDeviceType", deviceType)
                            val isopen = SPUtil.getBoolean(Tools.isHaveOpen, false)
                            Log.e("CurrentDeviceType", isopen.toString())
                            SPUtil.set(CurrentDeviceType, deviceType)

                            if (deviceType == "02") {

                                currentDeviceType = 2
                            } else {
                                currentDeviceType = 1
                            }
                            setData(currentDevice!!)
                            sendEvent(DeviceTypeMsg(device.bleAddress, deviceType))

                            if (!isopen) {
                                start<HelpFirstActivity> {
                                    putExtra("type", deviceType)

                                }
                                SPUtil.set(Tools.isHaveOpen, true)
                            }

                        }
                        //双击左键
                        if (msg.pk().startsWith("55aa0b000802")) {
                            //事件唯一ID
                            //55 aa 0b 00 0802 02 00 00 00 00 00 00 00 02e85a
                            val eventId = msg.pk().subSequence(14, 26).toString()
                            val eventType = msg.pk().subSequence(26, 28).toString()
                            val eventState = msg.pk().subSequence(28, 30).toString()
                            var cameraType = "1"
                            when (eventType) {
                                "01", "02" -> cameraType = "1"
                                "03" -> cameraType = "2"
                                "04" -> cameraType = "6"
                            }
                            Log.e("Report", msg.pk())
                            sendDeviceData(eventId, cameraType, eventState)


                        }
                        //（55AA0B00 06 02 01 00 0000000000 00 02
                        if (msg.pk().startsWith("55aa0b000602")) {
                            //事件唯一ID
                            //55aa0b000802 01   00 00 00 00 00 00  00 03 e8 5a
                            val eventId = msg.pk().subSequence(14, 26).toString()
                            val eventType = msg.pk().subSequence(26, 28).toString()
                            val eventState = msg.pk().subSequence(28, 30).toString()
                            var cameraType = "107"
                            when (eventType) {
                                "01" -> cameraType = "107"
                                "02" -> cameraType = "110"

                            }
                            Log.e("Report", msg.pk())
                            sendDeviceData(eventId, cameraType, eventState)


                        }
                        //（55 AA 0B0007 03 01C8 24 24 5C 03 000101
                        if (msg.pk().startsWith("55aa0b000603")) {
                            //事件唯一ID
                            //55aa0b000802 01   00 00 00 00 00 00  00 03 e8 5a
                            val eventId = msg.pk().subSequence(14, 26).toString()
                            val eventType = msg.pk().subSequence(26, 28).toString()
                            val eventState = msg.pk().subSequence(28, 30).toString()
                            var cameraType = "20"
                            when (eventType) {
                                "01" -> cameraType = "20"
                                "02" -> cameraType = "21"
                                "03" -> cameraType = "22"
                                "04" -> cameraType = "23"
                                "05" -> cameraType = "24"
                                "06" -> cameraType = "25"
                                "07" -> cameraType = "26"
                                "08" -> cameraType = "29"

                            }
                            Log.e("Report", msg.pk())
                            sendDeviceData(eventId, cameraType, eventState)


                        }

                        if (msg.pk().startsWith("55aa03000501")) {
                            val dianMsg = msg.pk().subSequence(12, 14).toString()

//                            if (dianMsg.toInt(16) == 0) {
//                                toast("设置失败")
//                            } else {
//                                toast("设置成功")
//                            }

                        }

                    }

                    override fun onNotifyFailed(device: BleRssiDevice, failedCode: Int) {
                        super.onNotifyFailed(device, failedCode)
                        Log.e(
                            "Notify",
                            "onNotifyFailed: " + device.bleName + "失败原因______${failedCode}"
                        )

                    }

                    override fun onNotifySuccess(device: BleRssiDevice) {
                        super.onNotifySuccess(device)
                        Log.e(
                            "Notify",
                            "onNotifySuccess: " + device.bleName
                        )

                        mHandler.sendEmptyMessageDelayed(MSG_Heart, 2000)
                        sendToBle(BLE_LianJie)
                        globalUITask(500) {
                            sendToBle(BLE_DianLiang)
                            sendDefaultSet(currentDevice!!)
                        }


                    }
                })

        }

    }

    fun resetFragment() {
        scopeNetLife {
            val data = getEquipmentList()
            deviceList.clear()
            data?.data?.let {
                deviceList.addAll(it)
            }
            fragments.clear()
            if (deviceList.isEmpty) {
                ble.stopScan()
                start<AddDeviceActivity> { }
                finish()
                return@scopeNetLife
            }
            var index = 0
            currentDevice = deviceList[0]
            binding.apply {
                for (i in deviceList.indices) {

                    val devices = ble.getConnectedDevices()
                    if (devices.isNotEmpty()) {
                        val device1 = devices[0]
                        if (deviceList[i].uuid == HexUtils.swapMacFirstTwoBytesNoColon(device1.bleAddress)) {
                            deviceList[i].connect = true
                            currentDevice = deviceList[i]
                            index = i
                        }
                    }
                    fragments.add(DeviceFragment.newInstance(deviceList[i], deviceList.size > 1))

                }
                pagerAdapter = ViewPageAdapter(supportFragmentManager, fragments)
                viewPager.adapter = pagerAdapter
                viewPager.offscreenPageLimit = fragments.size
                indicatorView.apply {
                    val density = context.resources.displayMetrics.density
                    fun Int.dp(): Int = (this * density + 0.5f).toInt()

                    setIndicatorGap(6.dp())
                    setIndicatorDrawable(
                        R.drawable.banner_indicator_nornal,
                        R.drawable.banner_indicator_focus
                    )
                    setIndicatorSize(
                        8.dp(),
                        8.dp(),
                        30.dp(),
                        8.dp()
                    )
                    setupWithViewPager(binding.viewPager)
                }
                indicatorView.setPageSize(fragments.size)
                viewPager.pageChangedListener({
                        position,
                        positionOffset,
                        positionOffsetPixels,
                    ->
                    indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }, {}, {
                    currentItem = it
                    currentDevice = deviceList[it]
                    setData(currentDevice!!)
                    indicatorView.onPageSelected(it)
                })
                setData(currentDevice!!)
                viewPager.currentItem = index
                indicatorView.onPageSelected(index)


            }
        }
    }

    /**
     * 处理设备指令
     */
    fun sendDeviceData(eventId: String, cameraType: String, eventState: String) {
        Log.e("Report", "eventId__${eventId}__cameraType_${cameraType}__eventState_${eventState}")
        //新增
        when (eventState) {
            //不存在，已删除
            "00" -> {
                scopeNet {
                    currentLatLng?.apply {
                        updateCameraReport(
                            lat.toString(),
                            lng.toString(),
                            HexUtils.littleEndianHexToDecimal(eventId).toString(),
                            "104"
                        )
                    }

                }
            }

            "02" -> {
                scopeNet {
                    currentLatLng?.apply {
                        reportNewCamera(
                            lat.toString(),
                            lng.toString(),
                            cameraType
                        )
                    }

                }
            }
        }
    }

    private fun fetchPlaceDetails(placeId: String) {
        scopeDialog(BubbleDialog(this, "")) {

            val placeFields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            Log.e("Ning", "搜索地点——————${placeId}")
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                val latLng = place.latLng
                //终点
                var tagPoint: MiddlePoint = MiddlePoint(
                    placeId,
                    latLng.latitude, latLng.longitude, "", ""
                )

                start<SearchMapActivity> {
                    putExtra("device", currentDevice)
                    putExtra("placeId", tagPoint)

//                putExtra("middlePointPlaceId", middlePointPlaceId)
//                            putExtra("orangePointPlaceId", originPointPlaceId)
                }
            }.addOnFailureListener { exception ->

                Log.e("Song", "搜索地点异常____$exception")
                // 处理错误
                exception.printStackTrace()
            }

        }
    }

    fun setData(bean: DeviceData) {
        Log.e("DisConnect", "setData___${bean.uuid}__${bean.connect}__${currentDeviceType}")
        currentDevice = bean
        globalUITask {
            Log.e("DisConnect", "___${bean.uuid}__${bean.connect}__${currentDeviceType}")
            binding.apply {

                ivLight.isVisible = currentDeviceType != 1
                ivLightIcon.isVisible = currentDeviceType != 1
                clDevice02.isVisible = currentDeviceType != 1
                cl01.isVisible = currentDeviceType == 1
                tvReconnect.isVisible = !bean.connect
                binding.tvReconnect.text = getString(R.string.reconnect)
                binding.tvReconnect.isEnabled = true
                if (bean!!.connect) {
                    clSet.visible()

                } else {
                    clSet.gone()

                }
                seekbar0.progress = bean!!.brightness
                seekbar1.progress = bean!!.lighting
                seekbar11.progress = bean!!.lighting
                seekbar2.progress = bean!!.sound
                tvSeekbar0.text = bean!!.brightness.toString()
                tvSeekbar1.text = bean!!.lighting.toString()
                tvSeekbar11.text = bean!!.lighting.toString()
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
                seekbar11.setOnTouchListener { v, event ->
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
        }
    }

    fun deleteDevice() {
        scopeDialog(BubbleDialog(this, "")) {
            delEquipment(currentDevice!!.id)
            sendTag("delete")
            val ble = Ble.getInstance<BleRssiDevice>()
            val connectDevices = ble.connectedDevices

            if (connectDevices.isNotEmpty() && currentDevice!!.connect) {
                for (i in connectDevices) {
                    ble.disconnect(i)
                }
            }

        }

    }

    // 给 Activity 获取最新语言的 Context
    fun wrapContext(context: Context): Context {
        val name = SPUtil.get("language")
        val locale = LanguageUtil.getLocaleByLanguage(name)


        val resources = context.resources
        val config = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }

        Locale.setDefault(locale)
        return context.createConfigurationContext(config)
    }

    fun changeLanguage() {
//        recreate()
        globalUITask {
            binding.apply {
                tvSet0.text = getString(R.string.screen_brightness)
                tvSet0Desc.text = getString(R.string.adjust_display_brightness)
                tvSet1.text = getString(R.string.warning_light_brightness)
                tvSet1Desc.text = getString(R.string.adjust_warning_light_brightness)
                tvSet2.text = getString(R.string.alert_volume)
                tvSet2Desc.text = getString(R.string.adjust_alert_volume)
                tvSet11.text = getString(R.string.warning_light_brightness)
                tvSet11Desc.text = getString(R.string.adjust_warning_light_brightness)
            }
        }
    }
}