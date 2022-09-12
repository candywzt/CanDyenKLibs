package candyenk.java;

import java.math.BigInteger;
import java.util.Random;


public class RSA2 {
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger ZERO = BigInteger.ZERO;
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger product;

    /**
     * 初始化RSA算法
     */
    public static RSA2 create() {
        RSA2 rsa = new RSA2();
        rsa.createKey();
        return rsa;
    }

    public static RSA2 create(BigInteger publicKey, BigInteger privateKey, BigInteger product) {
        RSA2 rsa = new RSA2();
        rsa.publicKey = publicKey;
        rsa.privateKey = privateKey;
        rsa.product = product;
        return rsa;
    }

    /**
     * 导入公钥
     */
    public static RSA2 publicKey(BigInteger publicKey, BigInteger product) {
        RSA2 rsa = new RSA2();
        rsa.publicKey = publicKey;
        rsa.product = product;
        return rsa;
    }

    /**
     * 导入私钥
     */
    public static RSA2 privateKey(BigInteger privateKey, BigInteger product) {
        RSA2 rsa = new RSA2();
        rsa.privateKey = privateKey;
        rsa.product = product;
        return rsa;
    }

    private RSA2() {

    }

    /**
     * RSA加密
     */
    public BigInteger[] encryption(String string) {
        return encryption(string.getBytes());
    }

    public BigInteger[] encryption(byte[] bytes) {
        String textNum = "";// 明文数字字符串表示形式
        BigInteger m;// 明文数字表示形式
        for (int i = 0; i < bytes.length; i++) {// 每个字节用3位数的整数表示，不够则在前面补0
            int bn = bytes[i] & 0xff;
            if (bn < 10) {
                textNum += "00" + bn;
            } else if (bn < 100) {
                textNum += "0" + bn;
            } else {
                textNum += bn;
            }
        }
        m = new BigInteger(textNum);
        BigInteger[] mArray;// 明文分组结果
        if (isLess(m, product)) {// m < n，可直接加密
            mArray = new BigInteger[1];
            mArray[0] = m;
        } else {
            int groupLength = product.toString().length() - 1;// 每组明文长度
            int mStringLength = m.toString().length();// 明文转化为字符串的长度
            while (groupLength % 3 != 0) {// 由于前面每个字节用3位整数表示，因此每组的长度必须为3的整数，避免恢复时错误
                groupLength--;
            }
            if (mStringLength % groupLength != 0) {// 如果最后一组的长度不足
                mArray = new BigInteger[mStringLength / groupLength + 1];
            } else {
                mArray = new BigInteger[mStringLength / groupLength];
            }
            String tmp;
            for (int i = 0; i < mArray.length; i++) {
                if (i != mArray.length - 1) {// 根据每组长度进行分割分组保存
                    tmp = textNum.substring(groupLength * i, groupLength * i + groupLength);
                } else {
                    tmp = textNum.substring(groupLength * i);
                }
                mArray[i] = new BigInteger(tmp);
            }
        }

        for (int i = 0; i < mArray.length; i++) {// 逐组加密并返回
            mArray[i] = expMod(mArray[i], publicKey, product);
        }
        return mArray;
    }

    /**
     * RSA解密
     */
    public String decryption(BigInteger[] c) {
        String cPadding = "";
        String mToString = "";
        int mToStringLengthMod = 0;
        BigInteger m;
        for (int i = 0; i < c.length; i++) {// 逐组解密
            m = expMod(c[i], privateKey, product);
            mToString = m.toString();
            mToStringLengthMod = m.toString().length() % 3;
            if (mToStringLengthMod != 0) {// 由于加密时String转BigInter时前者前面的0并不会计入，所以需要确认并补全
                for (int j = 0; j < 3 - mToStringLengthMod; j++) {
                    mToString = "0" + mToString;
                }
            }
            cPadding += mToString;
        }

        int byteNum = cPadding.length() / 3;// 明文总字节数
        byte[] result = new byte[byteNum];
        for (int i = 0; i < byteNum; i++) {// 每三位数转化为byte型并返回该byte数组所表达的字符串
            result[i] = (byte) (Integer.parseInt(cPadding.substring(i * 3, i * 3 + 3)));
        }
        return new String(result);
    }

    /**
     * 获取公钥
     */
    public BigInteger getPublicKey() {
        return publicKey;
    }

    /**
     * 获取私钥
     */
    public BigInteger getPrivateKey() {
        return privateKey;
    }

    /**
     * 获取乘积
     */
    public BigInteger getProduct() {
        return product;
    }

    /**
     * 创建私钥和公钥
     */
    private void createKey() {
        BigInteger prime1 = createPrimeNumber(1024);//质数1
        BigInteger prime2 = createPrimeNumber(1024);//质数2
        BigInteger φn = euler(prime1, prime2);//欧拉函数
        product = prime1.multiply(prime2);//乘积
        publicKey = BigInteger.valueOf(65537);//公钥
        privateKey = calculatePrivateKey(φn);//计算私钥
    }

    /**
     * 创建一个指定大小的质数
     * 随机产生n比特的素数。最高位是1，从高位开始随机产生共n-1个0和1（0和1的比例是随机的，不是
     * 平均），并将每位所得值相加最终形成一个n位数。然后判断该数是否是素数，是则返回，否则重新开始产生新的数直至该数为素数为止。
     */
    private BigInteger createPrimeNumber(int length) {
        BigInteger result = TWO.pow(length - 1);
        Random random = new Random();
        int r1 = random.nextInt(101);// 产生0-100的整数，用于确定0和1的比例
        int r2;
        while (true) {// 循环产生数，直到该数为素数
            for (int i = length - 2; i >= 0; i--) {// 逐位产生表示数的0和1，并根据所在位计算结果相加起来
                r2 = random.nextInt(101);
                if (0 < r2 && r2 < r1) {// 产生的数为1
                    result = result.add(TWO.pow(i));
                }
            }
//            while (!isPrime(result)) {// 素数判断
//                result.add(ONE);
//            }
//            return result;
            if (isPrime(result)) {
                return result;
            }
            result = TWO.pow(length - 1);// 重新计算,最高位肯定是1
        }
    }


    /**
     * 蒙哥马利快速幂模运算，返回base^exponent mod module的结果。
     *
     * @param base     底数
     * @param exponent 指数
     * @param module   模数
     * @return result 结果
     */
    private BigInteger expMod(BigInteger base, BigInteger exponent, BigInteger module) {
        BigInteger result = ONE;
        BigInteger tmp = base.mod(module);
        while (!isZero(exponent)) {
            if (!isZero(exponent.and(ONE))) {
                result = result.multiply(tmp).mod(module);
            }
            tmp = tmp.multiply(tmp).mod(module);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    /**
     * 利用扩展欧几里得算法求出私钥d，使得de = kφ(n)+1，k为整数。
     *
     * @param e  公钥
     * @param φn =(p-1)(q-1)
     * @return gdk BigInteger数组形式返回最大公约数、私钥d、k
     */
    public BigInteger[] extdGcd(BigInteger e, BigInteger φn) {
        BigInteger[] gdk = new BigInteger[3];
        if (φn.compareTo(BigInteger.ZERO) == 0) {
            gdk[0] = e;
            gdk[1] = BigInteger.ONE;
            gdk[2] = BigInteger.ZERO;
            return gdk;
        } else {
            gdk = extdGcd(φn, e.remainder(φn));
            BigInteger tmp_k = gdk[2];
            gdk[2] = gdk[1].subtract(e.divide(φn).multiply(gdk[2]));
            gdk[1] = tmp_k;
            return gdk;
        }
    }

    /**
     * 欧拉函数
     */
    public BigInteger euler(BigInteger p, BigInteger q) {
        return p.subtract(ONE).multiply(q.subtract(ONE));
    }

    /**
     * 计算私钥
     */
    private BigInteger calculatePrivateKey(BigInteger φn) {
        BigInteger bigInteger = RSA.extdGcd(publicKey, φn)[1];
        if (!isPositive(bigInteger)) bigInteger = bigInteger.add(φn);
        return bigInteger;
    }

    /**
     * 是质数?
     * 米勒·罗宾算法
     */
    private boolean isPrime(BigInteger b) {
        if (isLess(b, TWO)) return false;// 小于2
        if (!isEqual(b, TWO) && isEven(b)) return false;// 非2偶数
        BigInteger b1 = b.subtract(ONE);
        BigInteger m = b1;// 找到q和m使得p = 1 + 2^q * m
        int q = m.getLowestSetBit();// 二进制下从右往左返回第一次出现1的索引
        m = m.shiftRight(q);
        for (int i = 0; i < 5; i++) {// 判断的轮数，精度、轮数和时间三者之间成正比关系
            BigInteger b2;
            do {// 在区间1~p上生成均匀随机数
                b2 = BigInteger.valueOf(b.bitLength());
            } while (!isPositive(b2) || !isLess(b2, b));
            int j = 0;
            BigInteger z = expMod(b2, m, b);
            while (!((j == 0 && isEqual(z, ONE)) || isEqual(z, b1))) {
                if ((j > 0 && isEqual(z, ONE)) || ++j == q) {
                    return false;
                }
                z = expMod(z, TWO, b);
            }
        }
        return true;
    }

    /**
     * a是不是b的因数
     */
    private boolean isFactor(BigInteger a, BigInteger b) {
        return isZero(b.remainder(a));
    }

    /**
     * 等于0?
     */
    private boolean isZero(BigInteger b) {
        return b.compareTo(ZERO) == 0;
    }

    /**
     * 大于0?
     */
    private boolean isPositive(BigInteger b) {
        return b.compareTo(ZERO) > 0;
    }

    /**
     * 小于0?
     */
    private boolean isNegative(BigInteger b) {
        return b.compareTo(ZERO) < 0;
    }

    /**
     * 相等?
     */
    private boolean isEqual(BigInteger b1, BigInteger b2) {
        return b1.compareTo(b2) == 0;
    }

    /**
     * 大于?
     */
    private boolean isGreater(BigInteger b1, BigInteger b2) {
        return b1.compareTo(b2) > 0;
    }

    /**
     * 小于?
     */
    private boolean isLess(BigInteger b1, BigInteger b2) {
        return b1.compareTo(b2) < 0;
    }

    /**
     * 是偶数?
     */
    private boolean isEven(BigInteger b) {
        return b.remainder(TWO).compareTo(ZERO) == 0;
    }
}
