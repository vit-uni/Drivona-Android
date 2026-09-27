package com.drivona.speed.utils

import android.app.Activity
import com.drake.brv.BindingAdapter
import com.drivona.speed.api.RoomUser
import com.lalifa.extension.removeAt
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

fun Activity.addListNewUser(adapter: BindingAdapter, userList: ArrayList<String>, position: Int) {
    var index = position
    if(index >=userList.size) {
        index = 0
    }
    val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    // 延迟 5 秒钟后开始执行任务，并且每隔 1 秒钟执行一次
    executor.scheduleAtFixedRate({
        runOnUiThread {
            adapter.apply {
                adapter.removeAt(0)
                var lisr = arrayListOf(userList.get(index))
                adapter.addModels(lisr, true, models?.size?:0)
                index++
                if(index >=userList.size) {
                    index = 0
                }
            }
        }
    }, 0, 1500, TimeUnit.MILLISECONDS)
}

fun Activity.addListNewUser2(adapter: BindingAdapter, userList: ArrayList<RoomUser>, position: Int) {
    var index = position
    if(index >=userList.size) {
        index = 0
    }
    val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    // 延迟 5 秒钟后开始执行任务，并且每隔 1 秒钟执行一次
    executor.scheduleAtFixedRate({
        runOnUiThread {
            adapter.apply {
                adapter.removeAt(0)
                var lisr = arrayListOf(userList[index])
                adapter.addModels(lisr, true, models?.size?:0)
                index++
                if(index >=userList.size) {
                    index = 0
                }
            }
        }
    }, 0, 1500, TimeUnit.MILLISECONDS)
}