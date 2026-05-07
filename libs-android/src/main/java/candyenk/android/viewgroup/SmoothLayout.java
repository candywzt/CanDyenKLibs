package candyenk.android.viewgroup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import candyenk.android.R;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.Shapeable;

import java.util.Arrays;

/**
 * 圆角容器
 * 三段三次贝塞尔曲线
 * 模拟IOS7 系统圆角
 * 数据来源  <a href="https://www.paintcodeapp.com/news/code-for-ios-7-rounded-rectangles">paintcode</a>
 */
public final class SmoothLayout extends FrameLayout implements Shapeable {
    private final SmoothDrawer drawer = new SmoothDrawer();
    
    public SmoothLayout(Context context) {
        this(context, null);
    }
    
    public SmoothLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.smoothLayoutStyle);
    }
    
    public SmoothLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    
    public SmoothLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        super.setWillNotDraw(false);
       
        drawer.m = ShapeAppearanceModel.builder(context, attrs, defStyleAttr, defStyleRes).build();
        
        
        if (attrs == null) return;
        
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Log.e("AAA", attrs.getAttributeNamespace(i));
            Log.e("AAA", attrs.getAttributeName(i));
            Log.e("AAA", attrs.getAttributeValue(i));
            Log.e("AAA", "===");
        }
        
        
    }
    
    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(drawer.path);
        super.draw(canvas);
        canvas.restore();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawer.rf.set(0, 0, w, h);
        drawer.calculatePath();
        Log.e("TAG", "onSizeChanged: " + drawer.rf + "===" + w + ":" + h + ":" + oldh + ":" + oldh);
        Log.e("TAG", "onSizeChanged: " + Arrays.toString(drawer.r));
    }
    
    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.clipPath(drawer.path);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
    
    /**
     * IOS7圆角绘制器
     */
    private static class SmoothDrawer {
        private static final float K1 = 1.52866471f;
        private static final float K2 = 1.08849323f;
        private static final float K3 = 0.86840689f;
        private static final float K4 = 0.66993427f;
        private static final float K5 = 0.63149399f;
        private static final float K6 = 0.37282401f;
        private static final float K7 = 0.16906001f;
        private static final float K8 = 0.07491100f;
        private static final float K9 = 0.06549569f;
        private final float[] r = new float[4];
        private final Path path = new Path();
        private final RectF rf = new RectF();
        private ShapeAppearanceModel m = ShapeAppearanceModel.builder().build();
        
        private void flushRadius() {
            float limit = Math.min(rf.width(), rf.height()) / 2 / K1;
            r[0] = Math.min(m.getTopLeftCornerSize().getCornerSize(rf), limit);
            r[1] = Math.min(m.getTopRightCornerSize().getCornerSize(rf), limit);
            r[2] = Math.min(m.getBottomRightCornerSize().getCornerSize(rf), limit);
            r[3] = Math.min(m.getBottomLeftCornerSize().getCornerSize(rf), limit);
        }
        
        private void calculatePath() {
            if (rf.isEmpty()) return;
            path.reset();
            flushRadius();
            moveTo(TL(K1, 0));
            lineTo(TR(K1, 0));
            cubicTo(TR(K2, 0), TR(K3, 0), TR(K4, K9));
            lineTo(TR(K5, K8));
            cubicTo(TR(K6, K7), TR(K7, K6), TR(K8, K5));
            cubicTo(TR(0, K3), TR(0, K2), TR(0, K1));
            lineTo(BR(0, K1));
            cubicTo(BR(0, K2), BR(0, K3), BR(K9, K4));
            lineTo(BR(K8, K5));
            cubicTo(BR(K7, K6), BR(K6, K7), BR(K5, K8));
            cubicTo(BR(K3, 0), BR(K2, 0), BR(K1, 0));
            lineTo(BL(K1, 0));
            cubicTo(BL(K2, 0), BL(K3, 0), BL(K4, K9));
            lineTo(BL(K5, K8));
            cubicTo(BL(K6, K7), BL(K7, K6), BL(K8, K5));
            cubicTo(BL(0, K3), BL(0, K2), BL(0, K1));
            lineTo(TL(0, K1));
            cubicTo(TL(0, K2), TL(0, K3), TL(K9, K4));
            lineTo(TL(K8, K5));
            cubicTo(TL(K7, K6), TL(K6, K7), TL(K5, K8));
            cubicTo(TL(K3, 0), TL(K2, 0), TL(K1, 0));
            path.close();
        }
        
        private void moveTo(float[] p) {
            path.moveTo(p[0], p[1]);
        }
        
        private void lineTo(float[] p) {
            path.lineTo(p[0], p[1]);
        }
        
        private void cubicTo(float[] p1, float[] p2, float[] p3) {
            path.cubicTo(p1[0], p1[1], p2[0], p2[1], p3[0], p3[1]);
        }
        
        private float[] TL(float x, float y) {
            return new float[]{x * r[0], y * r[0]};
        }
        
        private float[] TR(float x, float y) {
            return new float[]{rf.width() - (x * r[0]), y * r[0]};
        }
        
        private float[] BR(float x, float y) {
            return new float[]{rf.width() - (x * r[0]), rf.height() - (y * r[0])};
        }
        
        private float[] BL(float x, float y) {
            return new float[]{x * r[0], rf.height() - (y * r[0])};
        }
    }
    
    @NonNull
    @Override
    public ShapeAppearanceModel getShapeAppearanceModel() {
        return drawer.m;
    }
    
    @Override
    public void setShapeAppearanceModel(@NonNull ShapeAppearanceModel model) {
        drawer.m = model;
        invalidate();
    }
    
}