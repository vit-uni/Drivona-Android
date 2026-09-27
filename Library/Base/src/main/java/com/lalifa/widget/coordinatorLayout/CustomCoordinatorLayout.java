package com.lalifa.widget.coordinatorLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class CustomCoordinatorLayout extends CoordinatorLayout {
    private OnInterceptTouchListener mListener;

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        mListener = listener;
    }

    public CustomCoordinatorLayout(Context context) {
        super(context);
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mListener != null) {
            mListener.onIntercept();
        }
        return super.onInterceptTouchEvent(ev);
    }


    public interface OnInterceptTouchListener {
        void onIntercept();
    }
}
