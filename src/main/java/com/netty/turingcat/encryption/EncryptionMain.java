package com.netty.turingcat.encryption;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

/**
 * Created by arvin on 2017/3/2.
 */
public class EncryptionMain {
    public static void main(String[] arg) {
        String str = "你好啊 啊   小学生";
//        byte[] encodeAES = AESUtil.encodeAES2(str.getBytes(), 0, str.getBytes().length, "1323456");
//        byte[] decodeAES = AESUtil.decodeAES2(encodeAES, 0, encodeAES.length, "1323456");
//        System.out.println(new String(decodeAES));

        try {
            HashMap<String, Object> rsa = RSAUtil.getKeys();
            String encode  = RSAUtil.encryptByPublicKey(str, (RSAPublicKey) rsa.get("public"));
            System.out.println("加密后数据" + encode);
            String decode  = RSAUtil.decryptByPrivateKey(str, (RSAPrivateKey) rsa.get("private"));
            System.out.println("解密后数据" + decode);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyPair keyPair = RSAUtils.generateRSAKeyPair();
        System.out.println(">>>" + keyPair.getPublic().toString());
        System.out.println(">>>" + keyPair.getPrivate().toString());
        byte[] encryptData = RSAUtils.encryptData(str.getBytes(), keyPair.getPublic());
        System.out.println(">>>" + new String(encryptData));
        byte[] decryptData = RSAUtils.decryptData(encryptData, keyPair.getPrivate());
        System.out.println(">>>" + new String(decryptData));

    }
}
