package candyenk.java.io;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * JavaIO实体类
 * 尽量避免Stream到Reader的转换,效率会降低
 */
public class IO {
    /**
     * 输入流读取字符串(默认编码)
     * 底层采用byte[]读取String
     *
     * @param in 输入流
     * @return 流无法读取返回空字符串
     */
    public static String readString(InputStream in) {
        return readString(in, Charset.defaultCharset());
    }

    /**
     * 输入流读取字符串
     * 底层采用byte[]读取String
     *
     * @param in      输入流
     * @param charset 字符编码
     * @return 流无法读取返回空字符串
     */
    public static String readString(InputStream in, Charset charset) {
        return new String(readBytes(in), charset);
    }

    /**
     * Reader读取字符串
     * 不建议读取StreamReader,直接读取Stream较好
     *
     * @param reader 输入Reader
     * @return Reader无法读取返回空字符串
     */
    public static String readString(Reader reader) {
        StringBuilder sb = new StringBuilder();
        return readString(reader, s -> sb.append(s).append("\n")) ? sb.toString() : "";
    }

    /**
     * Reader按行读取字符串
     *
     * @param reader 输入Reader
     * @param action 每一行的操作
     * @return 读取是否成功
     */
    public static boolean readString(Reader reader, Consumer<String> action) {
        if (reader == null || action == null) return false;
        try {
            BufferedReader br = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) action.accept(line);
            return true;
        } catch (IOException ignored) {}
        return false;
    }

    /**
     * 输入流读取字节数组
     *
     * @param in 输入流
     * @return 流无法读取返回空数组
     */
    public static byte[] readBytes(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        streamReadWrite(in, baos);
        return baos.toByteArray();
    }

    /**
     * 流间读写
     *
     * @param in  输入流
     * @param out 输出流
     * @return 无法读写返回空字符串
     */
    public static boolean streamReadWrite(InputStream in, OutputStream out) {
        if (in == null || out == null) return false;//必要,所有的判空都靠这个
        try {
            BufferedInputStream bis = in instanceof BufferedInputStream ? (BufferedInputStream) in : new BufferedInputStream(in);
            BufferedOutputStream bos = out instanceof BufferedOutputStream ? (BufferedOutputStream) out : new BufferedOutputStream(out);
            byte[] b = new byte[1024];
            int size;
            while ((size = bis.read(b)) != -1) {
                bos.write(b, 0, size);
            }
            bos.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 字符串写入到流(默认编码)
     * 底层采用byte[]写入
     *
     * @param out  写入流
     * @param text 写入内容
     * @return 写入成功与否
     */
    public static boolean writeString(OutputStream out, String text) {
        return writeString(out, text, Charset.defaultCharset());
    }

    /**
     * 字符串写入到流
     * 底层采用byte[]写入
     *
     * @param out     写入流
     * @param text    写入内容
     * @param charset 写入编码
     * @return 写入成功与否
     */
    public static boolean writeString(OutputStream out, String text, Charset charset) {
        if (out == null || text == null) return false;
        return writeBytes(out, text.getBytes(charset));
    }

    /**
     * 字符串写入到Writer
     * 不建议写入StreamReader,直接写入Stream较好
     *
     * @param writer 写入Writer
     * @param text   写入内容
     * @return 写入成功与否
     */
    public static boolean writeString(Writer writer, String text) {
        if (writer == null || text == null) return false;
        try {
            BufferedWriter bw = writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
            bw.write(text);
            bw.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 字节数组写入到流
     *
     * @param out     写入流
     * @param content 写入内容
     * @return 写入成功与否
     */
    public static boolean writeBytes(OutputStream out, byte[] content) {
        if (out == null || content == null) return false;
        try {
            BufferedOutputStream bos = out instanceof BufferedOutputStream ? (BufferedOutputStream) out : new BufferedOutputStream(out);
            bos.write(content);
            bos.flush();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * 资源回收
     * 关闭输入流
     *
     * @param in 输入流
     */
    public static void close(InputStream in) {
        try {in.close();} catch (Exception ignored) {}
    }

    /**
     * 资源回收
     * 关闭输出流
     *
     * @param out 输入流
     */
    public static void close(OutputStream out) {
        try {out.close();} catch (Exception ignored) {}
    }

    /**
     * 资源回收
     * 关闭Reader
     *
     * @param reader 输入流
     */
    public static void close(Reader reader) {
        try {reader.close();} catch (Exception ignored) {}
    }

    /**
     * 资源回收
     * 关闭Writer
     *
     * @param writer 输入流
     */
    public static void close(Writer writer) {
        try {writer.close();} catch (Exception ignored) {}
    }
}
