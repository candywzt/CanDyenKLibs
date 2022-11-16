package candyenk.java.io;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * JavaIO 帮助类
 * 尽量避免Stream到Reader的转换,效率会降低
 */
public class IO {
    /**
     * 输入流读取字符串(默认编码)(自动关闭)
     */
    public static String readString(InputStream in) {
        return readString(in, true);
    }

    /**
     * 输入流读取字符串(默认编码)
     * 底层采用byte[]读取String
     *
     * @param in 输入流
     * @return 流无法读取返回空字符串
     */
    public static String readString(InputStream in, boolean isClose) {
        return readString(in, Charset.defaultCharset(), isClose);
    }

    /**
     * 输入流读取字符串(自动关闭)
     */
    public static String readString(InputStream in, Charset charset) {
        return readString(in, charset, true);
    }

    /**
     * 输入流读取字符串
     * 底层采用byte[]读取String
     *
     * @param in      输入流
     * @param charset 字符编码
     * @return 流无法读取返回空字符串
     */
    public static String readString(InputStream in, Charset charset, boolean isClose) {
        return new String(readBytes(in, isClose), charset);
    }


    /**
     * Reader读取字符串(自动关闭)
     */
    public static String readString(Reader reader) {
        return readString(reader, true);
    }

    /**
     * Reader读取字符串
     * 不建议读取StreamReader,直接读取Stream较好
     * Reader作为一个包装,不是用来全盘读取字符串的
     *
     * @param reader 输入Reader
     * @return Reader无法读取返回空字符串
     */
    public static String readString(Reader reader, boolean isClose) {
        StringBuilder sb = new StringBuilder();
        char[] chars = new char[1024];
        int size;
        try {
            while ((size = reader.read(chars)) != -1) {
                sb.append(chars, 0, size);
            }
        } catch (Exception ignored) {return "";} finally {if (isClose) close(reader);}
        return sb.toString();
    }

    /**
     * Reader按行读取字符串(自动关闭)
     */
    public static boolean readString(Reader reader, Consumer<String> action) {
        return readString(reader, true, action);
    }

    /**
     * Reader按行读取字符串
     *
     * @param reader 输入Reader
     * @param action 每一行的操作
     * @return 读取是否成功
     */
    public static boolean readString(Reader reader, boolean isClose, Consumer<String> action) {
        if (reader == null || action == null) return false;
        try {
            BufferedReader br = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) action.accept(line);
            return true;
        } catch (IOException ignored) {} finally {if (isClose) close(reader);}
        return false;
    }

    /**
     * 输入流读取字节数组(自动关闭)
     */
    public static byte[] readBytes(InputStream in) {
        return readBytes(in, true);
    }

    /**
     * 输入流读取字节数组
     *
     * @param in 输入流
     * @return 流无法读取返回空数组
     */
    public static byte[] readBytes(InputStream in, boolean isClose) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        streamRW(in, baos, isClose, false);
        return baos.toByteArray();
    }

    /**
     * 流间读写(自动关闭输入流)
     * 输出流不会关闭(关了还怎么输出)
     * 默认true,false
     */
    public static boolean streamRW(InputStream in, OutputStream out) {
        return streamRW(in, out, true, false);
    }

    /**
     * 流间读写
     *
     * @param in         输入流
     * @param out        输出流
     * @param isCloseIn  是否关闭输入流
     * @param isCloseOut 是否关闭输出流
     * @return 读写成功与否
     */
    public static boolean streamRW(InputStream in, OutputStream out, boolean isCloseIn, boolean isCloseOut) {
        if (in == null || out == null) return false;//必要,所有的判空都靠这个
        try {
            BufferedInputStream bis = in instanceof BufferedInputStream ? (BufferedInputStream) in : new BufferedInputStream(in);
            BufferedOutputStream bos = out instanceof BufferedOutputStream ? (BufferedOutputStream) out : new BufferedOutputStream(out);
            byte[] b = new byte[1024];
            int size;
            while ((size = bis.read(b)) > -1) bos.write(b, 0, size);
            bos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(isCloseIn ? in : null, isCloseOut ? out : null);
        }
    }


    /**
     * 字符串写入到流(默认编码)(自动关闭)
     */
    public static boolean writeString(OutputStream out, String text) {
        return writeString(out, text, true);
    }

    /**
     * 字符串写入到流(默认编码)
     * 底层采用byte[]写入
     *
     * @param out  写入流
     * @param text 写入内容
     * @return 写入成功与否
     */
    public static boolean writeString(OutputStream out, String text, boolean isClose) {
        return writeString(out, text, Charset.defaultCharset(), isClose);
    }

    /**
     * 字符串写入到流(自动关闭)
     */
    public static boolean writeString(OutputStream out, String text, Charset charset) {
        return writeString(out, text, charset, true);
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
    public static boolean writeString(OutputStream out, String text, Charset charset, boolean isClose) {
        if (out == null || text == null) return false;
        return writeBytes(out, text.getBytes(charset), isClose);
    }

    /**
     * 字符串写入到Writer(自动关闭)
     */
    public static boolean writeString(Writer writer, String text) {
        return writeString(writer, text, true);
    }

    /**
     * 字符串写入到Writer
     * 不建议写入StreamReader,直接写入Stream较好
     *
     * @param writer 写入Writer
     * @param text   写入内容
     * @return 写入成功与否
     */
    public static boolean writeString(Writer writer, String text, boolean isClose) {
        if (writer == null || text == null) return false;
        BufferedWriter bw = null;
        try {
            bw = writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
            bw.write(text);
            bw.flush();
            return true;
        } catch (IOException ignored) {} finally {if (isClose) close(bw);}
        return false;
    }

    /**
     * 字节数组写入到流(自动关闭)
     */
    public static boolean writeBytes(OutputStream out, byte[] content) {
        return writeBytes(out, content, true);
    }

    /**
     * 字节数组写入到流
     *
     * @param out     写入流
     * @param content 写入内容
     * @return 写入成功与否
     */
    public static boolean writeBytes(OutputStream out, byte[] content, boolean isClose) {
        if (out == null || content == null) return false;
        BufferedOutputStream bos = null;
        try {
            bos = out instanceof BufferedOutputStream ? (BufferedOutputStream) out : new BufferedOutputStream(out);
            bos.write(content);
            bos.flush();
            return true;
        } catch (IOException ignored) {} finally {if (isClose) close(bos);}
        return false;
    }

    /**
     * 关闭器
     */
    public static void close(Closeable... c) {
        if (c == null) return;
        for (Closeable able : c) try {able.close();} catch (Exception ignored) {}
    }
}
