package com.drivona.speed.utils;

import static com.lalifa.utils.ResUtil.getDrawable;
import static per.goweii.layer.core.utils.Utils.requireActivity;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.car.app.model.CarIcon;
import androidx.car.app.navigation.model.Lane;
import androidx.car.app.navigation.model.Maneuver;
import androidx.car.app.navigation.model.Step;
import androidx.core.graphics.drawable.IconCompat;

import com.drivona.speed.utils.map.ManeuverUtils;
import com.google.android.libraries.mapsplatform.turnbyturn.model.StepInfo;

public class AppUtils {
    public static int dp2px(Context context, float dipValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        } catch (Exception e) {
            return (int) dipValue;
        }
    }

    public static int px2dp(Context context, float px) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (px / scale + 0.5f);
        } catch (Exception e) {
            return (int) px;
        }
    }

    public static int getPhoneWidthPixels(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics var2 = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(var2);
        }

        return var2.widthPixels;
    }

    public static int getPhoneHeightPixels(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics var2 = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(var2);
        }

        return var2.heightPixels;
    }

    public static Step buildStepFromStepInfo(Resources res,StepInfo stepInfo) {

        IconCompat maneuverIcon =
                IconCompat.createWithBitmap(HexUtils.Companion.getBitMap(res,ManeuverUtils.getManeuverIconResId(stepInfo)));
        Maneuver.Builder
                maneuverBuilder = new Maneuver.Builder(
                ManeuverConverter
                        .getAndroidAutoManeuverType(stepInfo.getManeuver()));
        // 环岛类型强制赋值出口角度
        if (stepInfo.getManeuver()>=43&& stepInfo.getManeuver()<=62){
            maneuverBuilder.setRoundaboutExitAngle(90);
        }
        CarIcon maneuverCarIcon = new CarIcon.Builder(maneuverIcon).build();
        maneuverBuilder.setIcon(maneuverCarIcon);
        Step.Builder stepBuilder =
                new Step.Builder()
                        .setRoad(stepInfo.getFullRoadName())
                        .setCue(stepInfo.getFullInstructionText())
                        .setManeuver(maneuverBuilder.build());


        return stepBuilder.build();
    }

}
