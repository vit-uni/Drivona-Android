package com.lalifa.widget;
 
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作用 : 旋转轮播图
 *
 * @作者: duwei
 * @时间: 2020/4/15
 */
public class CustomRecyclerView extends RecyclerView {
 
    /**
     * 按下的X轴坐标
     */
    private float mDownX;
 
    /**
     * 布局器构建者
     */
    private CustomRecyclerViewManager.Builder mManagerBuilder;
 
    public CustomRecyclerView(Context context) {
        super(context);
        init();
    }
 
    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
 
    private void init() {
        createManageBuilder();
        setLayoutManager(mManagerBuilder.build());
        setChildrenDrawingOrderEnabled(true); //开启重新排序
        setOverScrollMode(OVER_SCROLL_NEVER);
    }
 
    /**
     * 创建布局构建器
     */
    private void createManageBuilder() {
        if (mManagerBuilder == null) {
            mManagerBuilder = new CustomRecyclerViewManager.Builder();
        }
    }
 
    /**
     * 设置是否为普通平面滚动
     * @param isFlat true:平面滚动；false:叠加缩放滚动
     */
    public void setFlatFlow(boolean isFlat) {
        createManageBuilder();
        mManagerBuilder.setFlat(isFlat);
        setLayoutManager(mManagerBuilder.build());
    }
 
    /**
     * 设置Item灰度渐变
     * @param greyItem true:Item灰度渐变；false:Item灰度不变
     */
    public void setGreyItem(boolean greyItem) {
        createManageBuilder();
        mManagerBuilder.setGreyItem(greyItem);
        setLayoutManager(mManagerBuilder.build());
    }
 
    /**
     * 设置Item灰度渐变
     * @param alphaItem true:Item半透渐变；false:Item透明度不变
     */
    public void setAlphaItem(boolean alphaItem) {
        createManageBuilder();
        mManagerBuilder.setAlphaItem(alphaItem);
        setLayoutManager(mManagerBuilder.build());
    }
 
    /**
     * 设置Item的间隔比例
     * @param intervalRatio Item间隔比例。
     *                      即：item的宽 x intervalRatio
     */
    public void setIntervalRatio(float intervalRatio) {
        createManageBuilder();
        mManagerBuilder.setIntervalRatio(intervalRatio);
        setLayoutManager(mManagerBuilder.build());
    }
 
    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof CustomRecyclerViewManager)) {
            throw new IllegalArgumentException("The layout manager must be MyRecyclerViewManager");
        }
        super.setLayoutManager(layout);
    }
 
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int center = getCoverFlowLayout().getCenterPosition()
                - getCoverFlowLayout().getFirstVisiblePosition(); //计算正在显示的所有Item的中间位置
        if (center < 0) center = 0;
        else if (center > childCount) center = childCount;
        int order;
        if (i == center) {
            order = childCount - 1;
        } else if (i > center) {
            order = center + childCount - 1 - i;
        } else {
            order = i;
        }
        return order;
    }
 
    /**
     * 获取LayoutManger，MyRecyclerViewManager
     */
    public CustomRecyclerViewManager getCoverFlowLayout() {
        return ((CustomRecyclerViewManager)getLayoutManager());
    }
 
    /**
     * 获取被选中的Item位置
     */
    public int getSelectedPos() {
        return getCoverFlowLayout().getSelectedPos();
    }
 
    /**
     * 设置选中监听
     * @param l 监听接口
     */
    public void setOnItemSelectedListener(CustomRecyclerViewManager.OnSelected l) {
        getCoverFlowLayout().setOnSelectedListener(l);
    }
 
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                getParent().requestDisallowInterceptTouchEvent(true); //设置父类不拦截滑动事件
                break;
            case MotionEvent.ACTION_MOVE:
                if ((ev.getX() > mDownX && getCoverFlowLayout().getCenterPosition() == 0) ||
                        (ev.getX() < mDownX && getCoverFlowLayout().getCenterPosition() ==
                                getCoverFlowLayout().getItemCount() -1)) {
                    //如果是滑动到了最前和最后，开放父类滑动事件拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    //滑动到中间，设置父类不拦截滑动事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}