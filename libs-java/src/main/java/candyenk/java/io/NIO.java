package candyenk.java.io;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Java NIO 帮助类
 * 为什么没有read?
 * 因为NIO的read似乎不太彳亍
 */
public class NIO {
    /**
     * 文件的复制操作
     * 使用Channel和Mapped
     * 对大于512MB的大文件效率高
     *
     * @param from 源文件
     * @param to   目标文件
     * @return 复制操作的成功与否
     */
    public static boolean copy(File from, File to) {
        FileOutputStream out = null;
        FileInputStream in = null;
        try {
            out = new FileOutputStream(to);
            in = new FileInputStream(from);
            MappedByteBuffer map = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, from.length());
            out.getChannel().write(map);
            return true;
        } catch (Exception e) {return false;} finally {IO.close(out, in);}
    }

    /**
     * 写入文件(通过Channel方式)
     * 对大于8MB的文件效率高
     */
    public static boolean write(FileChannel channel, byte[] bytes) {
        if (channel == null) return false;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        Buffer b = createBuyyfer();
        try {
            while ((in.read((byte[]) b.array())) > 0) {
                b.flip();
                channel.write((ByteBuffer) b);
                b.clear();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*** 创建缓存区 ***/
    private static ByteBuffer createBuyyfer() {
        return ByteBuffer.allocate(1024);
    }
}
