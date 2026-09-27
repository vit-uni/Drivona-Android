/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drivona.speed.utils.map

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.drake.channel.sendEvent
import com.drake.channel.sendTag
import com.google.android.libraries.mapsplatform.turnbyturn.model.DrivingSide
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavInfo
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavState
import com.google.android.libraries.mapsplatform.turnbyturn.model.StepInfo
import com.lalifa.extension.gone
import com.lalifa.extension.toJson
import com.drivona.speed.R
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.IsChao
import com.drivona.speed.utils.map.NavInfoBottomDisplayFragment.DistanceUnitConst.M_PER_FT
import com.drivona.speed.utils.map.NavInfoBottomDisplayFragment.DistanceUnitConst.M_PER_KM
import com.drivona.speed.utils.map.NavInfoBottomDisplayFragment.DistanceUnitConst.M_PER_MI
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.text.toInt

/**
 * Shows navigation information from the receiving service in a separate header fragment above the
 * base navigation fragment.
 */
class NavInfoBottomDisplayFragment : Fragment() {
    private var displayHeader: View? = null
    private var selectedStepNumber = -1
    private var headerNavInfo: NavInfo? = null
    private var showingCurrentStep = true

    /** Returns whether the displayed step is the current step rather than a future step preview. */
    private val isDisplayedStepCurrentStep: Boolean
        get() =
            headerNavInfo?.currentStep != null &&
                    headerNavInfo?.currentStep?.stepNumber == selectedStepNumber &&
                    headerNavInfo?.distanceToCurrentStepMeters != null &&
                    headerNavInfo?.timeToCurrentStepSeconds != null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_nav_info_bottom_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayHeader = view

        showAwaitingNavigationText()
        // Observe live data for nav info updates.
        val navInfoObserver = Observer { navInfo: NavInfo? ->
            headerNavInfo = navInfo
            headerNavInfo?.let { showNavInfo(it) }
        }

        NavInfoReceivingService.navInfoLiveData.observe(this.viewLifecycleOwner, navInfoObserver)

    }

    private fun showNavInfo(navInfo: NavInfo) {
        when (navInfo.navState) {
            NavState.REROUTING -> {
                // Rerouting: Clear the header and indicate that we're rerouting.
                clearHeader()
                displayHeader?.findViewById<TextView>(R.id.tv_primary_text)?.text = "Rerouting..."

            }

            NavState.STOPPED -> {
                // Stopped: Nav has stopped, so clear the header and indicate that we're awaiting
                // navigation.
                clearHeader()
                showAwaitingNavigationText()
            }

            NavState.ENROUTE -> {
                navInfo.currentStep?.let { currentStep ->
                    // Enroute:
                    // Show the latest current step if
                    //  1) The last shown step was the current step.
                    //  2) This is the first step to be shown.
                    //  3) If the route has changed since the last message.
                    // Otherwise, continue to show whichever step is currently being shown, which may be
                    // a step preview.
                    if (
                        navInfo.routeChanged ||
                        selectedStepNumber < 0 ||
                        showingCurrentStep ||
                        !isStepNumberAvailable(navInfo, selectedStepNumber)
                    ) {
                        currentStep.stepNumber?.let { selectedStepNumber = it }
                    }
                    showSelectedStep(navInfo)
                }
            }

            else -> showToast("Received unknown NavInfo.")
        }
    }

    /**
     * Checks if a step number is part of the route. This includes the current step and remaining
     * steps.
     */
    private fun isStepNumberAvailable(navInfo: NavInfo?, stepNumber: Int): Boolean {
        val currentStepNumber = navInfo?.currentStep?.stepNumber ?: return false

        if (navInfo.remainingSteps.isEmpty()) {
            return stepNumber == currentStepNumber
        }
        val lastAvailableStepNumber =
            navInfo.remainingSteps[navInfo.remainingSteps.size - 1].stepNumber ?: return false
        return stepNumber in currentStepNumber..lastAvailableStepNumber
    }

    /** Shows the step selected by the user. This could be a current or remaining step. */
    private fun showSelectedStep(navInfo: NavInfo) {
        val currentStepNumber = navInfo.currentStep?.stepNumber ?: return

        val selectedStep =
            if (selectedStepNumber != currentStepNumber) {
                // If the selected step is not the current step, then it must be a step preview.
                // Subtract the current step number from the selected step number to get the index
                // of the selected step in the array of remaining steps.
                navInfo.remainingSteps[selectedStepNumber - currentStepNumber - 1]
            } else {
                navInfo.currentStep
            } ?: return

        showingCurrentStep = selectedStep.stepNumber == currentStepNumber

        // Show the full road name, maneuver icon, time and distance to step, and further details.
        displayHeader?.findViewById<TextView>(R.id.tv_primary_text)?.text =
            getDistanceFormatted(navInfo.distanceToFinalDestinationMeters)

        setTimeAndDistanceToSelectedStepTexts(selectedStep, navInfo)
        setHeaderDetailTexts(selectedStep, navInfo)

    }

    private fun setTimeAndDistanceToSelectedStepTexts(selectedStep: StepInfo, navInfo: NavInfo) {
        // Get the estimated remaining time and distance to the current step.


    }


    var currentSpeed = 0
    var currentSpeedLimit = 0
    fun setSpeed(speed: Int) {
        currentSpeed = speed
        displayHeader?.findViewById<TextView>(R.id.tv_speed)?.text = speed.toString()
        if (currentSpeedLimit != 0) {
            currentDevice?.apply {
                if (speed_unit != 2) {
                    Log.e(
                        "Speed",
                        "currentSpeed___${speed}__${speed} __${alarm_threshold} ————${currentSpeedLimit.toInt() + alarm_threshold}"
                    )
                    //km.h
                    displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible =
                        speed > currentSpeedLimit.toInt() + alarm_threshold && displayHeader?.findViewById<View>(
                            R.id.limit_bg
                        )!!.isVisible
                } else {
                    displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible =
                        speed > currentSpeedLimit.toInt() * (1 + alarm_threshold.toDouble() / 100) && displayHeader?.findViewById<View>(
                            R.id.limit_bg
                        )!!.isVisible
                }

                sendEvent(IsChao(displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible == true))
            }
        }
    }


    fun setSpeedLimit(speed: String, isVisi: Boolean) {
        if (speed.isNullOrEmpty()) {
            displayHeader?.findViewById<TextView>(R.id.tv_xian)?.gone()
            displayHeader?.findViewById<View>(R.id.limit_bg)?.gone()
            return
        }
        if (speed == "0") {
            currentSpeedLimit = 0
            displayHeader?.findViewById<TextView>(R.id.tv_xian)?.isVisible = false
            displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible = false
        } else {
            if (currentSpeedType == "km/h") {
                displayHeader?.findViewById<TextView>(R.id.tv_xian)?.text = speed
                currentSpeedLimit = speed.toInt()
            } else {
                currentSpeedLimit = (speed.toInt() * 0.621371).toInt()
                displayHeader?.findViewById<TextView>(R.id.tv_xian)?.text =
                    (speed.toInt() * 0.621371).toInt().toString()
            }

            displayHeader?.findViewById<TextView>(R.id.tv_xian)?.isVisible = true
            displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible = isVisi
            currentDevice?.apply {

                if (speed_unit != 2) {
                    Log.e(
                        "Speed",
                        "currentSpeed___${currentSpeed}__${speed} __${alarm_threshold} ————${speed.toInt() + alarm_threshold}"
                    )
                    if (currentSpeedType == "km/h") {
                        //km.h
                        displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible =
                            currentSpeed > speed.toInt() + alarm_threshold && isVisi
                    } else {
                        //km.h
                        displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible =
                            currentSpeed > currentSpeedLimit.toInt() + alarm_threshold  && isVisi
                    }

                } else {
                    //百分比
                    Log.e(
                        "Speed",
                        "currentSpeed___${currentSpeed}__${speed} __${alarm_threshold} ————${speed.toInt() * (1 + alarm_threshold.toDouble() / 100)}__${(1 + alarm_threshold.toDouble() / 100)}"
                    )
                    if (currentSpeedType == "km/h") {
                        //km.h
                        displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible =
                            currentSpeed > speed.toInt() * (1 + alarm_threshold.toDouble() / 100) && isVisi
                    } else {
                        displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible =
                            currentSpeed > currentSpeedLimit.toInt() * (1 + alarm_threshold.toDouble() / 100) && isVisi
                    }
                }
                sendEvent(IsChao(displayHeader?.findViewById<View>(R.id.limit_bg)?.isVisible == true))
            }
        }

    }

    var currentDevice: DeviceData? = null
    fun setDeviceData(device: DeviceData?) {
        currentDevice = device
    }

    var currentDanwei = "km"
    fun setDanwei(danwei: String) {
        currentDanwei = danwei
    }

    var currentSpeedType = "km/h"
    fun setSpeedType(speed: String) {
        currentSpeedType = speed
        displayHeader?.findViewById<TextView>(R.id.tv_speed_desc)?.text = speed
    }

    /**
     * Returns the distance in the format of "mi" or "ft". Only shows ft if remaining distance is less
     * than 0.25 miles.
     *
     * @param distanceMeters the distance in meters.
     * @return the distance in the format of "mi" or "ft".
     */
    private fun getDistanceFormatted(distanceMeters: Int?): String {
        distanceMeters ?: return "Unknown Distance"

        // Distance can be negative so set the min distance to 0.
        // Only show the tenths place digit if the distance is less than 10 miles.
        // Only show feet if the distance is less than 0.25 miles.
        return getDisplayDistance(distanceMeters.toDouble(), currentDanwei != "mi")

    }

    /**
     * Returns the time in the format of "hr min sec". Only shows hr if remaining minutes > 60. Only
     * shows min if remaining minutes % 60 != 0. Only shows sec if remaining minutes < 1.
     *
     * @param timeSeconds the time in seconds
     * @return the time in the format of "hr min sec".
     */
    private fun getTimeFormatted(timeSeconds: Int?): StringBuilder {
        timeSeconds ?: return StringBuilder().append("Unknown Time")

        val remainingSeconds = timeSeconds.coerceAtLeast(0)
        val remainingHours = remainingSeconds / 3600
        val remainingMinutesRounded = (remainingSeconds % 3600.0 / 60).roundToInt()
        val timeBuilder = StringBuilder()
        if (remainingHours > 0) {
            timeBuilder.append(remainingHours).append(" hr ")
        }
        if (remainingMinutesRounded > 0 && timeSeconds >= 60) {
            timeBuilder.append(remainingMinutesRounded).append(" min ")
        }
        if (remainingSeconds < 60) {
            timeBuilder.append(remainingSeconds).append(" sec ")
        }
        return timeBuilder
    }

    private fun getTimeNewFormatted(timeSeconds: Int?): String {
        timeSeconds ?: return "Unknown Time"
        val stime = System.currentTimeMillis() + timeSeconds * 1000
        return getRelativeTimeDesc(stime)
    }

    /** Shows detailed navigation information. */
    private fun setHeaderDetailTexts(stepInfo: StepInfo, navInfo: NavInfo) {

        displayHeader?.findViewById<TextView>(R.id.tv_distance_to_step)?.text =
            getTimeFormatted(navInfo.timeToFinalDestinationSeconds)

        displayHeader?.findViewById<TextView>(R.id.tv_primary_text)?.text =
            getDistanceFormatted(navInfo.distanceToFinalDestinationMeters)

        Log.e("Song", "______${navInfo.toJson()}")

    }


    private fun clearHeader() {

        for (tvId in HEADER_TEXTVIEWS) {
            displayHeader?.findViewById<TextView>(tvId)?.text = ""
        }
        showingCurrentStep = true
        selectedStepNumber = -1
    }

    private fun showAwaitingNavigationText() {
        displayHeader?.findViewById<TextView>(R.id.tv_primary_text)?.text = ""
    }

    private fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "NavInfoDisplay"

        /**
         * Conversion values for imperial measurement units. This sample app simply shows imperial
         * units. In your real app, you may want to use locale settings to determine whether to display
         * metric or imperial units.
         */
        private const val MIN_MILES_TO_SHOW_INTEGER = 10
        private const val FEET_PER_MILE = 5280
        private const val FEET_PER_METER = 3.28

        private val mDrivingSideStrings: Map<Int, String> =
            mapOf(
                DrivingSide.NONE to "NONE",
                DrivingSide.LEFT to "LEFT",
                DrivingSide.RIGHT to "RIGHT"
            )

        private val HEADER_TEXTVIEWS =
            intArrayOf(
                R.id.tv_primary_text,
                R.id.tv_distance_to_step,
            )


    }


    fun getRelativeTimeDesc(targetTimeStamp: Long): String {
        // 校验时间戳合法性（避免负数或过大值）
        if (targetTimeStamp <= 0) {
            return "无效时间"
        }

        val currentCalendar = Calendar.getInstance() // 当前时间
        val targetCalendar = Calendar.getInstance().apply {
            timeInMillis = targetTimeStamp // 目标时间
        }

        // 1. 计算日期差（今天=0，明天=1，后天=2）
        val dayDiff = calculateDayDifference(currentCalendar, targetCalendar)

        // 2. 解析时段（上午/下午）
        val period = getTimePeriod(targetCalendar)

        // 3. 解析小时和分钟（补0，如 3点05分 而非 3点5分）
        val hour = targetCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = targetCalendar.get(Calendar.MINUTE)
        val minuteStr = if (minute < 10) "0$minute" else minute.toString()

        // 4. 拼接最终描述
        return when (dayDiff) {
            0 -> "${hour}:${minuteStr}"
            1 -> "明天${hour}:${minuteStr}"
            2 -> "后天${hour}${minuteStr}"
            else -> {
                // 超过后天则返回具体日期（扩展场景）
                val year = targetCalendar.get(Calendar.YEAR)
                val month = targetCalendar.get(Calendar.MONTH) + 1 // 月份从0开始
                val day = targetCalendar.get(Calendar.DAY_OF_MONTH)
                "${month}-${day} $period${hour}:${minuteStr}"
            }
        }
    }

    /**
     * 计算两个时间的日期差（仅按日期算，忽略时分秒）
     */
    private fun calculateDayDifference(current: Calendar, target: Calendar): Int {
        // 重置当前时间的时分秒为0，只保留日期
        val currentDate = Calendar.getInstance().apply {
            timeInMillis = current.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 重置目标时间的时分秒为0
        val targetDate = Calendar.getInstance().apply {
            timeInMillis = target.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 计算天数差（1天=86400000毫秒）
        val diffMillis = targetDate.timeInMillis - currentDate.timeInMillis
        return (diffMillis / 86400000).toInt()
    }

    /**
     * 判断时间属于上午还是下午
     * 上午：00:00-11:59，下午：12:00-23:59
     */
    private fun getTimePeriod(calendar: Calendar): String {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return if (hour in 0..11) "上午" else "下午"
    }

    // 扩展方法：获取当前时间戳（毫秒）
    fun getCurrentTimeStamp(): Long = System.currentTimeMillis()
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

    enum class DistanceUnit { KM, M, MI, FT }

    fun formatDistance(meters: Double, unit: DistanceUnit, decimal: Int = 2): String {
        val value = when (unit) {
            DistanceUnit.KM -> meters.mToKm()
            DistanceUnit.M -> meters
            DistanceUnit.MI -> meters.mToMi()
            DistanceUnit.FT -> meters.mToFt()
        }
        return "%.${decimal}f".format(value)
    }


    object DistanceUnitConst {
        // 1km = 1000m
        const val M_PER_KM = 1000.0

        // 1mi = 1609.344m
        const val M_PER_MI = 1609.344

        // 1ft = 0.3048m
        const val M_PER_FT = 0.3048
    }
}

/** 米 -> 千米 km */
fun Double.mToKm(): Double = this / M_PER_KM

/** 米 -> 英里 mi */
fun Double.mToMi(): Double = this / M_PER_MI

/** 米 -> 英尺 ft */
fun Double.mToFt(): Double = this / M_PER_FT

/** 千米 -> 米 m */
fun Double.kmToM(): Double = this * M_PER_KM

/** 英里 -> 米 m */
fun Double.miToM(): Double = this * M_PER_MI

/** 英尺 -> 米 m */
fun Double.ftToM(): Double = this * M_PER_FT