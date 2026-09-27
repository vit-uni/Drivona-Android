package com.lalifa.base

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drake.brv.PageRefreshLayout
import com.lalifa.base.databinding.LayoutCommonListBinding

/**
 *
 * @ClassName BaseListFragment
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/4/13 11:44
 * @Des
 */
abstract class BaseListFragment : BaseFragment<LayoutCommonListBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        LayoutCommonListBinding.inflate(inflater, container, false)

    open fun hasRefresh(): Boolean = true
    open fun hasLoadMore(): Boolean = true
    override fun initView() {
        binding.apply {
            refreshLayout.apply {
                emptyLayout = R.layout.layout_common_empty
                errorLayout = R.layout.layout_common_empty
                loadingLayout = R.layout.layout_common_empty
                setEnableRefresh(hasRefresh())
                setEnableLoadMore(hasLoadMore())
                onRefresh {
                    getData()
                }
            }

            refreshLayout.autoRefresh()
        }
    }

    open fun PageRefreshLayout.getData() {}
}