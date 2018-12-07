package com.casc.pmtools.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.casc.pmtools.MyApplication;

/**
 * 配置信息存储辅助类
 */
public class SpHelper {

    private static final String TAG = SpHelper.class.getSimpleName();
    private static final String FILE_NAME = "PMToolsSP";
    private static final int CONTEXT_MODEL = Context.MODE_PRIVATE;
    private static final SharedPreferences SP =
            MyApplication.getInstance().getSharedPreferences(FILE_NAME, CONTEXT_MODEL);

    public static String getString(String key) {
        return SP.getString(key, "");
    }

    public static int getInt(String key) {
        return Integer.valueOf(getString(key).replaceAll("[a-zA-Z]", ""));
    }

    public static boolean getBool(String key) {
        return Boolean.valueOf(getString(key));
    }

    /**
     * 保存配置信息
     *
     * @param key 要set的preference的key值
     * @param value 要set的preference的value值
     */
    public static void setParam(String key, String value) {
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 清除所有数据
     */
    public static void clearAll() {
        SharedPreferences.Editor editor = SP.edit();
        editor.clear().apply();
    }

    /**
     * 清除指定数据
     */
    public static void clear(Context context, String key) {
        SharedPreferences.Editor editor = SP.edit();
        editor.remove(key);
        editor.apply();
    }
}

