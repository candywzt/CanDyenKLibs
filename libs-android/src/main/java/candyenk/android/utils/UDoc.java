package candyenk.android.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import androidx.annotation.RequiresApi;
import candyenk.java.io.IO;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Android Access documents工具类
 * 共享文档工具
 * 用于在Android设备上读写共享空间的内容
 * 就是文档选择器
 * 创建的事Intent,配合UShare使用
 * 文档操作类:DocumentsContract
 */
public class UDoc {
    /**
     * 创建文件(保存文件)
     * MIME类型不建议无脑All
     *
     * @param mime     调用共享器的MIME类型
     * @param fileName 文件名
     * @param uri      指定具体路径Uri,null为默认
     * @return 返回创建的文件Uri
     */
    public static Intent create(String fileName, Uri uri, String mime) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mime);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        if (uri != null && USDK.O())
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        return intent;
    }

    public static Intent create(String fileName) {
        return create(fileName, null);
    }

    public static Intent create(String fileName, Uri defaultUri) {
        return create(fileName, defaultUri, "*/*");
    }


    /**
     * 打开文件
     *
     * @param mime  调用共享器的MIME类型
     * @param types 选取文件的MIME类型
     * @param uri   指定具体路径Uri,null为默认
     * @return 返回文件uri
     */
    public static Intent open(Uri uri, String mime, String... types) {
        //Intent intent = new Intent(Intent.ACTION_OPEN_CONTENT);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mime);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, types);
        if (uri != null && USDK.O())
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        return intent;
    }

    public static Intent open(String mime, String... types) {
        return open(null, mime, types);
    }

    public static Intent open(Uri defaultUri) {
        return open(defaultUri, "*/*");
    }

    public static Intent open() {
        return open("*/*");
    }

    /**
     * 授予目录访问权限
     *
     * @param uri 目录Uri
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Intent grant(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        return intent;
    }

    /**
     * 读取文件
     */
    public static ParcelFileDescriptor readFile(Context context, Uri uri) {
        try {
            return context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 写入文件
     */
    public static ParcelFileDescriptor writeFile(Context context, Uri uri) {
        try {
            return context.getContentResolver().openFileDescriptor(uri, "w");
        } catch (Exception e) {return null;}
    }

    /**
     * 读取到输入流
     */
    public static InputStream read(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 写入到输出流
     */
    public static OutputStream write(Context context, Uri uri) {
        try {
            return context.getContentResolver().openOutputStream(uri);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取文件Meta信息
     * 返回数组0:文件名 1:文件大小
     * 若为空则为空字符串不是NULL
     */
    public static String[] getMetaData(Context context, Uri uri) {
        String[] meta = {"", ""};
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    meta[0] = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    meta[1] = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE));
                }
            } catch (Exception ignored) {
            } finally {IO.close(cursor);}
        }
        //文件名(非Content)
        if (meta[0].isEmpty()) {
            meta[0] = uri.getPath();
            int cut = meta[0].lastIndexOf('/');
            if (cut != -1) meta[0] = meta[0].substring(cut + 1);
        }
        return meta;
    }
}
