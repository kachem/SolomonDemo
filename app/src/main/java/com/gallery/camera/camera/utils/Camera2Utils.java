package com.gallery.camera.camera.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.gallery.camera.framework.constant.Constant;
import com.gallery.camera.privacy.solomon.R;
import com.gallery.camera.framework.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 5.0及更高版本使用此类的相机
 * Created by kachem on 2016/3/28.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Utils {
    private static Camera2Utils mCamera2Utils;

    private final int REQUEST_CAMERA_PERMISSION = 1;
    private final int STATE_PREVIEW = 0;
    //等待焦点锁定
    private final int STATE_WAITING_LOCK = 1;
    //等待
    private final int STATE_WAITING_PRECAPTURE = 2;
    //捕获图像前的等待曝光状态
    private final int STATE_WAITING_NON_PRECAPTURE = 3;
    //图像已捕捉
    private final int STATE_PICTURE_TAKEN = 4;

    private int mState = STATE_PREVIEW;

    private AppCompatActivity mActivity;
    private TextureView mTextureView;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private CameraManager mCameraManager;
    private Size mPreviewSize;
    private String mCameraId;
    private Semaphore mCameraLock = new Semaphore(1);
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;

    private ImageReader mImageReader;
    private static final SparseIntArray oritationArr = new SparseIntArray();
    private CameraCaptureSession mCaptureSesstion;

    static {
        oritationArr.append(Surface.ROTATION_0, 90);
        oritationArr.append(Surface.ROTATION_90, 0);
        oritationArr.append(Surface.ROTATION_180, 270);
        oritationArr.append(Surface.ROTATION_270, 180);
    }


    public static Camera2Utils getInstance() {

        if (mCamera2Utils == null) {
            mCamera2Utils = new Camera2Utils();
        }

        return mCamera2Utils;
    }

    /**
     * 首先调用此方法，否则会报错
     *
     * @param activity
     * @param view
     */
    public void init(AppCompatActivity activity, TextureView view) {
        mActivity = activity;
        mTextureView = view;

        startBackgroudThread();

        mCameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
    }

    public void startBackgroudThread() {
        mHandlerThread = new HandlerThread("CAMERA2");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    /**
     * 关闭后台相机线程
     */
    public void stopBackgroudThread() {
        mHandlerThread.quitSafely();
        try {
            mHandlerThread.join();
            mHandlerThread = null;
            mHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开预览
     */
    public void startPreview() {
        mTextureView.setSurfaceTextureListener(mTextureListener);
    }

    private final TextureView.SurfaceTextureListener mTextureListener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void openCamera() {
        if (!checkCameraPermission()) {
            requestCameraPermission();
            return;
        }
        setUpCameraOutputs();
        try {
            if (!mCameraLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening");
            }
            mCameraManager.openCamera(mCameraId, mStateCameraCallBack, mHandler);
        } catch (SecurityException e) {
            mCameraLock.release();
            //未开启权限 请手动开启
            //mActivity.startActivity(new Intent(mActivity, MainActivity.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    public void reOpenCamera() {
        if (mTextureView.isAvailable()) {
            openCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mTextureListener);
        }
    }

    private void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.CAMERA)) {
            showConfirmDialog();
        } else {
            ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private boolean checkCameraPermission() {
        boolean hasPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            try {
                hasPermission = PermissionChecker.checkSelfPermission(mActivity, Manifest
                        .permission.CAMERA)
                        == PermissionChecker.PERMISSION_GRANTED;
            } catch (IllegalArgumentException e) {

            }

        }
        return hasPermission;
    }

    /**
     * 设置preview大小
     */
    private void setUpCameraOutputs() {
        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacter = mCameraManager.
                        getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacter.get(CameraCharacteristics.LENS_FACING);
                if (facing == null || facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = cameraCharacter.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null)
                    continue;
                Size largest = getSize(map.getOutputSizes(ImageFormat.JPEG));
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, 7);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);

                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

                mCameraId = cameraId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size getSize(Size[] sizes){
        if(sizes == null)
            return null;
        Size result;
        int maxSize = 0;
        int width = 0;
        int height = 0;
        for (int i=0;i<sizes.length;i++){
            Size size = sizes[i];
            int pix = size.getWidth()*size.getHeight();
            if(pix>maxSize){
                maxSize = pix;
                width = size.getWidth();
                height = size.getHeight();
            }
        }
        result = new Size(width,height);

        return result;
    }

    /**
     * 创建一个新的相机预览
     */
    private void createCameraPreviewSession() {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);
        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice
                    .TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface,mImageReader.getSurface()),
                    mStateCaptureCallBack, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示提供使用相机的权限
     */
    private void showConfirmDialog() {
        new AlertDialog.Builder(mActivity)
                .setMessage(R.string.permission_tip)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA_PERMISSION);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mActivity != null) {
                    mActivity.finish();
                }
            }
        }).create().show();
    }

    /**
     * 捕获图像
     */
    private void captureStillPicture() {
        try {
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, oritationArr.get(rotation));

            CameraCaptureSession.CaptureCallback captureCallback =
                    new CameraCaptureSession.CaptureCallback() {

                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session,
                                                       CaptureRequest request, TotalCaptureResult
                                                               result) {
                            unlockFocus();
                        }
                    };

            mCaptureSesstion.stopRepeating();
            mCaptureSesstion.capture(captureBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void runCaptureSequence() {

        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSesstion.capture(mPreviewRequestBuilder.build(), mCaptureCallBack,
                    mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除焦点锁定，此方法在序列图像捕捉完毕后调用
     */
    private void unlockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mCaptureSesstion.capture(mPreviewRequestBuilder.build(), mCaptureCallBack,
                    mHandler);
            mState = STATE_PREVIEW;
            mCaptureSesstion.setRepeatingRequest(mPreviewRequest, mCaptureCallBack, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void lockFocus() {
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            mState = STATE_WAITING_LOCK;
            mCaptureSesstion.capture(mPreviewRequestBuilder.build(), mCaptureCallBack, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍摄照片直接调用此方法
     */
    public void takePicture() {
        lockFocus();
    }

    public void closeCamera() {
        try {
            mCameraLock.acquire();
            if (null != mCaptureSesstion) {
                mCaptureSesstion.close();
                mCaptureSesstion = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraLock.release();
        }
    }

    private final CameraDevice.StateCallback mStateCameraCallBack = new CameraDevice
            .StateCallback() {


        @Override
        public void onOpened(CameraDevice camera) {
            mCameraLock.release();
            mCameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraLock.release();
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            mCameraLock.release();
            camera.close();
            mCameraDevice = null;
            if (mActivity != null) {
                mActivity.finish();
            }
        }
    };

    private final CameraCaptureSession.StateCallback mStateCaptureCallBack =
            new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    if (mCameraDevice == null)
                        return;
                    mCaptureSesstion = session;
                    //设置对焦方式为自动连续对焦
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    mPreviewRequest = mPreviewRequestBuilder.build();
                    try {
                        mCaptureSesstion.setRepeatingRequest(mPreviewRequest, mCaptureCallBack,
                                mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            };

    private CameraCaptureSession.CaptureCallback mCaptureCallBack =
            new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureProgressed(CameraCaptureSession session,
                                                CaptureRequest request,
                                                CaptureResult partialResult) {
                    process(partialResult);

                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                    process(result);

                }

                private void process(CaptureResult result) {
                    switch (mState) {
                        case STATE_PREVIEW:

                            break;
                        case STATE_WAITING_LOCK: {
                            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                            if (afState == null) {
                                captureStillPicture();
                            } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                    CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                                Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                                if (aeState == null || aeState == CaptureResult
                                        .CONTROL_AE_STATE_CONVERGED) {
                                    mState = STATE_PICTURE_TAKEN;
                                    captureStillPicture();
                                } else {
                                    runCaptureSequence();
                                }
                            }
                        }
                        break;
                        case STATE_WAITING_PRECAPTURE: {

                            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                            if (aeState == null ||
                                    aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                                mState = STATE_WAITING_NON_PRECAPTURE;
                            }
                        }
                        break;
                        case STATE_WAITING_NON_PRECAPTURE: {
                            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                            if (aeState == null || aeState != CaptureResult
                                    .CONTROL_AE_STATE_PRECAPTURE) {
                                mState = STATE_PICTURE_TAKEN;
                                captureStillPicture();
                            }
                        }
                        break;
                    }
                }
            };

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {

                @Override
                public void onImageAvailable(ImageReader reader) {
                    Log.i("kachem","paizhao---5.0");
                    new ImageSaver(reader.acquireNextImage(),
                            new File(FileUtils.getDirPath(Constant.BUCKET_DCIM)+"/" +
                                    System.currentTimeMillis() + ".jpg")).start();
                }
            };

    class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    class ImageSaver extends Thread {
        private final Image mImage;
        private final File mFile;

        public ImageSaver(Image image, File file) {
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.mImage = image;
            this.mFile = file;
        }

        @Override
        public void run() {
            Log.i("kachem",mImage.getWidth()+"");
            Log.i("kachem",mImage.getHeight()+"");
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] b = new byte[buffer.capacity()];
            buffer.get(b);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(mFile);
                out.write(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                mImage.close();
                FileUtils.close(out);
            }

        }
    }
}
