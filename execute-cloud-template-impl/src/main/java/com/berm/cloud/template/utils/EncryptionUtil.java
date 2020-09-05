package com.berm.cloud.template.utils;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {
    /**
     * 将任意字符通过 MD5 摘要 与 Base64 进行定长加密
     *
     * @param message   待加密字符
     * @param algorithm 信息生成摘要的算法，默认为  md5，可选值有  SHA-1、SHA-256、MD5
     * @return 加密后字符
     */
    public static String digestEncryption(String message, String algorithm) {
        String result = "";
        try {
            algorithm = algorithm == null ? "MD5" : algorithm;
            //指定信息摘要算法提取摘要的哈希值. 哈希值字节数组，如果直接 new String(md5Byte) 是会乱码的
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] md5Byte = messageDigest.digest(message.getBytes());
            //使用 BASE64 进行定长编码
            BASE64Encoder base64Encoder = new BASE64Encoder();
            result = base64Encoder.encode(md5Byte);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * xor字符加密
     * @param parmObj1
     * @param parmObj2
     * @return
     */
    public static String Getxor(String parmObj1, String parmObj2) {
        String result = "";
        byte[] ct = new byte[0];
        byte[] pd = new byte[0];
        try {
            ct = parmObj1.getBytes("UTF-8");
            pd = parmObj2.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] rBytes = new byte[ct.length];
        for (int i = 0; i < ct.length; i++){
            rBytes[i] = (byte)(ct[i] ^ pd[i]);
        }
        //base64位加密
        result = new BASE64Encoder().encodeBuffer(rBytes);
        return result;
    }

    public static void main(String[] args) {
        String sourceMessage = "123456万里长城_&*$#.Nice";
        System.out.println("原字符：" + sourceMessage);//原字符：123456万里长城_&*$#.Nice

        String md5Msg = digestEncryption(sourceMessage, "md5");
        String sha1Msg = digestEncryption(sourceMessage, "SHA-1");
        String sha256Msg = digestEncryption(sourceMessage, "SHA-256");
        //md5 + base64 加密后：rIJQEL19bo+eV5p7qPLlDg==
        System.out.println("md5 + base64 加密后：" + md5Msg);
        //md5 + base64 加密后：UYg5qBdsuCdloFP+0CVPasziIEU=
        System.out.println("SHA-1 + base64 加密后：" + sha1Msg);
        //md5 + base64 加密后：dqTa6BTTvZ4zLF5WNAH5Cv660RxYMEIJlBRIGKdNXmM=
        System.out.println("SHA-256 + base64 加密后：" + sha256Msg);
    }
}
