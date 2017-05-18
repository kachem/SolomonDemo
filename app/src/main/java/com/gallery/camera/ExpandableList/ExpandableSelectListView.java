package com.gallery.camera.ExpandableList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 可折叠勾选带动画的listView
 * Created by kachem on 2017/1/6.
 */

public class ExpandableSelectListView extends ExpandableListView{


    public ExpandableSelectListView(Context context) {
        super(context);
        initViews();
    }

    public ExpandableSelectListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public ExpandableSelectListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews(){
        setGroupIndicator(null); //去掉ExpandableListView默认箭头

    }
}
