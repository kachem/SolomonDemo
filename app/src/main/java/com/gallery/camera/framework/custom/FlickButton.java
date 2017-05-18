package com.gallery.camera.framework.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by kachem on 2017/2/23.
 */

public class FlickButton extends LinearLayout {
    View view;

    public FlickButton(Context context) {
        super(context);
    }

    public FlickButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
}
