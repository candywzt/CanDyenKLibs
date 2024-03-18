package candyenk.java.ead;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * CDK RSA加解密
 */
public class CRSA {
    private static CRSA INSTANCE;
    private final Cipher cipher;
    private final KeyFactory kf;
    private final KeyPairGenerator keyPairGen;

    /**
     * 初始化EAD
     * 初始化是为了后续调用不额外耗时(2秒多)
     * 这一秒几乎用在了随机数生成器上面(屑)
     */
    public static void initialization() {
        INSTANCE = new CRSA();
    }

    /**
     * 创建一对秘钥
     */
    public static KeyPair createKeys() {
        if (INSTANCE == null) initialization();
        return INSTANCE.keyPairGen.genKeyPair();
    }

    /**
     * 公钥加密
     * 数据长度不得超过117字节]
     */
    public static byte[] Encrypt(PublicKey publicKey, byte[] data) {
        try {
            INSTANCE.cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return INSTANCE.cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] Encrypt(byte[] publicKey, byte[] data) {
        try {
            X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKey);
            PublicKey pubKey = INSTANCE.kf.generatePublic(ks);
            return Encrypt(pubKey, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * 私钥解密
     */
    public static byte[] Decrypt(PrivateKey privateKey, byte[] data) {
        try {
            INSTANCE.cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return INSTANCE.cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] Decrypt(byte[] privateKey, byte[] data) {
        try {
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKey);
            PrivateKey priKey = INSTANCE.kf.generatePrivate(ks);
            return Decrypt(priKey, data);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private CRSA() {
        cipher = createCipher();
        kf = createKeyFactory();
        keyPairGen = createkeyPairGen();
        keyPairGen.initialize(1024, new SecureRandom());
    }

    /**
     * 创建秘钥工厂
     */
    private KeyFactory createKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA秘钥工厂创建失败", e);
        }

    }

    /**
     * 创建加密器
     */
    private Cipher createCipher() {
        try {
            return Cipher.getInstance("RSA");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("RSA加密器创建失败", e);
        }
    }

    /**
     * 创建密钥生成器
     */
    private KeyPairGenerator createkeyPairGen() {
        try {
            return KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA加密器创建失败", e);
        }
    }
}
