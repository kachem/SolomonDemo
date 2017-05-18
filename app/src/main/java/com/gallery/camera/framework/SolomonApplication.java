package com.gallery.camera.framework;

import android.app.Application;

/**
 * Created by kachem on 2016/3/26.
 */
public class SolomonApplication extends Application{
    private static SolomonApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static SolomonApplication getInstance(){
        return application;
    }
}
