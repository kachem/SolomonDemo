package com.gallery.camera.framework.fingerprint;

/**
 * 指纹识别回调
 * Created by kachem on 2017/2/7.
 */

public interface FingerprintCallBack {
    /**
     * 硬件不支持
     * @param code {@link FingerprintManager HARDWARE_NOT_SUPPORT等}
     */
    void notSupport(int code);

    void initErorr(Exception e);

    /**
     * 系统指纹认证出现报错。此处应该提示用户重新尝试一遍
     * @param erorrCode 代码对应解释见FingerprintManager类中
     */
    void onAuthenticationError(int erorrCode, CharSequence errString);

    /**
     * 系统指纹认证失败，提示用户重新尝试，一般都是手指移动太快，还没来得及采集完
     * @param helpMsgId 代码对应解释见FingerprintManager类中
     */
    void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

    /**
     * 指纹认证成功
     */
    void onAuthenticationSucceeded();

    /**
     * 指纹认证失败
     */
    void onAuthenticationFailed();
}
