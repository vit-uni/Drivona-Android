package com.drivona.speed.car

import android.annotation.SuppressLint
import android.app.Presentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.Surface
import androidx.car.app.AppManager
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Distance
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.drake.channel.receiveEvent
import com.drake.channel.receiveTag
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.drake.net.utils.scopeNet
import com.drivona.speed.BuildConfig
import com.drivona.speed.MApplication
import com.drivona.speed.MApplication.Companion.placesClient
import com.drivona.speed.R
import com.drivona.speed.api.CameraDeviceData
import com.drivona.speed.api.CarLocation
import com.drivona.speed.api.CarRouteInfo
import com.drivona.speed.api.LatLngG
import com.drivona.speed.api.LocationDataCar
import com.drivona.speed.api.LocationG
import com.drivona.speed.api.MiddlePoint
import com.drivona.speed.api.Origin
import com.drivona.speed.api.RouteModifiers
import com.drivona.speed.api.RoutesEntity
import com.drivona.speed.api.computeRoutes
import com.drivona.speed.car.model.DemoScripts
import com.drivona.speed.utils.map.NavInfoReceivingService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavInfo
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.NavigationViewForAuto
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.Navigator.RouteStatus
import com.google.android.libraries.navigation.RoadSnappedLocationProvider
import com.google.android.libraries.navigation.SimulationOptions
import com.google.android.libraries.navigation.Waypoint
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.lalifa.ext.Tools
import com.lalifa.ext.Tools.Companion.Car_Center
import com.lalifa.ext.Tools.Companion.Car_Exit
import com.lalifa.extension.globalTask
import com.lalifa.extension.toJson

class NavSession : Session() {

    companion object {
        private const val VIRTUAL_DISPLAY_NAME = "NavAutoDisplay"
    }

    //定位客户端
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    //地图是否开启镜头跟随模式
    var followLocation: Boolean = true
    private val DEFAULT_ZOOM = 15f

    //定位刷新间隔 毫秒
    private val LOCATION_INTERVAL = 2000L
    private val LOCATION_FASTEST = 1000L


    private var virtualDisplay: VirtualDisplay? = null
    private var presentation: Presentation? = null
    private var navigationView: NavigationViewForAuto? = null
    private var googleMap: GoogleMap? = null
    lateinit var mNavigationScreen: NavScreen
    var isNavigating = false
    var isNight = false
    var mService: NavigationService? = null
    val cameraMap = HashMap<String, Marker>()
    private var navigator: Navigator? = null
    var mRoadSnappedLocationProvider: RoadSnappedLocationProvider? = null
    private var arrivalListener: Navigator.ArrivalListener? = null
    val mServiceListener: NavigationService.Listener = object : NavigationService.Listener {
        override fun navigationStateChanged(
            isNavigating: Boolean,
            isRerouting: Boolean,
            hasArrived: Boolean,
            destinations: MutableList<Destination?>?,
            steps: MutableList<Step>?,
            nextDestinationTravelEstimate: TravelEstimate?,
            nextStepRemainingDistance: Distance?,
            shouldShowNextStep: Boolean,
            shouldShowLanes: Boolean,
            junctionImage: CarIcon?,
            navInfo: NavInfo?,
        ) {
            if (this@NavSession.isNavigating && !isNavigating) {
                startLocationUpdates()
            }
            if (!this@NavSession.isNavigating && isNavigating) {
                stopLocationUpdates()
            }
            this@NavSession.isNavigating = isNavigating
            mNavigationScreen.updateTrip(
                isNavigating,
                isRerouting,
                hasArrived,
                destinations,
                steps,
                nextDestinationTravelEstimate,
                nextStepRemainingDistance,
                shouldShowNextStep,
                shouldShowLanes,
                junctionImage,
                navInfo
            )

        }
    }

    // Monitors the state of the connection to the Navigation service.
    val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(
                NavigationService.TAG,
                "In onServiceConnected() component:" + name
            )
            val binder: NavigationService.LocalBinder = service as NavigationService.LocalBinder
            mService = binder.service
            Log.e(
                NavigationService.TAG,
                "setCarContext:"
            )
            mService!!.setCarContext(carContext, mServiceListener)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(
                NavigationService.TAG,
                "In onServiceDisconnected() component:" + name
            )
            // Unhook map models here
            mService!!.clearCarContext()
            mService = null
        }
    }
    private val mLifeCycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(lifecycleOwner: LifecycleOwner) {
            Log.e(
                NavigationService.TAG,
                "In onCreate()"
            )
        }

        override fun onStart(lifecycleOwner: LifecycleOwner) {
            Log.e(
                NavigationService.TAG,
                "In onStart()"
            )
            carContext
                .bindService(
                    Intent(carContext, NavigationService::class.java),
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
        }

        override fun onResume(lifecycleOwner: LifecycleOwner) {
            Log.e(
                NavigationService.TAG,
                "In onResume()"
            )
        }

        override fun onPause(lifecycleOwner: LifecycleOwner) {
            Log.e(
                NavigationService.TAG,
                "In onPause()"
            )
        }

        override fun onStop(lifecycleOwner: LifecycleOwner) {
            Log.e(
                NavigationService.TAG,
                "In onStop()"
            )
            carContext.unbindService(mServiceConnection)
            mService = null
        }

        override fun onDestroy(lifecycleOwner: LifecycleOwner) {
            Log.e(
                NavigationService.TAG,
                "In onDestroy()"
            )


        }
    }
    var targetData: LocationDataCar? = null
    var isAppOpen = false
    var tagPoint: MiddlePoint = MiddlePoint(
        "",
        0.0, 0.0, "", ""
    )

    override fun onCreateScreen(intent: Intent): Screen {
        // 在Session创建后绑定观察者
        lifecycle.addObserver(mLifeCycleObserver)
        mNavigationScreen = NavScreen(carContext)
        val carContext = carContext
        carContext.getCarService(AppManager::class.java).setSurfaceCallback(surfaceCallback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(carContext)
        initLocationRequest()
        receiveTag(Tools.Stop_Navigation, Tools.Start_Navigation, Tools.Car_APP_CHECK_RESULT,Car_Exit,Car_Center) {
            when (it) {
                Tools.Start_Navigation -> {
                    googleMap?.clear()
                }

                Tools.Stop_Navigation -> {
                    googleMap?.clear()
                }

                Tools.Car_APP_CHECK_RESULT -> {
                    isAppOpen = true
                }
                Tools.Car_Exit -> {
                    navigator?.clearDestinations()
                    navigator?.stopGuidance()
                    stopNav()
                    googleMap?.clear()
                }
                Car_Center->{

                }
            }
        }
        receiveTag("startCarMapNav") {
            navigateToPlace()
        }

        receiveEvent<LocationDataCar> {
            targetData = it
            isAppOpen = false
            Log.e("Ning", "收到消息￥${it.toJson()}")
            sendTag(Tools.Car_APP_CHECK)
            globalTask(500) {
                if (isAppOpen) {
                    return@globalTask
                }
                Log.e("Ning", "收到消息开始搜索${it.toJson()}")
                fetchPlaceDetails(it.place_id)
            }

        }
        receiveEvent<CarLocation> {
            stopLocationUpdates()
            googleMap?.apply {
                clear()
                val latLng = LatLng(it.lat, it.lng)
                // 添加标记
                googleMap!!.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))

                )
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.lat, it.lng),
                        15f
                    )
                )
            }
        }
        receiveEvent<CameraDeviceData> {
            val data = it
            googleMap?.apply {
                val location =
                    LatLng(data.latitude.toDouble(), data.longitude.toDouble()) // 示例坐标（北京）
                var bitmap: Bitmap
                when (data.type) {
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

                    "22", "23", "24", "25", "26", "29" -> {
                        if (isNight) {
                            bitmap = getBitMap(R.mipmap.ic_map_weixian_ye)
                        } else {
                            bitmap = getBitMap(R.mipmap.ic_map_weixian)
                        }

                    }

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
                    cameraMap.put(data.id.toString(), this)
                }


            }
        }
        return mNavigationScreen
    }

    var currentLatLng: LatLng? = null
    var isNaving = false
    var isStart = false
    var currentSpeedKmh = 0f
    var distanceData = 0
    val locationListener = object : RoadSnappedLocationProvider.LocationListener {
        override fun onLocationChanged(location: Location) {
            location.also {
                currentLatLng = LatLng(
                    it.latitude,
                    it.longitude
                )


                if (!isStart) {
                    return
                }
                val navInfo = NavInfoReceivingService.navInfoLiveData.value
                navInfo?.apply {
                    setNavInfo(navInfo)
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
                Log.e("Speed", "_ it.speed___${speed}")

                if (realSpeed < 0) {
                    realSpeed = 0
                }


            }

        }

        override fun onRawLocationUpdate(p0: Location?) {


        }


//                        Log.e("Song", "LocationListener_extras_" + it.extras?.toString())
    }
    private val surfaceCallback = object : SurfaceCallback {
        override fun onScale(focusX: Float, focusY: Float, scaleFactor: Float) {
            val update = CameraUpdateFactory.zoomBy(
                scaleFactor - 1,
                Point(focusX.toInt(), focusY.toInt())
            )
            googleMap?.animateCamera(update)
        }

        override fun onScroll(distanceX: Float, distanceY: Float) {
            googleMap?.moveCamera(
                CameraUpdateFactory.scrollBy(distanceX, distanceY)
            )
        }

        private fun isSurfaceReady(surfaceContainer: SurfaceContainer): Boolean {


            val surface: Surface? = surfaceContainer.surface
            return surface != null
                    && surfaceContainer.dpi != 0
                    && surfaceContainer.width != 0
                    && surfaceContainer.height != 0
        }

        override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
            if (!isSurfaceReady(surfaceContainer)) {
                return
            }
            cleanUpResources()

            val carCtx = carContext
            virtualDisplay = carCtx
                .getSystemService(DisplayManager::class.java)
                .createVirtualDisplay(
                    VIRTUAL_DISPLAY_NAME,
                    surfaceContainer.width,
                    surfaceContainer.height,
                    surfaceContainer.dpi,
                    surfaceContainer.surface,
                    0
                )

            presentation = Presentation(carCtx, virtualDisplay!!.display)
            navigationView = NavigationViewForAuto(carCtx).apply {
                onCreate(null)
                onStart()
                onResume()
            }
// 初始化导航SDK
            // ===== 正确获取Navigator！！全局单例，不是navView的方法 =====
            NavigationApi.getNavigator(
                MApplication.get(),
                object : NavigationApi.NavigatorListener {
                    override fun onNavigatorReady(nav: Navigator) {
                        navigator = nav
                        navigator!!.addNavigationSessionListener {

                        }

                        arrivalListener =
                            Navigator.ArrivalListener { // Show an onscreen message
                                    event ->

                                val isFinalDestination = event.isFinalDestination

                                Log.e("Song1", "ArrivalListener到达")
                                if (isFinalDestination) {
                                    Log.e("Song1", "到达终点")
                                    isStart = false
                                    navigator!!.clearDestinations()
                                    navigator!!.stopGuidance()
//退出导航
                                    stopNav()

                                }


                            }
                        navigator!!.addArrivalListener(arrivalListener)

                        mRoadSnappedLocationProvider =
                            NavigationApi.getRoadSnappedLocationProvider(MApplication.get())!!;

                        if (mRoadSnappedLocationProvider != null) {
                            mRoadSnappedLocationProvider?.addLocationListener(locationListener);
                        }

//                        navigator?.setNavigatorListener(navCallback)
                    }

                    override fun onError(@NavigationApi.ErrorCode errorCode: Int) {
                        //初始化失败
                    }
                }
            )
            presentation?.setContentView(navigationView!!)
            presentation?.show()

            // post 等待View挂载，修复getMapAsync黑屏、回调丢失
            navigationView?.post {
                navigationView?.getMapAsync { map ->
                    googleMap = map
                    onMapReady(map)
                }
            }
        }

        override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
            presentation?.dismiss()
            virtualDisplay?.release()
            cleanUpResources()
        }
    }
    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

        }
    }
    private val restoreFollowRunnable =
        Runnable @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            Log.e("map", "_____followMyLocation__________TILTED____")
            //拖拽结束静止 1.5s 之后自动重新开启跟随
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true

            googleMap?.followMyLocation(GoogleMap.CameraPerspective.TILTED)


        }

    /**
     * 地图就绪，开启当前位置蓝点
     */
    @SuppressLint("MissingPermission")
    private fun onMapReady(map: GoogleMap) {
        map.isTrafficEnabled = true

        // ========== 新增：开启当前定位蓝点 ==========
        map.isMyLocationEnabled = true

        val ui: UiSettings = map.uiSettings
        ui.isMyLocationButtonEnabled = true    // 车机地图上显示【回到我的位置】按钮
        ui.isCompassEnabled = true
        startLocationUpdates()
        map.setOnPoiClickListener {
            Log.e("SOng", "点击-------------------------")
            map?.addMarker(
                MarkerOptions()
                    .position(it.latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))
            )
        }
        map?.setOnCameraMoveStartedListener { reason ->
            Log.e("CAR", "--move---------------${reason}")

            //用户手动拖拽，标记跟随关闭
//                        autoFollow = false
            if (isNavigating) {
                mHandler.removeCallbacks(restoreFollowRunnable)
                mHandler.postDelayed(restoreFollowRunnable, 5000) // 8秒后自动恢复

            }


        }
    }

    var currentLat = 0.0
    var currentLng = 0.0
    private fun initLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_INTERVAL
            fastestInterval = LOCATION_FASTEST
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation ?: return
                val latLng = LatLng(loc.latitude, loc.longitude)
                currentLat = loc.latitude
                currentLng = loc.longitude
                //开启跟随才移动镜头
                if (followLocation) {
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }

    /**
     * 开启实时位置更新
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val req = locationRequest ?: return
        val callback = locationCallback ?: return
        fusedLocationClient.requestLocationUpdates(req, callback, null)
    }


    /**
     * 停止实时位置更新
     */
    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    /**
     * 资源回收，防止内存泄漏
     */
    private fun cleanUpResources() {
        navigationView?.run {
            onPause()
            onStop()
            onDestroy()
        }
        navigationView = null

        presentation = null
        virtualDisplay = null
        googleMap = null
    }

    /**
     * 获取缓存位置，镜头跳转到当前位置
     */
    @SuppressLint("MissingPermission")
    private fun moveCameraToLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location ?: return@addOnSuccessListener
                val latLng = LatLng(location.latitude, location.longitude)
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
            .addOnFailureListener {
                //无缓存位置，GPS还没拿到
            }
    }

    private fun getBitMap(resourceId: Int): Bitmap {
        var bitmap = BitmapFactory.decodeResource(carContext.resources, resourceId)
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


    private fun fetchPlaceDetails(placeId: String) {


        val placeFields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        Log.e("Ning", "搜索地点——————${placeId}")
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val latLng = place.latLng
            //终点
            tagPoint = MiddlePoint(
                placeId,
                latLng.latitude, latLng.longitude, "", ""
            )
            Log.e("Ning", "搜索地点————成功——${tagPoint.toJson()}")
            stopLocationUpdates()
            googleMap?.apply {
                clear()
//                val latLng = LatLng(latLng.latitude, latLng.longitude,)
                // 添加标记
                googleMap!!.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))

                )
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        15f
                    )
                )
                getRoute(LatLngG(latLng.latitude, latLng.longitude), place.name)

            }
        }.addOnFailureListener { exception ->

            Log.e("Ning", "搜索地点异常____$exception")
            // 处理错误
            exception.printStackTrace()
        }


    }


    var route = ""
    fun getRoute(tag: LatLngG, name: String) {

        var originPoint =
            LocationG(LatLngG(currentLat, currentLng))

        val middlePoint = arrayListOf<com.drivona.speed.api.Destination>()

        val entity = RoutesEntity(
            true,
            com.drivona.speed.api.Destination(
                LocationG(
                    tag
                )
            ),
            "en-US",
            Origin(originPoint),
            RouteModifiers(false, false, false),
            "TRAFFIC_AWARE_OPTIMAL",
            "DRIVE",
            "METRIC",
            middlePoint
        )

        scopeNet {


            val data = computeRoutes(entity)
            Log.e("Song", data.toJson())


            if (data.routes?.isNotEmpty() == true) {
                mNavigationScreen.setRoute(data.routes[0], name)
                route = data.routes[0].routeToken
                Log.e("Route", "选中路线————${route}")


            } else {
                Log.e("Song", "------------找不到路线----------------")
            }

        }

    }

    /**
     * Requests directions from the user's current location to a specific place (provided by the
     * Google Places API).
     */
    private fun navigateToPlace() {

        val list = arrayListOf<Waypoint>()

        list.add(Waypoint.builder().setLatLng(tagPoint.latitude, tagPoint.longitude).build())


        navigator?.apply {
            // 1. 先停止旧导航，清空路线缓存
           clearDestinations()
            stopGuidance()

            clearDestinations()
//
            if (mService != null) {
                Log.e(NavigationService.TAG, "Start_Navigation__mService != null")
                mService!!.startNavigation()
            }
            val pendingRoute = setDestinations(list)

            // Set an action to perform when a route is determined to the destination
            pendingRoute.setOnResultListener { code ->
                when (code) {
                    RouteStatus.OK -> {

                        isStart = true
                        // Enable voice audio guidance (through the device speaker)
                        setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE)

                        // Simulate vehicle progress along the route (for demo/debug builds)

                        if (BuildConfig.DEBUG) {
                            simulator.simulateLocationsAlongExistingRoute(
                                SimulationOptions().speedMultiplier(5f)
                            )
                        }

                        // Start turn-by-turn guidance along the current route
                        startGuidance()
                        navigator!!.registerServiceForNavUpdates(
                            carContext.packageName,
                            NavInfoReceivingService::class.java.name,
                            Int.MAX_VALUE,
                        )

                    }

                    RouteStatus.ROUTE_CANCELED -> {
                        isStart = false
                        // Return to top-down perspective
                        Log.e("Song", "Route guidance cancelled.")

                    }

                    RouteStatus.NO_ROUTE_FOUND,
                    RouteStatus.NETWORK_ERROR,
                        -> {
                        stopNav()
                        // TODO: Add logic to handle when a route could not be determined
                        Log.e("Song", "Error starting guidance: $code")
                    }

                    else -> Log.e("Song", "Error starting guidance: $code")
                }
            }

        }

    }

    fun setNavInfo(navIfo: NavInfo) {
        var trave: TravelEstimate? = null
        if (navIfo.distanceToFinalDestinationMeters != null) {
            trave = TravelEstimate.Builder(
                Distance.create(
                    navIfo.distanceToFinalDestinationMeters.toDouble(),
                    Distance.UNIT_METERS
                ),
                DemoScripts.getCurrentDateTimeZoneWithOffset(navIfo.timeToFinalDestinationSeconds)
            )
                .setRemainingTimeSeconds(
                    navIfo.timeToFinalDestinationSeconds.toLong()
                )
                .build()
        }

        mService!!.updateInfo(
            trave,  /* isRerouting= */
            navIfo

        )
    }

    fun stopNav() {
        if (mService != null) {
            mService!!.stopNavigation()


        }
    }
}
