package candyenk.android.graphics;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 渐变图像背景
 * 使用构建器
 */
public class Gradient extends Drawable {
    private static final int[] bc = {Color.WHITE, 0xFF404040};
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Context context;
    private final boolean isDM;
    private final float[][] arr;
    private BitmapDrawable cache;
    
    private Gradient(Context context, float[][] arr) {
        this.context = context;
        isDM         = context.getResources().getConfiguration().isNightModeActive();
        this.arr     = arr;
    }
    
    /**
     * 创建渐变图构造器
     *
     * @param context 上下文,用来判断深色模式
     * @return 构造器
     */
    public static Build build(Context context) {
        return new Build(context, (float[][]) null);
    }
    
    /**
     * 默认渐变图,冷淡色渐变
     *
     * @param context 上下文
     * @return 图像
     */
    public static Drawable DEFAULT(Context context) {
        return new Build(context, new float[][]{{0.2f, 0.2f, 0.7f, 0xFFEBECF1, 0xFF1A1B21},
                                                {0.8f, 0.2f, 0.6f, 0xFFFFD6E8, 0xFF2D1F2A},
                                                {0.6f, 0.4f, 0.5f, 0xFFE6C8FF, 0xFF2A2D3F},
                                                {0.2f, 0.8f, 0.7f, 0xFFCCE6FF, 0xFF1F2C3A},
                                                {0.8f, 0.8f, 0.5f, 0xFFE8EFF5, 0xFF42475A}}).build();
    }
    
    @Override
    public void draw(@NonNull Canvas canvas) {
        int w = getBounds().width();
        int h = getBounds().height();
        
        if (cache == null) {
            Bitmap tempBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawColor(isDM ? bc[1] : bc[0]);
            for (float[] f : arr) {
                Shader s = new RadialGradient(w * f[0], h * f[1], w * f[2], (int) (isDM ? f[4] : f[3]), 0,
                                              Shader.TileMode.CLAMP);
                p.setShader(s);
                tempCanvas.drawRect(0, 0, w, h, p);
            }
            
            cache = new BitmapDrawable(context.getResources(), tempBitmap);
            cache.setBounds(getBounds());
        }
        cache.draw(canvas);
    }
    
    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
    
    @Override
    protected void onBoundsChange(@NonNull final Rect bounds) {
        super.onBoundsChange(bounds);
        if (cache != null) cache.setBounds(bounds);
    }
    
    @Override
    public void setAlpha(int alpha) {
        p.setAlpha(alpha);
    }
    
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        p.setColorFilter(colorFilter);
    }
    
    @Override
    public void setFilterBitmap(boolean filter) {
        p.setFilterBitmap(filter);
    }
    
    public static class Build {
        private final Context context;
        private List<float[]> list;
        
        private Build(Context context, float[]... arr) {
            this.context = context;
            if (arr != null) this.list = Arrays.asList(arr);
        }
        
        /**
         * 添加渐变着色器
         *
         * @param x            中心点X轴坐标百分百
         * @param y            中心点Y轴坐标百分百
         * @param r            半径百分百
         * @param cloorInt     颜色值
         * @param colorIntDark 深色模式下颜色值
         * @return 链式调用
         */
        public Build add(float x, float y, float r, int cloorInt, int colorIntDark) {
            if (list == null) list = new ArrayList<>();
            list.add(new float[]{x, y, r, cloorInt, colorIntDark});
            return this;
        }
        
        /**
         * 构建渐变图像
         *
         * @return 图像
         */
        public Drawable build() {
            return new Gradient(context, list.toArray(new float[0][0]));
        }
    }
    
}