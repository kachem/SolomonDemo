package com.gallery.camera.framework.custom;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gallery.camera.framework.fingerprint.FingerprintCallBack;
import com.gallery.camera.framework.fingerprint.FingerprintManager;
import com.gallery.camera.privacy.solomon.R;

/**
 * 测试专用activity
 * Created by kachem on 2017/1/22.
 */

public class TestActivity extends AppCompatActivity implements FingerprintCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final LayoutInflater inflater = getLayoutInflater();
        LayoutInflaterCompat.setFactory(inflater, new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                Log.i("customSkin", name);
                View view = null;
                try {
                    if (name.equals("com.gallery.camera.framework.custom.MarqueeTextView")) {
                        view = inflater.createView(name, null, attrs);
                        Log.i("customSkin", "is MarqueeTextView");
                    }

                    AppCompatDelegate delegate = getDelegate();
                    if (view == null) {
                        view = delegate.createView(parent, name, context, attrs);
                    }
                    if (view != null && (view instanceof TextView)) {
                        view.setBackgroundColor(Color.RED);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return view;
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //initFingerprint();
    }

    private void initFingerprint() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = new FingerprintManager(this);
            fingerprintManager.init(this);
        }
    }

    @Override
    public void notSupport(int code) {

    }

    @Override
    public void initErorr(Exception e) {

    }

    @Override
    public void onAuthenticationError(int erorrCode, CharSequence errString) {

    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

    }

    @Override
    public void onAuthenticationSucceeded() {

    }

    @Override
    public void onAuthenticationFailed() {

    }
}
