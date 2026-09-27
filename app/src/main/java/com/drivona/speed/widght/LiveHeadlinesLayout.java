package com.drivona.speed.widght;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.drivona.speed.R;
import com.drivona.speed.api.AppSetting;
import com.drivona.speed.api.WolrdGift;
import com.google.gson.Gson;
import com.lalifa.ext.Tools;

import java.util.ArrayList;

public class LiveHeadlinesLayout extends LinearLayout {
    private static float scale;
    private TextView tvName;
    private TextView tvLiveName;
    private TextView tvGiftName;
    private TextView roomName;
    private ImageView ivGift;
    private static final int TOTAL = 5;
    private int mCount = TOTAL;
    private ArrayList<WolrdGift> giftInfoList = new ArrayList<>();
    private WolrdGift bean;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mCount--;
            if (mCount > 0) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            } else {
                mCount = TOTAL;
                mGlobalOutGiftShowAnimator.start();
            }
        }
    };
    private int mDp500;
    private int mDp0;
    private ObjectAnimator mGlobalGiftShowAnimator;
    private ObjectAnimator mGlobalOutGiftShowAnimator;

    public LiveHeadlinesLayout(Context context) {
        super(context);
    }

    public LiveHeadlinesLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scale = getContext().getResources().getDisplayMetrics().density;
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_live_headlins, this, false);
        addView(inflate);
        setClipChildren(false);
        tvName = inflate.findViewById(R.id.tv_name);
        tvLiveName = inflate.findViewById(R.id.tv_live_name);
        tvGiftName = inflate.findViewById(R.id.tv_gift_name);
        roomName = inflate.findViewById(R.id.roomName);
        ivGift = inflate.findViewById(R.id.iv_gift);
        mDp500 = dp2px(500);
        mDp0 = dp2px(-500);

        LinearInterpolator linearInterpolator = new LinearInterpolator();

        mGlobalGiftShowAnimator = ObjectAnimator.ofFloat(this, "translationX", mDp500, 0);
        mGlobalGiftShowAnimator.setDuration(1000);
        mGlobalGiftShowAnimator.setInterpolator(linearInterpolator);
        mGlobalGiftShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                if (mHandler != null) {
//                    mHandler.sendEmptyMessageDelayed(WHAT_GLOBAL, 1200);
//                }
            }
        });

        mGlobalOutGiftShowAnimator = ObjectAnimator.ofFloat(this, "translationX", 0, mDp0);
        mGlobalOutGiftShowAnimator.setDuration(1000);
        mGlobalOutGiftShowAnimator.setInterpolator(linearInterpolator);
        mGlobalOutGiftShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
                giftInfoList.remove(0);
                if(giftInfoList.size() > 0) {
                    refreshNotify(giftInfoList.get(0));
                }
            }
        });
    }

    public void addNotify(WolrdGift bean) {
        String string = SPUtils.getInstance().getString(Tools.SETTING);
        AppSetting appSetting = new Gson().fromJson(string, AppSetting.class);
        if(appSetting != null && appSetting.getPermissions().getSet11() == 1) {
            return;
        }
        if(giftInfoList.size() == 0) {
            giftInfoList.add(bean);
            refreshNotify(bean);
        } else {
            giftInfoList.add(bean);
        }
    }

    public void refreshNotify(WolrdGift bean) {
        String string = SPUtils.getInstance().getString(Tools.SETTING);
        AppSetting appSetting = new Gson().fromJson(string, AppSetting.class);
        if(appSetting != null && appSetting.getPermissions().getSet11() == 1) {
            giftInfoList.clear();
            return;
        }
        this.bean = bean;
        mHandler.removeMessages(0);
        tvName.setText(bean.getNickname() + ":");
        tvLiveName.setText(bean.getLiveUserName());
        roomName.setText(String.format("在 %s 送给 ", bean.getRoomName()));
        tvGiftName.setText(String.format("%sx%s", bean.getGiftName(), bean.getGiftNum()));
        Glide.with(getContext()).load(getImageUrl(bean.getGiftAvatar())).error(R.drawable.tuiroomkit_head).into(ivGift);
        setVisibility(VISIBLE);
        mCount = TOTAL;
        if (mGlobalOutGiftShowAnimator.isRunning()) {
            mGlobalOutGiftShowAnimator.end();
        }
        mHandler.sendEmptyMessageDelayed(0, 0);
        mGlobalGiftShowAnimator.start();
//        String liveUid = bean.getLiveUid();
    }
    public String getImageUrl(String url) {
        return url.contains("http") ? url : Tools.HOST + url;
    }

    public static int dp2px(int dpVal) {
        return (int) (scale * dpVal + 0.5f);
    }

    public WolrdGift getBean() {
        return bean;
    }
}
