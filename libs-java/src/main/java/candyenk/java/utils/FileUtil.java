package candyenk.java.utils;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    //配置文件目录
    public static final String PATH_SDCARD = "/CanDyenK/";
    //编码格式
    public static final Charset CHARSET_UFT8 = StandardCharsets.UTF_8;
    public static final Charset CHARSET_GBK = Charset.forName("GBK");

    /******************************************************************************************************************/
    /***********************************************文件(夹)操作*********************************************************/
    /******************************************************************************************************************/
    /**
     * 文件读取
     *
     * @param filePath 文件绝对路径
     * @return 文件内容
     */
    public static String readFile(String filePath) {
        return readFile(filePath, CHARSET_UFT8);
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
     * 文件写入
     *
     * @param filePath 文件绝对路径
     * @param content  写入字符串内容
     * @param append   是否增量写入
     * @return 写入结果true成功false失败
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        return writeFile(filePath, content, CHARSET_UFT8, append);
    }

    public static boolean writeFile(String filePath, String content, Charset charset, boolean append) {
        try {
            writeParent(new FileOutputStream(filePath, append), content, charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return true;
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
    /******************************************************************************************************************/
    /********************************************文件路径操作************************************************************/
    /******************************************************************************************************************/

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

    /**********************************************************************************************/
    /**
     * 文件读取基类
     */
    private static String readParent(InputStream in, Charset charset) throws IOException {
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
    private static boolean writeParent(OutputStream out, String writestr, Charset charset) throws IOException {
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
     * 文件类型
     * 无类型:-1(没有后缀)
     * 其它:0
     * 文本:1()
     * 图片:2()
     * 音频:3()
     * 视频:4()
     * 压缩:5()
     * 二进制进制:11(.bin)
     * 十六进制:12(.hex)
     */
}
