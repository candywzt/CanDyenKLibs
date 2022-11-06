package candyenk.android.view;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Checkable;
import androidx.annotation.Nullable;
import candyenk.android.R;

/**
 * CDK灵动Switch
 */
public class Switch extends View implements Checkable {
    protected static final float switchRadiusStart = 0.3f;
    protected static final float switchRadiusEnd = 0.45f;

    protected Context context;
    protected boolean checked;//当前开关状态
    protected int backgroundColor;//背景色
    protected int switchColor;//开关颜色
    protected Paint switchPaint;//进度条画笔
    protected Paint backgroundPaint;//背景画笔

    protected int layoutHeight, layoutWidth;//控件宽高
    protected float startPoint;//绘制x起点
    protected float endPoint;//绘制x终点
    protected float switchPoint;//当前switch触点位置
    protected float switchRadius;//当前switch触点半径
    protected int touchState;//触摸状态0:直接滑动；1:按下后第一下滑动；2:按下后的持续滑动
    protected ValueAnimator downAnimator, upAnimator, checkAnimator;
    protected OnCheckedChangeListener mOnCheckedChangeListener;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/
    public interface OnCheckedChangeListener {
        void onCheckedChanged(Switch mSwitch, boolean isChecked);
    }
    /**********************************************************************************************/
    /*****************************************构造方法**********************************************/
    /**********************************************************************************************/

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Switch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Switch(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initAttrs(attrs);
        initPaints();
    }
    /**********************************************************************************************/
    /*****************************************重写方法**********************************************/
    /**********************************************************************************************/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initLayout();
        drawBackground(canvas);//背景
        drawSwitch(canvas);//开关
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dp2px(50), dp2px(25));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下(0)
                this.touchState = 1;
                //提前取消抬起动画
                if (this.upAnimator != null) {
                    this.upAnimator.cancel();
                    this.upAnimator = null;
                }
                this.downAnimator = getValueAnimations(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    changeSwitchSize(true, value);
                    postInvalidate();
                    this.downAnimator = null;
                });
                this.downAnimator.start();
                break;
            case MotionEvent.ACTION_MOVE://手指滑动(2)
                switch (touchState) {
                    case 1://按下后开始滑动第一下
                    case 0://没有按下直接滑动
                        this.touchState = 2;
                        break;
                    case 2://按下后的持续滑动

                        float x = event.getX();
                        if (x < this.startPoint) x = this.startPoint;
                        if (x > this.endPoint)
                            x = this.endPoint;
                        if (x != this.switchPoint) {
                            this.switchPoint = x;
                            invalidate();
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:// 手指抬起(1)
            case MotionEvent.ACTION_CANCEL:// 触摸动作取消
                //提前取消按下动画
                if (this.downAnimator != null) {
                    this.downAnimator.cancel();
                    this.downAnimator = null;
                }
                if (this.touchState == 1) {//按下后后直接抬起
                    setChecked(!this.checked);
                } else {//按下后滑动并抬起
                    boolean checked = this.switchPoint > this.layoutWidth * 0.5f;
                    float end = checked ? this.endPoint : this.startPoint;
                    this.upAnimator = getValueAnimations(animation -> {
                        float value = (float) animation.getAnimatedValue();
                        changeSwitchSize(false, value);
                        changeSwitchPoint(this.switchPoint, end, value);
                        postInvalidate();
                        if (value > 0.5) this.checked = checked;
                        this.upAnimator = null;
                    });
                    this.upAnimator.start();
                    this.touchState = 0;
                }
                break;
        }
        return true;
    }

    protected int dp2px(double dpValue) {
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKNimbleSwitch);
        this.checked = typedArray.getBoolean(R.styleable.CDKNimbleSwitch_android_checked, false);
        this.backgroundColor = context.getColor(R.color.back_view);
        this.switchColor = context.getColor(R.color.main_01);
        typedArray.recycle();

    }

    protected void initPaints() {
        //开关画笔
        switchPaint = new Paint();
        switchPaint.setColor(switchColor);
        switchPaint.setStyle(Paint.Style.FILL);
        switchPaint.setAntiAlias(true);
        //背景画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
    }

    protected void initLayout() {
        //开关高度25dp
        //开关宽度50dp
        layoutHeight = dp2px(25);
        //进度条宽度=控件宽度-内距*2-进度条高度
        layoutWidth = dp2px(50);
        //绘制起点
        startPoint = this.layoutHeight * 0.5f;
        //绘制终点
        endPoint = this.layoutWidth - this.startPoint;
        //触点位置
        if (switchPoint == 0) {
            switchPoint = this.checked ? this.endPoint : this.startPoint;
            //触点半径
            switchRadius = this.layoutHeight * 0.3f;
        }
        switchPaint.setColor(checked ? 0xFFFFFFFF : switchColor);
        backgroundPaint.setColor(checked ? switchColor : backgroundColor);
    }

    @Override
    public void setChecked(boolean mChecked) {
        if (layoutHeight == 0) this.checked = mChecked;
        else if (this.checked != mChecked) {
            float end = mChecked ? this.endPoint : this.startPoint;
            this.checkAnimator = getValueAnimations(animation -> {
                float value = (float) animation.getAnimatedValue();
                if (this.touchState == 1) {//按下后后直接抬起
                    if (this.downAnimator != null) {
                        this.downAnimator.cancel();
                        this.downAnimator = null;
                    }
                    changeSwitchSize(false, value);
                }
                changeSwitchPoint(this.switchPoint, end, value);
                postInvalidate();
                if (value > 0.5) {
                    this.checked = mChecked;
                    if (mOnCheckedChangeListener != null) {
                        mOnCheckedChangeListener.onCheckedChanged(this, this.checked);
                    }
                }
            });
            this.checkAnimator.start();
        }
    }
    @Override
    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }
    /**********************************************************************************************/
    /*****************************************公共方法**********************************************/
    /**********************************************************************************************/
    /**
     * 设置开关变动监听
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }
    /**********************************************************************************************/
    /*****************************************私有方法**********************************************/
    /**********************************************************************************************/

    private ValueAnimator getValueAnimations(ValueAnimator.AnimatorUpdateListener l) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(500);
        anim.setInterpolator(new OvershootInterpolator(3.0f));
        anim.addUpdateListener(l);
        return anim;
    }

    private void drawSwitch(Canvas canvas) {
        canvas.drawCircle(this.switchPoint, this.layoutHeight * 0.5f, this.switchRadius, switchPaint);
    }

    private void drawBackground(Canvas canvas) {
        //左侧半圆
        canvas.drawCircle(this.startPoint, this.layoutHeight * 0.5f, this.layoutHeight * 0.5f, backgroundPaint);
        //右侧半圆
        canvas.drawCircle(this.endPoint, this.layoutHeight * 0.5f, this.layoutHeight * 0.5f, backgroundPaint);
        //中间方形
        RectF rectF = new RectF(this.startPoint, 0, this.endPoint, this.layoutHeight);
        canvas.drawRect(rectF, backgroundPaint);
    }

    private void changeSwitchSize(boolean isDown, float value) {
        if (!isDown) value = 1 - value;
        float num = this.switchRadiusEnd - this.switchRadiusStart;
        float radiusValue = value * num + this.switchRadiusStart;
        this.switchRadius = radiusValue * this.layoutHeight;
    }

    private void changeSwitchPoint(float start, float end, float value) {
        float num = end - start;
        float pointValue = value * num + start;
        this.switchPoint = pointValue;
    }
}
