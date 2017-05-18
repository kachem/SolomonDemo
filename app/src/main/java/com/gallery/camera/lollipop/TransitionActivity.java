package com.gallery.camera.lollipop;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.gallery.camera.privacy.solomon.R;

/**
 * Created by kachem on 2017/5/15.
 */

public class TransitionActivity extends AppCompatActivity {
    private ImageView imgCircle;
    private TextView tvType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        imgCircle = (ImageView) findViewById(R.id.img_circle);
        tvType = (TextView) findViewById(R.id.tv_type);
        initAnim();

    }

    /**
     * 初始化进场动画
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initAnim() {
        Intent intent = getIntent();
        if (intent == null)
            return;

        String type = intent.getStringExtra("transition");
        switch (type) {
            case "slide":
                imgCircle.setBackgroundResource(R.drawable.circle_green);
                tvType.setText("Slide");
                Slide slide = new Slide(Gravity.LEFT);
                slide.setDuration(500);
                getWindow().setEnterTransition(slide);
                break;
            case "explode":
                imgCircle.setBackgroundResource(R.drawable.circle_pink);
                tvType.setText("Explode");
                Explode explode = new Explode();
                explode.setDuration(500);
                getWindow().setEnterTransition(explode);
                break;
            case "fade":
                imgCircle.setBackgroundResource(R.drawable.circle_orange);
                tvType.setText("Fade");
                //如果只需要在进场需要动画，出场不需要，可以在构造方法传入Fade.IN
                //同样只需要在出场时需要动画，进场不要，可以在构造方法传入Fade.OUT
                //不传入参数就是默认进场为Fade.IN，出场为Fade.OUT
                Fade fade = new Fade();
                fade.setDuration(500);
                getWindow().setEnterTransition(fade);
                break;
            case "shared_elements":
                imgCircle.setBackgroundResource(R.drawable.circle_yellow);
                tvType.setText("Shared Elements");
                Slide slideShare = new Slide(Gravity.RIGHT);
                slideShare.setDuration(500);
                getWindow().setEnterTransition(slideShare);
                ChangeBounds changeBounds = new ChangeBounds();
                changeBounds.setDuration(500);
                getWindow().setSharedElementEnterTransition(changeBounds);
                break;
        }
    }
}
