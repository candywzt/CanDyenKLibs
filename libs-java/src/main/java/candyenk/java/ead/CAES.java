package candyenk.java.ead;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * CDK加解密(AES)
 * 初始化耗时2000ms
 * 1MB字符串加解密耗时100ms以内
 * 别特么整几个G的大文件,文件多大就占多少内存,小心炸堆
 */
public class CAES {
    private static CAES INSTANCE;
    private final Cipher cipher;
    private final SecureRandom random;
    private final int blockSize;

    /**
     * 初始化EAD
     * 初始化是为了后续调用不额外耗时(2秒多)
     * 这一秒几乎用在了随机数生成器上面(屑)
     */
    public static void initialization() {
        INSTANCE = new CAES();
    }

    /**
     * 加密数据
     */
    public static byte[] Encrypt(byte[] data) {
        if (INSTANCE == null) initialization();
        byte zeroCount = INSTANCE.countZero(data);
        data = INSTANCE.completion(data, zeroCount);
        byte[] key = INSTANCE.createRandom();
        key[15] = zeroCount;
        try {
            INSTANCE.cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            data = INSTANCE.cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
        return INSTANCE.merge(data, key);
    }

    /**
     * 解密数据
     * 只能解密Encryption的数据
     * 解密不了反回空字符串
     */
    public static byte[] Decrypt(byte[] data) {
        if (INSTANCE == null) initialization();
        byte[] key = INSTANCE.getKey(data);
        byte[] dData = INSTANCE.getData(data);
        try {
            INSTANCE.cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            dData = INSTANCE.cipher.doFinal(dData);
            dData = INSTANCE.removeZero(dData, key[15]);
        } catch (Exception e) {
            //数据出错
            e.printStackTrace();
            return new byte[0];
        }
        return dData;
    }

    private CAES() {
        cipher = createCipher();
        blockSize = cipher.getBlockSize();//加密块大小
        random = new SecureRandom();
        random.nextBytes(new byte[1]);
    }

    /**
     * 创建加密器
     */
    private Cipher createCipher() {
        try {
            //"算法/模式/补码方式"NoPadding PkcsPadding
            return Cipher.getInstance("AES/ECB/NoPadding");
        } catch (Exception e) {
            //不可能,绝对不可能
            throw new NullPointerException("创建加密器失败");
        }
    }


    /**
     * 创建16位随机字节
     */
    private byte[] createRandom() {
        byte[] key = new byte[16];
        INSTANCE.random.nextBytes(key);
        return key;
    }

    /**
     * 合并数据与秘钥
     */
    private byte[] merge(byte[] data, byte[] key) {
        byte[] okData = new byte[data.length + key.length];
        System.arraycopy(data, 0, okData, 0, data.length / 2);
        System.arraycopy(key, 0, okData, data.length / 2, key.length);
        System.arraycopy(data, data.length / 2, okData, data.length / 2 + key.length, data.length / 2);
        return okData;
    }

    /**
     * 获取需要补零的数量
     */
    private byte countZero(byte[] data) {
        if (data.length % INSTANCE.blockSize == 0) return 0;
        return (byte) (INSTANCE.blockSize - (data.length % INSTANCE.blockSize));
    }

    /**
     * 补全数据块
     */
    private byte[] completion(byte[] data, byte zeroCount) {
        if (zeroCount == 0) return data;
        byte[] okBytes = new byte[data.length + zeroCount];
        System.arraycopy(data, 0, okBytes, 0, data.length);
        return okBytes;
    }

    /**
     * 拆卸KEY
     */
    private byte[] getKey(byte[] data) {
        int index = data.length / 2 - 8;
        byte[] key = new byte[16];
        System.arraycopy(data, index, key, 0, 16);
        return key;
    }

    /**
     * 拆卸数据
     */
    private byte[] getData(byte[] data) {
        int index = data.length / 2 + 8;
        byte[] eData = new byte[data.length - 16];
        System.arraycopy(data, 0, eData, 0, eData.length / 2);
        System.arraycopy(data, index, eData, eData.length / 2, eData.length / 2);
        return eData;
    }

    /**
     * 数据除0
     */
    private byte[] removeZero(byte[] data, byte zeroCount) {
        if (zeroCount == 0) return data;
        byte[] mData = new byte[data.length - zeroCount];
        System.arraycopy(data, 0, mData, 0, data.length - zeroCount);
        return mData;
    }
}
