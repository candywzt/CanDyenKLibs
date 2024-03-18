package candyenk.java.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * 加解密工具类
 * RSA加解密:
 * UED.init();
 * byte[] b1 = UED.createKey();//要加密的数据
 * Cipher rsa = UED.createRSA();
 * KeyPair kp = UED.createKeyPair();
 * byte[] b2 = UED.Do(UED.toggleE(rsa, kp.getPublic()), b1);//加密后的数据
 * byte[] b3 = UED.Do(UED.toggleD(rsa, kp.getPrivate()), b2);//解密后的数据
 * System.out.println(Arrays.toString(b1));
 * System.out.println(Arrays.toString(b3));
 * AES加解密:
 * UED.init();
 * byte[] b1 = UED.createKey(1024);//要加密的数据
 * Cipher aes = UED.createAES();
 * byte[] b2 = UED.createKey();//秘钥
 * byte[] b3 = UED.Do(UED.toggleE(aes, UED.keyAES(b2)), b1);//加密后的数据
 * byte[] b4 = UED.Do(UED.toggleD(aes, UED.keyAES(b2)), b3);//解密后的数据
 */
public class UED {
    private static SecureRandom sr;
    private static KeyFactory kf;
    private static KeyPairGenerator kpg;

    /**
     * 初始化工具类,包含一些耗时操作
     */
    public static void init() {
        try {
            sr = new SecureRandom();
            kf = KeyFactory.getInstance("RSA");
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024, sr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密工具初始化异常", e);
        }
    }

    /**
     * 创建一个制定长度的随机byte数组作为秘钥
     *
     * @param length 密钥长度
     * @return byte数组秘钥
     */
    public static byte[] createKey(int length) {
        if (sr == null) sr = new SecureRandom();
        byte[] key = new byte[length];
        sr.nextBytes(key);
        return key;
    }

    /**
     * 返回一个16位长度的byte数组作为秘钥
     *
     * @return 长度为16的byte数组秘钥
     */
    public static byte[] createKey() {
        return createKey(16);
    }

    /**
     * 创建一对RSA秘钥
     * 长度1024
     *
     * @return RSA密钥对
     */
    public static KeyPair createKeyPair() {
        if (kpg == null) init();
        return kpg.genKeyPair();
    }

    /**
     * 创建一个RSA加密器
     *
     * @return RSA加密器
     */
    public static Cipher createRSA() {
        try {
            return Cipher.getInstance("RSA");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("RSA在当前环境不可用", e);
        }
    }

    /**
     * 创建一个默认的AES加密器
     *
     * @return AES加密器
     */
    @SuppressWarnings("GetInstance")
    public static Cipher createAES() {
        try {
            //算法/模式/补码方式
            return Cipher.getInstance("AES/ECB/NoPadding");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("AES在当前环境不可用", e);
        }
    }

    /**
     * 将加密器换为解密模式
     *
     * @param cipher 加密器
     * @param key    秘钥
     * @return 返回加密器
     */
    public static Cipher toggleD(Cipher cipher, Key key) {
        toggle(cipher, Cipher.DECRYPT_MODE, key);
        return cipher;
    }

    /**
     * 将加密器切换为加密模式
     *
     * @param cipher 加密器
     * @param key    秘钥
     * @return 返回加密器
     */
    public static Cipher toggleE(Cipher cipher, Key key) {
        toggle(cipher, Cipher.ENCRYPT_MODE, key);
        return cipher;
    }

    /**
     * 进行加解密
     *
     * @param cipher 加密器
     * @param data   要加/解密的数据
     * @return 加/解密结果
     */
    public static byte[] Do(Cipher cipher, byte[] data) {
        try {
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("加/解密失败", e);
        }
    }


    /**
     * 将byte数组的AESkey转化为Key对象
     *
     * @param key byte类型的可key
     * @return SecretKeySpec类型的key
     */
    public static SecretKeySpec keyAES(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    /**
     * 将byte数组的RSA秘钥转化为Key对象
     *
     * @param key       byte数组类型的key
     * @param isPrivate 是否是私钥
     * @return 返回PublicKey或PrivateKey
     */
    public static Key keyRSA(byte[] key, boolean isPrivate) {
        try {
            if (kf == null) kf = KeyFactory.getInstance("RSA");
            if (isPrivate) return kf.generatePrivate(new PKCS8EncodedKeySpec(key));
            else return kf.generatePublic(new X509EncodedKeySpec(key));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("RSA秘钥转换失败", e);
        }
    }

    /**
     * 将数据对齐
     *
     * @param data      要对齐的原数据
     * @param blockSize 通过Cipher.getBlockSize获取
     * @return 如果已对齐, 返回元数据, 否则返回对齐后的数据
     */
    public static byte[] completion(byte[] data, int blockSize) {
        if (data.length % blockSize == 0) return data;
        int count = blockSize - (data.length % blockSize);
        return Arrays.copyOf(data, data.length + count);

    }


    private static void toggle(Cipher cipher, int type, Key key) {
        try {
            cipher.init(type, key);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("秘钥无效", e);
        }
    }


}
