package com.drivona.speed.ui.activity

import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.drake.channel.sendEvent
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.getIntentBoolean
import com.lalifa.extension.getIntentInt
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.text
import com.lalifa.extension.toJson
import com.lalifa.extension.visible
import com.drivona.speed.MApplication
import com.drivona.speed.api.SearchPlaceData
import com.drivona.speed.api.SearchPlaceOtherData
import com.drivona.speed.databinding.ActivitySearchLocationOtherBinding
import com.drivona.speed.ui.adapter.PlacePredictionAdapter
import com.lalifa.utils.GsonUtil
import com.lalifa.utils.SPUtil


class SearchLocationOtherActivity : BaseActivity<ActivitySearchLocationOtherBinding>() {

    private var sessionToken: AutocompleteSessionToken? = null
    private lateinit var queue: RequestQueue
    private lateinit var placesClient: PlacesClient
    private val adapter = PlacePredictionAdapter()
    override fun getViewBinding() = ActivitySearchLocationOtherBinding.inflate(layoutInflater)
    var isFirst = false
    var position = 0
    override fun initView() {
        isFirst = getIntentBoolean("isFirst")
        position = getIntentInt("position", 0)

        // Create a new PlacesClient instance
        placesClient = MApplication.placesClient
        binding.apply {
            sessionToken = AutocompleteSessionToken.newInstance()
            ivBack.onClick {
                finish()
            }

            val recyclerView = binding.placeSearchResultsView
            val layoutManager = LinearLayoutManager(this@SearchLocationOtherActivity)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.onPlaceClickListener = {
                val name = it.getPrimaryText(null).toString()
                val nameDesc = it.getFullText(null).toString()
                Log.e("Song", "_____${it.toJson()}")
                sendEvent(SearchPlaceOtherData(it.placeId, name, nameDesc, isFirst, position))

                finish()
            }

            etSearch.afterTextChanged {
                if (it.length == 0) {
                    binding.placeSearchResultsView.gone()


                } else {
                    getPlacePredictions(it)
                }

            }
            etSearch.setOnEditorActionListener { v, actionId, keyEvent ->
                if (actionId === EditorInfo.IME_ACTION_SEARCH) {
                    // 隐藏软键盘
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    // 执行搜索逻辑
                    val text = etSearch.text()
                    if (text.isNotEmpty()) {
                        getPlacePredictions(text)

//                        searchAddress(text)
//                        etSearch.setText("")
                    }

                    true
                }
                false
            }

        }
    }

    /**
     * This method demonstrates the programmatic approach to getting place predictions. The
     * parameters in this request are currently biased to Boulder, Colorado, USA.
     *
     * @param query the plus code query string (e.g. "GCG2+3M K")
     */
    private fun getPlacePredictions(query: String) {

        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        val newRequest = FindAutocompletePredictionsRequest.builder()
//            .setLocationBias(bias)
//            .setCountries("US")
            // Session Token only used to link related Place Details call. See https://goo.gle/paaln
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        // Perform autocomplete predictions request
        placesClient.findAutocompletePredictions(newRequest)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                Log.e("Song", "搜索结果__${predictions.size}")
//                binding.placeSearchResultsView.models = predictions
                if (predictions.isNotEmpty()) {
                    binding.placeSearchResultsView.visible()


                    adapter.setPredictions(predictions)
                } else {
                    binding.placeSearchResultsView.gone()


                }

            }.addOnFailureListener { exception: Exception? ->

                if (exception is ApiException) {
                    Log.e("Song", "Place not found: ${exception.message}")
                }
            }
    }


    override fun onClick() {
        binding.apply {


        }
    }

    fun saveHistory(data: SearchPlaceData) {

        val json = SPUtil.get(Tools.Place_History)
        if (json.isNotEmpty()) {
            val list = GsonUtil.json2List<SearchPlaceData>(json, SearchPlaceData::class.java)


            for (i in list) {

                if (i.placeId == data.placeId) {
                    list.remove(i)
                    break
                }

            }
            list.add(0, data)


            SPUtil.set(Tools.Place_History, list.toJson())
            Log.e("SOng", "list____${list.size}")
        } else {
            val list = arrayListOf<SearchPlaceData>()

            list.add(data)
            SPUtil.set(Tools.Place_History, list.toJson())
        }


    }


}