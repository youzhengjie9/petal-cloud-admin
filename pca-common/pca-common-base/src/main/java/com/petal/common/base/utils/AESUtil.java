package com.petal.common.base.utils;

import com.petal.common.base.constant.AESConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


/**
 * AES加密工具类
 *
 * @author youzhengjie
 * @date 2023/05/28 17:08:14
 */
public class AESUtil {
    /**
     * 日志相关
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AESUtil.class);
    /**
     * 编码
     */
    private static final String ENCODING = "UTF-8";
    /**
     * 算法定义
     */
    private static final String AES_ALGORITHM = "AES";
    /**
     * 指定填充方式
     */
    private static final String CIPHER_PADDING = "AES/ECB/PKCS5Padding";

    /**
     * AES加密
     *
     * @param text 待加密内容
     * @param aesKey  密钥
     * @return
     */
    public static String encrypt(String text, String aesKey){
        if(StringUtils.isBlank(text)){
            throw new RuntimeException("待加密内容text不能为空");
        }
        // 如果秘钥(aesKey)不为空,并且长度等于16位,则可以进行加密
        if(StringUtils.isNotBlank(aesKey) && aesKey.length() == 16){
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置加密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_PADDING);
                //选择加密
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                //根据待加密内容生成字节数组
                byte[] encrypted = cipher.doFinal(text.getBytes(ENCODING));
                //返回base64字符串
                return Base64Utils.encodeToString(encrypted);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        // 如果秘钥(aesKey)为空,或者长度不等于16位,则加密失败
        else {
            throw new RuntimeException("加密失败,原因是秘钥(aesKey)为空,或者长度不等于16位");
        }
    }

    /**
     * AES解密(注意: 只能解密从当前这个工具类的encrypt方法所加密出来的密码，外面的AES加解密网站加密出来的密码不能进行解密！)
     *
     * @param text 待解密内容
     * @param aesKey  密钥
     * @return
     */
    public static String decrypt(String text, String aesKey){

        if(StringUtils.isBlank(text)){
            throw new RuntimeException("待解密内容text不能为空");
        }
        // 如果秘钥(aesKey)不为空,并且长度等于16位,则可以进行解密
        if(StringUtils.isNotBlank(aesKey) && aesKey.length() == 16){
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置解密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_PADDING);
                //选择解密
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                //先进行Base64解码
                byte[] decodeBase64 = Base64Utils.decodeFromString(text);
                //根据待解密内容进行解密
                byte[] decrypted = cipher.doFinal(decodeBase64);
                //将字节数组转成字符串
                return new String(decrypted, ENCODING);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 如果秘钥(aesKey)为空,或者长度不等于16位,则解密失败
        else {
            throw new RuntimeException("解密失败,原因是秘钥(aesKey)为空,或者长度不等于16位");
        }
    }

    public static void main(String[] args) {
        // AES支持三种长度的密钥：128位、192位、256位。
        // 代码中这种就是128位（长度为16）的加密密钥，16字节 * 8位/字节 = 128位。

//        String aesKey = RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz1234567890");
//        System.out.println("生成随机aesKey:" + aesKey);
//        System.out.println();
//
//        System.out.println("---------加密---------");
//        String aesResult = encrypt("youzhengjie123", aesKey);
//        System.out.println("AES加密结果:" + aesResult);
//        System.out.println();
//
//        System.out.println("---------解密---------");
//        String decrypt = decrypt(aesResult, aesKey);
//        System.out.println("aes解密结果:" + decrypt);

        //D8z3s1nyDtYpPg+AWX/TLA==
        String encrypt = encrypt("123456", AESConstant.LOGIN_PASSWORD_AES_KEY);
        System.out.println(encrypt);
        System.out.println(decrypt(encrypt, AESConstant.LOGIN_PASSWORD_AES_KEY));

    }
}