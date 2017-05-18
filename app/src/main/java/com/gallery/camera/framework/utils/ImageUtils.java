package com.gallery.camera.framework.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 图片处理工具类
 * Created by kachem on 2016/4/13.
 */
public class ImageUtils {

    public static Bitmap rotateBitmap(Bitmap b,float rotateDegree){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotateBitmap = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),matrix,false);
        return rotateBitmap;
    }
}
