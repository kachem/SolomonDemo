package com.gallery.camera.ExpandableList;

import android.support.v7.widget.Toolbar;

import com.gallery.camera.framework.baseui.BaseActivity;
import com.gallery.camera.privacy.solomon.R;

/**
 * ExpandableList演示界面
 * Created by kachem on 2017/1/6.
 */

public class SetCityActivity extends BaseActivity {


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(R.string.set_city);
    }

    @Override
    protected void initViews() {

    }
}
