package com.drivona.speed.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import cn.com.heaton.blelibrary.ble.Ble
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.blankj.utilcode.util.SPUtils
import com.drake.channel.receiveEvent
import com.drake.channel.receiveTag
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.drake.net.utils.scopeNet
import com.drake.net.utils.scopeNetLife
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.navigation.CustomRoutesOptions
import com.google.android.libraries.navigation.ForceNightMode
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.NavigationApi.NavigatorListener
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.Navigator.RouteChangedListener
import com.google.android.libraries.navigation.Navigator.RouteStatus
import com.google.android.libraries.navigation.RoadSnappedLocationProvider
import com.google.android.libraries.navigation.SimulationOptions
import com.google.android.libraries.navigation.Waypoint
import com.google.android.libraries.places.api.net.PlacesClient
import com.lalifa.base.BaseActivity
import com.lalifa.extension.getIntentSerializable
import com.lalifa.extension.getIntentString
import com.lalifa.extension.globalUITask
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.toJson
import com.lalifa.extension.toast
import com.drivona.speed.BuildConfig
import com.drivona.speed.MApplication
import com.drivona.speed.R
import com.drivona.speed.api.ApiLocation
import com.drivona.speed.api.BleRssiDevice
import com.drivona.speed.api.CMDMsg
import com.drivona.speed.api.CameraDeviceData
import com.drivona.speed.api.CarRouteInfo
import com.drivona.speed.api.ConnectIDMsg
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.DeviceIDMsg
import com.drivona.speed.api.DeviceState
import com.drivona.speed.api.DistanceData
import com.drivona.speed.api.IsChao
import com.drivona.speed.api.MiddlePoint
import com.drivona.speed.api.UserInfoManager
import com.drivona.speed.api.getCamera
import com.drivona.speed.api.getEquipmentList
import com.drivona.speed.api.getMapEvent
import com.drivona.speed.car.NavigationService
import com.drivona.speed.databinding.ActivitySearchMapBinding
import com.drivona.speed.ext.showArrivedDialog
import com.drivona.speed.ext.showDeviceSetDialog
import com.drivona.speed.ext.showExitDialog
import com.drivona.speed.ui.activity.login.FeedBackActivity
import com.drivona.speed.ui.fragment.DeviceFragment
import com.drivona.speed.utils.HexUtils
import com.drivona.speed.utils.HexUtils.Companion.decimalToLittleEndianHex
import com.drivona.speed.utils.InitializedMapScope
import com.drivona.speed.utils.InitializedNavRunnable
import com.drivona.speed.utils.InitializedNavScope
import com.drivona.speed.utils.LocationPermissionUtil
import com.drivona.speed.utils.map.CustomizationPanelsDelegate
import com.drivona.speed.utils.map.NavInfoBottomDisplayFragment
import com.drivona.speed.utils.map.NavInfoBottomDisplayFragment.DistanceUnit
import com.drivona.speed.utils.map.NavInfoReceivingService
import com.lalifa.ext.Tools
import com.lalifa.ext.Tools.Companion.Car_Exit
import com.lalifa.extension.globalTask
import per.goweii.layer.dialog.DialogLayer
import kotlin.collections.get
import kotlin.compareTo


class SearchMapActivity : BaseActivity<ActivitySearchMapBinding>(),
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient
    private lateinit var queue: RequestQueue
    override fun getViewBinding() = ActivitySearchMapBinding.inflate(layoutInflater)
    var route = ""
    var tagLat = 0.0
    var tagLng = 0.0
    var googleMap: GoogleMap? = null
    var currentLatLng: LatLng? = null
    var isNaving = false
    var isStart = false
    val middlePointPlaceId = arrayListOf<MiddlePoint>()
    val orangePointPlaceId = arrayListOf<MiddlePoint>()
    private val ble = Ble.getInstance<BleRssiDevice>()
    var navInfoDisplayFragment: Fragment? = null
    var navInfoDisplayBottom: NavInfoBottomDisplayFragment? = null
    var mRoadSnappedLocationProvider: RoadSnappedLocationProvider? = null
    var currentSpeedKmh = 0f
    var distanceData = 0
    var streetName = ""
    var currentApiLocation = ApiLocation(0.0, 0.0)
    var speedType = "01"
    var distanceType = "01"
    var isNight = false

    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

        }
    }
    val speedTypeInfo = UserInfoManager.get()!!.speed
    var danwei = UserInfoManager.get()!!.distance
    var isMetric: Boolean = danwei != "mi"

    val locationListener = object : RoadSnappedLocationProvider.LocationListener {
        override fun onLocationChanged(location: Location) {
            location.also {
                currentLatLng = LatLng(
                    it.latitude,
                    it.longitude
                )

                currentApiLocation.lat = it.latitude
                currentApiLocation.lng = it.longitude
                sendEvent(currentApiLocation)
                if (!isStart) {
                    return
                }
                val navInfo = NavInfoReceivingService.navInfoLiveData.value
                navInfo?.apply {

                    sendEvent(navInfo)
                    Log.e(NavigationService.TAG, navInfo.toJson())
                    navInfo.distanceToFinalDestinationMeters?.apply {

                        sendEvent(
                            CarRouteInfo(
                                navInfo.distanceToFinalDestinationMeters.toDouble(),
                                navInfo.timeToFinalDestinationSeconds!!
                            )
                        )
                    }

                }

//        getCameraDevice(it.longitude.toString(), it.latitude.toString(), it.bearing.toString())
                // lat/lng: (48.1417978,11.579486)
                currentSpeedKmh = it.speed * 3.6f
                var realSpeed = 0
                val speed = it.speed


                if (speedTypeInfo == "mph") {
                    realSpeed = (speed * 2.236936).toInt()
                } else {
                    realSpeed = (speed * 3.6).toInt()
                }

                if (realSpeed < 0) {
                    realSpeed = 0
                }
                //向设备发送速度
                val cmd =
                    CMDMsg(
                        "55 AA 06 00 05 04 0${device?.type} ${
                            decimalToLittleEndianHex(
                                realSpeed,
                                2
                            )
                        } ${speedType}"
                    )
                sendEvent(cmd)

                if (isNaving) {
                    navInfoDisplayBottom?.setSpeed(realSpeed)
                }

                Log.e("Song", "LocationListener__" + it.toString())

                if (speedType == "02") {
                    navInfoDisplayBottom?.setSpeedType("mph")
                } else {
                    navInfoDisplayBottom?.setSpeedType("km/h")
                }

                //
                withNavigatorAsync {

                    val routes = navigator?.routeSegments
                    if (routes?.isNotEmpty() == true) {
                        routes?.get(0)?.latLngs?.apply {
                            if (this.size > 0) {
                                Log.e("Point", "集合大小__${this.size}")
                                getCameraDevice(
                                    it.longitude.toString(),
                                    it.latitude.toString(),
                                    it.bearing.toString(),
                                    route = this.take(50).toJson()
                                )


                            }
                        }

                    }


                }

            }

        }

        override fun onRawLocationUpdate(p0: Location?) {


        }


//                        Log.e("Song", "LocationListener_extras_" + it.extras?.toString())
    }

    var device: DeviceData? = null
    var cameraId = 0
    var cameraIdType = ""
    var eventId = 0
    var eventIdType = ""
    private val restoreFollowRunnable =
        Runnable @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            Log.e("map", "_____followMyLocation__________TILTED____")
            //拖拽结束静止 1.5s 之后自动重新开启跟随
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            if (binding.ivLocation.isSelected) {
                googleMap?.followMyLocation(GoogleMap.CameraPerspective.TOP_DOWN_NORTH_UP)
            } else {
                googleMap?.followMyLocation(GoogleMap.CameraPerspective.TILTED)
            }


        }

    @SuppressLint("MissingPermission")
    override fun initView() {
        device = getIntentSerializable<DeviceData>("device")
        getCurrentDevice()

        Log.e("Speed", "____${UserInfoManager.get()?.toJson()}")
        if (speedTypeInfo == "mph") {
            speedType = "02"
        }
        if (danwei == "mi") {
            distanceType = "02"
        }
        Log.e("Speed", "____${speedType}")
        binding.apply {


            ivCamera.isSelected = true
            navView.onCreate(savedInstanceStat)

            navView.setHeaderEnabled(false)
            navView.setEtaCardEnabled(false)
            navView.setRecenterButtonEnabled(false)
            navView.setReportIncidentButtonEnabled(false)
            navView.setTrafficIncidentCardsEnabled(false)




            queue = Volley.newRequestQueue(this@SearchMapActivity)
            val tag = getIntentSerializable<MiddlePoint>("placeId")
            tagLat = tag!!.latitude
            tagLng = tag!!.longitude
            route = getIntentString("route")
            val list = getIntentSerializable<List<MiddlePoint>>("middlePointPlaceId")
            if (!list.isNullOrEmpty()) {
                middlePointPlaceId.addAll(list)
            }
//            val orangeId = getIntentSerializable<MiddlePoint>("orangePointPlaceId")
//            if (orangeId != null) {
//                orangePointPlaceId.add(orangeId)
//            }
            placesClient = MApplication.placesClient
            registerNavigationListeners()

            initializeNavigationApi()



            ivLocation.onClick {
                ivLocation.isSelected = !ivLocation.isSelected
                if (ivLocation.isSelected) {
                    googleMap?.followMyLocation(GoogleMap.CameraPerspective.TOP_DOWN_NORTH_UP)
                } else {
                    googleMap?.followMyLocation(GoogleMap.CameraPerspective.TILTED)
                }
            }

            ivCamera.onClick {
                ivCamera.isSelected = !ivCamera.isSelected
                for (i in eventMap) {
                    i.value.isVisible = ivCamera.isSelected
                }
                for (i in cameraMap) {
                    i.value.isVisible = ivCamera.isSelected
                }
            }
            ivExit.onClick {
                showExitDialog {
                    exitNavigation()
                }


            }
            tvLocationState.onClick {
                LocationPermissionUtil.jumpToAppPermissionSetting(this@SearchMapActivity)
            }
            // Ensure the screen stays on during nav.
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            // Register some example listeners for navigation events.

//            geocodePlaceAndDisplay(placeId)
            withMapAsync {
                googleMap = map
                map.isMyLocationEnabled = true
                map.uiSettings.isCompassEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false

                googleMap?.setOnCameraMoveStartedListener { reason ->
                    Log.e("CAR", "--move----1-----------${reason}")
                    if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                        //用户手动拖拽，标记跟随关闭
//                        autoFollow = false

                        mHandler.removeCallbacks(restoreFollowRunnable)
                        mHandler.postDelayed(restoreFollowRunnable, 5000) // 8秒后自动恢复

                    }

                }




                for (i in middlePointPlaceId) {
                    // 添加标记
                    val marker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(i.latitude, i.longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_map_middle_point)))
                    )
                }
                globalUITask(3000) {
                    if (route.isNotEmpty()) {
                        navigateToPlaceByRoute(route)
                    } else {
                        navigateToPlace()
                    }

                }
            }

        }
//        getCameraDevice()
        receiveTag(Car_Exit) {
            when (it) {
                Car_Exit -> {
                    exitNavigation()
                }

            }

        }
        var isPlayed = false
        receiveEvent<IsChao> {
            if (isPlayed) {
                if (!it.chao) {
                    isPlayed = false
                }
            } else {
                if (it.chao) {
                    if (device?.speed_alarm_phone != 0) {
                        MApplication.get().playChaosu()
                    }
                    isPlayed = true
                }
            }

        }
        receiveEvent<DeviceState> {
            Log.e("DeviceState", "-----------------------${it.toJson()}")
            Log.e("DeviceState", "-----------------isStart------${isStart}")
            binding.tvDeviceState.isVisible = !it.connect
            binding.ivDevice.isSelected = !it.connect

        }
        receiveEvent<DistanceData> {
            distanceData = it.distance
            streetName = it.street
        }
        receiveEvent<ConnectIDMsg> {
            getCurrentDevice()
            if (isStart) {
                //开始导航
                globalTask(2500) {
                    Log.e("DeviceState", "---------------发")
                    sendEvent(CMDMsg("55 AA 03 00 01 06 0${device?.type}"))
                }

            }

        }
    }


    override fun onClick() {
        binding.apply {
            ivDevice.onClick {
                if (ivDevice.isSelected) {
                    device?.apply {
                        sendEvent(DeviceIDMsg(uuid))
                    }

                } else {
                    device?.apply {
                        showDeviceSetDialog(this) {

                        }
                    }

                }
            }
            tvDeviceState.onClick {
                device?.apply {
                    sendEvent(DeviceIDMsg(uuid))
                }


            }

        }
    }

    override fun onMyLocationButtonClick(): Boolean {

        return false
    }

    override fun onMyLocationClick(p0: Location) {

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        with(googleMap) {
            setOnMyLocationButtonClickListener(this@SearchMapActivity)
            setOnMyLocationClickListener(this@SearchMapActivity)
            isMyLocationEnabled = true

            googleMap.uiSettings.isCompassEnabled = false
            googleMap.uiSettings.isMyLocationButtonEnabled = false

        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }


    private var navigatorScope: InitializedNavScope? = null

    // TODO: Update to be lifecycle aware.
    private var pendingNavActions = mutableListOf<InitializedNavRunnable>()
    private var arrivalListener: Navigator.ArrivalListener? = null
    private var routeChangedListener: RouteChangedListener? = null
    var isReady = false

    /** Starts the Navigation API, saving a reference to the ready Navigator instance. */
    private fun initializeNavigationApi() {
        NavigationApi.getNavigator(
            this,
            object : NavigatorListener {
                override fun onNavigatorReady(navigator: Navigator) {
                    isReady = true
                    val scope = InitializedNavScope(navigator)
                    navigatorScope = scope
                    pendingNavActions.forEach { block -> scope.block() }
                    pendingNavActions.clear()


                    val lat = SPUtils.getInstance().getFloat("lat")
                    val lng = SPUtils.getInstance().getFloat("lng")
                    if (lat != 0f) {
                        if (BuildConfig.DEBUG) {
                            navigator.simulator.setUserLocation(
                                LatLng(
                                    lat.toDouble(),
                                    lng.toDouble()
                                )
                            )

                        }
                    }
                }

                override fun onError(@NavigationApi.ErrorCode errorCode: Int) {
                    when (errorCode) {
                        NavigationApi.ErrorCode.NOT_AUTHORIZED -> {
                            // Note: If this message is displayed, you may need to check that
                            // your API_KEY is specified correctly in AndroidManifest.xml
                            // and is been enabled to access the Navigation API
                            toast(
                                "Error loading Navigation API: Your API key is " +
                                        "invalid or not authorized to use Navigation."
                            )
                        }

                        NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> {
                            toast(
                                "Error loading Navigation API: User did not " +
                                        "accept the Navigation Terms of Use."
                            )
                        }

                        else -> toast("Error loading Navigation API: $errorCode")
                    }
                }
            },
        )


    }

    /**
     * Registers a number of example event listeners that show an on screen message when certain
     * navigation events occur (e.g. the driver's route changes or the destination is reached).
     */
    private fun registerNavigationListeners() {
        withNavigatorAsync {
            arrivalListener =
                Navigator.ArrivalListener { // Show an onscreen message
                        event ->

                    val isFinalDestination = event.isFinalDestination

                    Log.e("Song1", "ArrivalListener到达")
                    if (isFinalDestination) {
                        Log.e("Song1", "到达终点")
                        isStart = false
                        navigator.clearDestinations()
                        navigator.stopGuidance()
//退出导航
                        device?.apply {
                            val cmd = CMDMsg(
                                "55 AA 03 00 00 06 0${type}  "
                            )
                            sendEvent(cmd)
                        }

                        if (BuildConfig.DEBUG) {
                            navigator.simulator?.pause()
                            navigator.simulator?.unsetUserLocation()
                        }
                        sendTag(Tools.Stop_Navigation)
                        showArrivedDialog {
                            if (it == 1) {
                                start<FeedBackActivity> { }
                            }
                            finish()
                        }
                    } else {
                        Log.e("Song1", "到达途径点")
                        navigator.continueToNextDestination() // 自动切下一段导航
                    }


                }
            navigator.addArrivalListener(arrivalListener)
            mRoadSnappedLocationProvider =
                NavigationApi.getRoadSnappedLocationProvider(getApplication())!!;

            if (mRoadSnappedLocationProvider != null) {
                mRoadSnappedLocationProvider?.addLocationListener(locationListener);
            } else {
                Log.e("Song", "ERROR: Failed to get a location provider")

            }
            navigator.setSpeedingListener { percent, _ ->
                if (percent < 0) {
                    // 无道路限速
//                    Log.e("Route", "当前限速____无")
                    return@setSpeedingListener
                }
                val limit = currentSpeedKmh / (1f + percent)
                val speedLimit = limit.toInt()
                Log.e("Route", "当前限速____${speedLimit}")
            }

            routeChangedListener =
                RouteChangedListener { // Show an onscreen message when the route changes
//                    showToast("onRouteChanged: the driver's route changed")
                }
            navigator.addRouteChangedListener(routeChangedListener)

        }
    }


    /**
     * Runs [block] once navigator is initialized. Block is ignored if the navigator is never
     * initialized (error, etc.).
     *
     * This ensures that calls using the navigator before the navigator is initialized gets executed
     * after the navigator has been initialized.
     */
    private fun withNavigatorAsync(block: InitializedNavRunnable) {
        val navigatorScope = navigatorScope
        if (navigatorScope != null) {
            navigatorScope.block()
        } else {
            pendingNavActions.add(block)
        }
    }

    /**
     * Runs [block] once map is initialized. Block is ignored if map is never initialized.
     *
     * This ensures that calls using the map before the map is initialized gets executed after the map
     * has been initialized.
     */
    private fun withMapAsync(block: InitializedMapScope.() -> Unit) {
        binding.navView.getMapAsync { map ->
            object : InitializedMapScope {
                override val map = map
            }
                .block()
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        binding.navView.onTrimMemory(level)
    }


    override fun onStart() {
        super.onStart()

        binding.navView.onStart()


    }

    override fun onResume() {
        super.onResume()
        binding.navView.onResume()
        isNight = isDarkModeAppCompat(this)
        if (isNight) {
            binding.navView.setForceNightMode(ForceNightMode.FORCE_NIGHT)
        } else {
            binding.navView.setForceNightMode(ForceNightMode.FORCE_DAY)
        }
        binding.tvLocationState.isVisible = !LocationPermissionUtil.isLocationAlwaysAllow(this)


    }

    override fun onPause() {
        binding.navView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.navView.onStop()
        super.onStop()
    }


    override fun onDestroy() {
        isStart = false
        binding.navView.onDestroy()
        withNavigatorAsync {
            navigator.stopGuidance()
            // 1. 停止导航
            navigator.stopGuidance()
            navigator.clearDestinations()

            // 2. 清空所有监听器（位置、超速、到达、路线变更）
            navigator.setSpeedingListener(null)
            navigator.clearLicensePlateRestrictionInfo()

        }

        super.onDestroy()
    }


    /**
     * Requests directions from the user's current location to a specific place (provided by the
     * Google Places API).
     */
    private fun navigateToPlace() {

        val list = arrayListOf<Waypoint>()

        for (i in orangePointPlaceId) {
            list.add(Waypoint.builder().setLatLng(i.latitude, i.longitude).build())
        }
        for (i in middlePointPlaceId) {
            list.add(Waypoint.builder().setLatLng(i.latitude, i.longitude).build())
        }
        list.add(Waypoint.builder().setLatLng(tagLat, tagLng).build())



        withNavigatorAsync {
            // 1. 先停止旧导航，清空路线缓存
            navigator.stopGuidance()
            navigator.clearDestinations()
//
            sendTag(Tools.Start_Navigation)
            val pendingRoute = navigator.setDestinations(list)

            // Set an action to perform when a route is determined to the destination
            pendingRoute.setOnResultListener { code ->
                when (code) {
                    RouteStatus.OK -> {
                        binding.navView.setTrafficIncidentCardsEnabled(false)
                        // Hide the toolbar to maximize the navigation UI
                        actionBar?.hide()
                        isStart = true
                        // Enable voice audio guidance (through the device speaker)
                        navigator.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)

                        // Simulate vehicle progress along the route (for demo/debug builds)

                        if (BuildConfig.DEBUG) {
                            navigator.simulator.simulateLocationsAlongExistingRoute(
                                SimulationOptions().speedMultiplier(2f)
                            )
                        }

                        // Start turn-by-turn guidance along the current route
                        navigator.startGuidance()
                        //开始导航
                        device?.apply {
                            val cmd = CMDMsg(
                                "55 AA 03 00 01 06 0${type}  "
                            )
                            sendEvent(cmd)
                        }

                        if (isMetric) {
                            val cmd = CMDMsg("55 AA 04 00 02 05 02 01")
                            sendEvent(cmd)
                        } else {
                            val cmd = CMDMsg("55 AA 04 00 02 05 02 00")
                            sendEvent(cmd)
                        }

                        toggleNavFwding()


                    }

                    RouteStatus.ROUTE_CANCELED -> {
                        isStart = false
                        // Return to top-down perspective
                        toast("Route guidance cancelled.")

                    }

                    RouteStatus.NO_ROUTE_FOUND,
                    RouteStatus.NETWORK_ERROR,
                        -> {
                        sendTag(Tools.Stop_Navigation)
                        // TODO: Add logic to handle when a route could not be determined
                        toast("Error starting guidance: $code")
                    }

                    else -> toast("Error starting guidance: $code")
                }
            }

        }
    }

    /**
     * Requests directions from the user's current location to a specific place (provided by the
     * Google Places API).
     */
    private fun navigateToPlaceByRoute(route: String) {
        Log.e("Route", "route__________${route}")
        val customRoutesOptions =
            CustomRoutesOptions.builder()
                .setRouteToken(route)

                .setTravelMode(CustomRoutesOptions.TravelMode.DRIVING)
                .build();

        val list = arrayListOf<Waypoint>()
        for (i in orangePointPlaceId) {
            list.add(Waypoint.builder().setLatLng(i.latitude, i.longitude).build())
        }

        for (i in middlePointPlaceId) {
            list.add(Waypoint.builder().setLatLng(i.latitude, i.longitude).build())
        }

        list.add(Waypoint.builder().setLatLng(tagLat, tagLng).build())

        withNavigatorAsync {
//            if (BuildConfig.DEBUG) {
//                if (orangePointPlaceId.isNotEmpty()) {
//                    navigator.simulator.setUserLocation(
//                        LatLng(
//                            orangePointPlaceId.get(0).latitude,
//                            orangePointPlaceId.get(0).longitude
//                        )
//                    )
//                }
//
//            }
//            val lat = SPUtils.getInstance().getFloat("lat")
//            val lng = SPUtils.getInstance().getFloat("lng")
//            if (lat != 0f) {
//                if (BuildConfig.DEBUG) {
//                    navigator.simulator.setUserLocation(LatLng(lat.toDouble(), lng.toDouble()))
//
//                }
//            }


            val pendingRoute = navigator.setDestinations(list, customRoutesOptions)
            sendTag(Tools.Start_Navigation)
            // Set an action to perform when a route is determined to the destination
            pendingRoute.setOnResultListener { code ->
                when (code) {
                    RouteStatus.OK -> {
                        // Hide the toolbar to maximize the navigation UI
                        binding.navView.setTrafficIncidentCardsEnabled(false)
                        actionBar?.hide()
                        isStart = true
                        // Enable voice audio guidance (through the device speaker)
                        navigator.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)

                        // Simulate vehicle progress along the route (for demo/debug builds)

                        if (BuildConfig.DEBUG) {
                            navigator.simulator.simulateLocationsAlongExistingRoute(
                                SimulationOptions().speedMultiplier(2f)
                            )
                        }

                        // Start turn-by-turn guidance along the current route
                        navigator.startGuidance()
                        //开始导航
                        device?.apply {
                            val cmd = CMDMsg(
                                "55 AA 03 00 01 06 0${type}  "
                            )
                            sendEvent(cmd)
                        }

                        if (isMetric) {
                            val cmd = CMDMsg("55 AA 04 00 02 05 02 01")
                            sendEvent(cmd)
                        } else {
                            val cmd = CMDMsg("55 AA 04 00 02 05 02 00")
                            sendEvent(cmd)
                        }
                        toggleNavFwding()

                    }

                    RouteStatus.ROUTE_CANCELED -> {
                        isStart = false
                        // Return to top-down perspective
                        toast("Route guidance cancelled.")

                    }

                    RouteStatus.NO_ROUTE_FOUND -> {
                        sendTag(Tools.Stop_Navigation)
                        // Return to top-down perspective
                        toast("Error starting guidance: $code")

                    }


                    RouteStatus.NETWORK_ERROR,
                        -> {
                        sendTag(Tools.Stop_Navigation)
                        // TODO: Add logic to handle when a route could not be determined
                        toast("Error starting guidance: NETWORK_ERROR")
                    }

                    else -> toast("Error starting guidance: $code")
                }
            }

        }
    }


    fun toggleNavFwding() {
        withNavigatorAsync {
            navInfoDisplayFragment =
                CustomizationPanelsDelegate.toggleNavForwarding(
                    this@SearchMapActivity,
                    R.id.nav_info_frame,
                    navigator,
                    navInfoDisplayFragment,
                )
            navInfoDisplayBottom =
                CustomizationPanelsDelegate.toggleNavBottomForwarding(
                    this@SearchMapActivity,
                    R.id.nav_info_bottom,
                    navigator,
                    navInfoDisplayBottom,
                )

            navInfoDisplayBottom?.setDeviceData(device)
            navInfoDisplayBottom?.setDanwei(danwei)

            isNaving = true
        }
    }

    val cameraIdMap = HashMap<String, Double>()
    val cameraMap = HashMap<String, Marker>()
    val eventMap = HashMap<String, Marker>()
    var realDistance = 0.0

    // lat/lng: (48.1417978,11.579486)
    fun getCameraDevice(
        long: String = "8.778969",
        lat: String = "50.822749",
        direction: String = "6.7",
        distance: String = distanceData.toString(),
        street: String = streetName.pk(),
        route: String = "",
    ) {

        scopeNet {
            val data = getCamera(long, lat, direction, distance, street, route)
            val list = data.data
            if (list.isNullOrEmpty() && cameraId != 0) {
                passCamera()
            }
            data.data?.apply {
                if (this.size > 0) {
                    if (cameraId != this[0].id && cameraId != 0) {
                        passCamera()
                        globalUITask {
                            if (eventSpeed == 0) {
                                navInfoDisplayBottom?.setSpeedLimit("0", false)
                            }

                        }
                    }

                    //和上一个不一致
                    if (cameraId != this[0].id && getDisplayDistance(
                            this[0].distance,
                            isMetric
                        ) <= 1000
                    ) {
                        //判断类型是红绿灯
                        if (this[0].type == "A" || this[0].type == "GA") {
                            if (device?.traffic_light_camera_phone != 0) {
                                MApplication.get().playShexiangtou()
                            }
                        } else {
                            //判断类型是固定测速摄像头
                            if (device?.fixed_speed_phone != 0) {
                                MApplication.get().playShexiangtou()
                            }
                        }
                    }
                    //固定超速摄像头告警消息
                    if (getDisplayDistance(this[0].distance, isMetric) <= 1000) {
                        cameraId = this[0].id
                        cameraIdType = this[0].type
                        Log.e("Camera", this[0].toJson())
                        //是否手机提醒

                        navInfoDisplayBottom?.setSpeedLimit(
                            this[0].speed.toString(),
                            device?.speed_alarm_phone == 1
                        )

                        Log.e("_RSB", "device?.speed_alarm_device___${device?.speed_alarm_device}")
                        sendCameraCmd(this)


                    } else {
                        if (eventSpeed == 0) {
                            navInfoDisplayBottom?.setSpeedLimit("0", false)
                        }

                    }

                } else {
                    if (eventSpeed == 0) {
                        navInfoDisplayBottom?.setSpeedLimit("0", false)
                    }

                    if (cameraId != 0) {
                        passCamera()
                    }

                }

                for (i in this) {
                    if (cameraMap.contains(i.id.toString())) {
                        continue
                    }
                    sendEvent(i)
                    val location = LatLng(i.latitude.toDouble(), i.longitude.toDouble()) // 示例坐标（北京）
                    var bitmap: Bitmap
                    when (i.type) {
                        "A", "GA" -> {
                            if (isNight) {
                                bitmap = getBitMap(R.mipmap.ic_map_deng_ye)
                            } else {
                                bitmap = getBitMap(R.mipmap.ic_map_deng)
                            }


                        }

                        else -> {
                            if (isNight) {
                                bitmap = getBitMap(R.mipmap.ic_map_speed_camera_ye)
                            } else {
                                bitmap = getBitMap(R.mipmap.ic_map_speed_camera)
                            }
                        }
                    }
                    // 添加标记
                    val marker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    )
                    marker?.apply {
                        cameraMap.put(i.id.toString(), this)
                        if (!binding.ivCamera.isSelected) {
                            this.isVisible = false
                        }
                    }


                }
            }
//

            val dataEvent = getMapEvent(long, lat, direction, distance, street, route)
            val listEvent = dataEvent.data
            if (listEvent.isNullOrEmpty() && eventId != 0) {
                passEvent()
            }
            dataEvent.data?.apply {
                if (this.size > 0) {
                    if (eventId != this[0].id && eventId != 0) {
                        passEvent()
                    }
                    if (getDisplayDistance(this[0].distance, isMetric) <= 1000) {
                        if (eventId != this[0].id) {
                            when (this[0].type) {
                                //移动测速摄像头
                                "1", "7" -> {
                                    if (device?.mobile_speed_phone != 0) {
                                        MApplication.get().playShexiangtou()
                                    }
                                }
                                //道路危险
                                "20", "21", "22", "23", "24", "25", "26", "29" -> {
                                    if (device?.road_alarm_phone != 0) {
                                        MApplication.get().playWeixian()
                                    }
                                }
                                //红绿灯
                                "2" -> {
                                    if (device?.traffic_light_camera_phone != 0) {
                                        MApplication.get().playShexiangtou()
                                    }
                                }


                            }
                        }

                        eventId = this[0].id
                        eventIdType = this[0].type
                        val distanceOld = cameraIdMap.get(eventId.toString())
                        if (distanceOld == null) {
                            cameraIdMap.put(
                                eventId.toString(),
                                getDisplayDistance(this[0].distance, isMetric)
                            )
                            sendEventCmd(this)
                        } else {
                            if (distanceOld > getDisplayDistance(this[0].distance, isMetric)) {
                                cameraIdMap.put(
                                    eventId.toString(),
                                    getDisplayDistance(this[0].distance, isMetric)
                                )
                                sendEventCmd(this)

                            }
                        }

                    }

                } else {
                    if (eventId != 0) {
                        passEvent()

                    }
                }
                for (i in this) {
                    if (eventMap.contains(i.id.toString())) {
                        continue
                    }
                    val location = LatLng(
                        i.latitude.toDouble(),
                        i.longitude.toDouble()
                    ) // 坐标加个偏移，避免重叠
                    var bitmap: Bitmap
                    //     // 1	移动测速
                    //        // 2	移动闯红灯
                    //        // 6	移动车距监测
                    //        // 20	拥堵/车尾结束
                    //        // 21	事故
                    //        // 22	临时施工
                    //        // 23	路面障碍物
                    //        // 24	路面湿滑
                    //        // 25	视线受阻
                    //        // 26	长期施工
                    //        // 29	故障车辆
                    when (i.type) {
                        "20" -> {
                            if (isNight) {
                                bitmap = getBitMap(R.mipmap.ic_map_du_ye)
                            } else {
                                bitmap = getBitMap(R.mipmap.ic_map_du)
                            }


                        }

                        "2" -> {
                            if (isNight) {
                                bitmap = getBitMap(R.mipmap.ic_map_deng_ye)
                            } else {
                                bitmap = getBitMap(R.mipmap.ic_map_deng)
                            }

                        }

                        "1", "6", "7" -> {
                            if (isNight) {
                                bitmap = getBitMap(R.mipmap.ic_map_yidong_ye)
                            } else {
                                bitmap = getBitMap(R.mipmap.ic_map_yidong)
                            }


                        }

                        "21" -> {
                            if (isNight) {
                                bitmap = getBitMap(R.mipmap.ic_map_shigu_new_ye)
                            } else {
                                bitmap = getBitMap(R.mipmap.ic_map_shigu_new)
                            }

                        }

                        else -> {
                            if (isNight) {
                                bitmap = getBitMap(R.mipmap.ic_map_weixian_ye)
                            } else {
                                bitmap = getBitMap(R.mipmap.ic_map_weixian)
                            }

                        }
                    }


                    // 添加标记
                    val marker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(location)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    )
                    marker?.apply {
                        eventMap.put(i.id.toString(), this)

                    }


                }
            }
        }
    }

    private fun getBitMap(resourceId: Int): Bitmap {
        var bitmap = BitmapFactory.decodeResource(resources, resourceId)
        val width = bitmap.width
        val height = bitmap.height
        val newWidth = 80
        val newHeight = 80
        val widthScale = (newWidth.toFloat()) / width
        val heightScale = (newHeight.toFloat()) / height
        val matrix = Matrix()
        matrix.postScale(widthScale, heightScale)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        return bitmap
    }


    override fun onBackPressed() {


    }

    var eventSpeed = 0
    fun sendCameraCmd(data: List<CameraDeviceData>) {
        if (!isStart) {
            return
        }

        data.apply {
            if (speedType == "02") {
                this[0].speed = (this[0].speed.toInt() * 0.621371).toInt()
            }

            if (this[0].type == "A" || this[0].type == "GA") {

                val distanceOld = cameraIdMap.get(cameraId.toString())
                if (distanceOld == null) {
                    cameraIdMap.put(
                        cameraId.toString(),
                        getDisplayDistance(this[0].distance, isMetric)
                    )
                    Log.e("_RSB", "接口数据____${this[0].toJson()}")

                    //固定红绿灯
                    val cmd = CMDMsg(
                        "55AA1000 0502 0${device?.type} ${
                            decimalToLittleEndianHex(
                                cameraId,
                                6
                            )
                        } 02 ${
                            decimalToLittleEndianHex(
                                this[0].speed.toInt(),
                                2
                            )
                        } ${speedType}  ${
                            decimalToLittleEndianHex(
                                getDisplayDistance(this[0].distance, isMetric).toInt(),
                                2
                            )
                        }  ${distanceType} "
                    )
                    Log.e("_RSB", "发命令_红绿灯___${cmd.cmd}")
                    sendEvent(cmd)
                } else {
                    if (distanceOld > getDisplayDistance(this[0].distance, isMetric)) {
                        cameraIdMap.put(
                            cameraId.toString(),
                            getDisplayDistance(this[0].distance, isMetric)
                        )
                        Log.e("_RSB", "接口数据____${this[0].toJson()}")
                        //固定红绿灯
                        val cmd = CMDMsg(
                            "55AA1000 0502 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    cameraId,
                                    6
                                )
                            } 02 ${
                                decimalToLittleEndianHex(
                                    this[0].speed.toInt(),
                                    2
                                )
                            } ${speedType}  ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )
                        Log.e("_RSB", "发命令_红绿灯___${cmd.cmd}")
                        sendEvent(cmd)
                    }
                }


            } else {

                val distanceOld = cameraIdMap.get(cameraId.toString())
                if (distanceOld == null) {
                    cameraIdMap.put(
                        cameraId.toString(),
                        getDisplayDistance(this[0].distance, isMetric)
                    )
                    Log.e("_RSB", "接口数据____${this[0].toJson()}")

                    //是否设备提醒
                    val cmd = CMDMsg(
                        "55AA1000 0502 0${device?.type} ${
                            decimalToLittleEndianHex(
                                cameraId,
                                6
                            )
                        } 01 ${
                            decimalToLittleEndianHex(
                                this[0].speed,
                                2
                            )
                        } ${speedType} ${
                            decimalToLittleEndianHex(
                                getDisplayDistance(this[0].distance, isMetric).toInt(),
                                2
                            )
                        } ${distanceType}"
                    )

                    Log.e("_RSB", "发命令____${cmd.cmd}")
                    sendEvent(cmd)
                } else {
                    if (distanceOld > getDisplayDistance(this[0].distance, isMetric)) {
                        cameraIdMap.put(
                            cameraId.toString(),
                            getDisplayDistance(this[0].distance, isMetric)
                        )
                        Log.e("_RSB", "接口数据____${this[0].toJson()}")
                        //是否设备提醒
                        val cmd = CMDMsg(
                            "55AA1000 0502 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    cameraId,
                                    6
                                )
                            } 01 ${
                                decimalToLittleEndianHex(
                                    this[0].speed,
                                    2
                                )
                            } ${speedType} ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            } ${distanceType}"
                        )

                        Log.e("_RSB", "发命令____${cmd.cmd}")
                        sendEvent(cmd)
                    }
                }

            }
        }
    }

    fun sendEventCmd(data: List<CameraDeviceData>) {
        if (!isStart) {
            return
        }
        data.apply {
            eventSpeed = this[0].speed
            if (speedType == "02") {
                eventSpeed = (this[0].speed.toInt() * 0.621371).toInt()
            }

            Log.e("_RSB", "接口数据__事件__${this[0].toJson()}")
            //移动测速
            if (this[0].type == "1") {
                //是否手机提醒

                navInfoDisplayBottom?.setSpeedLimit(
                    this[0].speed.toString(),
                    device?.speed_alarm_phone == 1
                )

                val cmd = CMDMsg(
                    "55AA1000 0702 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 01 ${
                        decimalToLittleEndianHex(
                            eventSpeed.toInt(),
                            2
                        )
                    } ${speedType}  ${
                        decimalToLittleEndianHex(
                            getDisplayDistance(this[0].distance, isMetric).toInt(),
                            2
                        )
                    }  ${distanceType} "
                )

                sendEvent(cmd)
                //移动闯红灯
            } else if (this[0].type == "2") {
                val cmd = CMDMsg(
                    "55AA1000 0702 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 03 ${
                        decimalToLittleEndianHex(
                            eventSpeed.toInt(),
                            2
                        )
                    } ${speedType}  ${
                        decimalToLittleEndianHex(
                            getDisplayDistance(this[0].distance, isMetric).toInt(),
                            2
                        )
                    }  ${distanceType} "
                )

                sendEvent(cmd)

                //普通的移动摄像头
            } else if (this[0].type == "6") {
                //0x4：移动摄像头（非测速）
                val cmd = CMDMsg(
                    "55AA1000 0702 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 04 ${
                        decimalToLittleEndianHex(
                            eventSpeed.toInt(),
                            2
                        )
                    } ${speedType}  ${
                        decimalToLittleEndianHex(
                            getDisplayDistance(this[0].distance, isMetric).toInt(),
                            2
                        )
                    }  ${distanceType} "
                )

                sendEvent(cmd)
                //移动测速摄像头
            } else if (this[0].type == "7") {

                //是否手机提醒

                navInfoDisplayBottom?.setSpeedLimit(
                    this[0].speed.toString(),
                    device?.speed_alarm_phone == 1
                )

                //0x2：半固定移动测速摄像头
                val cmd = CMDMsg(
                    "55AA1000 0702 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 02 ${
                        decimalToLittleEndianHex(
                            eventSpeed.toInt(),
                            2
                        )
                    } ${speedType}  ${
                        decimalToLittleEndianHex(
                            getDisplayDistance(this[0].distance, isMetric).toInt(),
                            2
                        )
                    }  ${distanceType} "
                )

                sendEvent(cmd)
            } else {


                when (this[0].type) {
                    "20" -> {
                        //20 交通拥堵车流尾
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 01 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    "21" -> {
                        //Type=21交通事故现场
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 02 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    "22" -> {
                        //Type=22临时道路施工
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 03 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    "23" -> {
                        //Type=23路面障碍物
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 04 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    "24" -> {
                        //湿滑/结冰路面 降雨、结冰、积雪路面，易打滑失控。
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 05 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    "25" -> {
                        //视线受阻路段大雾、沙尘、暴雨导致能见度骤降。
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 06 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    "26" -> {
                        //长期道路改扩建工程
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 07 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    "29" -> {
                        //长期道路改扩建工程
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 08 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }

                    else -> {
                        //未知
                        val cmd = CMDMsg(
                            "55AA0D000503 0${device?.type} ${
                                decimalToLittleEndianHex(
                                    eventId,
                                    6
                                )
                            } 00 ${
                                decimalToLittleEndianHex(
                                    getDisplayDistance(this[0].distance, isMetric).toInt(),
                                    2
                                )
                            }  ${distanceType} "
                        )

                        sendEvent(cmd)
                    }
                }


            }
        }
    }

    fun passEvent() {

        Log.e("Song", "发送通过事件————id__${eventId}__类型_${eventIdType}")
        //通过事件
        when (eventIdType) {
            "1" -> {
                eventSpeed = 0
                globalUITask {
                    navInfoDisplayBottom?.setSpeedLimit("0", false)
                }
                //通过移动测速
                //55 AA 0B 00 0A 02 01  C8 24 24 5C 03 00  01 01
                val cmd = CMDMsg(
                    "55 AA 0B 00 0A 02  0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 01 01 "
                )
                sendEvent(cmd)
            }

            "2" -> {
                //通过红绿灯
                //55 AA 0B 00 0A 02 01  C8 24 24 5C 03 00  01 01
                val cmd = CMDMsg(
                    "55 AA 0B 00 0A 02  0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 03 01 "
                )
                sendEvent(cmd)
            }

            "6" -> {
                //通过普通的移动摄像头
                //55 AA 0B 00 0A 02 01  C8 24 24 5C 03 00  01 01
                val cmd = CMDMsg(
                    "55 AA 0B 00 0A 02  0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 04 01 "
                )
                sendEvent(cmd)
            }

            "7" -> {
                eventSpeed = 0
                globalUITask {
                    navInfoDisplayBottom?.setSpeedLimit("0", false)
                }
                //通过半固定移动测速摄像头
                //55 AA 0B 00 0A 02 01  C8 24 24 5C 03 00  01 01
                val cmd = CMDMsg(
                    "55 AA 0B 00 0A 02  0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 02 01 "
                )
                sendEvent(cmd)
            }

            "20" -> {
                //已通过“前方道路危险”的信息  交通拥堵车流尾
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 01 01 "
                )
                sendEvent(cmd)
            }

            "21" -> {
                //已通过“前方道路危险”的信息  事故
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 02 01 "
                )
                sendEvent(cmd)
            }

            "22" -> {
                //已通过“前方道路危险”的信息  道路施工
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 03 01 "
                )
                sendEvent(cmd)
            }

            "23" -> {
                //已通过“前方道路危险”的信息  路面障碍
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 04 01 "
                )
                sendEvent(cmd)
            }

            "24" -> {
                //已通过“前方道路危险”的信息  路面结冰
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 05 01 "
                )
                sendEvent(cmd)
            }

            "25" -> {
                //已通过“前方道路危险”的信息  路面结冰
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 06 01 "
                )
                sendEvent(cmd)
            }

            "26" -> {
                //已通过“前方道路危险”的信息  占道
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 07 01 "
                )
                sendEvent(cmd)
            }

            "29" -> {
                //已通过“前方道路危险”的信息  占道
                //55 AA 0B0007 03 01 C8 24 24 5C 03 000101
                val cmd = CMDMsg(
                    "55AA0B00 0703 0${device?.type} ${
                        decimalToLittleEndianHex(
                            eventId,
                            6
                        )
                    } 08 01 "
                )
                sendEvent(cmd)
            }
        }
        eventId = 0


    }

    fun passCamera() {

        Log.e("Song", "发送通过摄像头————摄像头id__${cameraId}__摄像头类型_${cameraIdType}")
        when (cameraIdType) {
            //固定红绿灯
            "A", "GA" -> {
                //通过摄像头
                val cmd = CMDMsg(
                    "55AA0B00 0902 0${device?.type} ${
                        decimalToLittleEndianHex(
                            cameraId,
                            6
                        )
                    } 02 01 "
                )
                sendEvent(cmd)
            }

            else -> {
                //通过摄像头
                val cmd = CMDMsg(
                    "55AA0B00 0902 0${device?.type} ${
                        decimalToLittleEndianHex(
                            cameraId,
                            6
                        )
                    } 01 01 "
                )
                sendEvent(cmd)
            }
        }
        cameraId = 0
    }

    fun isDarkModeAppCompat(context: Context): Boolean {
        val uiMode = context.resources.configuration.uiMode
        val mask = Configuration.UI_MODE_NIGHT_MASK
        return (uiMode and mask) == Configuration.UI_MODE_NIGHT_YES
    }

    fun exitNavigation() {
        Log.e("isReady", isReady.toString())
        if (!isReady) {
            sendTag(Tools.Stop_Navigation)
            finish()
        }
        withNavigatorAsync {
            isStart = false
            //退出导航
            val cmd = CMDMsg(
                "55 AA 03 00 00 06 0${device?.type}  "
            )
            sendEvent(cmd)
            if (BuildConfig.DEBUG) {
                navigator.simulator?.pause()
                navigator.simulator?.unsetUserLocation()
            }

            navigator.stopGuidance()
            navigator.clearDestinations()

            if (mRoadSnappedLocationProvider != null) {
                mRoadSnappedLocationProvider?.removeLocationListener(locationListener);
            }
            sendTag(Tools.Stop_Navigation)
            finish()
        }
    }

    fun getDisplayDistance(meters: Double, isMetric: Boolean): Double {
        return if (isMetric) {
            meters
        } else {
            meters * 1.0936133
        }
    }

    fun getCurrentDevice() {
        val connectList = ble.connectedDevices
        if (connectList.size > 0) {
            val connectedData = connectList[0]
            binding.tvDeviceState.isVisible = false
            binding.ivDevice.isSelected = false
            scopeNetLife {
                val data = getEquipmentList()
                data?.data?.apply {
                    for (i in this) {
                        if (i.uuid == HexUtils.swapMacFirstTwoBytesNoColon(
                                connectedData.bleAddress
                            )
                        ) {
                            device = i
                            device!!.connect = true
                            navInfoDisplayBottom?.setDeviceData(device)
                        }

                    }
                }

            }
        } else {
            binding.tvDeviceState.isVisible = true
            binding.ivDevice.isSelected = true
        }
    }
}