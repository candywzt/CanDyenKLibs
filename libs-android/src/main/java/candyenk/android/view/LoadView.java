package candyenk.android.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;

import androidx.annotation.ColorInt;

import candyenk.android.R;

/**
 * 呼吸动态控件
 */
public class LoadView extends View {
    protected static final float startPercent = 0.6f;//绘制起点百分比
    protected static final float endPercent = 0.4f;//绘制终点百分比

    protected Context context;
    protected int color;//颜色
    private Paint loadPaint;//画笔
    private ValueAnimator loadAnim, startAnim, closeAnim;//加载动画
    private OnLoadDismissListener onLoadDismissListener;

    protected int layoutWidth;//控件宽高
    protected float radius;//可绘制总半径
    protected float xCenter;//绘制x中心
    protected float yCenter;//绘制y中心
    protected float loadRadius;//当前绘制半径
    protected float percent;//绘制百分比

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/
    public interface OnLoadDismissListener {
        void onLoadDismiss();
    }

    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/

    public LoadView(Context context) {
        this(context, null);
    }

    public LoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(attrs);
        initPaints();
        initAnimations();
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initLayout();
        drawView(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, width);
    }

    protected void changeRadius(float value) {
        this.percent = value;
        this.loadRadius = value * this.radius;
        postInvalidate();
    }
    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/

    private void initAttrs(AttributeSet attrs) {
        //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CDKProgressBar);
        color = context.getResources().getColor(R.color.mainGC4_1);
        //a.recycle();
    }

    private void initPaints() {
        //画笔
        loadPaint = new Paint();
        loadPaint.setColor(color);
        loadPaint.setShadowLayer(0, 0, 5, 0xFFFFFFFF);
        loadPaint.setStyle(Paint.Style.STROKE);
        loadPaint.setAntiAlias(true);
        loadPaint.setStrokeWidth(32);
    }

    private void initLayout() {
        //宽高取最小值
        layoutWidth = Math.min(getWidth(), getHeight());
        //可绘制半径
        radius = this.layoutWidth * 0.5f;
        //绘制x轴坐标
        xCenter = getWidth() * 0.5f;
        //绘制y坐标
        yCenter = getHeight() * 0.5f;
    }

    private void initAnimations() {
        loadAnim = ValueAnimator.ofFloat(this.startPercent, this.endPercent);
        loadAnim.setDuration(4000);
        loadAnim.setRepeatMode(ValueAnimator.RESTART);
        loadAnim.setRepeatCount(-1);
        loadAnim.setInterpolator(new CycleInterpolator(1));
        loadAnim.addUpdateListener(anim -> {
            float value = (float) anim.getAnimatedValue();
            if (this.startAnim != null) {
                if (this.startAnim.isRunning()) {
                    this.startAnim.cancel();
                }
            }
            if (this.closeAnim != null) {
                if (this.closeAnim.isRunning()) {
                    this.closeAnim.cancel();
                }
            }
            changeRadius(value);
        });
        startAnim = new ValueAnimator();
        closeAnim = new ValueAnimator();
    }

    private ValueAnimator getAnimator(float start, float end, ValueAnimator.AnimatorUpdateListener l) {
        ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.setDuration(200);
        anim.addUpdateListener(l);
        return anim;
    }

    private void drawView(Canvas canvas) {
        canvas.drawCircle(this.xCenter, this.yCenter, this.loadRadius, this.loadPaint);
    }

    private void pauseAndResume(ValueAnimator anim) {
        if (!anim.isPaused()) {
            anim.pause();
        } else {
            anim.resume();
        }
    }

    private void startAnimation() {
        if (this.percent == this.startPercent) return;
        this.startAnim = getAnimator(this.percent, this.startPercent, anim -> {
            float value = (float) anim.getAnimatedValue();
            if (value == this.startPercent) {
                this.loadAnim.start();
            }
            changeRadius(value);
        });
        this.startAnim.start();
    }

    private void closeAnimation() {
        if (this.percent == 0) return;
        this.closeAnim = getAnimator(this.percent, 0, anim -> {
            float value = (float) anim.getAnimatedValue();
            Log.e("ccc", "" + value);
            if (value == 0 && onLoadDismissListener != null) {
                onLoadDismissListener.onLoadDismiss();
                Log.e("aaa", "关闭回调被调用");
            }
            changeRadius(value);
        });
        this.closeAnim.start();
    }

    private int dp2px(double dpValue) {
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**********************************************************************************************/
    /*****************************************公共方法***********************************************/
    /**********************************************************************************************/
    /**
     * 获取进度条颜色
     *
     * @return 颜色值
     */
    public int getColor() {
        return color;
    }


    /**
     * 设置进度条颜色
     *
     * @param color 颜色值
     */
    public LoadView setColor(@ColorInt int color) {
        this.color = color;
        loadPaint.setColor(color);
        return this;
    }

    /**
     * 开始加载
     */
    public void start() {
        if (this.startAnim.isRunning() || this.loadAnim.isRunning()) return;
        this.closeAnim.cancel();
        startAnimation();
    }

    /**
     * 加载暂停
     */
    public void pause() {
        if (this.loadAnim.isRunning()) {
            pauseAndResume(this.loadAnim);
        } else if (this.startAnim.isRunning()) {
            pauseAndResume(this.startAnim);
        } else if (this.closeAnim.isRunning()) {
            pauseAndResume(this.closeAnim);
        }
    }

    /**
     * 结束加载
     */
    public void dismiss() {
        if (this.closeAnim.isRunning()) return;
        this.startAnim.cancel();
        this.loadAnim.cancel();
        closeAnimation();

    }

    public void dismiss(OnLoadDismissListener l) {
        this.onLoadDismissListener = l;
        this.dismiss();
    }
}
