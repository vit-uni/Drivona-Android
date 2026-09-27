package com.drivona.speed.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * 功能描述：修改app内部的语言工具类
 */
public class LanguageUtil {

    /*语言类型：
     * 此处支持3种语言类型，更多可以自行添加。
     * */
    private static final String ENGLISH = "en";
    private static final String CHINESE = "ch";
    private static final String DE = "de";
    private static final String FR = "fr";
    private static final String ES = "es";
    private static final String IT = "it";
    private static final String TRADITIONAL_CHINESE = "zh_rTW";

    private static HashMap<String, Locale> languagesList = new HashMap<String, Locale>(3) {{
        put(ENGLISH, Locale.ENGLISH);
        put(CHINESE, Locale.CHINESE);
        put(DE, Locale.GERMAN);
        put(ES, new Locale("es"));
        put(IT, Locale.ITALY);
        put(FR, Locale.FRANCE);
        put(TRADITIONAL_CHINESE, Locale.TRADITIONAL_CHINESE);
    }};

    /**
     * 修改语言
     *
     * @param activity 上下文
     * @param language 例如修改为 英文传“en”，参考上文字符串常量
     * @param cls      要跳转的类（一般为入口类）
     */
    public static void changeAppLanguage(Activity activity, String language, Class<?> cls) {
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        // app locale 默认简体中文
        Locale locale = getLocaleByLanguage(language.isEmpty() ? "en" : language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);

        Log.e("Log", "设置的语言：" + language);

    }

    /**
     * 获取指定语言的locale信息，如果指定语言不存在
     * 返回本机语言，如果本机语言不是语言集合中的一种，返回英语
     */
    public static Locale getLocaleByLanguage(String language) {
        if (isContainsKeyLanguage(language)) {
            return languagesList.get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : languagesList.keySet()) {
                if (TextUtils.equals(languagesList.get(key).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.ENGLISH;
    }

    /**
     * 如果此映射包含指定键的映射关系，则返回 true
     */
    private static boolean isContainsKeyLanguage(String language) {
        return languagesList.containsKey(language);
    }

    // 保存当前选中语言
    private static Locale sCurrentLocale;

    /**
     * 设置App语言
     */
    public static void setAppLanguage(Context context, Locale locale) {
        sCurrentLocale = locale;
        // 更新全局配置
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(config, dm);
    }
    /**
     * 获取当前语言
     */
    public static Locale getCurrentLocale() {
        return sCurrentLocale;
    }

    /**
     * 依附Activity更新上下文（关键：不重建页面也能生效）
     */
    public static Context attachBaseContext(Context context) {
        if (sCurrentLocale == null) {
            return context;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration config = context.getResources().getConfiguration();
            config.setLocale(sCurrentLocale);
            return context.createConfigurationContext(config);
        } else {
            setAppLanguage(context, sCurrentLocale);
            return context;
        }
    }

}

