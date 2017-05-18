package com.gallery.camera.framework.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.gallery.camera.framework.SolomonApplication;

/**
 * Created by kachem on 2016/4/12.
 */
public class DeviceUtils {
    private static Context mContext = SolomonApplication.getInstance().getApplicationContext();

    /**
     * 判断sdk版本是否大于等于指定版本
     *
     * @return
     */
    public static boolean compareVer(int sdkVersion) {
        if (Build.VERSION.SDK_INT >= sdkVersion) {
            return true;
        }
        return false;
    }

    /**
     * 获取设备屏幕宽高信息
     *
     * @return
     */
    public static Point getScreen() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point screenResolution = null;

        Point p = new Point();
        display.getSize(p);
        int x = p.x;
        int y = p.y;


        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            screenResolution = new Point(Math.max(x, y), Math.min(x, y));
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            screenResolution = new Point(Math.min(x, y), Math.max(x, y));
        }

        return screenResolution;
    }

    /**
     * 将dp值转化为px值
     *
     * @param dp
     * @return
     */
    public static int dp2px(float dp) {
        Resources resources = mContext.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, resources.getDisplayMetrics());
        return px;
    }
}
