package com.gallery.camera.lollipop;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.gallery.camera.privacy.solomon.R;

/**
 * Transition动画演示界面
 * Created by kachem on 2017/5/10.
 */

public class SceneActivity extends AppCompatActivity {
    private ImageView imgLeft;
    private ImageView imgRight;
    private Scene scene1, scene2;
    private boolean isScene2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_transition);
        initViews();
        initScene();
    }

    private void initViews() {
        imgLeft = (ImageView) findViewById(R.id.img_center);
        imgRight = (ImageView) findViewById(R.id.img_right);
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTransit();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initScene() {
        ViewGroup sceneRoot = (ViewGroup) findViewById(R.id.root_view);
        scene1 = Scene.getSceneForLayout(sceneRoot, R.layout.transit_scene1, this);
        scene2 = Scene.getSceneForLayout(sceneRoot, R.layout.transit_scene2, this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void goTransit() {
        TransitionManager.go(isScene2 ? scene1 : scene2, new ChangeBounds());
        isScene2 = !isScene2;
    }
}
