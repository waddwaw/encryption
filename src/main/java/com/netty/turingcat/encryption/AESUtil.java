package com.netty.turingcat.encryption;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by RyanLee on 2015/10/8.
 */
public class AESUtil {
    /**
     * 二次base64的AES加密
     * @param content   待加密字符串
     * @param key   秘钥
     * @return 密文
     */
    public static String encodeAES(String content, String key){

        String str_encode = null;
        try{
            byte[] input = content.getBytes("utf-8");

            byte[] thedigest = encodeMD5(key);
            String key2=new String(thedigest);
            System.out.println("key1="+key+"|key2="+key2);

            String iv = getIv();
            byte[] byte_iv = iv.getBytes();
            System.out.println("iv=" + byte_iv);

            SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(byte_iv));

            byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
            int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);

            System.out.println("cipherText="+cipherText);
            String str_base64 = Base64.encodeToString(cipherText, Base64.DEFAULT).trim();
            ////String str_base64 = Base64.encode(cipherText).trim();
            System.out.println("str_base64="+str_base64);
            // 带上iv再base64
            str_encode = Base64.encodeToString((iv+str_base64).getBytes(), Base64.DEFAULT);
            ////str_encode = Base64.encode((iv+str_base64).getBytes());
            System.out.println("str_encode="+str_encode);
        }catch (Exception e){
            e.printStackTrace();
        }

        return str_encode;
    }

	/**
     * 二次base64的AES加密, (iv + (密文->base64)) ->base64
     * @param input 明文
     * @param pos 明文起始位置
     * @param length 明文长度
     * @param key 秘钥
     * @return 密文
     */
    public static byte[] encodeAES(byte[] input, int pos, int length, String key) {
        try{
            byte[] thedigest = encodeMD5(key);
            String key2=new String(thedigest);
            System.out.println("key1="+key+"|key2="+key2);

            String iv = getIv();
            byte[] byte_iv = iv.getBytes();
            System.out.println("iv=" + byte_iv);

            SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(byte_iv));

            byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
            int ctLength = cipher.update(input, pos, length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);

            String str_base64 = Base64.encodeToString(cipherText, Base64.DEFAULT).trim();
            // 带上iv再base64
            return Base64.encode((iv+str_base64).getBytes(), Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 一次base64的AES加密， (iv+密文)->base64
     * @param input 明文
     * @param pos 明文起始位置
     * @param length 明文长度
     * @param key 秘钥
     * @return 密文
     */
    public static byte[] encodeAESBase64(byte[] input, int pos, int length, String key){
        byte[] cipherTextBase64 = null;
        try{
            byte[] thedigest = encodeMD5(key);
            String key2 = new String(thedigest);
            System.out.println("key1=" + key + "|key2=" + key2);

            String iv = getIv();
            byte[] byte_iv = iv.getBytes();
            int ivLength = byte_iv.length;

            SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(byte_iv));

            byte[] cipherText = new byte[cipher.getOutputSize(input.length) + ivLength];
            System.arraycopy(byte_iv, 0, cipherText, 0, ivLength);
            int ctLength = cipher.update(input, pos, length, cipherText, ivLength);
            ctLength += cipher.doFinal(cipherText, ivLength + ctLength);

            cipherTextBase64 = Base64.encode(cipherText, Base64.DEFAULT);

        }catch (Exception e){
            e.printStackTrace();
        }

        return cipherTextBase64;
    }

    /**
     * 二次base64的AES解密
     * @param encrypted 密文
     * @param key 秘钥
     * @return 密文字符串
     */
    public static String decodeAES(String encrypted, String key) {
        String str_decode = null;
        try{
            byte[] decode_base64 = Base64.decode(encrypted, Base64.DEFAULT);
            String str = new String(decode_base64);


            String str_iv = str.substring(0, 16);
            String content_base64 = str.substring(16);
            byte[] content = Base64.decode(content_base64, Base64.DEFAULT);

            byte[] thedigest = encodeMD5(key);
            SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
            Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(str_iv.getBytes()));

            byte[] clearbyte = dcipher.doFinal(content);

            str_decode = new String(clearbyte);
        }catch (Exception e){
            e.printStackTrace();
        }

        return str_decode;
    }

    /**
     * 二次base64的AES解密
     * @param input 密文
     * @param pos 密文起始位置
     * @param length 密文长度
     * @param key 秘钥
     * @return 明文
     */
    public static byte[] decodeAES(byte[] input, int pos, int length, String key) {
        try{
            byte[] decode_base64 = Base64.decode(input, pos, length, Base64.DEFAULT);
            String content_base64 = new String(decode_base64, 16, decode_base64.length-16);

            byte[] content = Base64.decode(content_base64, Base64.DEFAULT);

            byte[] thedigest = encodeMD5(key);
            SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
            Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(decode_base64, 0, 16));

            byte[] clearbyte = dcipher.doFinal(content);
            return clearbyte;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 一次base64的AES解密
     * @param input 密文
     * @param pos 密文起始位置
     * @param length 密文长度
     * @param key 秘钥
     * @return 明文
     */
    public static byte[] dncodeAESBase64(byte[] input, int pos, int length, String key){
        try{
            byte[] decode_base64 = Base64.decode(input, pos, length, Base64.DEFAULT);

            byte[] thedigest = encodeMD5(key);
            SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
            Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(decode_base64, 0, 16));

            byte[] clearbyte = dcipher.doFinal(decode_base64, pos+16, length-16);
            return clearbyte;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    /**
     * 获取随机IV
     * @return 16位IV
     */
    public static String getIv(){
        StringBuffer str = new StringBuffer();
        Random rdm = new Random();
        for(int i=0;i<16;i++){
            char ch = (char)rdm.nextInt(128);
            str.append(ch);
        }

        return str.toString();
    }


    public static byte[] encodeMD5(String str) {
        byte[] byteArray =null;
        try {
            MessageDigest messageDigest = null;
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
            byteArray = messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return byteArray;
    }

	/**
	 * 二进制数据段的AES加密，不做base64
     * @param input 要加密的数据段
     * @param pos   要加密数据段起始位置
     * @param length 要加密数据段长度
     * @param key   秘钥
     * @return  密文，格式为二进制字符
     */
    public static byte[] encodeAES2(byte[] input, int pos, int length, String key) {
        try{
            byte[] thedigest = encodeMD5(key);
            String key2=new String(thedigest);
            System.out.println("key1="+key+"|key2="+key2);

            String iv = getIv();
            byte[] byte_iv = iv.getBytes();
            System.out.println("iv=" + byte_iv);

            SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skc, new IvParameterSpec(byte_iv));

            byte[] cipherText = new byte[byte_iv.length + cipher.getOutputSize(input.length)];
            int ctLength = cipher.update(input, pos, length, cipherText, byte_iv.length);
            ctLength += cipher.doFinal(cipherText, byte_iv.length + ctLength);
            System.arraycopy(byte_iv, 0, cipherText, 0, byte_iv.length);

            return cipherText;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

	/**
     * 二进制数据段的AES解密，不做base64
     * @param input 要解密的密文
     * @param pos   密文起始位置
     * @param length 密文长度
     * @param key   秘钥
     * @return  解密后的明文，格式为二进制字符
     */
    public static byte[] decodeAES2(byte[] input, int pos, int length, String key) {
        try{

            byte[] thedigest = encodeMD5(key);
            SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
            Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            dcipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(input, pos, 16));

            return dcipher.doFinal(input, pos+16, length-16);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
