package com.drivona.speed.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    // 计算两个日期之间的天数差异
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getDaysDifference(String dateStr1, String dateStr2) {
        LocalDate date1 = LocalDate.parse(dateStr1);
        LocalDate date2 = LocalDate.parse(dateStr2);
        return ChronoUnit.DAYS.between(date1, date2);
    }

    // 将日期字符串转换为 Calendar 对象
    private static Calendar getCalendarFromDateStr(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 计算两个日期之间的天数差异
    public static long getDaysDifference2(String dateStr1, String dateStr2) {
        Calendar calendar1 = getCalendarFromDateStr(dateStr1);
        Calendar calendar2 = getCalendarFromDateStr(dateStr2);

        if (calendar1 != null && calendar2 != null) {
            long diffMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
            return diffMillis / (24 * 60 * 60 * 1000);
        }

        return 0;
    }
}
