package com.drivona.speed.widght;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drivona.speed.R;

import org.jetbrains.annotations.Nullable;


public class LevelLayout extends LinearLayout {

    private ImageView huiyuan;
    private ImageView ivLevel;
    private ImageView ivLiveLevel;
    private ImageView liang;
    private ImageView loveLogotype;
//    private CommonAppConfig appConfig;
    private TextView gangBusinessCard;
    private ImageView loveLevel;

    public LevelLayout(Context context) {
        super(context);
    }

    public LevelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LevelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
//        appConfig = CommonAppConfig.getInstance();
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.level_layout, this, false);
        addView(inflate);
        huiyuan = inflate.findViewById(R.id.huiyuan);
        ivLevel = inflate.findViewById(R.id.iv_level);
        ivLiveLevel = inflate.findViewById(R.id.iv_live_level);
        liang = inflate.findViewById(R.id.liang);
        loveLogotype = inflate.findViewById(R.id.love_logotype);
        gangBusinessCard = inflate.findViewById(R.id.gang_business_card);
        loveLevel = inflate.findViewById(R.id.love_level);
    }

    public void setVip(String type){
        setVip(Integer.parseInt(type));
    }

    public void setVip(int type){
        huiyuan.setVisibility(type == 1 ? VISIBLE : GONE);
    }

    public void setLevel(int level) {
//        LevelBean levelBean = appConfig.getLevel(level);
//        if (levelBean != null) {
//            ivLevel.setVisibility(View.VISIBLE);
//            ImgLoader.display(getContext(), levelBean.getThumb(), ivLevel);
//        } else {
//            ivLevel.setVisibility(View.GONE);
//        }
    }

    public void setLiveLevel(int level) {
//        LevelBean anchorLevelBean = appConfig.getAnchorLevel(level);
//        if (anchorLevelBean != null) {
//            ivLiveLevel.setVisibility(View.VISIBLE);
//            ImgLoader.display(getContext(), anchorLevelBean.getThumb(), ivLiveLevel);
//        } else {
//            ivLiveLevel.setVisibility(View.GONE);
//        }
    }

    public void setLiang(String name) {
        if(!TextUtils.isEmpty(name) && !"0".equals(name)) {
            liang.setVisibility(VISIBLE);
        } else {
            liang.setVisibility(GONE);
        }
    }

    /**
     * 真爱团标识
     * @param level
     */
    public void setLoveLogotype(int level) {
//        LevelBean levelBean = appConfig.getLevelTeam(level);
//        if(levelBean != null) {
//            loveLogotype.setVisibility(VISIBLE);
//            ImgLoader.display(getContext(), levelBean.getThumbIcon(), loveLogotype);
//        } else {
//            loveLogotype.setVisibility(GONE);
//        }
    }

    /**
     * 真爱团标识
     * @param level
     */
    public void setLoveLogoLevel(int level) {
//        LevelBean levelBean = appConfig.getLevelTeam(level);
//        if(levelBean != null) {
//            loveLevel.setVisibility(VISIBLE);
//            ImgLoader.display(getContext(), levelBean.getThumbIcon(), loveLevel);
//        } else {
//            loveLevel.setVisibility(GONE);
//        }
    }

    /**
     * 真爱团标识
     * @param icon
     */
    public void setLoveLogotype(String icon) {
//        if(!TextUtils.isEmpty(icon)) {
//            loveLogotype.setVisibility(VISIBLE);
//            ImgLoader.display(getContext(), icon, loveLogotype);
//        } else {
//            loveLogotype.setVisibility(GONE);
//        }
    }

    /**
     * 公会标识
     * @param name
     */
    public void setGuildID(String name) {
        if(!TextUtils.isEmpty(name)) {
            gangBusinessCard.setVisibility(VISIBLE);
            gangBusinessCard.setText(name);
        } else {
            gangBusinessCard.setVisibility(GONE);
        }
    }
}
