package candyenk.android.utils;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.RawRes;

import java.io.*;

/**
 * Android文件工具
 * 私有与共享目录获取
 * Raw资源文件读取
 * Assets资源文件读取
 * Data文件读写删
 * SD卡文件读写删改同Java
 */
public class UFile extends candyenk.java.utils.UFile {
    public static final String rootPath = "/";//根目录
    public static final String sdcardPath = "/storage/emulated/0";//外部存储目录
    public static final String systemPath = "/system";//System目录

    /**
     * 获取私有Data目录(/data/user/0/APPID)
     */
    public static String getDataPath(Context context) {
        return context.getDataDir().getAbsolutePath();
    }

    /**
     * 获取私有Files目录(/data/user/0/APPID/files)
     */
    public static String getFilesPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取私有Cache目录(/data/user/0/APPID/cache)
     */
    public static String getCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取共享Files目录(/storage/emulated/0/Android/data/APPID/files
     */
    public static String getEFilesPath(Context context) {
        return context.getExternalFilesDir("").getAbsolutePath();
    }

    /**
     * 获取共享Cache目录(/storage/emulated/0/Android/data/APPID/cache)
     */
    public static String getECachePath(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }


    /**
     * Raw资源文件读取(res/raw/文件夹)
     *
     * @param resId R.raw.fileName
     */
    public static InputStream readRaw(Context context, @RawRes int resId) {
        return context.getResources().openRawResource(resId);
    }

    /**
     * Assets资源文件读取(assets/文件夹)
     *
     * @param fileName 文件名带后缀
     * @return 无法读取返回NULL
     */
    public static InputStream readAssets(Context context, String fileName) {
        try {
            return context.getResources().getAssets().open(fileName);
        } catch (IOException e) {return null;}
    }

    /**
     * Data目录文件读取(data/data/包名/files/目录)
     *
     * @param fileName 文件名带后缀
     * @return
     */
    public static FileInputStream readData(Context context, String fileName) {
        try {
            return context.openFileInput(fileName);
        } catch (IOException e) {return null;}
    }

    /**
     * Data文件写入(data/data/包名/files/目录)
     *
     * @param fileName 写入文件名带后缀
     * @param isAppend 是够追加写入
     */
    public static FileOutputStream writeData(Context context, String fileName, boolean isAppend) {
        try {
            return context.openFileOutput(fileName, isAppend ? Context.MODE_APPEND : 0);
        } catch (IOException e) {return null;}
    }

    /**
     * Data文件删除(data/data/包名/files/目录)
     *
     * @param fileName 删除文件名
     * @return 执行结果
     */
    public static boolean deleteData(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * Uri文件读取
     *
     * @param context 应用上下文
     * @param uri     目标uri
     */
    public static InputStream readUri(Context context, Uri uri) {
        try {return context.getContentResolver().openInputStream(uri);} catch (Exception e) {return null;}
    }

    /**
     * Uri文件写入
     *
     * @param context 应用上下文
     * @param uri     目标uri
     */
    public static OutputStream writeUri(Context context, Uri uri) {
        try {return context.getContentResolver().openOutputStream(uri);} catch (Exception e) {return null;}
    }


    /**
     * Data文件(夹)重命名(data/data/包名/files/目录)
     *
     * @param filePath 重命名文件路径
     * @param newName  新文件名
     * @return 重命名成功返回true, 其他情况通通返回false
     */
    public static boolean renameData(Context context, String filePath, String newName) {
        File file = context.getFileStreamPath(filePath);
        return renameFile(file, newName);
    }

}
