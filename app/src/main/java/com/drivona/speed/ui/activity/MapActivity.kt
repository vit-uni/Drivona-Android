package com.drivona.speed.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import androidx.core.view.isVisible
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.blankj.utilcode.util.SPUtils
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.channel.receiveEvent
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNet
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.PolyUtil
import com.lalifa.base.BaseActivity
import com.lalifa.extension.getIntentDouble
import com.lalifa.extension.getIntentSerializable
import com.lalifa.extension.getIntentString
import com.lalifa.extension.globalUITask
import com.lalifa.extension.gone
import com.lalifa.extension.invisible
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.toJson
import com.lalifa.extension.toast
import com.lalifa.extension.visible
import com.drivona.speed.BuildConfig
import com.drivona.speed.MApplication
import com.drivona.speed.R
import com.drivona.speed.api.CameraDeviceData
import com.drivona.speed.api.CarLocation
import com.drivona.speed.api.Destination
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.LatLngG
import com.drivona.speed.api.LocationData
import com.drivona.speed.api.LocationG
import com.drivona.speed.api.MiddlePoint
import com.drivona.speed.api.Origin
import com.drivona.speed.api.RouteModifiers
import com.drivona.speed.api.RouteResult
import com.drivona.speed.api.RoutesEntity
import com.drivona.speed.api.SearchPlaceData
import com.drivona.speed.api.SearchPlaceOtherData
import com.drivona.speed.api.UserInfoManager
import com.drivona.speed.api.addressAdd
import com.drivona.speed.api.computeRoutes
import com.drivona.speed.api.getCamera
import com.drivona.speed.api.getEquipmentList
import com.drivona.speed.api.getMapEvent
import com.drivona.speed.databinding.ActivityMapBinding
import com.drivona.speed.ext.showAddDescDialog
import com.drivona.speed.ext.showNoticeDialog
import com.drivona.speed.ext.showSetSuccessDialog
import com.drivona.speed.ui.adapter.locationList
import com.drivona.speed.ui.adapter.locationRouteList
import com.google.android.gms.maps.model.MapColorScheme


class MapActivity : BaseActivity<ActivityMapBinding>(),
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, OnMapClickListener {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient
    private lateinit var queue: RequestQueue
    override fun getViewBinding() = ActivityMapBinding.inflate(layoutInflater)
    var placeId = ""

    var placeName = ""
    var placeDesc = ""
    var placeLat = 0.0
    var placeLong = 0.0
    var currentLat: Double = 0.0
    var currentLng: Double = 0.0
    var googleMap: GoogleMap? = null

    //终点
    var tagPoint: MiddlePoint = MiddlePoint("", 0.0, 0.0, "", "")

    //途径点
    val middlePointPlaceId = arrayListOf<MiddlePoint>()

    //起点
    var originPointPlaceId = MiddlePoint("", 0.0, 0.0, "", "")

    lateinit var dialog: BubbleDialog
    lateinit var device: DeviceData

    @SuppressLint("MissingPermission")
    override fun initView() {
        originPointPlaceId = MiddlePoint("", 0.0, 0.0, getString(R.string.starting_point), "")

        dialog = BubbleDialog(this)
        device = getIntentSerializable<DeviceData>("device")!!
        binding.apply {
            llMoni.isVisible = BuildConfig.DEBUG
            val mapFragment: SupportMapFragment? =
                supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            mapFragment?.getMapAsync(this@MapActivity)

            queue = Volley.newRequestQueue(this@MapActivity)
            placeId = getIntentString("placeId").pk()
            placeName = getIntentString("placeName").pk()
            placeDesc = getIntentString("placeDesc").pk()
            placeLat = getIntentDouble("placeLat")
            placeLong = getIntentDouble("placeLong")

            tagPoint = MiddlePoint(placeId, placeLat, placeLong, placeName, placeDesc)

            placesClient = MApplication.placesClient
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this@MapActivity);

            recLocation.locationList().apply {
                models = listOf(getString(R.string.starting_point))
                onFastClick(R.id.iv_add, R.id.tv_name) {
                    when (it) {
                        R.id.iv_add -> {
//                            if (modelPosition == 0) {
//                                start<SearchLocationOtherActivity> {
//                                    putExtra("position", -1)
//                                }
//                            } else {

                                changeRoute()


//                            }
                        }

                        R.id.tv_name -> {

                            start<SearchLocationOtherActivity> {
                                putExtra("isFirst", modelPosition == 0)
                                putExtra("position", modelPosition)
                            }

                        }
                    }

                }
            }
            var danwei = UserInfoManager.get()!!.distance
            recRoutes.locationRouteList(danwei).apply {
                onFastClick(R.id.item) {
                    route = getModel<RouteResult>().routeToken
                    Log.e("Route","选中路线————${route}")
                    setChecked(modelPosition, true)

                }
                onChecked { position, checked, allChecked ->
                    val model = getModel<RouteResult>(position)
                    model.select = checked
                    notifyDataSetChanged()
                    drawLines(recRoutes.models as List<RouteResult>)
                }
            }


            ivCamera1.onClick {
                googleMap?.apply {
                    val resetPosition = CameraPosition.Builder()
                        .target(getCameraPosition().target) // 保持当前中心点
                        .zoom(getCameraPosition().zoom) // 保持当前缩放级别
                        .bearing(0f) // 重置旋转角度
                        .tilt(0f) // 重置倾斜角度
                        .build()
                    animateCamera(CameraUpdateFactory.newCameraPosition(resetPosition))
                }


            }


            // Ensure the screen stays on during nav.
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        }
        receiveEvent<LocationData> {

            binding.apply {
                clLocationDetail.visible()
                isViewShow = true
                binding.clLocationDetail.translationY = 0f
                tvLocationName.text = it.title
                tvLocationDesc.text = it.address
            }
            tagPoint =
                MiddlePoint(
                    it.place_id,
                    it.lat.toDouble(),
                    it.long.toDouble(),
                    it.title,
                    it.address
                )

            googleMap?.clear()
            // 添加标记
            googleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(tagPoint.latitude, tagPoint.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))
                    .title(it.title ?: "未知地点")
            )
            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(tagPoint.latitude, tagPoint.longitude),
                    15f
                )
            )

        }
        receiveEvent<SearchPlaceData> {

            binding.apply {
                clLocationDetail.visible()
                isViewShow = true
                binding.clLocationDetail.translationY = 0f
                isViewShow = true
                clRoutes.gone()
                binding.etSearch.visible()
                binding.ivLocation1.visible()
                binding.ivCamera1.visible()
                tvLocationName.text = it.placeName
                tvLocationDesc.text = it.placeDesc
                tagPoint.placeId = it.placeId
                tagPoint.placeName = it.placeName
                tagPoint.placeDesc = it.placeDesc
            }

            fetchPlaceDetails(it.placeId)
        }
        receiveEvent<SearchPlaceOtherData> {
            val placeFields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val request = FetchPlaceRequest.newInstance(it.placeId, placeFields)
            Log.e("Ning", "搜索地点")
            placesClient.fetchPlace(request).addOnSuccessListener { response ->

                val place = response.place
                val latLng = place.latLng


                if (latLng != null) {
                    if (it.isFirst) {

                        originPointPlaceId = MiddlePoint(
                            it.placeId,
                            latLng.latitude,
                            latLng.longitude,
                            it.placeName, response.place.address
                        )
                        googleMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(latLng.latitude, latLng.longitude),
                                15f
                            )
                        )

                    } else {
                        //修改
                        if (it.position != -1) {
                            if (it.position != binding.recLocation.bindingAdapter.modelCount - 1) {
                                //修改途径点
                                val data = middlePointPlaceId.get(it.position - 1)
                                data.latitude = latLng.latitude
                                data.longitude = latLng.longitude
                                data.placeId = it.placeId
                                data.placeName = it.placeName
                                data.placeDesc = it.placeDesc

                            } else {
                                //修改目的地
                                tagPoint = MiddlePoint(
                                    it.placeId,
                                    latLng.latitude,
                                    latLng.longitude,
                                    it.placeName,
                                    it.placeDesc
                                )
                                googleMap?.clear()
                                // 添加标记
                                googleMap!!.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(tagPoint.latitude, tagPoint.longitude))
                                        .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))
                                        .title(tagPoint.placeName ?: "未知地点")
                                )
                                googleMap?.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(tagPoint.latitude, tagPoint.longitude),
                                        15f
                                    )
                                )
                            }

                        } else {
                            //添加途径点
                            middlePointPlaceId.add(
                                MiddlePoint(
                                    it.placeId,
                                    latLng.latitude,
                                    latLng.longitude, place.name, it.placeDesc
                                )
                            )

                        }

                    }
                    binding.clRoutes.gone()
                    binding.etSearch.visible()
                    binding.ivLocation1.visible()
                    binding.ivCamera1.visible()
                    getRoute()
                }
            }.addOnFailureListener { exception ->
                Log.e("Song", "搜索地点异常____$exception")
                // 处理错误
                exception.printStackTrace()
            }

        }
    }

    private var startY = 0f
    private var isViewShow = true
    private val touchThreshold = 80f //滑动距离阈值，大于这个距离才触发隐藏
    override fun onClick() {
        binding.apply {

            val lat = SPUtils.getInstance().getFloat("lat")
            val lng = SPUtils.getInstance().getFloat("lng")
            if (lat != 0f) {
                etLat.setText("22.283983")
                etLng.setText("114.139046")
//                etLat.setText(lat.toString())
//                etLng.setText(lng.toString())
            }
            tvClear.onClick {
                SPUtils.getInstance().put("lat", 0f)
                SPUtils.getInstance().put("lng", 0f)
                toast("已清空")
                etLat.setText("0")
                etLng.setText("0")
            }
            tvSave.onClick {
                val lat = etLat.text().pk()
                val lng = etLng.text().pk()
                if (lat.isNullOrEmpty() && lng.isNullOrEmpty()) {
                    SPUtils.getInstance().put("lat", 0f)
                    SPUtils.getInstance().put("lng", 0f)
                    toast("已清空")
                } else if (lat.isNullOrEmpty() || lng.isNullOrEmpty()) {
                    toast("请填写坐标点")
                } else {
                    SPUtils.getInstance().put("lat", lat.toFloat())
                    SPUtils.getInstance().put("lng", lng.toFloat())
//                    SPUtils.getInstance().put("lat",22.366201f)
//                    SPUtils.getInstance().put("lng",114.011084f)
                    toast("已保存")

                }
            }
            clRoutes.onClick {

            }

            clLocationDetail.setOnTouchListener { _, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        startY = event.y
                    }

                    android.view.MotionEvent.ACTION_UP -> {
                        val diffY = event.y - startY
                        // diffY >0 = 向下滑动；diffY <0 =向上滑动
                        if (diffY > touchThreshold && isViewShow) {
                            //向下滑动，隐藏控件

                            binding.clLocationDetail.animate()
                                .translationY(binding.clLocationDetail.height.toFloat())
                                .setDuration(250)
                                .withEndAction {
                                    // ✅动画执行完毕再GONE
                                    binding.clLocationDetail.visibility = android.view.View.GONE

                                }
                                .start()

                        }
                    }
                }
                true //返回true：消费触摸事件，子view无法接收；false不消费
            }
            ivLocation1.onClick {
                googleMap?.apply {
                    val resetPosition = CameraPosition.Builder()
                        .target(LatLng(currentLat, currentLng)) // 保持当前中心点
                        .zoom(getCameraPosition().zoom) // 保持当前缩放级别
                        .bearing(0f) // 重置旋转角度
                        .tilt(0f) // 重置倾斜角度
                        .build()
                    animateCamera(CameraUpdateFactory.newCameraPosition(resetPosition))
                }
            }
            ivCamera1.onClick {
                ivCamera1.isSelected = !ivCamera1.isSelected
                for (i in eventMap) {
                    i.value.isVisible = ivCamera1.isSelected
                }
            }
            ivBack.onClick {
                if (clRoutes.isVisible) {
                    clRoutes.gone()
                    binding.etSearch.visible()
                    binding.ivLocation1.visible()
                    binding.ivCamera1.visible()
                    googleMap?.clear()
                    return@onClick
                }
                if (clLocationDetail.isVisible) {
                    clLocationDetail.gone()
                    googleMap?.clear()
                    return@onClick
                }
                finish()
            }
            etSearch.setOnClickListener {
                start<SearchLocationActivity> { }
            }

            ivAdd.onClick {
                showAddDescDialog {
                    scopeNetLife {
                        addressAdd(
                            tagPoint.latitude,
                            tagPoint.longitude,
                            tagPoint.placeDesc,
                            it,
                            tagPoint.placeName,
                            tagPoint.placeId
                        )
                        showSetSuccessDialog()
                    }
                }
            }
            tvStartNavigate.onClick {
                start<SearchMapActivity> {
                    putExtra("device", device)
                    putExtra("placeId", tagPoint)

                    putExtra("route", route)
                    putExtra("middlePointPlaceId", middlePointPlaceId)
                    putExtra("orangePointPlaceId", originPointPlaceId)
                }
            }
            tvNavigate.onClick {

                //途径点集合
                val middlePoint = arrayListOf<Destination>()
                for (i in middlePointPlaceId) {
                    middlePoint.add(Destination(LocationG(LatLngG(i.latitude, i.longitude))))
                }
                val entity = RoutesEntity(
                    true,
                    Destination(LocationG(LatLngG(tagPoint.latitude, tagPoint.longitude))),
                    "en-US",
                    Origin(LocationG(LatLngG(currentLat, currentLng))),
                    RouteModifiers(false, false, false),
                    "TRAFFIC_AWARE_OPTIMAL",
                    "DRIVE",
                    "METRIC",
                    middlePoint
                )
                scopeDialog(BubbleDialog(this@MapActivity, "")) {
                    val data = computeRoutes(entity)
                    Log.e("Song", data.toJson())

                    recRoutes.models = data.routes
                    if (data.routes?.isNotEmpty() == true) {
                        start<SearchMapActivity> {
                            putExtra("device", device)
                            putExtra("placeId", tagPoint)

                            putExtra("middlePointPlaceId", middlePointPlaceId)
//                            putExtra("orangePointPlaceId", originPointPlaceId)
                        }
                    } else {
                        showNoticeDialog()

                        if (BuildConfig.DEBUG) {
                            start<SearchMapActivity> {
                                putExtra("device", device)
                                putExtra("placeId", tagPoint)
//                                putExtra("orangePointPlaceId", originPointPlaceId)
                                putExtra("middlePointPlaceId", middlePointPlaceId)
                            }
                        }

                    }


                    clLocationDetail.gone()
                }

            }
            tvRoute.onClick {
                getRoute()

//                    GsonUtils.fromJson<RoutesResult::class.java>(data.)
            }
        }
    }

    var route = ""
    fun getRoute() {
        binding.apply {
            val list = arrayListOf<String>()
            list.add(originPointPlaceId.placeName)
            for (i in middlePointPlaceId) {
                list.add(i.placeName)
            }
            list.add(tagPoint.placeName)
            recLocation.models = list
            var originPoint =
                LocationG(LatLngG(originPointPlaceId.latitude, originPointPlaceId.longitude))

            val middlePoint = arrayListOf<Destination>()
            for (i in middlePointPlaceId) {
                middlePoint.add(Destination(LocationG(LatLngG(i.latitude, i.longitude))))
            }
            val entity = RoutesEntity(
                true,
                Destination(LocationG(LatLngG(tagPoint.latitude, tagPoint.longitude))),
                "en-US",
                Origin(originPoint),
                RouteModifiers(false, false, false),
                "TRAFFIC_AWARE_OPTIMAL",
                "DRIVE",
                "METRIC",
                middlePoint
            )
            clRoutes.visible()
            binding.etSearch.invisible()
            binding.ivLocation1.invisible()
            binding.ivCamera1.invisible()
            tvRouteState.visible()
            tvStartNavigate.gone()
            recRoutes.gone()
            tvRouteState.text =getString(R.string.calculating_route)
            scopeDialog(BubbleDialog(this@MapActivity, "")) {
                val data = computeRoutes(entity)
                Log.e("Song", data.toJson())

                recRoutes.models = data.routes
                if (data.routes?.isNotEmpty() == true) {
                    recRoutes.bindingAdapter.setChecked(0, true)
                    route = data.routes[0].routeToken
                    Log.e("Route","选中路线————${route}")

                    tvRouteState.gone()
                    tvStartNavigate.visible()
                    recRoutes.visible()
                    tvRouteState.text = getString(R.string.calculating_route)
                    val list = arrayListOf<List<LatLng>>()
                    for (i in data.routes) {
                        Log.e("SOng", i.toJson())
                        val listDot = arrayListOf<LatLng>()
                        Log.e("Song", "i.legs___${i.legs.size}")
                        for (a in i.legs) {
                            for (b in a.steps.indices) {
                                if (b == 0) {
                                    listDot.add(a.steps[b].startLocation.latLng)
                                    listDot.add(a.steps[b].endLocation.latLng)
                                } else {
                                    listDot.add(a.steps[b].endLocation.latLng)
                                }
                            }

                        }
                        Log.e("Song", "listDot___${listDot.size}")
                        list.add(listDot)
                    }
                    Log.e("Song", "list___${list.size}")
                    for (i in list) {
                        getCameraDevice(
                            tagPoint.longitude.toString(),
                            tagPoint.latitude.toString(),
                            route = i.toJson()
                        )
                    }
                } else {
                    tvRouteState.text = getString(R.string.no_routes_available_right_now)
                }

            }
        }

    }

    fun drawLines(routes: List<RouteResult>) {
        googleMap!!.clear()
        // 添加标记
        googleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(tagPoint.latitude, tagPoint.longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))

        )

        for (i in middlePointPlaceId) {
            // 添加标记
            googleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(i.latitude, i.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_location_middle)))

            )
        }
        drawEvent()
        drawCamera()

        for (i in routes) {
            if (i.select) {
                val path = PolyUtil.decode(i.polyline.encodedPolyline)
                val polylineOpt = PolylineOptions()
                    .addAll(path) // 绑定GMSPath点位
                    .width(12f)    // 线条宽度 dp
                    .color(0xFF346ee3.toInt()) // ARGB 红色路线
                    .geodesic(true) // 大地曲线（真实地球曲面路线）
                    .zIndex(100f)    // 层级，高于普通marker
                googleMap?.addPolyline(polylineOpt)
                val polylineOpt1 = PolylineOptions()
                    .addAll(path) // 绑定GMSPath点位
                    .width(8f)    // 线条宽度 dp
                    .color(0xFFffffff.toInt()) // ARGB 红色路线
                    .geodesic(true) // 大地曲线（真实地球曲面路线）
                    .zIndex(101f)    // 层级，高于普通marker
                googleMap?.addPolyline(polylineOpt1)
            } else {
                val path = PolyUtil.decode(i.polyline.encodedPolyline)
                val polylineOpt = PolylineOptions()
                    .addAll(path) // 绑定GMSPath点位
                    .width(10f)    // 线条宽度 dp
                    .color(0xFF346ee3.toInt()) // ARGB 红色路线
                    .geodesic(true) // 大地曲线（真实地球曲面路线）
                    .zIndex(10f)    // 层级，高于普通marker
                googleMap?.addPolyline(polylineOpt)
                val polylineOpt1 = PolylineOptions()
                    .addAll(path) // 绑定GMSPath点位
                    .width(6f)    // 线条宽度 dp
                    .color(0xFF0c327d.toInt()) // ARGB 红色路线
                    .geodesic(true) // 大地曲线（真实地球曲面路线）
                    .zIndex(11f)    // 层级，高于普通marker
                googleMap?.addPolyline(polylineOpt1)
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
            this@MapActivity.googleMap = googleMap
            setOnMyLocationButtonClickListener(this@MapActivity)
            setOnMyLocationClickListener(this@MapActivity)
            isMyLocationEnabled = true

            googleMap.uiSettings.isCompassEnabled = false
            googleMap.uiSettings.isMyLocationButtonEnabled = false

            googleMap.setOnPoiClickListener {
                middlePointPlaceId.clear()
                addMarker(it.latLng)
                originPointPlaceId =
                    MiddlePoint("", currentLat, currentLng, getString(R.string.starting_point), "")
                binding.apply {
                    clLocationDetail.visible()
                    isViewShow = true
                    binding.clLocationDetail.translationY = 0f
                    clRoutes.gone()
                    binding.etSearch.visible()
                    binding.ivLocation1.visible()
                    binding.ivCamera1.visible()
                    tvLocationName.text = it.name
                    recLocation.models = listOf(getString(R.string.starting_point))
                    recRoutes.models = null
//                        tvLocationDesc.text = it.
                }

                dialog.show()
                fetchPlaceDetails(it.placeId)

            }
            googleMap.setOnCameraIdleListener {
                val center =
                    googleMap.cameraPosition.target

                Log.e("Center", "纬度:" + center.latitude + ", 经度:" + center.longitude)


            }
            moveMapToCurrentLocation()
            if (placeId.isNotEmpty()) {
                sendEvent(SearchPlaceData(placeId, placeName, placeDesc, placeLat, placeLong))
            }

        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

    }


    private fun fetchPlaceDetails(placeId: String) {
        val placeFields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        Log.e("Ning", "搜索地点——————${placeId}")
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            googleMap?.clear()
            val place = response.place
            val latLng = place.latLng
            Log.e("Song", "latLng——————${place.latLng.toJson()}")
            tagPoint =
                MiddlePoint(placeId, latLng.latitude, latLng.longitude, place.name, place.address)
            if (latLng != null) {
                // 添加标记
                googleMap!!.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))
                        .title(place.name ?: "未知地点")
                )
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(tagPoint.latitude, tagPoint.longitude),
                        15f
                    )
                )
                sendEvent(CarLocation(tagPoint.latitude, tagPoint.longitude))
                binding.apply {
                    clLocationDetail.visible()
                    isViewShow = true
                    binding.clLocationDetail.translationY = 0f
                    tvLocationName.text = tagPoint.placeName
                    tvLocationDesc.text = tagPoint.placeDesc
                }
            }
            dialog.dismiss()
        }.addOnFailureListener { exception ->
            dialog.dismiss()
            Log.e("Song", "搜索地点异常____$exception")
            // 处理错误
            exception.printStackTrace()
        }
    }


    val eventMap = HashMap<String, Marker>()


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

    override fun onMapClick(location: LatLng) {

    }

    fun addMarker(location: LatLng) {
        googleMap?.clear()
        // 添加标记
        val marker = googleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_loaction)))
        )
    }

    // 获取当前定位，自动移动相机
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun moveMapToCurrentLocation() {
        fusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                originPointPlaceId = MiddlePoint(
                    "",
                    location.latitude,
                    location.longitude,
                    getString(R.string.starting_point),
                    ""
                )

                currentLat = location.latitude
                currentLng = location.longitude
                val currentLatLng = LatLng(location.latitude, location.longitude)
//                val currentLatLng = LatLng(currentLat, currentLng)
                // 移动并缩放，15为街道级别缩放
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                googleMap?.animateCamera(cameraUpdate)
            }
        }.addOnFailureListener {

        }
    }

    val cameraList = arrayListOf<CameraDeviceData>()
    val eventList = arrayListOf<CameraDeviceData>()

    // lat/lng: (48.1417978,11.579486)
    fun getCameraDevice(
        long: String = "8.778969",
        lat: String = "50.822749",
        direction: String = "6.7",
        distance: String = "0",
        street: String = "",
        route: String = "",
    ) {
        cameraList.clear()
        eventList.clear()
        scopeNet {
            val data = getCamera(long, lat, direction, distance, street, route)
            data.data?.apply {
                if (this.size > 0) {
                    cameraList.addAll(this)
                    drawCamera()
                }
                val dataEvent = getMapEvent(long, lat, direction, distance, street, route)
                dataEvent.data?.apply {
                    eventList.addAll(this)
                    drawEvent()
                }
            }
        }
    }

    fun drawCamera() {
        for (i in cameraList) {
            val location =
                LatLng(i.latitude.toDouble(), i.longitude.toDouble()) // 示例坐标（北京）
            // 添加标记
            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.ic_map_speed_camera)))
            )
            marker?.apply {
                eventMap.put(i.id.toString(), this)

            }

        }
    }

    fun drawEvent() {
        for (i in eventList) {

            val location = LatLng(
                i.latitude.toDouble(),
                i.longitude.toDouble()
            ) // 坐标加个偏移，避免重叠
            var bitmap: Bitmap
            when (i.type) {
                "20" -> {
                    bitmap = getBitMap(R.mipmap.ic_map_du)

                }

                "1", "2", "6", "7" -> bitmap = getBitMap(R.mipmap.ic_map_speed_camera)
                else -> {
                    bitmap = getBitMap(R.mipmap.ic_map_weixian)
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

    override fun onResume() {
        super.onResume()
        scopeNetLife {
            val data = getEquipmentList()
            data?.data?.let {
                for (i in it) {
                    if (i.uuid == device.uuid) {
                        i.connect= device.connect
                        device = i
                    }
                }
            }
        }
    }

    fun changeRoute() {
        tagPoint = originPointPlaceId.also { originPointPlaceId = tagPoint }
        middlePointPlaceId.reverse()
        getRoute()
    }

    override fun onBackPressed() {

        if (binding.clRoutes.isVisible) {

            binding.clRoutes.gone()
            binding.etSearch.visible()
            binding.ivLocation1.visible()
            binding.ivCamera1.visible()
            googleMap?.clear()

        } else {
            finish()
        }
    }


    fun isDarkModeAppCompat(context: Context): Boolean {
        val uiMode = context.resources.configuration.uiMode
        val mask = Configuration.UI_MODE_NIGHT_MASK
        return (uiMode and mask) == Configuration.UI_MODE_NIGHT_YES
    }
}