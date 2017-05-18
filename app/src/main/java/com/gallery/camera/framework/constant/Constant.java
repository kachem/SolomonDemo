package com.gallery.camera.framework.constant;

import android.os.Environment;

/**
 * 公共常量
 * Created by kachem on 2016/3/28.
 */
public class Constant {
    //应用文件夹路径
    public static final String APPLICATION_PATH = Environment.getExternalStorageDirectory()
            .getPath()+"/Solomon/";

    //隐私图片文件夹路径
    public static final String BUCKET_PRIVACY_PHOTO = APPLICATION_PATH+"privacy/picture";

    //隐私视频文件夹路径
    public static final String BUCKET_PRIVACY_VIDEO = APPLICATION_PATH+"privacy/video";

    //拍摄照片存储地址
    public static final String BUCKET_DCIM = APPLICATION_PATH+"DCIM";
}
