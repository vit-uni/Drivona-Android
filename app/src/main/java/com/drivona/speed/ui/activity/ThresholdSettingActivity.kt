package com.drivona.speed.ui.activity

import android.widget.SeekBar
import androidx.core.view.isVisible
import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.drivona.speed.databinding.ActivityThresholdSettingBinding


class ThresholdSettingActivity : BaseActivity<ActivityThresholdSettingBinding>() {


    override fun getViewBinding() = ActivityThresholdSettingBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {

            ivBack.onClick {
                finish()
            }
            tvKmh.isSelected= true
            tvKmh.onClick {
                tvKmh.isSelected= true
                tvOther.isSelected= false
                tvType.text= "km/h"
            }
            tvOther.onClick {
                tvKmh.isSelected= false
                tvOther.isSelected= true
                tvType.text= "%"
            }
            seekTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    ivDotStart.isVisible = progress != 0
                    ivDotEnd.isVisible = progress != 30
                    tvNumber.text = progress.toString()

                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }

            })
        }
    }

    override fun onClick() {
        binding.apply {


        }
    }


}