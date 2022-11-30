package candyenk.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import candyenk.android.R;
import candyenk.android.utils.ULay;
import candyenk.android.utils.USDK;
import candyenk.java.utils.UData;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CDK ProgressBar
 * 支持负数的
 * 控件高度固定DP25
 * 一次重构
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
    protected float progressPercent;//进度百分比
    protected int displayMode;//进度显示模式
    protected boolean ss;//是否启用流畅滑动

    private final GradientDrawable bgGD = new GradientDrawable();//背景
    private final GradientDrawable pgGD = new GradientDrawable();//进度
    private final Paint extremumPaint = new Paint();//极值画笔
    private final Paint numberPaint = new Paint();//进度画笔

    private float extW;//极值宽度
    private float startX, endX;//X轴始末
    private float proX, proY;//进度点坐标
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
        initLayout();
        bgGD.draw(canvas);//背景
        pgGD.draw(canvas);//进度条
        drawDisplay(canvas);//极值和进度显示

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, dp2px(25) + getPaddingTop() + getPaddingBottom());
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public void setMax(int max) {
        this.max = max;
        if (this.progress > max) setProgress(max);
        invalidate();
    }

    @Override
    public int getMin() {
        return this.min;
    }

    @Override
    public void setMin(int min) {
        this.min = min;
        if (this.progress < min) setProgress(min);
        invalidate();
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setProgress(int progress) {
        progress = Math.max(this.min, Math.min(this.max, progress));
        if (progress < min) progress = min;
        else if (progress > max) progress = max;
        updateProgress(progress);
    }

    @Override
    public void setBackground(Drawable bg) {
        if (bg instanceof ColorDrawable) {
            int color = ((ColorDrawable) bg).getColor();
            this.bgGD.setColor(color);
        } else if (USDK.Q() && bg instanceof ColorStateListDrawable) {
            ColorStateList color = ((ColorStateListDrawable) bg).getColorStateList();
            this.bgGD.setColor(color);
        } else return;
        invalidate();
    }


    /*** 设置进度条X轴坐标 ***/
    protected void setProgressPoint(float progressPoint) {
        progressPoint = Math.max(this.startX, Math.min(this.endX, progressPoint));
        if (this.proX != progressPoint) updateProgress(progressPoint);
    }

    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/
    /*** 初始化属性 ***/
    private void initAttrs(AttributeSet attrs) {
        try {
            @SuppressLint("CustomViewStyleable")
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CDKProgressBar);
            int max = a.getInt(R.styleable.CDKProgressBar_android_max, 100);
            int min = a.getInt(R.styleable.CDKProgressBar_android_min, 0);
            int progress = a.getInt(R.styleable.CDKProgressBar_android_progress, min);
            boolean ss = a.getBoolean(R.styleable.CDKProgressBar_smoothSliding, false);
            int displayMode = a.getInt(R.styleable.CDKProgressBar_displayMode, DM_NONE);
            int progressColor = context.getColor(R.color.main_01);
            int backgroundColor = context.getColor(R.color.back_view);
            a.recycle();
            setMax(max);
            setMin(min);
            setProgress(progress);
            enableSmoothSliding(ss);
            setDisplayMode(displayMode);
            setProgressColor(progressColor);
            setBackgroundColor(backgroundColor);
        } catch (Exception ignored) {}
        //初始化进度值
    }

    /*** 初始化画笔 ***/
    private void initPaints() {
        //极值画笔
        extremumPaint.setTextSize(24);
        extremumPaint.setTextAlign(Paint.Align.CENTER);
        extremumPaint.setStyle(Paint.Style.FILL);
        extremumPaint.setAntiAlias(true);
        //进度画笔
        numberPaint.setTextSize(24);
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setColor(0xFFFFFFFF);
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setAntiAlias(true);
    }

    /*** 初始化布局 ***/
    private void initLayout() {
        if (progressPercent == 0) progressPercent = (progress - min) * 1.0f / (max - min);
        this.extW = (displayMode & DM_EXTREMUM) != 0 ? dp2px(20) : 0.0f;
        float radius = (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
        this.startX = getPaddingLeft() + extW + radius;
        this.endX = getWidth() - getPaddingRight() - extW - radius;
        if (proX == 0) proX = (endX - startX) * progressPercent + startX;
        this.proY = radius + getPaddingTop();
        this.bgGD.setCornerRadius(radius);
        this.bgGD.setBounds((int) (startX - radius), getPaddingTop(), (int) (endX + radius), getHeight() - getPaddingBottom());
        this.pgGD.setCornerRadius(radius);
        this.pgGD.setBounds((int) (startX - radius), getPaddingTop(), (int) (proX + radius), getHeight() - getPaddingBottom());
    }

    /*** 绘制进度显示 ***/
    private void drawDisplay(Canvas canvas) {
        //不显示极值
        if ((this.displayMode & DM_NONE) != 0) return;
        Paint.FontMetrics f = extremumPaint.getFontMetrics();
        float y = this.proY - ((f.ascent + f.descent) * 0.5f);
        //显示极值
        if ((this.displayMode & DM_EXTREMUM) != 0) {
            canvas.drawText(UData.L2A(min, "", "%.1f"), extW * 0.5f + getPaddingLeft(), y, extremumPaint);
            canvas.drawText(UData.L2A(max, "", "%.1f"), getWidth() - extW * 0.5f - getPaddingRight(), y, extremumPaint);
        }
        //进度显示
        if ((this.displayMode & DM_NUM) != 0) {//显示数值
            canvas.drawText(UData.L2A(progress, "", "%.1f"), this.proX, y, numberPaint);
        } else if ((this.displayMode & DM_PERCENT) != 0) {//显示百分数
            canvas.drawText((int) (progressPercent * 100) + "%", this.proX, y, numberPaint);
        }
    }

    /*** 更新进度 ***/
    private void updateProgress(Number number) {
        int op = this.progress;//旧进度
        if (number instanceof Integer) {
            this.progress = (int) number;//进度
            if (op == this.progress) return;
            this.proX = c2b(a2c(this.progress));//进度转百分比转坐标
        } else {
            float b = (float) number;//坐标
            this.progress = c2a(b2c(b));//坐标转百分比转进度
            if (!ss && op == this.progress) return;
            this.proX = ss ? b : c2b(a2c(this.progress));//进度转百分比转坐标
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
        return (b - this.startX) / (this.endX - this.startX);
    }

    /*** 百分比转坐标 ***/
    private float c2b(float c) {
        return (this.endX - this.startX) * c + this.startX;
    }

    /*** 百分比转进度 ***/
    private int c2a(float c) {
        return Math.round((this.max - this.min) * c + this.min);
    }


    protected int dp2px(float dp) {
        return (int) ULay.dp2px(context, dp);
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
     * 设置进度条颜色
     *
     * @param progressColor 颜色值
     */
    public void setProgressColor(@ColorInt int progressColor) {
        pgGD.setColor(progressColor);
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
