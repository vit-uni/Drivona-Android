package com.lalifa.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.lalifa.api.ChoseTab
import com.lalifa.api.HeaderTitleBean
import com.lalifa.api.HomeMoreClassBean
import com.lalifa.extension.toJson

class KeyValueJsonUtils {
    companion object {
        fun getKeyValue(any: Any) {
            val toJson = any.toJson()
            val jsonObject = Gson().fromJson(toJson, JsonObject::class.java)
            //关键方法
            val entrySet = jsonObject.entrySet()
            val tabList = ArrayList<ChoseTab>()
            val tabClassList = ArrayList<Any>()
            entrySet.forEach {
                tabList.add(ChoseTab(it.key, tabList.size == 0))
                tabClassList.add(HeaderTitleBean(it.key, true))
                tabClassList.addAll(Gson().fromJson(it.value, object : TypeToken<List<HomeMoreClassBean?>?>() {}.getType())
                )
            }
        }

        fun getKeyStringValue(any: Any, tabList: ArrayList<ChoseTab>) {
            val toJson = any.toJson()
            val jsonObject = Gson().fromJson(toJson, JsonObject::class.java)
            //关键方法
            val entrySet = jsonObject.entrySet()
            entrySet.forEach {
                tabList.add(ChoseTab(it.value.toString().replace("\"", ""), tabList.size == 0, id = it.key))
            }
        }

        fun getKeyStringValue(any: Any, tabList: HashMap<Int, Int>) {
            val toJson = any.toJson()
            val jsonObject = Gson().fromJson(toJson, JsonObject::class.java)
            //关键方法
            val entrySet = jsonObject.entrySet()
            entrySet.forEach {
                tabList[it.key.toInt()] = it.value.toString().replace("\"", "").toDouble().toInt()
            }
        }
    }
}