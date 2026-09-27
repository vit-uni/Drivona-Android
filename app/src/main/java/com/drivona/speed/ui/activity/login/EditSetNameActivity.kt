package com.drivona.speed.ui.activity.login

import android.util.Log
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.drivona.speed.api.uploadApi
import com.drivona.speed.api.userEdit
import com.drivona.speed.databinding.ActivityEditSetNameBinding
import com.drivona.speed.ui.activity.AddDeviceActivity
import com.drivona.speed.utils.TakePhotoPathUtils
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.getCc
import com.lalifa.extension.getIntentString
import com.lalifa.extension.gone
import com.lalifa.extension.imagePick
import com.lalifa.extension.load
import com.lalifa.extension.loadFile
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.toJson
import java.io.File


class EditSetNameActivity : BaseActivity<ActivityEditSetNameBinding>() {

    var path = ""
    override fun getViewBinding() = ActivityEditSetNameBinding.inflate(layoutInflater)

    override fun initView() {
        path = getIntentString("path")
        binding.apply {
            ivIcon.gone()
            ivAvatar.load(path)
        }
    }

    override fun onClick() {
        binding.apply {
            etName.afterTextChanged {
                next.isSelected = it.isNotEmpty()
            }
            ivBack.onClick {
                finish()
            }
            ivAvatar.onClick {
                getCc {
                    imagePick(
                        canVideo = false,
                        maxCount = 1,
                        showCamera = true,
                        videoSingle = false,
                        maxVideoDuration = 30 * 1000
                    ) {
                        val imageItem = it[0]
                        //                    deleteBtn.visible()
                        val path = imageItem.path
                        Log.e("Song", imageItem.toJson())
                        Log.e("Song", path)
                        ivIcon.gone()
                        ivAvatar.loadFile(path)
                        val upfile = path
                        scopeDialog(BubbleDialog(this@EditSetNameActivity, ""), false) {
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
                    finish()
                }


            }
            tvSkip.onClick {
                start<AddDeviceActivity> { }
            }


        }
    }


}
