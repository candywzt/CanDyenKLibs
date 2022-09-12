package candyenk.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 安卓图片工具
 * 静态功能:
 * Bitmap转Drawable
 * Drawable转Bitmap
 * byte[]转Bitmap
 * Bitmap转byte[]
 * 保存Bitmap图片到JPG
 * 获取屏幕截图(带/不带状态栏)
 */
public class UImg {
    /**
     * Bitmap 转 Drawable
     */
    public static Drawable B2D(Bitmap bitmap) {
        return new BitmapDrawable(null, bitmap);
    }

    /**
     * Drawable 转 Bitmap
     */
    public static Bitmap D2B(Drawable drawable) {
        try {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();
            if (bitmap == null) throw new Exception();
            return bitmap;
        } catch (Exception e) {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * byte[] 转 Bitmap
     */
    public static Bitmap B2B(byte[] b) {
        if (b == null || b.length == 0) return null;
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * Bitmap 转 byte[]
     */
    public static byte[] B2B(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 保存Bitmap到JPG文件
     *
     * @return 1:保存成功
     * 0:路径权限不足
     * -1:文件已存在(未保存)
     * -2:保存失败
     */
    public static int saveBitmap(Bitmap bitmap, String filePath) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return 0;
        FileOutputStream b = null;
        int result;
        try {
            File file = new File(filePath);
            if (file.exists()) return -1;
            file.getParentFile().mkdirs();
            b = new FileOutputStream(file);
            result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b) ? 1 : -2;
        } catch (FileNotFoundException e) {
            result = 0;
        } catch (Exception e) {
            result = -2;
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                result = -2;
            }
        }
        return result;
    }
}
