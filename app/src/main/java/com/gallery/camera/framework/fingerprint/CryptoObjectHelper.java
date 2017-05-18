package com.gallery.camera.framework.fingerprint;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * 指纹验证安全帮助类
 * Created by kacem on 2017/2/7.
 */
@TargetApi(Build.VERSION_CODES.M)
public class CryptoObjectHelper {
    private static final String KEY_NAME = "com.iobit.mobilecare";
    private static final String KEYSTORE_NAME = "AndroidKeyStore";
    private static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = KEY_ALGORITHM + "/" + BLOCK_MODE + "/"
            + ENCRYPTION_PADDING;

    private final KeyStore mKeyStore;

    public CryptoObjectHelper() throws Exception {
        mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
        mKeyStore.load(null);
    }

    /**
     * 创建验证加密类
     */
    public FingerprintManagerCompat.CryptoObject buildCryptoObject() throws Exception {
        Cipher cipher = getCipher(true);
        return new FingerprintManagerCompat.CryptoObject(cipher);
    }

    /**
     * 获取Cipher
     *
     * @param retry 若获取失败是否再次尝试
     */
    public Cipher getCipher(boolean retry) throws Exception {
        Key key = getKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        try {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch (KeyPermanentlyInvalidatedException e) {
            //当key失效的时候，cipher会抛出此异常，需要删除无效的key，然后重新创建key重新获取
            mKeyStore.deleteEntry(KEY_NAME);
            if (retry) {
                getCipher(false);
            } else {
                throw new Exception("无法创建cipher", e);
            }
        }
        return cipher;
    }

    public Key getKey() throws Exception {
        Key secretKey;
        if (!mKeyStore.isKeyEntry(KEY_NAME)) {
            //如果没有key或者key失效就重新创建再获取
            //有一下几种情况会造成key失效
            //1. 一个新的指纹image已经注册到系统中
            //2. 当前设备中的曾经注册过的指纹现在不存在了，可能是被全部删除了
            //3. 用户关闭了屏幕锁功能
            //4. 用户改变了屏幕锁的方式
            createKey();
        }
        secretKey = mKeyStore.getKey(KEY_NAME, null);
        return secretKey;
    }

    private void createKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec spec = new KeyGenParameterSpec
                .Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(ENCRYPTION_PADDING)
                .setUserAuthenticationRequired(true)
                .build();
        keyGenerator.init(spec);
        keyGenerator.generateKey();
    }
}
