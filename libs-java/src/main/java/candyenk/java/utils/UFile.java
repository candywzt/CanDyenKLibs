package candyenk.java.utils;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import candyenk.java.entity.FileType;

/**
 * Java文件工具
 * 文件读取到字符串(可调编码)
 * 文件按行自定义读取(可调编码)
 * 文件按行读取到List
 * 字符串写入文件
 * 文件夹创建
 * 文件(夹)删除与重命名
 * 列出文件夹所有文件(包括子文件夹)
 * 文件(夹)路径拼接
 * 文件(夹)路径分割
 * 获取当前jar运行路径
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
     */
    public static boolean isEmpty(File file) {
        return file == null || !file.exists();
    }

    public static boolean isEmpty(String filePath) {
        return isEmpty(new File(filePath));
    }

    /**
     * 文件读取
     *
     * @param filePath 文件绝对路径
     * @return 文件内容
     */
    public static String readFile(String filePath) {
        return readFile(filePath, StandardCharsets.UTF_8);
    }

    public static String readFile(String filePath, Charset charset) {
        String str = "";
        try {
            str = readParent(new FileInputStream(filePath), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 文件按行读取
     */
    public static boolean readFileLine(String filePath, Charset charset, Consumer<String> action) throws IOException {
        String line;
        BufferedReader reader;
        if (charset == null) {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));
        }
        while ((line = reader.readLine()) != null) {
            action.accept(line);
        }
        if (reader != null) {
            reader.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 文件读取到列表
     */
    public static List<String> readFileList(String filePath, Charset charset) {
        ArrayList<String> list = new ArrayList<>();
        try {
            readFileLine(filePath, charset, s -> list.add(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 文件写入
     *
     * @param filePath 文件绝对路径
     * @param content  写入字符串内容
     * @param append   是否增量写入
     * @return 写入结果true成功false失败
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        return writeFile(filePath, content, StandardCharsets.UTF_8, append);
    }

    public static boolean writeFile(String filePath, String content, Charset charset, boolean append) {
        boolean result = false;
        try {
            result = writeParent(new FileOutputStream(filePath, append), content, charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 文件夹创建
     */
    public static boolean createFolder(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * 文件(夹)删除
     *
     * @param filePath 要删除的文件(夹)目录
     * @return 删除结果
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                f.delete();
            }
            return true;
        } else {
            return file.delete();
        }
    }

    /**
     * 文件(夹)重命名
     *
     * @param oldFilePath 重命名文件绝对路径
     * @param newFileName 新文件名
     * @return 返回状态码 -1:文件不存在;0:文件名已存在;1:重命名成功
     */
    public static int renameFile(String oldFilePath, String newFileName) {
        File oldFile = new File(oldFilePath);
        File newFile = new File(oldFile.getParent() + "/" + newFileName);
        if (!oldFile.exists()) {
            return -1;
        } else {
            if (newFile.exists()) {
                return 0;
            } else {
                oldFile.renameTo(newFile);
                return 1;
            }
        }
    }

    /**
     * 列出文件夹内所有文件
     * 包括子文件夹
     */
    public static List<File> listFiles(String filesPath) {
        final ArrayList<File> fileList = new ArrayList<>();
        File parentFile = new File(filesPath);
        String[] fileNames = parentFile.list();
        if (parentFile.exists() && parentFile.isDirectory() && fileNames != null) {
            for (String name : fileNames) {
                File file = new File(filesPath, name);
                if (file.isDirectory()) {
                    fileList.addAll(listFiles(file.getAbsolutePath()));
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 获取文件后缀
     */
    public static String getSuffix(String fileName) {
        if (fileName == null || fileName.trim().length() == 0) {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1);
        } else {
            return "";
        }
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
     * 文件读取基类
     */
    public static String readParent(InputStream in, Charset charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader reader;
        if (charset == null) {
            reader = new BufferedReader(new InputStreamReader(in));
        } else {
            reader = new BufferedReader(new InputStreamReader(in, charset));
        }
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        if (reader != null) {
            reader.close();
            return sb.toString();
        } else {
            return null;
        }
    }

    /**
     * 文件写入基类
     */
    public static boolean writeParent(OutputStream out, String writestr, Charset charset) throws IOException {
        BufferedWriter write;
        if (charset == null) {
            write = new BufferedWriter(new OutputStreamWriter(out));
        } else {
            write = new BufferedWriter(new OutputStreamWriter(out, charset));
        }
        write.write(writestr);
        if (write != null) {
            write.close();
            return true;
        } else {
            return false;
        }
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
