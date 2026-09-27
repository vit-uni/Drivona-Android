package com.lalifa.widget.coordinatorLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Field;

public class FixedBehavior extends AppBarLayout.Behavior {
    private OverScroller mOverScroller;

    public FixedBehavior() {
        super();
    }

    public FixedBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams params) {
        super.onAttachedToLayoutParams(params);
    }

    @Override
    public void onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams();
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            reflectOverScroller();
        }
        return super.onTouchEvent(parent, child, ev);
    }

    /**
     *
     */
    public void stopFling() {
        if (mOverScroller != null) {
            mOverScroller.abortAnimation();
        }
    }

    /**
     * 解决AppbarLayout在fling的时候，再主动滑动RecyclerView导致的动画错误的问题
     */
    private void reflectOverScroller() {
        if (mOverScroller == null) {
            Field field = null;
            try {
                field = getClass().getSuperclass()
                        .getSuperclass().getDeclaredField("mScroller");
                field.setAccessible(true);
                Object object = field.get(this);
                mOverScroller = (OverScroller) object;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }
}
