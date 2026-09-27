package com.drivona.speed.car

import android.text.Spannable
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.DurationSpan
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.Maneuver
import androidx.car.app.navigation.model.MapWithContentTemplate
import androidx.car.app.navigation.model.MessageInfo
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.RoutePreviewNavigationTemplate
import androidx.car.app.navigation.model.RoutingInfo
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.core.graphics.drawable.IconCompat
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.drake.net.utils.scopeNet
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.toast
import com.drivona.speed.R
import com.drivona.speed.api.DefaultLocationData
import com.drivona.speed.api.LocationData
import com.drivona.speed.api.LocationDataCar
import com.drivona.speed.api.RouteResult
import com.drivona.speed.api.getAddressDefaultList
import com.drivona.speed.ui.adapter.formatSecondToHM
import com.drivona.speed.ui.adapter.meterToKmOne
import com.drivona.speed.utils.AppUtils
import com.google.android.libraries.mapsplatform.turnbyturn.TurnByTurnManager
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavInfo
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavState
import com.google.android.libraries.mapsplatform.turnbyturn.model.StepInfo
import com.google.android.libraries.navigation.internal.qf.ho
import com.lalifa.ext.Tools.Companion.Car_Center
import com.lalifa.ext.Tools.Companion.Car_Exit
import com.lalifa.extension.toJson
import com.lalifa.utils.GsonUtil
import com.lalifa.utils.SPUtil

class NavScreen(carContext: CarContext) : Screen(carContext) {

    private var mIsNavigating = false
    private var mIsRerouting = false
    private var mHasArrived = false

    private var mDestinations: MutableList<Destination?>? = null

    private var mSteps: MutableList<Step>? = null
    private var navInfo: NavInfo? = null

    private var mStepRemainingDistance: Distance? = null

    private var mDestinationTravelEstimate: TravelEstimate? = null
    private var mShouldShowNextStep = false
    private var mShouldShowLanes = false
    var mJunctionImage: CarIcon? = null
    override fun onGetTemplate(): Template {

        if (routeResult != null) {
            // ========== 1. Pane：支持addAction放底部【开始导航】主按钮 ==========
            val pane = Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle(meterToKmOne(routeResult!!.distanceMeters) + "km")
                        .addText(formatSecondToHM(routeResult!!.duration))

                        .build()
                )

                // ✅ 底部大按钮，放在Pane里！
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.resources.getString(R.string.start_navigation))
                        .setOnClickListener {
                            sendTag("startCarMapNav")
                            routeResult= null
                            invalidate()
                        }
                        .build()
                )
                .build()

            // ========== 2. 内层 PaneTemplate，承载Pane和Header ==========
            val contentTemplate = PaneTemplate.Builder(pane)
                .setHeader(
                    Header.Builder()
                        .setStartHeaderAction(Action.BACK) //左上角返回箭头
                        .setTitle(addressName)
                        .build()
                )
                .build()

            // ========== 3. 地图侧边操作按钮（ActionStrip，图标按钮）可选 ==========
            val mapActionStrip = ActionStrip.Builder()
                .addAction(Action.PAN) //地图平移
                .build()

            // ========== 4. 组装MapWithContentTemplate ==========
            return MapWithContentTemplate.Builder()
                .setContentTemplate(contentTemplate) //内容+header+底部按钮全部在contentTemplate
                .setActionStrip(mapActionStrip) //地图侧边图标按钮（可选，不需要可以删掉这一行）
                .build()
        }


        val builder = NavigationTemplate.Builder()
        // ---------------------- 顶部按钮栏（可带文字标题） ----------------------
        val topActionStrip = ActionStrip.Builder()

        if (mIsNavigating) {

            val exitIcon = CarIcon.Builder(
                IconCompat.createWithResource(
                    getCarContext(),
                    R.drawable.ic_car_exit
                )
            ).build()
            topActionStrip.addAction(
                Action.Builder()
                    .setIcon(exitIcon)
                    .setOnClickListener {
                        sendTag(Car_Exit)
                    }
                    .build()
            )
            if (mDestinationTravelEstimate != null) {
                builder.setDestinationTravelEstimate(mDestinationTravelEstimate!!)
            }
            if (isRerouting()) {
                builder.setNavigationInfo(RoutingInfo.Builder().setLoading(true).build())
            } else if (mHasArrived) {
                val messageInfo = MessageInfo.Builder(
                    getCarContext().getString(R.string.navigation_arrived)
                ).build()
                builder.setNavigationInfo(messageInfo)
            } else {
                if (navInfo != null) {
                    val info = RoutingInfo.Builder()
                    val tmp = navInfo!!.currentStep
                    tmp?.apply {
                        val currentStep = AppUtils.buildStepFromStepInfo(carContext.resources, tmp)
                        info.setCurrentStep(
                            currentStep, Distance.create(
                                navInfo!!.distanceToCurrentStepMeters.toDouble(),
                                Distance.UNIT_METERS
                            )
                        )
                        if (mShouldShowNextStep && mSteps!!.size > 1) {
                            info.setNextStep(mSteps!!.get(1))
                        }
                        if (mJunctionImage != null) {
                            info.setJunctionImage(mJunctionImage!!)
                        }
                        builder.setNavigationInfo(info.build())
                    }


                }

            }
        } else {
            val iconHome = CarIcon.Builder(
                IconCompat.createWithResource(
                    getCarContext(),
                    R.drawable.ic_home
                )
            ).build()
            val iconCompany = CarIcon.Builder(
                IconCompat.createWithResource(
                    getCarContext(),
                    R.drawable.ic_company
                )
            ).build()
            val iconFavorite = CarIcon.Builder(
                IconCompat.createWithResource(
                    getCarContext(),
                    R.drawable.ic_favorite
                )
            ).build()

            val searchIconBuilder = CarIcon.Builder(
                IconCompat.createWithResource(
                    getCarContext(),
                    R.mipmap.ic_car_search
                )
            ).build()
            topActionStrip.addAction(
                Action.Builder()
                    .setIcon(searchIconBuilder)
                    .setOnClickListener {
                        // 搜索
                        screenManager.pushForResult(NavSearchScreen(carContext), object :
                            OnScreenResultListener {
                            override fun onScreenResult(p0: Any?) {

                            }

                        })

                    }
                    .build()
            )
            topActionStrip.addAction(
                Action.Builder()
                    .setTitle("Home")
                    .setIcon(iconHome)

                    .setOnClickListener {
                        Log.e("Song", "点击---------------")
                        val address = SPUtil.get("defaultAddress")
                        if (address.isNotEmpty()) {
                            val data = GsonUtil.json2Obj(address, DefaultLocationData::class.java)
                            data?.apply {
                                if (home != null) {
                                    homeData = home
                                    sendEvent(LocationDataCar(home.lat, home.long, home.place_id))
                                } else {
                                    toast("not set")
                                }

                            }
                        }
//                        scopeNet {
//                            Log.e("Song", "点击------请求接口---------")
//                            val data = getAddressDefaultList()
//                            data?.data?.apply {
//                                if (home != null) {
//                                    homeData = home
//                                    sendEvent(LocationDataCar(home.lat,home.long,home.place_id))
//                                } else {
//                                    toast("not set")
//                                }
//
//                            }
//                        }


                    }
                    .build()
            )
            topActionStrip.addAction(
                Action.Builder()
                    .setTitle("Company")
                    .setIcon(iconCompany)
                    .setOnClickListener {
                        Log.e("Song", "点击---------------")
                        scopeNet {
                            Log.e("Song", "点击------请求接口---------")
                            val data = getAddressDefaultList()
                            data?.data?.apply {
                                if (company != null) {
                                    sendEvent(
                                        LocationDataCar(
                                            company.lat,
                                            company.long,
                                            company.place_id
                                        )
                                    )
                                } else {
                                    toast("not set")
                                }

                            }
                        }


                    }
                    .build()
            )
            topActionStrip.addAction(
                Action.Builder()
                    .setTitle("Favorite")
                    .setIcon(iconFavorite)
                    .setOnClickListener {
                        Log.e("Song", "点击---------------")
                        scopeNet {
                            Log.e("Song", "点击------请求接口---------")
                            val data = getAddressDefaultList()
                            data?.data?.apply {
                                if (favorite != null) {
                                    sendEvent(
                                        LocationDataCar(
                                            favorite.lat,
                                            favorite.long,
                                            favorite.place_id
                                        )
                                    )
                                } else {
                                    toast("not set")
                                }

                            }
                        }


                    }
                    .build()
            )

        }

        // ---------------------- 右上角地图悬浮按钮栏（和官方Demo写法完全一样） ----------------------
        // PAN按钮图标，进入平移模式后变色

        val panIconBuilder = CarIcon.Builder(
            IconCompat.createWithResource(
                getCarContext(),
                R.drawable.ic_car_location
            )
        )
        val panIconEye = CarIcon.Builder(
            IconCompat.createWithResource(
                getCarContext(),
                R.drawable.ic_car_eye
            )
        )



        panIconBuilder.setTint(CarColor.BLUE)


        val mapActionStrip = ActionStrip.Builder()
        mapActionStrip.addAction(
            Action.Builder()
                .setIcon(
                    panIconBuilder.build()
                )
                .setOnClickListener {
                    // 回到中心点逻辑
                   sendTag(Car_Center)
                }
                .build()
        )
            .addAction(
                Action.Builder()
                    .setIcon(
                        panIconEye.build()
                    )
                    .setOnClickListener {
                        // 回到中心点逻辑
                        Log.e("Song", "点击----------------")
                    }
                    .build()
            )



        builder
            .setActionStrip(topActionStrip.build())          // 顶部文字按钮
            .setMapActionStrip(mapActionStrip.build())

        // ---------------------- 构建导航模板 ----------------------
        return builder.build()
    }

    /** Updates the navigation screen with the next instruction.  */
    fun updateTrip(
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


        // Demo：
        Log.e(NavigationService.TAG, "updateTrip  更新车机")
        mIsNavigating = isNavigating
        mIsRerouting = isRerouting
        mHasArrived = hasArrived
        mDestinations = destinations
        mSteps = steps
        mStepRemainingDistance = nextStepRemainingDistance
        mDestinationTravelEstimate = nextDestinationTravelEstimate
        mShouldShowNextStep = shouldShowNextStep
        mShouldShowLanes = shouldShowLanes
        mJunctionImage = junctionImage
        this.navInfo = navInfo
        invalidate()
    }

    private fun isRerouting(): Boolean {
        if (navInfo != null) {
            if (navInfo!!.navState == NavState.REROUTING) {
                return true
            }
        }
        return false

    }

    var homeData: LocationData? = null
    var favoriteData: LocationData? = null
    var companyData: LocationData? = null
    fun getHome() {

        scopeNetLife {
            val data = getAddressDefaultList()
            data?.data?.apply {
                if (home != null) {
                    homeData = home
                }
                if (favorite != null) {
                    favoriteData = favorite
                } else {
                }
                if (company != null) {
                    companyData = company
                } else {
                }
            }
        }

    }

    var routeResult: RouteResult? = null
    var addressName: String= ""
    fun setRoute(data: RouteResult,name:String) {
        routeResult = data
        addressName= name
        invalidate()
    }

}
