package com.gallery.camera.framework.fingerprint;

import android.app.KeyguardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.util.Log;

/**
 * 指纹解锁
 * Created by kachem on 2017/2/7.
 */
public class FingerprintManager {
    public static int HARDWARE_NOT_SUPPORT = 0; //硬件不支持指纹解锁
    public static int KEYGUARD_NOT_SECURE = 1; //当前系统未设置解锁方式，处于非安全保护中
    public static int WITHOUT_ENROLLED_FINGERPRINT = 2; //用户未注册任何指纹

    private Context mContext;
    private FingerprintManagerCompat mFingerprintManager;
    private FingerprintCallBack mCallBack;
    private CancellationSignal mCancellationSignal;

    public FingerprintManager(@NonNull Context context) {
        this.mContext = context;
    }

    public void init(@NonNull FingerprintCallBack callBack) {
        mFingerprintManager = FingerprintManagerCompat.from(mContext);
        mCancellationSignal = new CancellationSignal();
        this.mCallBack = callBack;
        if (!checkUsable())
            return;
        try {
            CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
            mFingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                    mCancellationSignal, new AuthCallback(mCallBack), null);
        } catch (Exception e) {
            callBack.initErorr(e);
            e.printStackTrace();
        }
    }

    /**
     * 取消指纹操作
     */
    public void cancel() {
        mCancellationSignal.cancel();
    }

    /**
     * 检查指纹解锁是否可用,并异步回调具体原因
     */
    private boolean checkUsable() {
        if (!mFingerprintManager.isHardwareDetected()) {
            mCallBack.notSupport(HARDWARE_NOT_SUPPORT);
            return false;
        }

        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {
            mCallBack.notSupport(KEYGUARD_NOT_SECURE);
            return false;
        }

        if (!mFingerprintManager.hasEnrolledFingerprints()) {
            mCallBack.notSupport(WITHOUT_ENROLLED_FINGERPRINT);
            return false;
        }
        return true;
    }

    class AuthCallback extends FingerprintManagerCompat.AuthenticationCallback {
        private FingerprintCallBack callBack;

        public AuthCallback(FingerprintCallBack callBack) {
            super();
            this.callBack = callBack;
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            //errMsgId为5时是因为代码中取消本次操作，可以不作过多的处理
            if (errMsgId != 5)
                callBack.onAuthenticationError(errMsgId, errString);
            Log.i("fingerprintLog", "onAuthenticationError -->" + errMsgId + " " + errString);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
            callBack.onAuthenticationHelp(helpMsgId, helpString);
            Log.i("fingerprintLog", "onAuthenticationHelp -->" + helpMsgId + " " + helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            callBack.onAuthenticationSucceeded();
            Log.i("fingerprintLog", "onAuthenticationSucceeded");
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            callBack.onAuthenticationFailed();
            Log.i("fingerprintLog", "onAuthenticationFailed");
        }
    }
}
