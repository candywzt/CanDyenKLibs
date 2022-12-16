package candyenk.java.tools;

import candyenk.java.io.IO;

import java.io.*;
import java.util.Base64;

/**
 * Java对象序列化帮助类
 */
public class JS {
    /**
     * 序列化
     */
    public static byte[] write(Serializable o) {
        byte[] bytes = new byte[0];
        ByteArrayOutputStream baos;
        ObjectOutputStream out = null;
        try {
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(o);
            out.flush();
            bytes = baos.toByteArray();
        } catch (Exception e) {
            IO.close(out);
        }
        return bytes;
    }

    /**
     * 序列化
     * 编码为字符串
     */
    public static String writeToString(Serializable o) {
        return Base64.getEncoder().encodeToString(write(o));
    }

    /**
     * 反序列化
     */
    public static <T extends Serializable> T read(byte[] b) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(b));
            return (T) in.readObject();
        } catch (Exception e) {
            IO.close(in);
            return null;
        }
    }

    /**
     * 反序列化
     * 从字符串解码
     */
    public static <T extends Serializable> T readOfString(String str) {
        return read(Base64.getDecoder().decode(str));
    }
}
