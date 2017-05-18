package com.gallery.camera.framework;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by kachem on 2016/4/13.
 */
public class SharedPreferencesApp {
    private static SharedPreferences shared;

    /**
     * 获取boolean值
     * @param PreferList 配置文件名
     * @param tag   键
     * @param bool 默认值
     * @return
     */
    public static boolean getBoolean(String PreferList , String tag,boolean bool){
        boolean result;
        shared = SolomonApplication.getInstance().getApplicationContext()
                .getSharedPreferences("Preferlist", Activity.MODE_PRIVATE);
        result = shared.getBoolean(tag,bool);
        shared = null;
        return result;
    }

    /**
     * 设置boolean值
     * @param preferList 配置文件名
     * @param tag 键
     * @param bool 存入值
     */
    public static void setBoolean(String preferList,String tag,boolean bool){
        shared = SolomonApplication.getInstance().getApplicationContext()
                .getSharedPreferences("Preferlist", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(tag,bool);
        editor.commit();
        shared = null;

    }
}
