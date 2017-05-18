package com.gallery.camera.camera.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.gallery.camera.framework.constant.Constant;
import com.gallery.camera.framework.utils.FileUtils;
import com.gallery.camera.framework.utils.ImageUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by kachem on 2016/4/13.
 */
public class CameraUtils {
    private static CameraUtils cameraUtils;

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;

    private CameraUtils(){}

    public interface CameraOpenCallBack{
        void cameraHasOpend();
    }

    public static synchronized CameraUtils getInstance(){
        if(cameraUtils ==null){
            cameraUtils = new CameraUtils();
        }

        return cameraUtils;
    }

    /**
     *打开相机
     * @param callBack
     */
    public void openCamera(CameraOpenCallBack callBack){
        mCamera = Camera.open();
        callBack.cameraHasOpend();
    }

    /**
     * 打开surfacetexture开启预览
     * @param texture
     */
    public void startPreview(SurfaceTexture texture){
        if(isPreviewing){
            mCamera.stopPreview();
            return;
        }
        if(mCamera!=null){
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera();
        }
    }

    /**
     *停止预览，释放相机资源
     */
    public void stopCamera(){
        if(mCamera!=null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 执行拍照
     */
    public void takePicture(){
        if(isPreviewing&&(mCamera!=null)){
            mCamera.takePicture(null,null,pictureCallback);
        }
    }

    private void initCamera(){
        if(mCamera!=null){
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);
            setPicture();
            mCamera.setDisplayOrientation(90);

            List<String> focusMode = mParams.getSupportedFocusModes();
            if(focusMode.contains("continuous-video")){
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.startPreview();

            isPreviewing = true;

        }
    }

    private void setPicture(){
        List<Camera.Size> sizes = mParams.getSupportedPictureSizes();
        if(sizes == null)
            return;
        int maxSize = 0;
        int width = 0;
        int height = 0;
        for (int i=0;i<sizes.size();i++){
            Camera.Size size = sizes.get(i);
            int pix = size.width*size.height;
            if(pix>maxSize){
                maxSize = pix;
                width = size.width;
                height = size.height;
            }

            mParams.setPictureSize(width,height);
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i("kachem","paizhao---4.4");
            new ImageSaver(data).start();
            //再次进入预览
            mCamera.startPreview();

        }
    };

    class ImageSaver extends Thread{
        private byte[] data;

        ImageSaver(byte[] data){
            this.data = data;
        }

        @Override
        public void run() {
            Bitmap b =null;
            if(data != null){
                b= BitmapFactory.decodeByteArray(data,0,data.length);
            }

            if(b!=null){
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
                Bitmap rotateBitmap = ImageUtils.rotateBitmap(b,90.0f);
                FileUtils.savePicture(rotateBitmap,FileUtils.getDirPath(Constant.BUCKET_DCIM));
                //此处以后要加上逻辑：若用户设置了拍照后是否自动加密
                // 是：保存到指定PRIVACY_PHOTO
                // 否：保存到DCIM
            }
        }
    }
}
