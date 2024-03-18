package candyenk.android.tools;

import android.graphics.Path;

/**
 * Android graphics帮助类
 */
public class G {
    /**
     * 创建圆角矩形Path
     * 路径Path,矩形左上角坐标XY,矩形右下角坐标XY,圆角半径
     */
    public static Path rg(Path path, float x1, float y1, float x2, float y2, float tl, float tr, float br, float bl) {
        float[] p = {x1, y1 + tl, x1 + tl, y1, x2 - tr, y1, x2, y1 + tr, x2, y2 - br, x2 - br, y2, x1 + bl, y2, x1, y2 - bl};
        float[] d = {x1, y1, x2, y1, x2, y2, x1, y2};
        path.reset();
        path.moveTo(p[0], p[1]);//左上圆角起点
        path.quadTo(d[0], d[1], p[2], p[3]);//左上圆角控制点,终点
        path.lineTo(p[4], p[5]);//右上圆角起点;
        path.quadTo(d[2], d[3], p[6], p[7]);//右上圆角控制点,终点
        path.lineTo(p[8], p[9]);//右下圆角起点;
        path.quadTo(d[4], d[5], p[10], p[11]);//右下圆角控制点,终点
        path.lineTo(p[12], p[13]);//左下圆角起点;
        path.quadTo(d[6], d[7], p[14], p[15]);//左下圆角控制点,终点
        path.close();
        return path;
    }

    public static Path rg(Path path, float x1, float y1, float x2, float y2, float r) {
        return rg(path, x1, y1, x2, y2, r, r, r, r);
    }
}
