package candyenk.android.utils;

import android.content.Context;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Android文件工具
 * Raw资源文件读取
 * Assets资源文件读取
 * Data文件读写删
 * SD卡文件读写删改同Java
 */
public class UFile extends candyenk.java.utils.UFile {

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
     * Data文件删除(data/data/包名/files/目录)
     *
     * @param fileName 删除文件名
     * @return 执行结果
     */
    public static boolean deleteDataFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * Data文件(夹)重命名(data/data/包名/files/目录)
     *
     * @param oldFilePath 重命名文件路径
     * @param newFileName 新文件名
     * @return 返回状态码 -1:文件不存在;0:文件名已存在;1:重命名成功
     */
    public static int renameDataFile(Context context, String oldFilePath, String newFileName) {
        File oldFile = context.getFileStreamPath(oldFilePath);
        File newFile = new File(oldFile.getParentFile(), newFileName);
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

}
