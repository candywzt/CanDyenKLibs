package candyenk.java.utils;


import candyenk.java.io.FileType;
import candyenk.java.io.IO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Java文件工具
 * 注意权限问题
 * <p>
 * 文件判空
 * 文件读写String和Bytes(功能超丰富)
 * 文件(夹)增删
 * 文件(夹)重命名
 * 列出所有文件(包括子文件夹)
 * 文件(夹)路径拼接
 * 文件(夹)路径分割
 * 当前jar运行路径
 * 创建缓存文件
 * 获取文件后缀
 * 判断文件类型
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
    public static boolean isEmptyFile(File file) {
        if (isEmpty(file)) return false;
        if (file.isDirectory()) return UArrays.isEmpty(file.list());
        else return file.length() == 0;
    }

    /**
     * 文件读取字符串(默认编码)
     * 底层采用Reader读取String
     *
     * @param file 输入文件
     * @return 文件无法读取返回空字符串
     */
    public static String readString(File file) {
        return readString(file, Charset.defaultCharset());
    }

    /**
     * 文件读取字符串
     * 底层采用Reader读取String
     *
     * @param file    输入文件
     * @param charset 字符编码
     * @return 文件无法读取返回空字符串
     */
    public static String readString(File file, Charset charset) {
        return IO.readString(getReader(file, charset));
    }

    /**
     * 文件读取字节数组
     *
     * @param file 输入文件
     * @return 文件无法读取返回空数组
     */
    public static byte[] readBytes(File file) {
        return IO.readBytes(getInputStream(file));
    }


    /**
     * 字符串写入到文件(默认编码)
     * 底层采用Writer写入char[]
     *
     * @param file     写入文件
     * @param text     写入内容
     * @param isAppend 是否增量写入
     * @return 写入成功与否
     */
    public static boolean writeString(File file, String text, boolean isAppend) {
        return writeString(file, text, Charset.defaultCharset(), isAppend);
    }

    /**
     * 字符串写入到文件
     * 底层采用Writer写入char[]
     *
     * @param file     写入文件
     * @param text     写入内容
     * @param charset  写入编码
     * @param isAppend 是否增量写入
     * @return 写入成功与否
     */
    public static boolean writeString(File file, String text, Charset charset, boolean isAppend) {
        return IO.writeString(getWriter(file, charset, isAppend), text);
    }


    /**
     * 字节数组写入到文件
     *
     * @param file     写入文件
     * @param content  写入内容
     * @param isAppend 是否增量写入
     * @return 写入成功与否
     */
    public static boolean writeBytes(File file, byte[] content, boolean isAppend) {
        return IO.writeBytes(getOutputStream(file, isAppend), content);
    }


    /**
     * 获取文件Reader
     *
     * @param file    输入文件
     * @param charset 文件编码
     * @return 文件无法读取返回NULL
     */
    public static Reader getReader(File file, Charset charset) {
        try {return new InputStreamReader(getInputStream(file), charset);} catch (Exception e) {return null;}
    }

    /**
     * 获取文件输入流
     *
     * @param file 输入文件
     * @return 文件无法读取返回NULL
     */
    public static InputStream getInputStream(File file) {
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
        try {return new OutputStreamWriter(getOutputStream(file, isAppend), charset);} catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件输出流
     *
     * @param file 输出文件
     * @return 文件无法写入返回NULL
     */
    public static OutputStream getOutputStream(File file, boolean isAppend) {
        try {
            return Files.newOutputStream(file.toPath(), isAppend ? StandardOpenOption.APPEND : StandardOpenOption.WRITE);
        } catch (Exception e) {return null;}

    }


    /**
     * 创建文件夹
     * 自动创建所有父级文件夹
     * 返回false优肯以及创建了一些文件夹
     *
     * @param file 文件对象
     * @return 创建成功或已存在返回true, 创建失败返回false
     */
    public static boolean createFolder(File file) {
        if (file == null) return false;
        return file.exists() || file.mkdirs();
    }

    /**
     * 创建文件
     * 自动创建所有父级文件夹
     *
     * @param file 文件对象
     * @return 创建成功或已存在返回true, 创建失败返回false
     */
    public static boolean createFile(File file) {
        if (file == null) return false;
        if (file.exists()) return true;
        if (file.getParentFile() != null && !createFolder(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
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
            if (!file.exists()) return true;
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
     * 列出文件夹内所有文件
     * 只列举文件,不列举文件夹
     * 包括子文件夹文件
     *
     * @param file 文件夹对象
     * @return 失败返回空list
     */
    public static List<File> listAllFile(File file) {
        final ArrayList<File> list = new ArrayList<>();
        if (isEmpty(file)) return list;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (!UArrays.isEmpty(files)) {
                for (File f : files) list.addAll(listAllFile(f));
            }
        } else {
            list.add(file);
        }
        return list;
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
