
package com.drivona.speed.widght;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.drivona.speed.R;

public class StatusBarView extends LinearLayout {
    private TextView tvTitle;
    private ImageView viewById;
    private TextView tvRightTitleOne;
    private int left_icon = 0;
    private String title;
    private String rightOneTitle;
    private View state_height;
    private boolean isShowState;
    private int sbv_state_color;
    private int sbv_bg_color;
    private int sbv_title_color;
    private int sbv_right_one_title_color;
    private ConstraintLayout cl_parent;
    private String left_title;
    private TextView tvLeftTitle;
    private int sbv_left_title_color;
    private float sbv_left_title_size;
    private View view_line;
    private int showLine;
    private boolean sbv_single_state;
    private LeftOneOnClickCallback leftOneOnClickCallback;

    public StatusBarView(Context context) {
        super(context);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StatusBarView);
        left_icon = ta.getResourceId(R.styleable.StatusBarView_sbv_left_icon, STYLE_NORMAL);
        sbv_state_color = ta.getColor(R.styleable.StatusBarView_sbv_state_color, STYLE_NORMAL);
        sbv_bg_color = ta.getColor(R.styleable.StatusBarView_sbv_bg_color, STYLE_NORMAL);
        sbv_title_color = ta.getColor(R.styleable.StatusBarView_sbv_title_color, STYLE_NORMAL);
        sbv_left_title_size = ta.getDimension(R.styleable.StatusBarView_sbv_left_title_size, 0);
        sbv_left_title_color = ta.getColor(R.styleable.StatusBarView_sbv_left_title_color, STYLE_NORMAL);
        sbv_right_one_title_color = ta.getColor(R.styleable.StatusBarView_sbv_right_one_title_color, STYLE_NORMAL);
        title = ta.getString(R.styleable.StatusBarView_sbv_title);
        left_title = ta.getString(R.styleable.StatusBarView_sbv_left_title);
        rightOneTitle = ta.getString(R.styleable.StatusBarView_sbv_right_one_title);
        isShowState = ta.getBoolean(R.styleable.StatusBarView_sbv_show_state, false);
        showLine = ta.getInt(R.styleable.StatusBarView_sbv_show_line, Type.SHOW_LINE.type);
        sbv_single_state = ta.getBoolean(R.styleable.StatusBarView_sbv_single_state, false);
        ta.recycle();
        initView();
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @SuppressLint("ResourceAsColor")
    private void initView() {
        setBackgroundColor(Color.parseColor("#00ffffff"));
        inflate(getContext(), R.layout.status_bar_view, this);
        viewById = findViewById(R.id.iv_left_back);
        tvTitle = findViewById(R.id.tv_title);
        tvLeftTitle = findViewById(R.id.tv_left_title);
        tvRightTitleOne = findViewById(R.id.tv_right_title_one);
        state_height = findViewById(R.id.state_height);
        view_line = findViewById(R.id.view_line);
        cl_parent = findViewById(R.id.cl_parent);
        if (left_icon > 0) {
            viewById.setImageResource(left_icon);
        }
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(left_title)) {
            tvLeftTitle.setText(left_title);
        }
        if (!TextUtils.isEmpty(rightOneTitle)) {
            tvRightTitleOne.setText(rightOneTitle);
        }
        if (sbv_left_title_size != 0) {
            tvLeftTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, sbv_left_title_size);
        }
        //状态栏高度
        int height = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getContext().getResources().getDimensionPixelSize(resourceId);
        }
        ViewGroup.LayoutParams layoutParams = state_height.getLayoutParams();
        layoutParams.height = height;
        state_height.setLayoutParams(layoutParams);
        state_height.setVisibility(isShowState ? VISIBLE : GONE);
        view_line.setVisibility(showLine == Type.SHOW_LINE.type ? VISIBLE : GONE);
        //状态栏颜色
//        if (sbv_state_color != 0) {
            state_height.setBackgroundColor(sbv_state_color);
//        }
        //toolbar背景色
//        if (sbv_bg_color != 0) {
            cl_parent.setBackgroundColor(sbv_bg_color);
//        }
        //标题字体颜色
        if (sbv_title_color != 0) {
            tvTitle.setTextColor(sbv_title_color);
        }
        if (sbv_left_title_color != 0) {
            tvLeftTitle.setTextColor(sbv_left_title_color);
        }
        //标题字体颜色
        if (sbv_right_one_title_color != 0) {
            tvRightTitleOne.setTextColor(sbv_right_one_title_color);
        }

        viewById.setOnClickListener(v -> {
            try {
                if(leftOneOnClickCallback != null) {
                    leftOneOnClickCallback.onViewClick();
                }
                ((Activity) getContext()).finish();
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString());
            }
        });
        if(sbv_single_state) {
            state_height.setVisibility(VISIBLE);
            tvLeftTitle.setVisibility(GONE);
            tvRightTitleOne.setVisibility(GONE);
            view_line.setVisibility(GONE);
            tvTitle.setVisibility(GONE);
            viewById.setVisibility(GONE);
//            ViewGroup.LayoutParams layoutParams1 = getLayoutParams();
//            layoutParams1.height = height;
//            setLayoutParams(layoutParams1);
        }
    }

    public void setLeftIcon(@DrawableRes int resourceId) {
        viewById.setImageResource(left_icon);
    }

    public void setStateBg(@ColorInt int resourceId) {
        state_height.setBackgroundColor(resourceId);
    }

    public void showStateHeight(boolean show) {
        state_height.setVisibility(isShowState ? VISIBLE : GONE);
    }

    public void setRightOne(String content) {
        tvRightTitleOne.setText(content);
    }

    public void setRightOneOnClick(OnClickListener oneOnClick) {
        tvRightTitleOne.setOnClickListener(oneOnClick);
    }

    public void setLeftOneOnClick(LeftOneOnClickCallback onClick) {
        leftOneOnClickCallback = onClick;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setLeftTitle(String title) {
        tvLeftTitle.setText(title);
    }

    public void setShowLine(Type type) {
        view_line.setVisibility(type.type == Type.SHOW_LINE.type ? VISIBLE : GONE);
    }

    public TextView getTitleView() {
        return tvTitle;
    }

    public TextView getLeftTitleView() {
        return tvLeftTitle;
    }

    public ImageView getLeftIconView() {
        return viewById;
    }

    public TextView getRightTitleOneView() {
        return tvRightTitleOne;
    }

    public enum Type{
        SHOW_LINE(1), HIDE_LINE(2);

        private int type;//自定义属性

        Type(int type) {
            this.type = type;
        }

        //自定义方法
        public int getType() {
            return type;
        }
    }

    public interface LeftOneOnClickCallback {
        void onViewClick();
    }
}
