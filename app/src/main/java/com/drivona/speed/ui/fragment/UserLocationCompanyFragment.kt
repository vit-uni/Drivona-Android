package com.drivona.speed.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.utils.models
import com.drake.channel.sendEvent
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseFragment
import com.lalifa.extension.removeAt
import com.lalifa.extension.start
import com.drivona.speed.R
import com.drivona.speed.api.DeviceData
import com.drivona.speed.api.LocationData
import com.drivona.speed.api.delAddressList
import com.drivona.speed.api.getAddressList
import com.drivona.speed.databinding.FragmentUserLocationCompanyBinding
import com.drivona.speed.ext.showDeleteConfirmDialog
import com.drivona.speed.ui.activity.MapActivity
import com.drivona.speed.ui.adapter.userLocationList


class UserLocationCompanyFragment : BaseFragment<FragmentUserLocationCompanyBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentUserLocationCompanyBinding.inflate(layoutInflater)

    var device: DeviceData? = null

    companion object {
        @JvmStatic
        fun newInstance(type: Int, device: DeviceData?) = UserLocationCompanyFragment().apply {
            arguments = Bundle().apply {
                putInt("type", type)
                if (type == 1) {
                    putSerializable("device", device)

                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            val type = arguments?.getInt("type")

            if (type == 1) {
                device = arguments?.getSerializable("device") as DeviceData
            }

            recLocation.userLocationList().apply {
                onFastClick(R.id.iv_delete, R.id.item) {
                    when (it) {
                        R.id.iv_delete -> {
                            requireActivity().showDeleteConfirmDialog {
                                scopeNetLife {
                                    delAddressList(getModel<LocationData>().id)
                                    removeAt(modelPosition)
                                }
                            }

                        }

                        R.id.item -> {
                            if (type == 1) {
                                requireActivity().start<MapActivity> {
                                    putExtra("placeId", getModel<LocationData>().place_id)
                                    putExtra("placeName", getModel<LocationData>().title)
                                    putExtra("placeDesc", getModel<LocationData>().address)
                                    putExtra("device", device)
                                }
                                requireActivity().finish()
                                return@onFastClick
                            }
                            sendEvent(getModel<LocationData>())
                            requireActivity().finish()
                        }

                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scopeNetLife {
            val data = getAddressList(2)
            binding.recLocation.models = data?.data
        }
    }


    override fun onClick() {
        super.onClick()

    }

}