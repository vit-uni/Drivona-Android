package com.drivona.speed.car

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Distance
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import com.drake.channel.sendEvent
import com.drivona.speed.MApplication
import com.drivona.speed.api.LocationDataCar
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavInfo
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.lalifa.extension.gone
import com.lalifa.extension.visible

class NavSearchScreen(carContext: CarContext) : Screen(carContext) {

    private var sessionToken: AutocompleteSessionToken? = null

    // 保存搜索结果
    private var searchResultItems: MutableList<Row> = mutableListOf()

    private var mSearchText: String = ""
    override fun onGetTemplate(): Template {

        val builder = SearchTemplate.Builder(object : SearchTemplate.SearchCallback {
            override fun onSearchTextChanged(searchText: String) {
                super.onSearchTextChanged(searchText)
                getPlacePredictions(searchText)
            }

            override fun onSearchSubmitted(searchText: String) {
                super.onSearchSubmitted(searchText)

            }
        })
            .setHeaderAction(Action.BACK)
            .setShowKeyboardByDefault(false)
            .setItemList(buildResultList())
            .setInitialSearchText(mSearchText)

        // ---------------------- 构建导航模板 ----------------------
        return builder.build()
    }

    // 构建结果 ItemList
    private fun buildResultList(): ItemList {
        val builder = ItemList.Builder()
        searchResultItems.forEach { row ->
            builder.addItem(row)
        }
        return builder.build()
    }

    /**
     * This method demonstrates the programmatic approach to getting place predictions. The
     * parameters in this request are currently biased to Boulder, Colorado, USA.
     *
     * @param query the plus code query string (e.g. "GCG2+3M K")
     */
    private fun getPlacePredictions(query: String) {
        sessionToken = AutocompleteSessionToken.newInstance()
        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        val newRequest = FindAutocompletePredictionsRequest.builder()
//            .setLocationBias(bias)
//            .setCountries("US")
            // Session Token only used to link related Place Details call. See https://goo.gle/paaln
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        // Perform autocomplete predictions request
        MApplication.placesClient.findAutocompletePredictions(newRequest)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                Log.e("Ning", "搜索结果__${predictions.size}")
//                binding.placeSearchResultsView.models = predictions
                // 清空旧结果
                searchResultItems.clear()
                if (predictions.isNotEmpty()) {
                    for (i in predictions) {
                        searchResultItems.add(
                            Row.Builder()
                            .setTitle(i.getPrimaryText(null).toString())
                            .addText(i.getSecondaryText(null).toString())
                            .setOnClickListener {
                                // 点击条目跳转，比如打开导航页 / Pane详情页
                                Log.e("Ning","发消息")
                                sendEvent(LocationDataCar("", "", i.placeId))
                                screenManager.pop()
                            }
                            .build())

                    }
                } else {


                }
// 通知页面刷新，onGetTemplate()重新执行 → 更新列表
                invalidate()
            }.addOnFailureListener { exception: Exception? ->

                if (exception is ApiException) {
                    Log.e("Ning", "Place not found: ${exception.message}")
                }
            }
    }

}
