package com.drivona.speed.utils

import com.google.android.gms.maps.model.LatLng

///** 经纬度点位实体 */
//data class LatLng(
//    val latitude: Double,   // 纬度
//    val longitude: Double   // 经度
//)

object LocationDistanceUtil {
    // 地球平均半径：米
    private const val EARTH_RADIUS = 6371000.0

    /**
     * Haversine公式：两点经纬度直线距离(米)
     */
    fun calculateDistance(start: LatLng, end: LatLng): Double {
        val lat1 = Math.toRadians(start.latitude)
        val lon1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lon2 = Math.toRadians(end.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return EARTH_RADIUS * c
    }

    /**
     * 筛选：当前位置前方1000米内所有途经点，附带距离
     * @param currentLoc 当前GPS坐标
     * @param allPassPoints 整条路线所有途经点有序列表
     * @param aheadMeter 前方范围：1000m
     * @return 筛选结果：点位+距离，按距离由近到远排序
     */
    fun getAheadPointsWithinDistance(
        currentLoc: LatLng,
        allPassPoints: List<LatLng>,
        aheadMeter: Double = 1000.0
    ): List<Pair<LatLng, Double>> {
        val result = mutableListOf<Pair<LatLng, Double>>()

        // 遍历路线有序途经点
        for (point in allPassPoints) {
            val dist = calculateDistance(currentLoc, point)
            // 距离小于等于1000米
            if (dist <= aheadMeter) {
                result.add(point to dist)
            }
        }

        // 按距离升序：最近的排在最前
        return result.sortedBy { it.second }
    }

    // ========== 进阶：精准判断【前方】点位（推荐导航场景使用） ==========
    /**
     * 获取当前前进方向朝向 + 判断途经点在前方
     * @param current 当前位置
     * @param prevLoc 上一秒定位点（用来算出行驶方向）
     * @param target 要判断的点位
     * @return true=点位在前进方向前方
     */
    fun isPointAhead(
        current: LatLng,
        prevLoc: LatLng,
        target: LatLng
    ): Boolean {
        // 1. 计算自身行驶方位角（车头朝向）
        val bearingCar = getBearing(prevLoc, current)
        // 2. 计算当前位置指向目标点的方位角
        val bearingTarget = getBearing(current, target)
        // 3. 两个方向夹角差值小于90° = 在前方
        var angleDiff = bearingTarget - bearingCar
        if (angleDiff > 180) angleDiff -= 360
        if (angleDiff < -180) angleDiff += 360
        return Math.abs(angleDiff) < 90
    }

    /** 计算两点之间方位角(0~360°，正北0°，正东90°) */
    private fun getBearing(start: LatLng, end: LatLng): Double {
        val lat1 = Math.toRadians(start.latitude)
        val lon1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lon2 = Math.toRadians(end.longitude)

        val dLon = lon2 - lon1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) -
                Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)
        var bearing = Math.toDegrees(Math.atan2(y, x))
        if (bearing < 0) bearing += 360.0
        return bearing
    }

    /**
     * 最终完整版：前方+1000米范围内途经点
     */
    fun getAheadFrontPoints(
        current: LatLng,
        lastLocation: LatLng,
        passPoints: List<LatLng>,
        rangeMeter: Double = 1000.0
    ): List<Pair<LatLng, Double>> {
        return passPoints
            .filter { point ->
                val dist = calculateDistance(current, point)
                dist <= rangeMeter && isPointAhead(current, lastLocation, point)
            }
            .map { it to calculateDistance(current, it) }
            .sortedBy { it.second }
    }
}