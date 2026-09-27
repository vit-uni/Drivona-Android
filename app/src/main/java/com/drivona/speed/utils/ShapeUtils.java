package com.drivona.speed.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.drivona.speed.R;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.ShapePath;
import com.google.android.material.shape.TriangleEdgeTreatment;


/**
 * @author ： I5
 * time    ： 2021/6/2
 * package_name : com.benben.sxjd.utils
 * usefuless    : sxjd
 */
public class ShapeUtils {

    /**
     * 设置圆角边框
     *
     * @param mActivity
     * @param v
     * @param color      边框颜色
     * @param tintColor  背景填充色
     * @param mRadius    圆角
     * @param strokeWidh 边框宽度
     */
    public static void setBorderRounde(Activity mActivity, View v, @ColorRes int color, @ColorRes int tintColor, int mRadius, float strokeWidh) {
        // 代码设置 角和边
        ShapeAppearanceModel shapeAppearanceModel =
                ShapeAppearanceModel.builder().setAllCorners(new RoundedCornerTreatment())
                        .setAllCornerSizes(mRadius)
                        .setAllCorners(CornerFamily.ROUNDED, mRadius).build();

        MaterialShapeDrawable drawable = new MaterialShapeDrawable(shapeAppearanceModel);
        //填充色
        drawable.setPaintStyle(Paint.Style.FILL_AND_STROKE);
        if(tintColor == 0){
            drawable.setTint(ContextCompat.getColor(mActivity, R.color.transparent));
        }else {
            drawable.setTint(ContextCompat.getColor(mActivity, tintColor));
        }
        drawable.setStrokeWidth(dip2px(mActivity, strokeWidh));
        drawable.setStrokeColor(ContextCompat.getColorStateList(mActivity, color));
        v.setBackground(drawable);
    }

    /**
     * 设置圆形边框
     *
     * @param mActivity
     * @param v
     * @param color      边框颜色
     * @param tintColor  背景填充色
     * @param strokeWidh 边框宽度
     */
    public static void setBorderRounde(Activity mActivity, View v, @ColorRes int color, @ColorRes int tintColor, float strokeWidh) {
        setBorderRounde(mActivity, v, color, tintColor, 360, strokeWidh);
    }

    /**
     * 设置圆形边框 背景填充色为透明
     *
     * @param mActivity
     * @param v
     * @param color      边框颜色
     * @param strokeWidh 边框宽度
     */
    public static void setBorderRounde(Activity mActivity, View v, @ColorRes int color, float strokeWidh) {
        setBorderRounde(mActivity, v, color, R.color.transparent, 360, strokeWidh);
    }

    /**
     * 设置圆形边框 背景填充色为透明
     *
     * @param mActivity
     * @param v
     * @param color      边框颜色
     * @param strokeWidh 边框宽度
     */
    public static void setBorderRounde(Activity mActivity, @ColorRes int color, float strokeWidh, View... v) {
        for (View view : v) {
            setBorderRounde(mActivity, view, color, R.color.transparent, 360, strokeWidh);
        }
    }

    /**
     * 设置圆形 无边框 背景填充色
     *
     * @param mActivity
     * @param v
     * @param tintColor 背景填充色
     */
    public static void setBorderRounde(Activity mActivity, View v, @ColorRes int tintColor) {
        setBorderRounde(mActivity, v, R.color.transparent, tintColor, 0);
    }


    /**
     * 设置圆形 无边框 背景填充色为透明
     *
     * @param mActivity
     * @param v
     */
    public static void setBorderRounde(Activity mActivity, View v) {
        setBorderRounde(mActivity, v, R.color.transparent, 0);
    }

    /**
     * 设置矩形切角边框
     *
     * @param mActivity
     * @param v
     * @param color          边框颜色
     * @param tintColor      填充颜色
     * @param topLeftCut     切角大小
     * @param topReightcut   切角大小
     * @param bottomLeftCut  切角大小
     * @param botoomRightCut 切角大小
     * @param strokeWidh     边框宽度
     */
    public static void setRectangularCornerCutBorder(Activity mActivity, View v, @ColorRes int color, @ColorRes int tintColor, int topLeftCut, int topReightcut, int bottomLeftCut, int botoomRightCut, float strokeWidh) {
        // 代码设置 角和边
        ShapeAppearanceModel shapeAppearanceModel =
                ShapeAppearanceModel.builder().setAllCorners(new RoundedCornerTreatment())
                        .setBottomLeftCorner(CornerFamily.CUT, dip2px(mActivity, bottomLeftCut))
                        .setTopLeftCorner(CornerFamily.CUT, dip2px(mActivity, topLeftCut))
                        .setTopRightCorner(CornerFamily.CUT, dip2px(mActivity, topReightcut))
                        .setBottomRightCorner(CornerFamily.CUT, dip2px(mActivity, botoomRightCut)).build();

        MaterialShapeDrawable drawable = new MaterialShapeDrawable(shapeAppearanceModel);
        //填充色
        drawable.setTint(ContextCompat.getColor(mActivity, tintColor));
        drawable.setPaintStyle(Paint.Style.FILL_AND_STROKE);
        drawable.setStrokeWidth(dip2px(mActivity, strokeWidh));
        drawable.setStrokeColor(ContextCompat.getColorStateList(mActivity, color));
        v.setBackground(drawable);
    }

    /**
     * 设置矩形切角
     *
     * @param mActivity
     * @param v
     * @param cut       切角大小
     */
    public static void setRectangularCornerCutBorder(Activity mActivity, View v, int cut) {
        setRectangularCornerCutBorder(mActivity, v, R.color.transparent, R.color.transparent, cut, cut, cut, cut, 0);
    }

    /**
     * 设置矩形切角
     *
     * @param mActivity
     * @param v
     * @param cut       切角大小
     * @param tintColor 背景填充颜色
     */
    public static void setRectangularCornerCutBorder(Activity mActivity, View v, int cut, @ColorRes int tintColor) {
        setRectangularCornerCutBorder(mActivity, v, R.color.transparent, tintColor, cut, cut, cut, cut, 0);
    }

    /**
     * 代码设置 聊天框效果
     *
     * @param mActivity
     * @param v
     * @param mCenter           三角位置
     * @param triangleDirection 三角方向
     * @param color             填充颜色
     * @param radius            显示内容圆角
     * @param clipChildren      不限制子view在其范围内
     */
    public static void setChatBoxEffect(Activity mActivity, View v, final int mCenter, TriangleDirection triangleDirection, @ColorRes int color, int radius, boolean clipChildren) {
        ShapeAppearanceModel.Builder builder = ShapeAppearanceModel.builder();

        builder.setAllCorners(new RoundedCornerTreatment())
                .setAllCornerSizes(dip2px(mActivity, radius));
        switch (triangleDirection) {
            case TOP:
                builder.setTopEdge(new TriangleEdgeTreatment(20f, false) {
                    @Override
                    public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
                        super.getEdgePath(length, mCenter == 0 ? center : mCenter, interpolation, shapePath);
                    }
                });
                break;
            case LEFT:
                builder.setLeftEdge(new TriangleEdgeTreatment(20f, false) {
                    @Override
                    public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
                        super.getEdgePath(length, mCenter == 0 ? center : mCenter, interpolation, shapePath);
                    }
                });
                break;
            case RIGHT:
                builder.setRightEdge(new TriangleEdgeTreatment(20f, false) {
                    @Override
                    public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
                        super.getEdgePath(length, mCenter == 0 ? center : mCenter, interpolation, shapePath);
                    }
                });
                break;
            case BOTTOM:
                builder.setBottomEdge(new TriangleEdgeTreatment(20f, false) {
                    @Override
                    public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
                        super.getEdgePath(length, mCenter == 0 ? center : mCenter, interpolation, shapePath);
                    }
                });
                break;
            default:
        }
        MaterialShapeDrawable drawable = new MaterialShapeDrawable(builder.build());
        drawable.setTint(ContextCompat.getColor(mActivity, color));
        drawable.setPaintStyle(Paint.Style.FILL);
        ViewGroup parent = (ViewGroup) v.getParent();
        parent.setClipChildren(clipChildren);// 不限制子view在其范围内
        v.setBackground(drawable);
    }

    /**
     * 三角在顶部
     *
     * @param mActivity
     * @param v
     * @param center    三角位置
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatTopBoxEffect(Activity mActivity, View v, int center, @ColorRes int color, int radius, boolean clipChildren) {
        setChatBoxEffect(mActivity, v, center, TriangleDirection.TOP, color, radius, clipChildren);
    }

    /**
     * 三角在顶部 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatTopBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius) {
        setChatTopBoxEffect(mActivity, v, 0, color, radius, false);
    }

    /**
     * 三角在顶部 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color        填充颜色
     * @param radius       显示内容圆角
     * @param clipChildren 不限制子view在其范围内
     */
    public static void setChatTopBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius, boolean clipChildren) {
        setChatTopBoxEffect(mActivity, v, 0, color, radius, clipChildren);
    }

    /**
     * 三角在底部
     *
     * @param mActivity
     * @param v
     * @param center    三角位置
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatBottomBoxEffect(Activity mActivity, View v, int center, @ColorRes int color, int radius, boolean clipChildren) {
        setChatBoxEffect(mActivity, v, center, TriangleDirection.BOTTOM, color, radius, clipChildren);
    }

    /**
     * 三角在底部 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatBottomBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius) {
        setChatBottomBoxEffect(mActivity, v, 0, color, radius, false);
    }

    /**
     * 三角在底部 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color        填充颜色
     * @param radius       显示内容圆角
     * @param clipChildren 不限制子view在其范围内
     */
    public static void setChatBottomBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius, boolean clipChildren) {
        setChatBottomBoxEffect(mActivity, v, 0, color, radius, clipChildren);
    }


    /**
     * 三角在底部
     *
     * @param mActivity
     * @param v
     * @param center    三角位置
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatRightBoxEffect(Activity mActivity, View v, int center, @ColorRes int color, int radius, boolean clipChildren) {
        setChatBoxEffect(mActivity, v, center, TriangleDirection.RIGHT, color, radius, clipChildren);
    }

    /**
     * 三角在底部 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatRightBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius) {
        setChatRightBoxEffect(mActivity, v, 0, color, radius, false);
    }

    /**
     * 三角在底部 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color        填充颜色
     * @param radius       显示内容圆角
     * @param clipChildren 不限制子view在其范围内
     */
    public static void setChatRightBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius, boolean clipChildren) {
        setChatRightBoxEffect(mActivity, v, 0, color, radius, clipChildren);
    }

    /**
     * 三角在底部
     *
     * @param mActivity
     * @param v
     * @param center    三角位置
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatLeftBoxEffect(Activity mActivity, View v, int center, @ColorRes int color, int radius, boolean clipChildren) {
        setChatBoxEffect(mActivity, v, center, TriangleDirection.LEFT, color, radius, clipChildren);
    }


    /**
     * 三角在左边 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatLeftBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius) {
        setChatLeftBoxEffect(mActivity, v, 0, color, radius, false);
    }

    /**
     * 三角在左边 默认在中心
     *
     * @param mActivity
     * @param v
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setChatLeftBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius, boolean clipChildren) {
        setChatLeftBoxEffect(mActivity, v, 0, color, radius, clipChildren);
    }

    /**
     * 代码设置 聊天气泡效果
     *
     * @param mActivity
     * @param v
     * @param mCenter   三角位置
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setAllChatBoxEffect(Activity mActivity, View v, final int mCenter, @ColorRes int color, int radius, boolean clipChildren) {
        ShapeAppearanceModel build = ShapeAppearanceModel.builder().setAllCorners(new RoundedCornerTreatment())
                .setAllCornerSizes(dip2px(mActivity, radius))
                .setAllEdges(new TriangleEdgeTreatment(radius, false) {
                    @Override
                    public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
                        super.getEdgePath(length, mCenter == 0 ? center : mCenter, interpolation, shapePath);
                    }
                }).build();
        MaterialShapeDrawable drawable = new MaterialShapeDrawable(build);
        drawable.setTint(ContextCompat.getColor(mActivity, color));
        drawable.setPaintStyle(Paint.Style.FILL);
        ViewGroup parent = (ViewGroup) v.getParent();
        parent.setClipChildren(clipChildren);// 不限制子view在其范围内
        v.setBackground(drawable);
    }

    /**
     * 代码设置 聊天框效果 默认在中间
     *
     * @param mActivity
     * @param v
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setAllChatBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius) {
        setAllChatBoxEffect(mActivity, v, 0, color, radius, false);
    }

    /**
     * 代码设置 聊天框效果 默认在中间
     *
     * @param mActivity
     * @param v
     * @param color     填充颜色
     * @param radius    显示内容圆角
     */
    public static void setAllChatBoxEffect(Activity mActivity, View v, @ColorRes int color, int radius, boolean clipChildren) {
        setAllChatBoxEffect(mActivity, v, 0, color, radius, clipChildren);
    }

    public enum TriangleDirection {
        TOP, RIGHT, BOTTOM, LEFT
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
