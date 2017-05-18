package com.gallery.camera.camera.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * 相机预览控件
 * Created by kachem on 2016/3/28.
 */
public class AutoFitTextureView extends TextureView{
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AutoFitTextureView(Context context) {
        super(context);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置宽高比
     * @param width
     * @param height
     */
    public void setAspectRatio(int width , int height){
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if(mRatioWidth == 0 || mRatioHeight == 0){
            setMeasuredDimension(width,height);
        }else{
            if(width<height*mRatioWidth/mRatioHeight){
                setMeasuredDimension(width,width*mRatioHeight/mRatioWidth);
            }else{
                setMeasuredDimension(height*mRatioWidth/mRatioHeight,height);
            }
        }
    }
}
