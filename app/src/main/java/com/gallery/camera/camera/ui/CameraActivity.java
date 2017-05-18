package com.gallery.camera.camera.ui;

import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.gallery.camera.framework.baseui.BaseActivity;
import com.gallery.camera.camera.customview.AutoFitTextureView;
import com.gallery.camera.camera.customview.VerticalSeekBar;
import com.gallery.camera.privacy.solomon.R;
import com.gallery.camera.camera.utils.Camera2Utils;
import com.gallery.camera.camera.utils.CameraUtils;
import com.gallery.camera.framework.utils.DeviceUtils;

/**
 * 静默拍照界面
 * Created by kachem on 2016/3/28.
 */
public class CameraActivity extends BaseActivity implements CameraUtils.CameraOpenCallBack{
    private ImageView coverImg,imgShot;
    private VerticalSeekBar seekBar;
    public boolean isLollipop;
    public Camera2Utils mCamera2Utils;
    public CameraUtils mCameraUtils;
    private AutoFitTextureView autoTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLollipop = DeviceUtils.compareVer(Build.VERSION_CODES.LOLLIPOP);
        if(isLollipop){
            mCamera2Utils = Camera2Utils.getInstance();
            mCamera2Utils.init(this, autoTexture);
            mCamera2Utils.startPreview();
        }else{
            mCameraUtils = CameraUtils.getInstance();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mCameraUtils.openCamera(CameraActivity.this);
                }
            }).start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLollipop){
            mCamera2Utils.startBackgroudThread();
            mCamera2Utils.reOpenCamera();
        }else{

        }
    }

    @Override
    protected void onPause() {
        if(isLollipop){
            mCamera2Utils.closeCamera();
            mCamera2Utils.stopBackgroudThread();
        }else{
            mCameraUtils.stopCamera();
        }
        super.onPause();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        toolbar.setTitle(R.string.desk_shot);
        toolbar.setTitleTextColor(0xffffffff);
    }

    @Override
    protected void initViews() {
        autoTexture = (AutoFitTextureView) findViewById(R.id.auto_texture);
        coverImg = (ImageView) findViewById(R.id.cover_img);
        coverImg.setImageResource(R.mipmap.cover);
        seekBar = (VerticalSeekBar) findViewById(R.id.seek);
        imgShot = (ImageView) findViewById(R.id.img_shot);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("kachem",progress+"");
                coverImg.setAlpha(1-progress/100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imgShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("kachem","onClick");
                if(isLollipop){
                    mCamera2Utils.takePicture();
                }else{
                    mCameraUtils.takePicture();
                }

            }
        });

    }

    @Override
    public void cameraHasOpend() {
        SurfaceTexture surface = autoTexture.getSurfaceTexture();
        mCameraUtils.startPreview(surface);
    }
}
