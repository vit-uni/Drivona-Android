package com.lalifa.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.lalifa.bean.FormatRangBean;
import com.lalifa.bean.RangBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by peng on 2020/11/25
 */
public class AitEditText extends androidx.appcompat.widget.AppCompatEditText {
    public List<FormatRangBean> mRangeManager;
    private boolean mIsSelected;

    public AitEditText(Context context) {
        super(context);
        init();

    }

    public AitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRangeManager = new ArrayList<>();
    }


    // 如果是删除操作MentionInputConnection中的sendKeyEvent优先触发
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        // 修复连续删除文字块会出现数组越界问题
        RangBean closestRange = getRangeOfClosestMentionString(selStart, selEnd);
        if (closestRange != null && closestRange.getTo() == selEnd) {
            mIsSelected = false;
        }
        RangBean nearbyRangBean = getRangeOfNearbyMentionString(selStart, selEnd);
        if (null != nearbyRangBean) {
            // 起始位置一样说明，没有选中任何字符
            if (selStart == selEnd) {
                Log.e("TGA", "selStart: " + selStart + ",selEnd:" + selEnd);
                // 如果光标是在文字块中 则设置光标在文字块两侧 --保证光标无法插入文字块
                int position = nearbyRangBean.getAnchorPosition(selStart);
                // 修复输入大量文字块标点符号特殊字符后，再连续删除所有 会出现数组越界的问题
                if(position<=getText().length())
                    setSelection(position);
            } else {
                if (selEnd < nearbyRangBean.getTo()) {
                    setSelection(selStart, nearbyRangBean.getTo());
                }
                if (selStart > nearbyRangBean.getFrom()) {
                    setSelection(nearbyRangBean.getFrom(), selEnd);
                }
            }
        }

    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MentionInputConnection(super.onCreateInputConnection(outAttrs), true, this);
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }

    public RangBean getRangeOfNearbyMentionString(int selStart, int selEnd) {
        if (mRangeManager == null) {
            return null;
        }
        for (RangBean rangBean : mRangeManager) {
            if (rangBean.isWrappedBy(selStart, selEnd)) {
                return rangBean;
            }
        }
        return null;
    }

    public RangBean getRangeOfClosestMentionString(int selStart, int selEnd) {
        if (mRangeManager == null) {
            return null;
        }
        for (RangBean rangBean : mRangeManager) {
            if (rangBean.contains2(selStart, selEnd)) {
                return rangBean;
            }
        }
        return null;
    }
}
