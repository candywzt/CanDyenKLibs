package candyenk.java.ead;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CDK数据加解密操作
 * 初始化耗时2000ms
 * 1MB字符串加解密耗时100ms以内
 */
public class EAD {
    private static EAD INSTANCE;
    private final Cipher cipher;
    private final SecureRandom random;
    private final int blockSize;
    /******************************************************************************************************************/
    /***************************************************初始化相关*******************************************************/
    /******************************************************************************************************************/

    private EAD() {
        cipher = creteCipher();
        blockSize = cipher.getBlockSize();//加密块大小
        random = new SecureRandom();
        random.nextBytes(new byte[1]);
    }

    /**
     * 初始化EAD
     * 初始化是为了后续调用不额外耗时(2秒多)
     * 这一秒几乎用在了随机数生成器上面(屑)
     */
    public static void initialization() {
        INSTANCE = new EAD();
    }

    /**
     * 创建加密器
     */
    private Cipher creteCipher() {
        try {
            //"算法/模式/补码方式"NoPadding PkcsPadding
            return Cipher.getInstance("AES/CBC/NoPadding");
        } catch (Exception e) {
            //不可能,绝对不可能
            throw new NullPointerException("创建加密器失败");
        }
    }

    /******************************************************************************************************************/
    /****************************************************加密相关*******************************************************/
    /******************************************************************************************************************/
    /**
     * 加密字符串数据
     */
    public static String EncryptionS(String data) {
        return Base64.getEncoder().encodeToString(Encryption(data.getBytes()));
    }

    /**
     * 加密字节数组数据
     * 切勿转为字符串再解密,达咩
     */
    public static byte[] Encryption(byte[] data) {
        if (INSTANCE == null) initialization();
        byte[] key = createRandom();
        byte[] iv = createRandom();
        byte[] eData = completion(data);
        try {
            INSTANCE.cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            eData = INSTANCE.cipher.doFinal(eData);
        } catch (Exception e) {
            //不可能,绝对不可能
            e.printStackTrace();
        }
        return merge(eData, key, iv);
    }

    /**
     * 创建16位随机字节
     */
    public static byte[] createRandom() {
        byte[] key = new byte[16];
        INSTANCE.random.nextBytes(key);
        return key;
    }

    /**
     * 合并数据与秘钥
     */
    private static byte[] merge(byte[]... data) {
        int dataL = data[0].length;
        byte[] okData = new byte[dataL + 32];
        System.arraycopy(data[0], 0, okData, 0, dataL / 2);
        System.arraycopy(data[1], 0, okData, dataL / 2, 16);
        System.arraycopy(data[2], 0, okData, dataL / 2 + 16, 16);
        System.arraycopy(data[0], dataL / 2, okData, dataL / 2 + 32, dataL / 2);
        return okData;
    }

    /**
     * 补全数据块
     */
    private static byte[] completion(byte[] data) {
        int dataLength = data.length;//数据长度
        if (dataLength % INSTANCE.blockSize != 0) {
            byte[] okBytes = new byte[dataLength + (INSTANCE.blockSize - (dataLength % INSTANCE.blockSize))];
            System.arraycopy(data, 0, okBytes, 0, dataLength);
            return okBytes;
        } else {
            return data;
        }
    }
    /******************************************************************************************************************/
    /****************************************************解密相关*******************************************************/
    /******************************************************************************************************************/
    /**
     * 解密字符串数据
     */
    public static String DecryptionS(String data) {
        return new String(Decryption(Base64.getDecoder().decode(data)));
    }

    /**
     * 解密字节数组数据
     * 只能解密Encryption的数据
     * 不要抱有侥幸心理
     */
    public static byte[] Decryption(byte[] data) {
        if (INSTANCE == null) initialization();
        byte[] key = getKey(data);
        byte[] iv = getIv(data);
        byte[] dData = getData(data);
        try {
            INSTANCE.cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            dData = INSTANCE.cipher.doFinal(dData);
            dData = removeZero(dData);
        } catch (Exception e) {
            //数据出错
            e.printStackTrace();
            return null;
        }
        return dData;
    }

    /**
     * 拆卸KEY
     */
    private static byte[] getKey(byte[] data) {
        int index = data.length / 2 - 16;
        byte[] key = new byte[16];
        System.arraycopy(data, index, key, 0, 16);
        return key;
    }

    /**
     * 拆卸IV
     */
    private static byte[] getIv(byte[] data) {
        int index = data.length / 2;
        byte[] iv = new byte[16];
        System.arraycopy(data, index, iv, 0, 16);
        return iv;
    }

    /**
     * 拆卸数据
     */
    private static byte[] getData(byte[] data) {
        int index = data.length / 2 + 16;
        byte[] eData = new byte[data.length - 32];
        System.arraycopy(data, 0, eData, 0, eData.length / 2);
        System.arraycopy(data, index, eData, eData.length / 2, eData.length / 2);
        return eData;
    }

    /**
     * 数据除0
     */
    private static byte[] removeZero(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                byte[] mData = new byte[i];
                System.arraycopy(data, 0, mData, 0, i);
                return mData;
            }
        }
        return data;
    }
}
