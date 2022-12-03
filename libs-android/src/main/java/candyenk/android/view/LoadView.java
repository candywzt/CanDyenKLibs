package candyenk.android.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.CycleInterpolator;
import androidx.annotation.ColorInt;
import candyenk.android.R;
import candyenk.android.tools.A;
import candyenk.android.tools.L;

import java.util.function.Consumer;

/**
 * 呼吸动态控件
 * 控件尺寸为正方形
 * 因为是方形,所有size取最短边,padding属性取最大值
 * 一次重构
 */
public class LoadView extends View {

    /*************************************静态变量**************************************************/

    private static final String TAG = LoadView.class.getSimpleName();
    protected static final float startP = 0.6f;//呼吸起点百分比
    protected static final float endP = 0.4f;//呼吸终点百分比

    /*************************************成员变量**************************************************/

    protected Context context;
    private Paint loadPaint;//画笔
    private ValueAnimator loadA, startA, closeA;//加载动画,启动动画,结束动画

    protected int size;//控件尺寸(方形)
    protected float radius;//可绘制总半径
    protected float center;//绘制中心坐标
    protected float nRadius;//当前绘制半径
    protected float nowP;//呼吸当前百分比

    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
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
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(center, center, nRadius, loadPaint);
    }

    @Override
    protected void onMeasure(int w, int h) {
        super.onMeasure(w, h);
        size = Math.min(getMeasuredWidth(), getMeasuredHeight()); //宽高取最小值
        setMeasuredDimension(size, size);
        radius = (size - 2 * getPaddingStart()) * 0.5f; //可绘制半径
        center = size * 0.5f;  //绘制中心坐标
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        int p = Math.max(Math.max(Math.max(left, top), right), bottom);
        super.setPadding(p, p, p, p);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        int p = Math.max(Math.max(Math.max(start, top), end), bottom);
        super.setPaddingRelative(p, p, p, p);
    }

    /*** 更新半径,是动画执行的关键 ***/
    protected void changeRadius(float v) {
        this.nowP = v;
        this.nRadius = v * radius;
        postInvalidate();
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 获取进度条颜色
     *
     * @return 颜色值
     */
    public int getColor() {
        return loadPaint.getColor();
    }


    /**
     * 设置进度条颜色
     *
     * @param color 颜色值
     */
    public void setColor(@ColorInt int color) {
        if (loadPaint == null) loadPaint = new Paint();
        loadPaint.setColor(color);
    }

    /**
     * 开始加载
     */
    public void start() {
        startAnimation();
    }

    /**
     * 结束加载
     */
    public void dismiss() {
        dismiss(null);
    }

    /**
     * 结束加载
     *
     * @param dismissRun 结束动画完毕后执行的事件
     */
    public void dismiss(Consumer<LoadView> dismissRun) {
        closeAnimation(dismissRun);
    }

    /**
     * 暂停
     */
    public void pause() {
        if (startA.isRunning()) startA.pause();
        else if (loadA.isRunning()) loadA.pause();
        else if (closeA.isRunning()) closeA.pause();
    }

    /**
     * 继续
     */
    public void resume() {
        if (startA.isPaused()) startA.resume();
        else if (loadA.isPaused()) loadA.resume();
        else if (closeA.isPaused()) closeA.resume();
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    private void initAttrs(AttributeSet attrs) {
        //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CDKProgressBar);
        int color = context.getColor(R.color.main_01);
        //a.recycle();
        setColor(color);//这行必须
    }

    private void initPaints() {
        loadPaint.setShadowLayer(0, 0, 5, 0xFFFFFFFF);
        loadPaint.setStyle(Paint.Style.STROKE);
        loadPaint.setAntiAlias(true);
        loadPaint.setStrokeWidth(32);
    }

    private void initAnimations() {
        loadA = ValueAnimator.ofFloat(startP, endP);
        loadA.setDuration(4000);
        loadA.setRepeatMode(ValueAnimator.RESTART);
        loadA.setRepeatCount(-1);
        loadA.setInterpolator(new CycleInterpolator(1));
        loadA.addUpdateListener(a -> {
            L.e(TAG, "动画更新");
            changeRadius((float) a.getAnimatedValue());
        });
        startA = new ValueAnimator();
        startA.setDuration(200);
        closeA = new ValueAnimator();
        closeA.setDuration(200);
    }

    /*** 停止动画 ***/
    private void cancle(ValueAnimator... anim) {
        for (ValueAnimator a : anim) if (a.isStarted()) a.cancel();
    }

    /*** 播放启动动画 ***/
    private void startAnimation() {
        if (startA.isStarted() || loadA.isStarted() || closeA.isStarted()) return;
        startA.setFloatValues(nowP, startP);
        startA.addUpdateListener(a -> changeRadius((float) a.getAnimatedValue()));
        startA.addListener((A.EndA) (a, b) -> loadA.start());
        startA.start();
    }

    /*** 播放结束动画 ***/
    private void closeAnimation(Consumer<LoadView> dismissRun) {
        if (closeA.isStarted()) return;
        cancle(startA, loadA);
        closeA.setFloatValues(nowP, 0);
        closeA.addUpdateListener(a -> changeRadius((float) a.getAnimatedValue()));
        closeA.addListener((A.EndA) (a, b) -> {
            if (dismissRun != null) dismissRun.accept(LoadView.this);
        });
        closeA.start();
    }
}
