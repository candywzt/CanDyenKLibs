package candyenk.android.tools;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import candyenk.android.utils.USDK;

/**
 * <h2>灵动卡片效果</h2>
 * Nimble
 * 集天下之大成
 * 现在所有控件都能用了
 */
public class NV implements View.OnTouchListener {
    private static final OvershootInterpolator ipl = new OvershootInterpolator(3f);
    private final ObjectAnimator anim;
    private int tState;

    public NV() {
        this.anim = new ObjectAnimator();
        this.anim.setInterpolator(ipl);
        this.anim.setDuration(300);
    }

    /**
     * 应用于指定控件
     */
    public static void apply(View view) {
        if (view == null) return;
        view.setOnTouchListener(new NV());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.tState = 1;
                NimbleDown(v, e);
                if (v.hasOnClickListeners()) return false;
                else if (!USDK.R()) return false;
                else return !v.hasOnClickListeners();
            case MotionEvent.ACTION_MOVE:
                switch (tState) {
                    case 1:
                    case 0: this.tState = 2; break;
                    case 2: Nimble(v, e.getX(), e.getY()); break;
                }
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: NimbleUp(v);
            default: return false;
        }
    }

    /*** 灵动控件动画(按下) ***/
    private void NimbleDown(View v, MotionEvent e) {
        float w = v.getWidth() / 2f;
        float h = v.getHeight() / 2f;
        float x = e.getX();
        float y = e.getY();
        v.setPivotX(w);
        v.setPivotY(h);
        boolean b = isCenter(w, h, x, y);
        float fx = b ? 0 : Math.max(Math.min((h - y) / h * 7f, 7f), -7f);
        float fy = b ? 0 : Math.max(Math.min((w - x) / w * -7f, 7f), -7f);
        startAnim(v, v.getScaleX(), 0.95f, v.getScaleY(), 0.95f, 0, fx, 0, fy);
    }

    /*** 灵动控件动画(回收) ***/
    private void NimbleUp(View v) {
        startAnim(v, v.getScaleX(), 1, v.getScaleY(), 1, v.getRotationX(), 0, v.getRotationY(), 0);
    }

    /*** 执行控件灵动变换 ***/
    private void Nimble(View v, float x, float y) {
        if (anim.isRunning()) anim.end();
        int w = v.getWidth() / 2;
        int h = v.getHeight() / 2;
        float fx = Math.max(Math.min((h - y) / h * 7f, 7f), -7f);
        float fy = Math.max(Math.min((w - x) / w * -7f, 7f), -7f);
        v.setRotationX(fx);
        v.setRotationY(fy);
    }

    /*** 创建ValueHolder ***/
    private PropertyValuesHolder ofFloat(String s, float... f) {
        return PropertyValuesHolder.ofFloat(s, f);
    }

    /*** 启动动画 ***/
    private void startAnim(View v, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        PropertyValuesHolder sx = ofFloat("scaleX", f1, f2);
        PropertyValuesHolder sy = ofFloat("scaleY", f3, f4);
        PropertyValuesHolder rx = ofFloat("rotationX", f5, f6);
        PropertyValuesHolder ry = ofFloat("rotationY", f7, f8);
        anim.setValues(sx, sy, rx, ry);
        anim.setTarget(v);
        anim.start();
    }

    /*** 是否居中 ***/
    private boolean isCenter(float w, float h, float x, float y) {
        return x > w * 4 / 5f && x < w * 6 / 5f && y > h * 4 / 5f && y < h * 6 / 5f;
    }
}
