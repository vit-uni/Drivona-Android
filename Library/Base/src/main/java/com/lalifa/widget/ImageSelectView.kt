package com.lalifa.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.lalifa.base.R
import com.lalifa.base.databinding.ItemSelectImgBinding
import com.lalifa.extension.*

/**
 *
 * @ClassName ImageSelecteView
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/9/6 9:47
 * @Des
 */
class ImageSelectView : RecyclerView {
    private var maxCount = 9
    private var isvSelectType = 2
    private var isvSelectShowpop = false
    private var adapter: BindingAdapter?
    private var selectType = SelectType.DEFAULT

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("UseCompatLoadingForDrawables")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.ImageSelectView)
        maxCount = attr.getInt(R.styleable.ImageSelectView_isv_max_count, 9)
        isvSelectShowpop = attr.getBoolean(R.styleable.ImageSelectView_isv_select_showpop, false)
        isvSelectType = attr.getInt(R.styleable.ImageSelectView_isv_select_type, 2)
        adapter = grid(4).divider {
            setDivider(8.dp)
            orientation = DividerOrientation.VERTICAL
        }.divider {
            setDivider(8.dp)
            orientation = DividerOrientation.HORIZONTAL
        }.setup {
            addType<ImageBean>(R.layout.item_select_img)
            onBind {
                val bean = getModel<ImageBean>()
                getBinding<ItemSelectImgBinding>().apply {
                    if (bean.type == ImageType.SELECT) {
                        cover.scaleType = ImageView.ScaleType.CENTER
                        cover.setImageDrawable(
                            attr.getDrawable(R.styleable.ImageSelectView_isv_select_icon)
                                ?: context.getDrawable(R.mipmap.icon_addgrey)
                        )
                        deleteBtn.gone()
                    } else if (bean.path?.contains("http") == true) {
                        cover.load(bean.path)
                        if(isvSelectType == 1) {
                            deleteBtn.visible()
                        }
                    } else {
                        cover.loadFile(bean.path)
                        if(isvSelectType == 1) {
                            deleteBtn.visible()
                        }
                    }
                }

            }
            R.id.item.onClick {
                val bean = getModel<ImageBean>()
                if (bean.type == ImageType.IMAGE) {
                    val list = ArrayList<String>()
                    models?.forEachIndexed { index, any ->
                        list.add((any as ImageBean).path)
                    }
                    context.preview(modelPosition - 1, list.subList(1, list.size))
                    return@onClick
                }
                val selectCount = maxCount - (models!!.size - 1)
                context.imagePick(maxCount = if (selectType == SelectType.COVER) maxCount else selectCount) {
                    if ((models!!.size - 1 + it.size) == maxCount) removeAt(0)
                    if (selectType == SelectType.COVER) {
                        models = arrayListOf<ImageBean>().apply {
                            add(ImageBean(ImageType.SELECT, ""))
                            it.forEach {
                                add(ImageBean(ImageType.IMAGE, it.path))
                            }
                        }
                    } else {
                        addModels(arrayListOf<ImageBean>().apply {
                            it.forEach {
                                add(ImageBean(ImageType.IMAGE, it.path))
                            }
                        })
                    }
                }
            }
            R.id.delete_btn.onClick {
                //删除当前图片
                removeAt(modelPosition)
                if (models.isNullOrEmpty()) {
                    //已经全部删除完了
                    addModels(arrayListOf(ImageBean(ImageType.SELECT, "")))
                } else if (models!!.size == maxCount - 1 &&
                    (models!![0] as ImageBean).type == ImageType.IMAGE
                ) {
                    //删除前是最大图片数量 需要添加第一个用于选择图片
                    val dataList = arrayListOf(ImageBean(ImageType.SELECT, ""))
                        .apply { addAll(this@setup._data as ArrayList<ImageBean>) }
                    (this@setup._data as ArrayList).apply {
                        clear()
                        addAll(dataList)
                    }
                    notifyItemRangeInserted(0, 1)
                }
            }
        }
        adapter?.models = arrayListOf(ImageBean(ImageType.SELECT, ""))
    }

    fun getData(): ArrayList<String> {
        return arrayListOf<String>().apply {
            (models as ArrayList<ImageBean>).forEach {
                if (it.type == ImageType.IMAGE)
                    add(it.path)
            }
        }
    }


    fun setData(list: ArrayList<String>) {
        val arrayList = ArrayList<ImageBean>()
        list?.apply {
            forEach {
                if (it?.isEmpty() == true) {
                    arrayList.add(ImageBean(ImageType.SELECT, ""))
                } else {
                    arrayList.add(ImageBean(ImageType.IMAGE, it))
                }
            }
        }
        adapter?.models = arrayList;
    }

    fun setSelectType(selectType: SelectType) {
        this.selectType = selectType
    }

    data class ImageBean(var type: ImageType, var path: String)


    enum class ImageType {
        SELECT, IMAGE, VIDEO
    }

    enum class SelectType {
        DEFAULT, COVER
    }
}