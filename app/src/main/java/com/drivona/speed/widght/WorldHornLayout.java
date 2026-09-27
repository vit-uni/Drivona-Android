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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.drivona.speed.R;
import com.drivona.speed.utils.GoRoomUtils;
import com.lalifa.ext.Tools;

public class WorldHornLayout extends LinearLayout {
    private static float scale;
    private TextView tvName;
    private TextView tvContent;
    private TextView follow;
    private static final int TOTAL = 5;
    private int mCount = TOTAL;
//    private ArrayList<TUICommonDefine.Message> giftInfoList = new ArrayList<>();
//    private TUICommonDefine.Message bean;

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

    public WorldHornLayout(Context context) {
        super(context);
    }

    public WorldHornLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scale = getContext().getResources().getDisplayMetrics().density;
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_world_horn, this, false);
        tvName = inflate.findViewById(R.id.tv_name);
        tvContent = inflate.findViewById(R.id.tv_content);
        follow = inflate.findViewById(R.id.follow);
        addView(inflate);
        setClipChildren(false);
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
//                giftInfoList.remove(0);
//                if(giftInfoList.size() > 0) {
//                    refreshNotify(giftInfoList.get(0));
//                }
            }
        });
    }

//    public void addNotify(TUICommonDefine.Message bean) {
//        String string = SPUtils.getInstance().getString(Tools.SETTING);
//        AppSetting appSetting = new Gson().fromJson(string, AppSetting.class);
//        if(appSetting != null && appSetting.getPermissions().getSet11() == 1) {
//            return;
//        }
//        if(giftInfoList.size() == 0) {
//            giftInfoList.add(bean);
//            refreshNotify(bean);
//        } else {
//            giftInfoList.add(bean);
//        }
//    }

//    public void refreshNotify(TUICommonDefine.Message bean) {
//        String string = SPUtils.getInstance().getString(Tools.SETTING);
//        AppSetting appSetting = new Gson().fromJson(string, AppSetting.class);
//        if(appSetting != null && appSetting.getPermissions().getSet11() == 1) {
//            giftInfoList.clear();
//            return;
//        }
//        this.bean = bean;
//        mHandler.removeMessages(0);
//        WorldChannelMessage worldChannelMessage = new Gson().fromJson(bean.message, WorldChannelMessage.class);
//        tvName.setText(bean.userName);
//        follow.setOnClickListener(v -> {
//            GoRoomUtils.Companion.showBottomIosDialog((Activity) getContext(), Integer.valueOf(worldChannelMessage.getRoom_id()), 0, "", false);
//        });
//        tvContent.setText(worldChannelMessage.getContents());
//        setVisibility(VISIBLE);
//        mCount = TOTAL;
//        if (mGlobalOutGiftShowAnimator.isRunning()) {
//            mGlobalOutGiftShowAnimator.end();
//        }
//        mHandler.sendEmptyMessageDelayed(0, 0);
//        mGlobalGiftShowAnimator.start();
////        String liveUid = bean.getLiveUid();
//    }
    public String getImageUrl(String url) {
        return url.contains("http") ? url : Tools.HOST + url;
    }

    public static int dp2px(int dpVal) {
        return (int) (scale * dpVal + 0.5f);
    }

//    public TUICommonDefine.Message getBean() {
//        return bean;
//    }
}
