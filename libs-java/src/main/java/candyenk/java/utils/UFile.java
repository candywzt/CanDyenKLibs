package candyenk.java.utils;


import candyenk.java.io.FileType;
import candyenk.java.io.IO;
import candyenk.java.io.NIO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Java文件工具
 * 注意权限问题
 * 功能衡多就不写了
 */
public class UFile {
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 文件判空
     * 当前文件路径是否为空或不可访问
     * 不是文件内容
     */
    public static boolean isEmpty(File file) {
        return file == null || !file.exists();
    }

    public static boolean isEmpty(String filePath) {
        return isEmpty(new File(filePath));
    }

    /**
     * 文件(夹)内容判空
     * 文件内容为空返回true
     * 空文件夹返回true
     * 文件无法读取返回false
     *
     * @param file 文件对象
     */
    public static boolean isEmptyContent(File file) {
        if (isEmpty(file)) return false;
        if (file.isDirectory()) return UArrays.isEmpty(file.list());
        else return file.length() == 0;
    }

    /**
     * 文件读取字符串(默认编码)
     * 超高性能文本文件读取
     *
     * @param file 输入文件
     * @return 文件无法读取返回空字符串
     */
    public static String readString(File file) {
        return readString(file, Charset.defaultCharset());
    }

    /**
     * 文件读取字符串
     * 超高性能文本文件读取
     *
     * @param file    输入文件
     * @param charset 字符编码
     * @return 文件无法读取返回空字符串
     */
    public static String readString(File file, Charset charset) {
        return new String(readBytes(file), charset);
    }

    /**
     * 文件读取字节数组
     * 超高性能文件读取
     *
     * @param file 输入文件
     * @return 文件无法读取返回空数组
     */
    public static byte[] readBytes(File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (!readStream(file, baos)) return new byte[0];
        else return baos.toByteArray();
    }

    /**
     * 文件读取到输出流(自动关闭输出流)
     * 超高性能文件读取
     */
    public static boolean readStream(File file, OutputStream out) {
        return readStream(file, out, true);
    }

    /**
     * 文件读取到输出流
     * 超高性能文件读取
     *
     * @param isClose 是否关闭输出流
     * @return 读取成功与否
     */
    public static boolean readStream(File file, OutputStream out, boolean isClose) {
        InputStream in;
        if (file.length() < 1 << 23) {//小于512MB采用FileInputStream方式
            in = getInputStream(file);
        } else {//大于512MB采用ChannelInputStream方式
            in = getChannelInputStream(file);
        }
        return IO.streamRW(in, out, true, isClose);
    }


    /**
     * 字符串写入到文件(默认编码)
     * 超高性能文本文件写入
     */
    public static boolean writeString(File file, String text, boolean isAppend) {
        return writeString(file, text, Charset.defaultCharset(), isAppend);
    }

    /**
     * 字符串写入到文件
     * 超高性能文本文件写入
     *
     * @param file     写入文件
     * @param text     写入内容
     * @param charset  写入编码
     * @param isAppend 是否增量写入
     * @return 写入成功与否
     */
    public static boolean writeString(File file, String text, Charset charset, boolean isAppend) {
        return writeBytes(file, text.getBytes(charset), isAppend);
    }


    /**
     * 字节数组写入到文件
     * 超高性能文件写入
     *
     * @param file     写入文件
     * @param bytes    写入内容
     * @param isAppend 是否增量写入
     * @return 写入成功与否
     */
    public static boolean writeBytes(File file, byte[] bytes, boolean isAppend) {
        FileOutputStream out = getOutputStream(file, isAppend);
        if (out == null) return false;
        if (file.length() < 1 << 23) {//小于8MB采用IO方式
            return IO.writeBytes(out, bytes);
        } else return NIO.write(out.getChannel(), bytes);//大于8MB采用NIO方式
    }

    /**
     * 输入流写入到文件(自动关闭输入流)
     */
    public static boolean writeStream(File file, InputStream in) {
        return writeStream(file, in, true);
    }

    /**
     * 输入流写入到文件
     *
     * @param isClose 是否关闭输入流
     * @return 写入成功与否
     */
    public static boolean writeStream(File file, InputStream in, boolean isClose) {
        return IO.streamRW(in, getOutputStream(file, false), isClose, true);
    }

    /**
     * 获取文件Reader
     *
     * @param file    输入文件
     * @param charset 文件编码
     * @return 文件无法读取返回NULL
     */
    public static Reader getReader(File file, Charset charset) {
        try {return new InputStreamReader(Objects.requireNonNull(getInputStream(file)), charset);} catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件输入流
     *
     * @param file 输入文件
     * @return 文件无法读取返回NULL
     */
    public static FileInputStream getInputStream(File file) {
        try {return new FileInputStream(file);} catch (Exception e) {return null;}
    }

    /**
     * 获取文件通道输入流
     *
     * @param file 输入文件
     * @return 文件无法读取返回NULL
     */
    public static InputStream getChannelInputStream(File file) {
        try {return Files.newInputStream(file.toPath());} catch (Exception e) {return null;}
    }

    /**
     * 获取文件Writer
     *
     * @param file    输入文件
     * @param charset 文件编码
     * @return 文件无法写入返回NULL
     */
    public static Writer getWriter(File file, Charset charset, boolean isAppend) {
        try {return new OutputStreamWriter(Objects.requireNonNull(getOutputStream(file, isAppend)), charset);} catch (
                Exception e) {
            return null;
        }
    }

    /**
     * 获取文件输出流
     *
     * @param file     输出文件
     * @param isAppend 是否累加
     * @return 文件无法写入返回NULL
     */
    public static FileOutputStream getOutputStream(File file, boolean isAppend) {
        try {return new FileOutputStream(file, isAppend);} catch (Exception e) {return null;}
    }

    /**
     * 获取文件通道输出流
     *
     * @param file     输出文件
     * @param isAppend 是否累加
     * @return 文件无法写入返回NULL
     */
    public static OutputStream getChannelOutputStream(File file, boolean isAppend) {
        try {
            StandardOpenOption append = isAppend ? StandardOpenOption.APPEND : StandardOpenOption.WRITE;
            return Files.newOutputStream(file.toPath(), append);
        } catch (IOException e) {return null;}
    }


    /**
     * 创建文件夹
     * 自动创建所有父级文件夹
     * 返回false优肯以及创建了一些文件夹
     *
     * @param files 文件对象
     * @return 创建成功或已存在返回true, 创建失败返回false
     */
    public static boolean createFolder(File... files) {
        if (UArrays.isEmpty(files)) return false;
        for (File file : files) {
            if (file == null) continue;
            if (!file.exists() && !file.mkdirs()) return false;
        }
        return true;
    }

    /**
     * 创建文件
     * 自动创建所有父级文件夹
     *
     * @param files 文件对象
     * @return 创建成功或已存在返回true, 创建失败返回false
     */
    public static boolean createFile(File... files) {
        if (UArrays.isEmpty(files)) return false;
        for (File file : files) {
            if (file == null) continue;
            if (file.exists()) return true;
            if (file.getParentFile() != null && !createFolder(file.getParentFile())) return false;
            try {if (!file.createNewFile()) return false;} catch (IOException e) {return false;}
        }
        return true;
    }

    /**
     * 文件(夹)删除
     * 删除所有子文件夹
     * 返回false也可能删除了一些文件
     *
     * @param files 要删除的文件对象们
     * @return 删除成功或不存在返回true, 删除失败返回false
     */
    public static boolean deleteFile(File... files) {
        if (UArrays.isEmpty(files)) return true;
        for (File file : files) {
            if (!file.exists()) continue;
            if (file.isDirectory() && !deleteFile(file.listFiles())) return false;
            if (!file.delete()) return false;
        }
        return true;
    }

    /**
     * 文件(夹)重命名
     *
     * @param file    需要重命名的文件对象
     * @param newName 新的文件名
     * @return 重命名成功返回true, 其他情况通通返回false
     */
    public static boolean renameFile(File file, String newName) {
        File nf = new File(file.getParent(), newName);
        if (!file.exists() || nf.exists()) return false;
        return file.renameTo(nf);
    }

    /**
     * 文件(夹)大小
     *
     * @param files 文件(夹)
     */
    public static long sizeFile(File... files) {
        if (UArrays.isEmpty(files)) return 0;
        long size = 0;
        for (File file : files) {
            if (file.isFile()) size += file.length();
            else if (file.isDirectory()) size += sizeFile(file.listFiles());
        }
        return size;
    }

    /**
     * 文件(夹)复制(NIO)
     * 如果目标文件存在,将被覆盖!!!
     *
     * @param from 源文件
     * @param to   目标文件
     * @return 复制是否成功
     */
    public static boolean copy(File from, File to) {
        if (isEmpty(from)) return false;
        if (from.isDirectory()) {
            if (isEmpty(to) && !to.isDirectory()) deleteFile(to);
            createFolder(to);
            String[] list = from.list();
            if (list == null) return false;
            else if (list.length == 0) return true;
            for (String s : list) {
                if (!copy(new File(from, s), new File(to, s))) return false;
            }
            return true;
        } else if (from.isFile()) {
            if (isEmpty(to) && !to.isFile()) deleteFile(to);
            createFile(to);
            if (from.length() > 1 << 29) {//小于512MB采用IO方式
                return IO.streamRW(getInputStream(from), getOutputStream(to, false));
            } else {//大于512MB采用NIO方式
                return NIO.copy(from, to);
            }
        } else return false;
    }

    /**
     * 列出所有文件
     * 包括子文件夹文件
     * 只列举文件,不列举文件夹
     *
     * @param files 文件(夹)(们)
     */
    public static List<File> listFiles(File... files) {
        final ArrayList<File> list = new ArrayList<>();
        if (UArrays.isEmpty(files)) return list;
        for (File file : files) {
            if (file.isFile()) list.add(file);
            else if (file.isDirectory()) {
                list.addAll(listFiles(file.listFiles()));
            }
        }
        return list;
    }

    /**
     * 计算文件数量
     *
     * @param files 文件(夹)(们)
     */
    public static int numberFiles(File... files) {
        if (UArrays.isEmpty(files)) return 0;
        int num = 0;
        for (File file : files) {
            if (file.isFile()) num++;
            else if (file.isDirectory()) num += numberFiles(file.listFiles());
        }
        return num;
    }

    /**
     * 计算文件夹数量
     *
     * @param files 文件(夹)(们)
     */
    public static int numberFolders(File... files) {
        if (UArrays.isEmpty(files)) return 0;
        int num = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                num += numberFiles(file.listFiles());
                num++;
            }
        }
        return num;
    }

    /**
     * 获取文件后缀
     * 无法获取多重后缀
     */
    public static String getSuffix(String fileName) {
        if (UString.isEmpty(fileName) || fileName.trim().length() == 0) return "";
        fileName = fileName.trim();
        int index = fileName.lastIndexOf(".");
        if (index != -1) return fileName.substring(index + 1);
        else return "";
    }


    /**
     * 文件路径拼接
     */
    public static String getPath(String... path) {
        StringBuilder sb = new StringBuilder();
        for (String p : path) {
            sb.append(p);
            if (!p.endsWith(File.pathSeparator)) {
                sb.append(File.pathSeparator);
            }
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    /**
     * 文件夹路径拼接
     */
    public static String getFolderPath(String... path) {
        StringBuilder sb = new StringBuilder();
        for (String p : path) {
            sb.append(p);
            if (!p.endsWith(File.pathSeparator)) {
                sb.append(File.pathSeparator);
            }
        }
        return sb.toString();
    }

    /**
     * 文件路径分割
     */
    public static String[] getPaths(String path) {
        String separator = File.pathSeparator;
        if (separator.equals("\\")) {
            separator = "\\\\";
        }
        return path.split(separator);
    }

    /**
     * 获取当前JAR运行路径
     */
    public static String getRunPath() {
        String filePath = System.getProperty("java.class.path");
        String pathSplit = System.getProperty("path.separator");
        if (filePath.contains(pathSplit)) {
            filePath = filePath.substring(0, filePath.indexOf(pathSplit));
        } else if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
        }
        return filePath;
    }


    /**
     * 创建缓存文件
     * 例如
     * xxx/cache/CDK/(Test)/abcd.tmp
     * xxx/cache/CDK/(Test/Demo)/abcd.cdk
     *
     * @param folderName 路径(不带前后斜杠)
     */
    public static File createTmp(String folderName) {
        String cache = System.getProperty("java.io.tmpdir");
        File file = new File(cache + "/CDK/" + folderName);
        if (file.isFile() && file.delete()) return createTmp(folderName);
        else file.mkdirs();
        File tmp = null;
        try {
            tmp = File.createTempFile(folderName, "cdk", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    /**
     * 判断文件类型
     */
    public static FileType getTypeFile(String filename) {
        return FileType.type(filename);
    }

    /**
     * 判断文件类型
     */
    public static FileType getTypeFile(File file) {
        return FileType.type(file);
    }
}
