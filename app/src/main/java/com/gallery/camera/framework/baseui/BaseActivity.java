package com.gallery.camera.framework.baseui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gallery.camera.privacy.solomon.R;
import com.gallery.camera.framework.SolomonApplication;

/**
 * Created by kachem on 2016/4/12.
 */
public abstract class BaseActivity extends AppCompatActivity{
    protected Context mContext;
    protected ActivityListener activityListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        mContext = SolomonApplication.getInstance().getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            initToolbar(toolbar);
            setSupportActionBar(toolbar);
        }
        initViews();
    }

    protected abstract int getLayoutRes();

    protected abstract void initToolbar(Toolbar toolbar);

    protected abstract void initViews();

    /**
     * 设置activity的按钮监听事件， 在fragment中调用
     * @param activityListener
     */
    public void setActivityListener(ActivityListener activityListener) {
        this.activityListener = activityListener;
    }

    /**
     * activity界面监听按钮动作的回调接口
     */
    public interface ActivityListener {

        void onClick(View v);
    }
}
