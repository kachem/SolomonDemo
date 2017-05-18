package com.gallery.camera.framework.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件操作工具类
 * Created by kachem on 2016/3/31.
 */
public class FileUtils {

    public static void close(Closeable closeable){
        if(closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定内容的文件夹路径
     * @param path Constant.BUCKET_DCIM,Constant.BUCKET_PRIVACY_PHOTO,Constant.BUCKET_PRIVACY_VIDEO
     * @return
     */
    public static String getDirPath(String path){
        File mPath = new File(path);
        if(!mPath.exists()){
            mPath.mkdirs();
        }
        return mPath.getPath();
    }

    /**
     * 拍照成功后保存照片到指定文件夹
     * @param b
     * @param dirPath
     */
    public static void savePicture(Bitmap b,String dirPath){
        File file=new File(dirPath+"/"+System.currentTimeMillis()+".jpg");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            b.compress(Bitmap.CompressFormat.JPEG,100,bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
