package candyenk.android.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 包含一些文件读写操作的工具类
 */
public class FileUtil {
    //预设目录
    public static final String PATH_SDCARD = "/storage/emulated/0";
    //编码格式
    public static final Charset CHARSET_UFT8 = StandardCharsets.UTF_8;
    public static final Charset CHARSET_GBK = Charset.forName("GBK");

    /**
     * Raw资源文件读取(res/raw/文件夹)
     *
     * @param raw R.raw.fileName
     */
    public static String readRawFile(Context context, int raw, Charset charset) {
        String str = null;
        try {
            InputStream in = context.getResources().openRawResource(raw);
            str = readParent(in, charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Assets资源文件读取(assets/文件夹)
     *
     * @param fileName 文件名带后缀
     */
    public static String readAssetsFile(Context context, String fileName, Charset charset) {
        String str = null;
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            str = readParent(in, charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Data目录文件读取(data/data/包名/files/目录)
     *
     * @param fileName 文件名带后缀
     * @return
     */
    public static String readDataFile(Context context, String fileName, Charset charset) {
        String str = null;
        try {
            FileInputStream in = context.openFileInput(fileName);
            str = readParent(in, charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Data文件写入(data/data/包名/files/目录)
     *
     * @param fileName 写入文件名带后缀
     * @param writestr 写入字符串
     * @param mode     Content.MODE_PRIVATE覆盖写入;Content.MODE_APPEND追加写入
     */
    public static boolean writeDataFile(Context context, String fileName, String writestr, Charset charset, int mode) {
        boolean b = false;
        try {
            FileOutputStream out = context.openFileOutput(fileName, mode);
            b = writeParent(out, writestr, charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return b;
    }

    /**
     * Data文件删除
     *
     * @param fileName 删除文件名
     * @return 执行结果
     */
    public static boolean deleteDataFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * SD卡文件读取(/storage/emulated/0开头那种)
     *
     * @param filePath 文件绝对路径
     * @return 文件内容
     */
    public static String readSDFile(String filePath, Charset charset) {
        String str = null;
        try {
            str = readParent(new FileInputStream(filePath), charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return str;
    }

    /**
     * SD卡文件写入(/storage/emulated/0开头那种)
     *
     * @param filePath 文件绝对路径
     * @param content  写入字符串内容
     * @param mode     Content.MODE_PRIVATE覆盖写入;Content.MODE_APPEND追加写入
     * @return 写入结果true成功false失败
     */
    public static boolean writeSDFile(String filePath, String content, Charset charset, int mode) {
        boolean append = mode == Context.MODE_APPEND;
        try {
            writeParent(new FileOutputStream(filePath, append), content, charset);
        } catch (IOException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return true;
    }

    /**
     * SD卡文件(夹)删除(注意，能删除非空文件夹，切记)
     *
     * @param filePath 要删除的文件(夹)目录
     * @return 删除结果
     */
    public static boolean deleteSDFile(String filePath) {
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
     * SD卡文件(夹)重命名(别忘了加后缀)
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
     * 列出文件夹内文件
     *
     * @param filesPath 文件夹目录
     * @return
     */
    public static List<FileEntity> listFiles(String filesPath) {
        File file = new File(filesPath);
        if (!file.exists() || !file.isDirectory()) {
            return new ArrayList<>();
        }
        final ArrayList<FileEntity> entityList = new ArrayList<>();
        file.listFiles(pathname -> {
            FileEntity entity = new FileEntity(pathname);
            entityList.add(entity);
            return true;
        });
        return entityList;
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
    /**********************************************************************************************/
    /*****************************************文件信息类*********************************************/
    /**********************************************************************************************/
    private static class FileEntity {
        private String name;
        private String absolutePath;
        private String type;
        private int FileType;
        private long lastModified;
        private long fileSize;

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

        public FileEntity() {

        }

        public FileEntity(File file) {
            setFileName(file.getName());
            setAbsolutePath(file.getAbsolutePath());
            setLastModified(file.lastModified());
            setFileSize(file.length());
        }

        /**
         * 设置文件名
         */
        private void setFileName(String fileName) {
            if (name.contains(".")) {
                int index = name.lastIndexOf(".");
                if (index != -1) {
                    this.FileType = -1;
                    this.name = fileName;
                } else {
                    this.name = fileName.substring(index + 1);
                    this.type = fileName.substring(0, index - 1);
                }
            }
        }

        /**
         * 设置最后修改时间
         */
        private void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        /**
         * 设置绝对路径
         */
        public void setAbsolutePath(String absolutePath) {
            this.absolutePath = absolutePath;
        }

        /**
         * 设置文件大小
         */
        public void setFileSize(long size) {
            this.fileSize = fileSize;
        }
    }
}
