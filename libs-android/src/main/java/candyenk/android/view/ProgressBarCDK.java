package candyenk.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import candyenk.android.R;
import candyenk.android.utils.ULay;
import candyenk.java.utils.UData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CDK ProgressBar
 * 支持负数的
 * 控件高度固定DP25
 */
public class ProgressBarCDK extends ProgressBar {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {DM_NONE, DM_EXTREMUM, DM_NUM, DM_PERCENT})
    public @interface DisplayMode {}

    public static final int DM_NONE = 1;//不显示进度
    public static final int DM_EXTREMUM = 2;//显示极值
    public static final int DM_NUM = 4;//显示进度数值
    public static final int DM_PERCENT = 8;//显示进度百分比

    protected String TAG;
    protected Context context;
    protected int max;//最大进度
    protected int min;//最小进度
    protected int progress;//当前进度
    protected int backgroundColor;//背景色
    protected int progressColor;//进度条颜色
    private Paint progressPaint;//进度条画笔
    private Paint backgroundPaint;//背景画笔
    private Paint extremumPaint;//极值画笔
    private Paint numberPaint;//进度画笔

    protected int layoutHeight, layoutWidth;//控件宽高
    protected float padding;//绘制内距

    protected float startPoint;//绘制x起点坐标
    protected float endPoint;//绘制x终点坐标
    protected float yPoint;//绘制y坐标
    protected float progressPercent;//进度百分比
    protected float progressPoint;//当前进度坐标

    protected int displayMode;//进度显示模式
    protected boolean ss;//是否启用流畅滑动
    private OnProgressChangedListener l;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    public interface OnProgressChangedListener {
        void onChanged(int progress, float percent);
    }
    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/

    public ProgressBarCDK(Context context) {
        this(context, null);
    }

    public ProgressBarCDK(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarCDK(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.TAG = this.getClass().getSimpleName();
        this.context = context;
        initAttrs(attrs);
        initPaints();
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initLayout();
        drawBackground(canvas);//背景
        drawExtremum(canvas);//极值
        drawProgress(canvas);//进度条
        drawNumber(canvas);//进度显示
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, dp2px(25));
    }

    public int getMax() {
        return max;
    }


    public void setMax(int max) {
        this.max = max;
        if (this.progress > max) this.progress = max;
        invalidate();
    }


    public int getMin() {
        return this.min;
    }


    public void setMin(int min) {
        this.min = min;
        if (this.progress < min) this.progress = min;
        invalidate();
    }


    public int getProgress() {
        return progress;
    }


    public void setProgress(int progress) {
        progress = Math.max(this.min, Math.min(this.max, progress));
        if (progress < min) progress = min;
        else if (progress > max) progress = max;
        updateProgress(progress);
    }

    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    /*** 设置进度条X轴坐标 ***/
    protected void setProgressPoint(float progressPoint) {
        progressPoint = Math.max(this.startPoint, Math.min(this.endPoint, progressPoint));
        if (this.progressPoint != progressPoint) updateProgress(progressPoint);
    }

    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/

    private void initAttrs(AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CDKProgressBar);
        max = a.getInt(R.styleable.CDKProgressBar_android_max, 100);
        min = a.getInt(R.styleable.CDKProgressBar_android_min, 0);
        progress = a.getInt(R.styleable.CDKProgressBar_android_progress, min);
        ss = a.getBoolean(R.styleable.CDKProgressBar_smoothSliding, false);
        displayMode = a.getInt(R.styleable.CDKProgressBar_displayMode, DM_NONE);

        progressColor = context.getColor(R.color.main_01);
        backgroundColor = context.getColor(R.color.back_view);
        a.recycle();
        //初始化进度值
    }

    private void initPaints() {
        //进度条画笔
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setAntiAlias(true);
        //背景画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        //极值画笔
        extremumPaint = new Paint();
        extremumPaint.setTextSize(24);
        extremumPaint.setTextAlign(Paint.Align.CENTER);
        extremumPaint.setColor(progressColor);
        extremumPaint.setStyle(Paint.Style.FILL);
        extremumPaint.setAntiAlias(true);
        //进度画笔
        numberPaint = new Paint();
        numberPaint.setTextSize(24);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setColor(0xFFFFFFFF);
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setAntiAlias(true);
    }

    private void initLayout() {
        layoutHeight = dp2px(25);
        layoutWidth = getWidth();
        padding = (this.displayMode & DM_EXTREMUM) != 0 ? dp2px(20) : 0.0f;
        yPoint = this.layoutHeight * 0.5f;
        startPoint = yPoint + padding;
        endPoint = this.layoutWidth - this.startPoint;
        if (progressPercent == 0) progressPercent = (this.progress - this.min) * 1.0f / (this.max - this.min);
        if (progressPoint == 0)
            this.progressPoint = (this.endPoint - this.startPoint) * this.progressPercent + this.startPoint;
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(this.startPoint, this.yPoint, this.yPoint, backgroundPaint);
        canvas.drawCircle(this.endPoint, this.yPoint, this.yPoint, backgroundPaint);
        canvas.drawRect(this.startPoint, 0, endPoint, layoutHeight, backgroundPaint);
    }

    private void drawExtremum(Canvas canvas) {
        if ((this.displayMode & DM_NONE) != 0) return;//不显示极值
        if ((this.displayMode & DM_EXTREMUM) != 0) {//显示极值
            Paint.FontMetrics f = extremumPaint.getFontMetrics();
            float y = this.yPoint - ((f.ascent + f.descent) * 0.5f);
            //TODO:这里方法改了
            canvas.drawText(UData.L2A(min, "", "%.1f"), padding * 0.5f, y, extremumPaint);
            canvas.drawText(UData.L2A(max, "", "%.1f"), this.layoutWidth - padding * 0.5f, y, extremumPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawCircle(this.startPoint, this.yPoint, this.yPoint, progressPaint);
        canvas.drawCircle(this.progressPoint, this.yPoint, this.yPoint, progressPaint);
        canvas.drawRect(this.startPoint, 0, this.progressPoint, this.layoutHeight, progressPaint);
    }

    private void drawNumber(Canvas canvas) {
        if ((this.displayMode & DM_NONE) != 0) return;//不显示进度
        String text = "";
        if ((this.displayMode & DM_NUM) != 0) {//显示数值
            text = UData.L2A(progress, "", "%.1f");
            //TODO:这里方法改了
        } else if ((this.displayMode & DM_PERCENT) != 0) {//显示百分数
            text = (int) (progressPercent * 100) + "%";
        }
        Paint.FontMetrics f = numberPaint.getFontMetrics();
        float y = this.yPoint - ((f.ascent + f.descent) * 0.5f);
        canvas.drawText(text, this.progressPoint, y, numberPaint);
    }

    private void updateProgress(Number number) {
        int op = this.progress;//旧进度
        if (number instanceof Integer) {
            this.progress = (int) number;//进度
            if (op == this.progress) return;
            this.progressPoint = c2b(a2c(this.progress));//进度转百分比转坐标
        } else {
            float b = (float) number;//坐标
            this.progress = c2a(b2c(b));//坐标转百分比转进度
            if (!ss && op == this.progress) return;
            this.progressPoint = ss ? b : c2b(a2c(this.progress));//进度转百分比转坐标
        }
        this.progressPercent = a2c(this.progress);
        postInvalidate();
        if (l != null && op != this.progress) l.onChanged(this.progress, this.progressPercent);
    }


    /*** 进度转百分比 ***/
    private float a2c(int a) {
        return (a - this.min) * 1.0f / (this.max - this.min);
    }

    /*** 坐标转百分比 ***/
    private float b2c(float b) {
        return (b - this.startPoint) / (this.endPoint - this.startPoint);
    }

    /*** 百分比转坐标 ***/
    private float c2b(float c) {
        return (this.endPoint - this.startPoint) * c + this.startPoint;
    }

    /*** 百分比转进度 ***/
    private int c2a(float c) {
        return Math.round((this.max - this.min) * c + this.min);
    }


    private int dp2px(double dpValue) {
        return ULay.dp2px(context, dpValue);
    }
    /**********************************************************************************************/
    /*****************************************公共方法***********************************************/
    /**********************************************************************************************/
    /**
     * 启用流畅滑动
     */
    public void enableSmoothSliding(boolean enable) {
        this.ss = enable;
    }

    /**
     * 获取进度百分比
     */
    public float getProgressPercent() {
        return progressPercent;
    }

    /**
     * 设置进度百分比
     * 开启流畅滑动后精度飙升
     */
    public void setProgressPercent(float progressPercent) {
        progressPercent = Math.max(0, Math.min(1, progressPercent));
        if (!ss) setProgress(c2a(progressPercent));
        else setProgressPoint(c2b(progressPercent));
    }

    /**
     * 增加进度值
     */
    public void addProgress(int progress) {
        setProgress(getProgress() + progress);
    }

    public void addProgress() {
        this.addProgress(1);
    }


    /**
     * 获取背景色
     *
     * @return 颜色值
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }


    /**
     * 获取进度条颜色
     *
     * @return 颜色值
     */
    public int getProgressColor() {
        return progressColor;
    }


    /**
     * 设置进度条颜色
     *
     * @param progressColor 颜色值
     */
    public void setProgressColor(@ColorInt int progressColor) {
        this.progressColor = progressColor;
        progressPaint.setColor(progressColor);
        invalidate();
    }

    /**
     * 设置进度显示模式
     */
    public void setDisplayMode(@DisplayMode int displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * 设置进度变化监听
     * 只会在进度改变时回调,百分比改变不管
     */
    public void setOnProgressChangedListener(OnProgressChangedListener l) {
        this.l = l;
    }


}
