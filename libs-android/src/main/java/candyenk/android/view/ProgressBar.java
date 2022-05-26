package candyenk.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;

import candyenk.android.R;
import candyenk.android.utils.DataUtil;

/**
 * 原生CDKProgressBar
 * 支持负数的
 * 别超过10k啊秋梨膏
 * 讲究顺滑可以整1k
 */
public class ProgressBar extends View {

    public static final int DISPLAY_MODE_NONE = 1;//不显示进度
    public static final int DISPLAY_MODE_EXTREMUM = 2;//显示极值
    public static final int DISPLAY_MODE_NUM = 4;//显示进度数值
    public static final int DISPLAY_MODE_PERCENT = 8;//显示进度百分比


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

    private OnProgressChangedListener mOnProgressChangedListener;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    public interface OnProgressChangedListener {
        void onProgressChanged(ProgressBar progressBar, int progress);
    }
    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    protected void changeProgress(int type, Object number) {
        //0:progress;1:progressPoint;2:progressParcent
        int progress = 0;
        float progressPoint = 0;
        float progressPercent = 0;
        switch (type) {
            case 0:
                progress = (int) number;//当前数值
                if (this.progress == progress) return;
                progressPercent = (progress - this.min) * 1.0f / (this.max - this.min);//得到百分比
                progressPoint = (this.endPoint - this.startPoint) * progressPercent + this.startPoint;//转换成坐标
                break;
            case 1:
                progressPoint = (float) number;//当前坐标
                progressPercent = (progressPoint - this.startPoint) * 1.0f / (this.endPoint - this.startPoint);//得到百分比
                progress = Math.round((this.max - this.min) * progressPercent + this.min);//转换成数值
                if (this.progress == progress) return;
                progressPercent = (progress - this.min) * 1.0f / (this.max - this.min);//重设百分比
                progressPoint = (this.endPoint - this.startPoint) * progressPercent + this.startPoint;//重设坐标
                break;
            case 2:
                progressPercent = (float) number;//当前百分比
                progress = Math.round((this.max - this.min) * progressPercent) + this.min;//得到数值
                if (this.progress == progress) return;
                progressPercent = (progress - this.min) * 1.0f / (this.max - this.min);//重设百分比
                progressPoint = (this.endPoint - this.startPoint) * progressPercent + this.startPoint;//转换成坐标
                break;
        }
        this.progressPercent = progressPercent;
        this.progress = progress;
        this.progressPoint = progressPoint;
        postInvalidate();
        if (mOnProgressChangedListener != null) {
            mOnProgressChangedListener.onProgressChanged(this, this.progress);
        }
    }
    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CDKProgressBar);
        max = a.getInt(R.styleable.CDKProgressBar_android_max, 100);
        min = a.getInt(R.styleable.CDKProgressBar_android_min, 0);
        progress = a.getInt(R.styleable.CDKProgressBar_android_progress, min);
        displayMode = a.getInt(R.styleable.CDKProgressBar_displayMode, DISPLAY_MODE_NONE);

        progressColor = context.getResources().getColor(R.color.mainGC4_1);
        backgroundColor = context.getResources().getColor(R.color.background_1);
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
        //进度条高度固定25dp
        layoutHeight = dp2px(25);
        //进度条宽度取控件宽度
        layoutWidth = getWidth();
        //绘制内距
        padding = (this.displayMode & DISPLAY_MODE_EXTREMUM) != 0 ? dp2px(20) : 0.0f;
        //绘制y坐标
        yPoint = this.layoutHeight * 0.5f;
        //绘制x起点
        startPoint = yPoint + padding;
        //绘制终点
        endPoint = this.layoutWidth - this.startPoint;
        //进度百分比
        if (progressPercent == 0)
            progressPercent = (this.progress - this.min) * 1.0f / (this.max - this.min);
        //进度条坐标
        if (progressPoint == 0)
            this.progressPoint = (this.endPoint - this.startPoint) * this.progressPercent + this.startPoint;
    }

    private void drawBackground(Canvas canvas) {
        //左侧半圆
        canvas.drawCircle(this.startPoint, this.yPoint, this.yPoint, backgroundPaint);
        //右侧半圆
        canvas.drawCircle(this.endPoint, this.yPoint, this.yPoint, backgroundPaint);
        //中间方形
        RectF rectF = new RectF(this.startPoint, 0, endPoint, layoutHeight);
        canvas.drawRect(rectF, backgroundPaint);
    }

    private void drawExtremum(Canvas canvas) {
        if ((this.displayMode & DISPLAY_MODE_NONE) != 0) return;//不显示极值
        if ((this.displayMode & DISPLAY_MODE_EXTREMUM) != 0) {//显示极值
            Paint.FontMetrics f = extremumPaint.getFontMetrics();
            float y = this.yPoint - ((f.ascent + f.descent) * 0.5f);
            canvas.drawText(DataUtil.intToShortString(min), padding * 0.5f, y, extremumPaint);
            canvas.drawText(DataUtil.intToShortString(max), this.layoutWidth - padding * 0.5f, y, extremumPaint);
        }
    }

    private void drawProgress(Canvas canvas) {
        //左侧圆形
        canvas.drawCircle(this.startPoint, this.yPoint, this.yPoint, progressPaint);
        if (progress != this.min) {
            //右侧圆形
            canvas.drawCircle(this.progressPoint, this.yPoint, this.yPoint, progressPaint);
            //中间方形
            RectF rectF = new RectF(this.startPoint, 0, this.progressPoint, this.layoutHeight);
            canvas.drawRect(rectF, progressPaint);
        }
    }

    private void drawNumber(Canvas canvas) {
        if ((this.displayMode & DISPLAY_MODE_NONE) != 0) return;//不显示进度
        String text = "";
        if ((this.displayMode & DISPLAY_MODE_NUM) != 0) {//显示数值
            text = DataUtil.intToShortString(progress);
        } else if ((this.displayMode & DISPLAY_MODE_PERCENT) != 0) {//显示百分数
            text = (int) (progressPercent * 100) + "%";
        }
        Paint.FontMetrics f = numberPaint.getFontMetrics();
        float y = this.yPoint - ((f.ascent + f.descent) * 0.5f);
        canvas.drawText(text, this.progressPoint, y, numberPaint);
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }
    /**********************************************************************************************/
    /*****************************************公共方法***********************************************/
    /**********************************************************************************************/

    /**
     * 获取最大进度
     *
     * @return 最大值
     */
    public int getMax() {
        return max;
    }

    /**
     * 设置最大进度
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        this.max = max;
        if (this.progress > max) this.progress = max;
        invalidate();
    }

    /**
     * @return 最小值
     */
    public int getMin() {
        return this.min;
    }

    /**
     * 设置最小进度
     */
    public void setMin(int min) {
        this.min = min;
        if (this.progress < min) this.progress = min;
        invalidate();
    }

    /**
     * 获取当前进度值
     *
     * @return 进度值
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 设置当前进度值
     *
     * @param progress 进度值
     */
    public void setProgress(int progress) {
        if (progress < min) {
            progress = 0;
        } else if (progress > max) {
            progress = max;
        }
        changeProgress(0, progress);
    }

    /**
     * 获取进度百分比
     *
     * @return percentage value
     */
    public float getProgressPercent() {
        return progressPercent;
    }

    /**
     * 设置进度百分比
     */
    public void setProgressPercent(float progressPercent) {
        if (progressPercent < 0) {
            progressPercent = 0;
        } else if (progressPercent > 1) {
            progressPercent = 1;
        }
        changeProgress(2, progressPercent);
    }

    /**
     * 设置进度条X轴坐标
     */
    public void setProgressPoint(float progressPoint) {
        if (progressPoint < this.startPoint) {
            progressPoint = this.startPoint;
        } else if (progressPoint > this.endPoint) {
            progressPoint = this.endPoint;
        }
        changeProgress(1, progressPoint);
    }

    /**
     * 增加进度值
     *
     * @param progress 增加的进度值
     */
    public void addProgress(int progress) {
        progress = this.progress + progress;
        if (progress > max) {
            progress = max;
        } else if (progress < min) {
            progress = min;
        }
        changeProgress(0, progress);

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
     * 设置背景色
     *
     * @param backgroundColor 颜色值
     */
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
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
    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * 设置进度变化监听
     *
     * @param onProgressChangedListener 进度值变化回调
     */
    public void setOnProgressChangedListener(OnProgressChangedListener
                                                     onProgressChangedListener) {
        this.mOnProgressChangedListener = onProgressChangedListener;
    }


}
