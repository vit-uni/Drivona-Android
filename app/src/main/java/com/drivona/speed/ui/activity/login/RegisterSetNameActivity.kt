package com.drivona.speed.ui.activity.login

import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.drivona.speed.api.uploadApi
import com.drivona.speed.api.userEdit
import com.drivona.speed.databinding.ActivityRegisterSetNameBinding
import com.drivona.speed.ui.activity.AddDeviceActivity
import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.extension.getCc
import com.lalifa.extension.getIntentString
import com.lalifa.extension.gone
import com.lalifa.extension.imagePick
import com.lalifa.extension.loadFile
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import java.io.File


class RegisterSetNameActivity : BaseActivity<ActivityRegisterSetNameBinding>() {


    override fun getViewBinding() = ActivityRegisterSetNameBinding.inflate(layoutInflater)
    var email = ""
    override fun initView() {
        email = getIntentString("email")
        binding.apply {

        }
    }

    override fun onClick() {
        binding.apply {

            ivAvatar.onClick {
                getCc {
                    imagePick(
                        canVideo = false,
                        maxCount = 1,
                        showCamera = true,
                        videoSingle = true,
                        maxVideoDuration = 30 * 1000
                    ) {
                        val imageItem = it[0]
                        ivIcon.gone()
                        ivAvatar.loadFile(imageItem.path)
                        val upfile = imageItem.path
                        scopeDialog(BubbleDialog(this@RegisterSetNameActivity, ""), false) {
                            val data = uploadApi(File(upfile))
                            data?.apply {
                                userEdit(avatar = data)
                            }

                        }
                    }

                }
//                start<RegisterChoosePictureActivity> { }
            }
            next.onClick {
                val name = etName.text()
                if (name.isNullOrEmpty()) {
                    return@onClick
                }
                scopeNetLife {
                    userEdit(nickname = name)
                    start<RegisterSuccessActivity> { }
                }


            }
            tvSkip.onClick {
                ActivityManager.getInstance().finishAllActivities()
                start<RegisterSuccessActivity> { }
                finish()
            }


        }
    }

    override fun onBackPressed() {
        ActivityManager.getInstance().finishAllActivities()
        start<AddDeviceActivity> { }
        finish()

    }

}
