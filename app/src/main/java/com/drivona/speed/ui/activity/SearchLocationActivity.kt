package com.drivona.speed.ui.activity

import android.content.Context
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.drake.brv.utils.models
import com.drake.channel.receiveEvent
import com.drake.channel.sendEvent
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.getIntentInt
import com.lalifa.extension.getIntentSerializable
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.toJson
import com.lalifa.extension.visible
import com.drivona.speed.MApplication
import com.drivona.speed.R
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.LocationData
import com.drivona.speed.api.MiddlePoint
import com.drivona.speed.api.SearchPlaceData
import com.drivona.speed.api.getAddressDefaultList
import com.drivona.speed.databinding.ActivitySearchLocationBinding
import com.drivona.speed.ext.showDeleteConfirmDialog
import com.drivona.speed.ui.adapter.PlacePredictionAdapter
import com.drivona.speed.ui.adapter.searchHistoryList
import com.lalifa.utils.GsonUtil
import com.lalifa.utils.SPUtil


class SearchLocationActivity : BaseActivity<ActivitySearchLocationBinding>() {

    private var sessionToken: AutocompleteSessionToken? = null
    private lateinit var queue: RequestQueue
    private lateinit var placesClient: PlacesClient
    private val adapter = PlacePredictionAdapter()
    override fun getViewBinding() = ActivitySearchLocationBinding.inflate(layoutInflater)
    var typeFrom = 0
    var device: DeviceData? = null
    override fun initView() {
        typeFrom = getIntentInt("type", 0)
        if (typeFrom == 1) {
            device = getIntentSerializable<DeviceData>("device")
        }

        placesClient = MApplication.placesClient
        binding.apply {
            sessionToken = AutocompleteSessionToken.newInstance()
            ivBack.onClick {
                finish()
            }
            recyclerView.searchHistoryList().apply {
                onFastClick(R.id.item) {
                    val model = getModel<SearchPlaceData>()


                    saveHistory(model)
                    if (typeFrom == 1) {
                        start<MapActivity> {
                            putExtra("placeId", model.placeId)
                            putExtra("placeName", model.placeName)
                            putExtra("placeDesc", model.placeDesc)
                            putExtra("placeLat", model.latitude)
                            putExtra("placeLong", model.longitude)
                            putExtra("device", device)
                        }
                    } else {
                        sendEvent(model)
                        finish()
                    }

                }
            }
            val recyclerView = binding.placeSearchResultsView
            val layoutManager = LinearLayoutManager(this@SearchLocationActivity)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.onPlaceClickListener = {
                val name = it.getPrimaryText(null).toString()
                val nameDesc = it.getFullText(null).toString()
                Log.e("Song", "_____${it.toJson()}")
                Log.e("Song", "it.name___${name}")
                Log.e("Song", "it.placeId_____${it.placeId}")
                fetchPlaceDetails(it.placeId)

            }

            etSearch.afterTextChanged {
                if (it.length == 0) {
                    binding.placeSearchResultsView.gone()
                    binding.tvResult.gone()
                    binding.lineResult.gone()

                } else {
                    getPlacePredictions(it)
                }

            }
            etSearch.setOnEditorActionListener { v, actionId, keyEvent ->
                if (actionId === EditorInfo.IME_ACTION_SEARCH) {
                    // 隐藏软键盘
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        receiveEvent<LocationData> {
            finish()
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
                    binding.tvResult.visible()
                    binding.lineResult.visible()

                    adapter.setPredictions(predictions)
                } else {
                    binding.placeSearchResultsView.gone()
                    binding.tvResult.gone()
                    binding.lineResult.gone()

                }

            }.addOnFailureListener { exception: Exception? ->

                if (exception is ApiException) {
                    Log.e("Song", "Place not found: ${exception.message}")
                }
            }
    }


    override fun onClick() {
        binding.apply {
            clHome.onClick {
                start<UserLocationListActivity> {
                    putExtra("index", 0)
                    putExtra("type", typeFrom)
                    if (typeFrom == 1) {
                        putExtra("device", device)
                    }

                }
            }

            clCompany.onClick {
                start<UserLocationListActivity> {
                    putExtra("index", 1)
                    putExtra("type", typeFrom)
                    if (typeFrom == 1) {
                        putExtra("device", device)
                    }
                }
            }

            clFavorite.onClick {
                start<UserLocationListActivity> {
                    putExtra("index", 2)
                    putExtra("type", typeFrom)
                    if (typeFrom == 1) {
                        putExtra("device", device)
                    }
                }
            }
            ivDelete.onClick {
                showDeleteConfirmDialog {
                    val list = arrayListOf<SearchPlaceData>()

                    SPUtil.set(Tools.Place_History, list.toJson())
                    binding.recyclerView.models = list
                    binding.tv1.gone()
                    binding.ivDelete.gone()
                    binding.line.gone()
                }

            }

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

    override fun onResume() {
        super.onResume()
        getHistory()
    }

    fun getHistory() {

        scopeNetLife {
            val data = getAddressDefaultList()
            data?.data?.apply {
                SPUtil.set("defaultAddress",this.toJson())
                if (home != null) {
                    binding.tvHomeDesc.text = home.title
                } else {
                    binding.tvHomeDesc.text = getString(R.string.not_set1)
                }
                if (favorite != null) {
                    binding.tvFavoriteDesc.text = favorite.title
                } else {
                    binding.tvFavoriteDesc.text = getString(R.string.not_set1)
                }
                if (company != null) {
                    binding.tvCompanyDesc.text = company.title
                } else {
                    binding.tvCompanyDesc.text = getString(R.string.not_set1)
                }
            }
        }
        val json = SPUtil.get(Tools.Place_History)
        Log.e("Song", "获取历史记录——${json}")
        if (json.isNotEmpty()) {
            val list = GsonUtil.json2List<SearchPlaceData>(json, SearchPlaceData::class.java)
            binding.recyclerView.models = list
            if (list.isNotEmpty()) {
                binding.tv1.visible()
                binding.ivDelete.visible()
                binding.line.visible()
            }

        } else {
            val list = arrayListOf<SearchPlaceData>()
            binding.recyclerView.models = list
            binding.tv1.gone()
            binding.ivDelete.gone()
            binding.line.gone()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun fetchPlaceDetails(placeId: String) {
        scopeDialog(BubbleDialog(this, "")) {

            val placeFields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS,Place.Field.LOCATION,
                    Place.Field.ADDRESS_COMPONENTS )
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            Log.e("Ning", "搜索地点——————${placeId}")
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                Log.e("Ning", "place——————${place.toJson()}")
               val location= place.location
                val latLng = place.latLng
                val data = SearchPlaceData(
                    placeId,
                    place.name,
                    place.address,
                    latLng.latitude,
                    latLng.longitude
                )
                Log.e("Ning", "latLng——————${place.latLng.toJson()}")
                Log.e("Ning", "location——————${location.toJson()}")
                saveHistory(data)
                if (typeFrom == 1) {
                    start<MapActivity> {
                        putExtra("placeId", placeId)
                        putExtra("placeName", place.name)
                        putExtra("placeDesc", place.address)
                        putExtra("placeLat", latLng.latitude)
                        putExtra("placeLong", latLng.longitude)
                        putExtra("device", device)
                    }
                } else {
                    sendEvent(data)
                }
                finish()
            }.addOnFailureListener { exception ->

                Log.e("Song", "搜索地点异常____$exception")
                // 处理错误
                exception.printStackTrace()
            }

        }
    }
}