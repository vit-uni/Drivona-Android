package com.lalifa.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.StringUtils;
import com.lalifa.api.DynamicList;
import com.lalifa.base.R;
import com.lalifa.utils.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ： I5
 * time    ： 2020/12/23
 * package_name : com.benben.guochao.widget
 * usefuless    : guochao
 */
@SuppressLint("AppCompatCustomView")
public class DrawableSpannableTextView extends AppCompatTextView implements View.OnTouchListener,
        View.OnFocusChangeListener, TextWatcher {

    private ProtocolClickListener protocolClickeListner;
    private Drawable drawable;
    private OnTouchListener mTouchListener;
    private OnFocusChangeListener mFocusChangeListener;
    private int dimension;
    private int bottom_icon;
    private int left_icon;
    private int top_icon;
    private int right_icon;
    private Drawable drawable1;
    private float icon_padding;
    private float right_icon_size;
    private boolean drawableClick;
    private String discoloration;
    private int discolorationColor;
    /**
     * 是否显示展开标识
     */
    private boolean dtvExpand;

    private StringBuilder myTitle;
    private String[] protocol;
    private String tag = " 展开  ";
    private int maxLines;
    private int exImgRotation = 0;

    public DrawableSpannableTextView(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public DrawableSpannableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
//        super.addTextChangedListener(this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableSpannableTextView);
        dimension = typedArray.getDimensionPixelSize(R.styleable.DrawableSpannableTextView_icon_size, 0);
        bottom_icon = typedArray.getResourceId(R.styleable.DrawableSpannableTextView_bottom_icon, 0);
        left_icon = typedArray.getResourceId(R.styleable.DrawableSpannableTextView_left_icon, 0);
        top_icon = typedArray.getResourceId(R.styleable.DrawableSpannableTextView_top_icon, 0);
        icon_padding = typedArray.getDimension(R.styleable.DrawableSpannableTextView_icon_padding, 0);
        right_icon_size = typedArray.getDimension(R.styleable.DrawableSpannableTextView_right_icon_size, 0);
        right_icon = typedArray.getResourceId(R.styleable.DrawableSpannableTextView_right_icon, 0);
        drawableClick = typedArray.getBoolean(R.styleable.DrawableSpannableTextView_dtv_drawable_click, false);
        discoloration = typedArray.getString(R.styleable.DrawableSpannableTextView_dtv_discoloration);
        discolorationColor = typedArray.getColor(R.styleable.DrawableSpannableTextView_dtv_discoloration_color, 0);
        dtvExpand = typedArray.getBoolean(R.styleable.DrawableSpannableTextView_dtv_expand, false);
        maxLines = typedArray.getInt(R.styleable.DrawableSpannableTextView_dtv_expand_lines, Integer.MAX_VALUE);
        typedArray.recycle();
        initView();
    }

    private void initView() {
        if (bottom_icon != 0) {
            setDrawableBottom(bottom_icon, dimension, dimension);
        }
        if (top_icon != 0) {
            setDrawableTop(top_icon, dimension, dimension);
        }
        if (left_icon != 0) {
            setDrawableLeft(left_icon, dimension, dimension);
        }
//        setDrawableLeft(drawable1, (int)dimension, (int)dimension);
        if (right_icon != 0) {
            setDrawableReghit(right_icon, dimension, dimension);
        }

        if (left_icon != 0 && right_icon != 0) {
            setDrawableLeftOrRight(left_icon, right_icon, (int)right_icon_size, (int)dimension);
        }
        setCompoundDrawablePadding((int) icon_padding);
        if(discoloration != null && !discoloration.isEmpty()){
            String[] split = discoloration.split(",");
            setTitle(discolorationColor, getText().toString(), split);
        }
    }

    /**
     * 设置跑马灯
     */
    public void startMarquee() {
        setMarqueeRepeatLimit(Integer.MAX_VALUE);
        setFocusable(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine();
        setFocusableInTouchMode(true);
        setHorizontallyScrolling(true);
        requestFocus();
    }

    /**
     * 左边添加图片, 可设置大小，传入dp
     *
     * @param left
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableLeftOrRight(@DrawableRes int left, @DrawableRes int right, int bundsRight, int bundsLeft) {
        drawable = getContext().getDrawable(left);
//        int reight = dipToPx(bundsRight);
//        int lefts = dipToPx(bundsLeft);
        drawable1 = getContext().getDrawable(right);
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(limitDrawableSize(drawable, bundsRight, bundsLeft), compoundDrawables[1], limitDrawableSize(drawable1, bundsRight, bundsLeft), compoundDrawables[3]);
    }

    /**
     * 左边添加图片
     *
     * @param left
     * @param dimension
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableLeft(@DrawableRes int left, float dimension, float v) {
        drawable = getContext().getDrawable(left);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(drawable, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

    /**
     * 左边添加图片, 可设置大小，传入dp
     *
     * @param left
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableLeft(@DrawableRes int left, int bundsRight, int bundsLeft) {
        drawable = getContext().getDrawable(left);
        drawable.setBounds(0, 0, bundsRight, bundsLeft);
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(limitDrawableSize(drawable, bundsRight, bundsLeft), compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableLeft(Drawable left, int bundsRight, int bundsLeft) {
        int reight = dipToPx(bundsRight);
        int lefts = dipToPx(bundsLeft);
        left.setBounds(0, 0, reight, lefts);
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(limitDrawableSize(left, bundsRight, bundsLeft), compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

    /**
     * 左边添加图片, 可设置大小，传入dp
     *
     * @param left
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableLeft(@DrawableRes int left) {
        drawable = getContext().getDrawable(left);
        drawable.setBounds(0, 0, 0, 0);
        setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * 左边添加图片, 可设置大小，传入dp
     *
     * @param reghit
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableReghit(@DrawableRes int reghit, int bundsRight, int bundsLeft) {
        drawable = getContext().getDrawable(reghit);
        int reight = dipToPx(bundsRight);
        int lefts = dipToPx(bundsLeft);
        drawable.setBounds(0, 0, reight, lefts);
        setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * 左边添加图片, 可设置大小，传入dp
     *
     * @param reghit
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableReghit(@DrawableRes int reghit) {
        drawable = getContext().getDrawable(reghit);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        setCompoundDrawables(null, null, drawable, null);
    }

    /**
     * 左边添加图片
     *
     * @param reghit
     * @param dimension
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableReghit(@DrawableRes int reghit, float dimension, float v) {
        drawable = getContext().getDrawable(reghit);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3]);
    }

    /**
     * 头部添加图片
     *
     * @param top
     * @param dimension
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableTop(@DrawableRes int top, float dimension, float v) {
        drawable = getContext().getDrawable(top);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], drawable, compoundDrawables[2], compoundDrawables[3]);
    }

    /**
     * 头部添加图片, 可设置大小，传入dp
     *
     * @param reghit
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableTop(@DrawableRes int reghit, int bundsRight, int bundsLeft) {
        drawable = getContext().getDrawable(reghit);
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], limitDrawableSize(drawable, bundsRight, bundsLeft), compoundDrawables[2], compoundDrawables[3]);
    }

    /**
     * 底部添加图片
     *
     * @param bottom
     * @param dimension
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableBottom(@DrawableRes int bottom, float dimension, float v) {
        drawable = getContext().getDrawable(bottom);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], drawable);
    }

    /**
     * 底部添加图片
     *
     * @param bottom
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setDrawableBottom(@DrawableRes int bottom, int bundsRight, int bundsLeft) {
        drawable = getContext().getDrawable(bottom);
        Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], limitDrawableSize(drawable, bundsRight, bundsLeft));
    }

    /**
     * 重新限定 Drawable 宽高
     */
    private Drawable limitDrawableSize(Drawable drawable, int with, int height) {
        if (drawable == null) {
            return null;
        }
        if (with == 0 || height == 0) {
            return drawable;
        }
        drawable.setBounds(0, 0, dimension, dimension);
        return drawable;
    }

    public int dipToPx(int dip) {
        return dip2px(getContext(), dip);
    }

    private void setDrawableVisible(boolean visible) {
        if (drawable == null) {
            return;
        }
        if (drawable.isVisible() == visible) {
            return;
        }

        drawable.setVisible(visible, false);
        Drawable[] drawables = getCompoundDrawablesRelative();
        setCompoundDrawablesRelative(
                drawables[0],
                drawables[1],
                visible ? drawable : null,
                drawables[3]);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        mFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mTouchListener = onTouchListener;
    }

    /**
     * {@link OnFocusChangeListener}
     */

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus && getText() != null) {
            setDrawableVisible(getText().length() > 0);
        } else {
            setDrawableVisible(false);
        }
        if (mFocusChangeListener != null) {
            mFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    public void setTitleHavePhone(String text) {
        String phoneNum = getPhoneNum(text);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);//设置电话号码字体颜色
                ds.setUnderlineText(true);//设置电话号码下划线
            }

            @Override
            public void onClick(@NonNull View widget) {
                //电话号码点击事件
            }
        }, text.indexOf(phoneNum), text.indexOf(phoneNum) + 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spannableString);
        setMovementMethod(LinkMovementMethod.getInstance());

    }

    /**
     * 获取字符串里的里的手机号码
     *
     * @param text 包含手机号的字符串
     * @return 手机号
     */
    public static String getPhoneNum(String text) {
        String regex = "1[35789]\\d{9}";//正则规则有待优化
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }


    /**
     * {@link OnTouchListener}
     */

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int x = (int) event.getX();
        if (drawable == null || drawableClick) {
            if(tTextViewOnCLikeClicke != null && event.getAction() == MotionEvent.ACTION_UP) {
//                tTextViewOnCLikeClicke.onClicke();
            }
            return mTouchListener != null && mTouchListener.onTouch(view, event) ;
        }
        // 是否触摸了 Drawable
        boolean touchDrawable = false;
        // 获取布局方向
        int layoutDirection = getLayoutDirection();
        if (layoutDirection == LAYOUT_DIRECTION_LTR) {
            // 从左往右
            touchDrawable = x > getWidth() - drawable.getIntrinsicWidth() - getPaddingEnd() &&
                    x < getWidth() - getPaddingEnd();
        } else if (layoutDirection == LAYOUT_DIRECTION_RTL) {
            // 从右往左
            touchDrawable = x > getPaddingStart() &&
                    x < getPaddingStart() + drawable.getIntrinsicWidth();
        }

        if (drawable.isVisible() && touchDrawable) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (drawableOnCLikeClicke != null) {
                    drawableOnCLikeClicke.onClicke(this, drawable);
                }
            }
            return true;
        } else {
            if(tTextViewOnCLikeClicke != null && event.getAction() == MotionEvent.ACTION_UP) {
//                tTextViewOnCLikeClicke.onClicke();
            }
        }
        return mTouchListener != null && mTouchListener.onTouch(view, event) ;
    }

    /**
     * {@link TextWatcher}
     */

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()) {
            setDrawableVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public DrawableOnCLikeClicke drawableOnCLikeClicke;
    public TextViewOnCLikeClicke tTextViewOnCLikeClicke;

    public void setDrawableOnCLikeClicke(DrawableOnCLikeClicke drawableOnCLikeClicke) {
        this.drawableOnCLikeClicke = drawableOnCLikeClicke;
    }

    public void setTextViewOnCLikeClicke(TextViewOnCLikeClicke textViewOnCLikeClicke) {
        this.tTextViewOnCLikeClicke = textViewOnCLikeClicke;
    }

    public interface DrawableOnCLikeClicke {
        void onClicke(DrawableSpannableTextView drawableTextView, Drawable drawable);
    }

    public interface TextViewOnCLikeClicke {
        void onClicke();
    }

    private boolean isInitialize = true;
    /**
     * dspannable
     */
    public void setSpannableString(int color, final String title, String... protocol) {
//        DynamicList dynamicList = (DynamicList) getTag();
//        if(dynamicList == null || dynamicList.getSpannableStringBuilder() == null) {
            myTitle = new StringBuilder();
            myTitle.append(title);
            discolorationColor = color;
            this.protocol = protocol;
            if (isInitialize) {
                isInitialize = false;
                getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        setSpannable(color, title, protocol);
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                });
//                post(() -> {
//                });
            } else {
                setSpannable(color, title, protocol);
            }
//        } else {
//            setText(dynamicList.getSpannableStringBuilder());
//        }
    }

    /**
     * 用来判断当前文本是否需要添加暂开
     */
    boolean isExpand = false;

    private void setSpannable(int color, String title, String[] protocol) {
        int lineMaxNumber = getLineMaxNumber(title, getPaint(), getWidth()) * maxLines;

        if (dtvExpand && title.length() > lineMaxNumber && getMaxLines() == maxLines) {
            isExpand = true;
            title = title.substring(0, lineMaxNumber - tag.length() * 3 < 0 ? lineMaxNumber : lineMaxNumber - tag.length() * 3);
            if(title .length()> 80) {
                title = title.substring(0, (int)(title.length() * 0.75f)) +  tag;
            } else {
                title += tag;
            }
        } else if(dtvExpand && title.length() > lineMaxNumber && isExpand){
            title += tag;
        }
        SpannableStringBuilder spannableString = new SpannableStringBuilder(title);
        if (StringUtils.isEmpty(title)) {
            return;
        }
        String userName = "@";
        for (int i = 0; i < protocol.length; i++) {
            int startIndex = 0;
            //防止出现多个相同字段
            while (title.indexOf(userName + protocol[i], startIndex) != -1) {
                //ids.add(startIndex);
                startIndex = title.indexOf(userName + protocol[i], startIndex);

                MyClickText myClickText = new MyClickText(i, color, protocol[i]);
                //传入一个MyClickText，并且继承ClickableSpan（必须这样写啊）
                spannableString.setSpan(myClickText, startIndex, startIndex + (userName + protocol[i]).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = startIndex + userName.length() + protocol[i].length();
            }
        }
        if(getTag() != null) {
            ((DynamicList)getTag()).setSpannableStringBuilder(spannableString);
            ((DynamicList)getTag()).setExpand(isExpand);
        }
        if(isExpand) {
            addTagToTextView(title, tag, null, spannableString);
        } else {
            setText(spannableString);
        }
        //设置点击后的颜色为透明
        if (getContext() != null) {
            setHighlightColor(getContext().getResources().getColor(R.color.transparent));
        }
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setExpandText(String title) {
        if (isInitialize) {
            isInitialize = false;
            final String title1 = title;
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    int lineMaxNumber = getLineMaxNumber(title1, getPaint(), getWidth()) * maxLines;
                    String title2 = title1;
                    if (dtvExpand && title1.length() > lineMaxNumber && getMaxLines() == maxLines) {
                        isExpand = true;
                        title2 = title1.substring(0, lineMaxNumber - tag.length()) + tag;
                    }
                    if(isExpand) {
                        addTagToTextView(title2.substring(0, lineMaxNumber - tag.length()) + tag, tag, null, null);
                    } else {
                        setText(title1);
                    }
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            int lineMaxNumber = getLineMaxNumber(title, getPaint(), getWidth()) * maxLines;

            if (dtvExpand && title.length() > lineMaxNumber && getMaxLines() == maxLines) {
                isExpand = true;
                title = title.substring(0, lineMaxNumber - tag.length()) + tag;
            }
            if(isExpand) {
                addTagToTextView(title, tag, null, null);
            } else {
                setText(title);
            }
        }
    }

    public static int screenWidthPx; //屏幕宽 px
    public static int screenhightPx; //屏幕高 px
    public static float density;//屏幕密度
    public static int densityDPI;//屏幕密度
    public static float screenWidthDip;//  dp单位
    public static float screenHightDip;//  dp单位

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 选中文本内所有相同文案并设置可点击
     * @param color
     * @param title
     * @param protocol
     * @return
     */
    public DrawableSpannableTextView setTitle(int color, final String title, String... protocol) {
        myTitle = new StringBuilder();
        myTitle.append(title);
        discolorationColor = color;
        this.protocol = protocol;
        if (isInitialize) {
            isInitialize = false;
            post(() -> {
                settitle2(color, title, protocol);
            });
        } else {
            settitle2(color, title, protocol);
        }
        return this;
    }

    private void settitle2(int color, String title, String[] protocol) {
        int lineMaxNumber = getLineMaxNumber(title, getPaint(), getWidth()) * maxLines;
        if (dtvExpand && title.length() > lineMaxNumber && getMaxLines() == maxLines) {
            isExpand = true;
            title = title.substring(0, lineMaxNumber - tag.length() * 2);
            title = title + tag;
        }
        setMovementMethod(LinkMovementMethod.getInstance());
        SpannableStringBuilder spannableString = new SpannableStringBuilder(title);
        for (int i = 0; i < protocol.length; i++) {
//            MyClickText myClickText = new MyClickText(i, color, protocol[i]);
//            //传入一个MyClickText，并且继承ClickableSpan（必须这样写啊）
//            spannableString.setSpan(myClickText, title.indexOf(protocol[i]),
//                    title.indexOf(protocol[i]) + protocol[i].length()
//                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int startIndex = 0;

            while (title.indexOf(protocol[i], startIndex) != -1) {
                //ids.add(startIndex);
                startIndex = title.indexOf(protocol[i], startIndex);
                MyClickText myClickText = new MyClickText(i, color, protocol[i]);
                //传入一个MyClickText，并且继承ClickableSpan（必须这样写啊）
                spannableString.setSpan(myClickText, startIndex, startIndex + protocol[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = startIndex + protocol[i].length();
            }
        }
        if(isExpand) {
            addTagToTextView(title, tag, null, spannableString);
        } else {
            setText(spannableString);
        }
        //设置点击后的颜色为透明
        if (getContext() != null) {
            setHighlightColor(getContext().getResources().getColor(R.color.transparent));
        }
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * span
     */
    private class MyClickText extends ClickableSpan {
        private int type = 0;
        private int color;
        private String title;

        public MyClickText(int type, int color, String title) {
            this.type = type;
            this.color = color;
            this.title = title;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            //设置文本的颜色
            ds.setColor(color);
            //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            if (protocolClickeListner != null) {
                protocolClickeListner.protocol(type, title);
            }
        }
    }

    /**
     * 获取textview一行最大能显示几个字(需要在TextView测量完成之后)
     *
     * @param text     文本内容
     * @param paint    textview.getPaint()
     * @param maxWidth textview.getMaxWidth()/或者是指定的数值,如200dp
     */
    private int getLineMaxNumber(String text, TextPaint paint, int maxWidth) {
        if (null == text || "".equals(text)) {
            return 0;
        }
        StaticLayout staticLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder obtain = StaticLayout.Builder.obtain(text, 0, text.length(), paint, maxWidth);
            obtain.setAlignment(Layout.Alignment.ALIGN_NORMAL);
            obtain.setIncludePad(false);
            obtain.setLineSpacing(0, 1.0f);
            staticLayout = obtain.build();
        } else {
            staticLayout = new StaticLayout(text, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
        }
        //获取第一行最后显示的字符下标
        return staticLayout.getLineEnd(0);
    }

    private void addTagToTextView(String title, String tag, Drawable bg, SpannableStringBuilder spannableString) {
        if (TextUtils.isEmpty(title)) {
            title = "";
        }

        /**
         * 创建TextView对象，设置drawable背景，设置字体样式，设置间距，设置文本等
         * 这里我们为了给TextView设置margin，给其添加了一个父容器LinearLayout。不过他俩都只是new出来的，不会添加进任何布局
         */
        LinearLayout layout = new LinearLayout(getContext());
        TextView textView = new TextView(getContext());
        textView.setText(tag);
        textView.setBackground(bg);
        textView.setTextSize(12f);
        textView.setTextColor(Color.parseColor("#4ACFFF"));
        textView.setIncludeFontPadding(false);
        textView.setPadding(dip2px(getContext(), 1), 0, dip2px(getContext(), 1), 0);
        textView.setHeight(dip2px(getContext(), 17));
        textView.setGravity(Gravity.CENTER_VERTICAL);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_text_ext);
        imageView.setRotation(exImgRotation);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置左间距
        layoutParams.leftMargin = dip2px(getContext(), 0f);
        // 设置下间距，简单解决ImageSpan和文本竖直方向对齐的问题
        layoutParams.bottomMargin = dip2px(getContext(), -2);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layout.addView(textView, layoutParams);
        layout.addView(imageView, layoutParams);

        /**
         * 第二步，测量，绘制layout，生成对应的bitmap对象
         */
        layout.setDrawingCacheEnabled(true);
        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        // 给上方设置的margin留出空间
        layout.layout(0, 0, textView.getMeasuredWidth() + dip2px(getContext(), (6 + 3)), textView.getMeasuredHeight());
        // 获取bitmap对象
        Bitmap bitmap = Bitmap.createBitmap(layout.getDrawingCache());
        //千万别忘最后一步
        layout.destroyDrawingCache();

        /**
         * 第三步，通过bitmap生成我们需要的ImageSpan对象
         */
        ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);

        /**
         * 第四步将ImageSpan对象设置到SpannableStringBuilder的对应位置
         */
        if(spannableString != null) {
            spannableString.setSpan(imageSpan, title.length() - tag.length(), title.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ExpandClickText(0, Color.parseColor("#4ACFFF"), tag), title.length() - tag.length(), title.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            setText(spannableString);
        } else {
            String content = title;
            SpannableStringBuilder ssb = new SpannableStringBuilder(content);
            //将尾部tag字符用ImageSpan替换
            ssb.setSpan(imageSpan, title.length() - tag.length(), content.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ssb.setSpan(new ExpandClickText(0, Color.parseColor("#4ACFFF"), tag), title.length() - tag.length(), content.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            setText(ssb);
        }
    }

    private class ExpandClickText extends ClickableSpan {
        private int type = 0;
        private int color;
        private String title;

        public ExpandClickText(int type, int color, String title) {
            this.type = type;
            this.color = color;
            this.title = title;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            //设置文本的颜色
            ds.setColor(color);
            //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            if(getMaxLines() > maxLines) {
                tag = " 展开  ";
                exImgRotation = 0;
                setMaxLines(maxLines);
//                isExpand = true;
                if(((DynamicList)getTag()) != null) {
                    ((DynamicList)getTag()).setExpand(true);
                }
            } else {
                tag = " 收起  ";
                exImgRotation = 180;
                setMaxLines(Integer.MAX_VALUE);
//                isExpand = false;
                if(((DynamicList)getTag()) != null) {
                    ((DynamicList)getTag()).setExpand(false);
                }
            }
//            if(((DynamicList)getTag()) != null && ((DynamicList)getTag()).getSpannableStringBuilder() != null) {
//                setText(((DynamicList)getTag()).getSpannableStringBuilder());
//            } else {
                setSpannableString(discolorationColor, myTitle.toString(), protocol);
//            }
        }
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public void setProtocolClickListener(ProtocolClickListener protocolClickeListner) {
        this.protocolClickeListner = protocolClickeListner;
    }

    public interface ProtocolClickListener {
        /**
         * @param type 根据输入的协议顺序的下标，从0开始
         */
        void protocol(int type, String title);
    }
}
