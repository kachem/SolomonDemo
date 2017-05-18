package com.gallery.camera.lollipop;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.gallery.camera.privacy.solomon.R;

import static com.gallery.camera.privacy.solomon.R.id.layout_slide;

/**
 * 动画主界面
 * Created by kachem on 2017/5/15.
 */

public class LollipopActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout layoutScene;
    private LinearLayout layoutSlide;
    private LinearLayout layoutExplode;
    private LinearLayout layoutFade;
    private LinearLayout layoutElement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lollipop);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.RIGHT);
            slide.setDuration(500);
            getWindow().setExitTransition(slide);
        }
        initView();
    }

    private void initView() {
        layoutScene = (LinearLayout) findViewById(R.id.layout_scene);
        layoutSlide = (LinearLayout) findViewById(layout_slide);
        layoutExplode = (LinearLayout) findViewById(R.id.layout_explode);
        layoutFade = (LinearLayout) findViewById(R.id.layout_fade);
        layoutElement = (LinearLayout) findViewById(R.id.layout_shared_element);

        layoutScene.setOnClickListener(this);
        layoutSlide.setOnClickListener(this);
        layoutExplode.setOnClickListener(this);
        layoutFade.setOnClickListener(this);
        layoutElement.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_scene) {
            Intent intent = new Intent(this, SceneActivity.class);
            startActivity(intent);
        } else if (id == R.id.layout_shared_element) {
            Intent intent = new Intent(this, TransitionActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    android.util.Pair.create(findViewById(R.id.img_shared_element), "share_img"),
                    android.util.Pair.create(findViewById(R.id.tv_share_element), "share_text"));
            intent.putExtra("transition","shared_elements");
            getWindow().setExitTransition(null);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(this, TransitionActivity.class);
            switch (id) {
                case layout_slide:
                    intent.putExtra("transition", "slide");
                    break;
                case R.id.layout_explode:
                    intent.putExtra("transition", "explode");
                    break;
                case R.id.layout_fade:
                    intent.putExtra("transition", "fade");
                    break;
            }
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }

    }
}
