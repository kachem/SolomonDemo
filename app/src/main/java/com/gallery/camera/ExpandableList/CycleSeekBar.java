package com.gallery.camera.ExpandableList;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * Created by kachem on 2017/1/6.
 */

public class CycleSeekBar extends View {
    private boolean mClockWise; //是否是顺时针


    public CycleSeekBar(Context context) {
        super(context);
    }

    public CycleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CycleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        Resources res = getResources();

    }
}
