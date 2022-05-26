package candyenk.android.viewgroup;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

/**
 * <h1>灵动卡片布局</h1>
 * 还凑合，挺顺滑的<br>
 * 放GIF也不是很卡<br>
 */
public class NimbleFrameLayout extends FrameLayout {
    private ObjectAnimator animator;
    private int touchState;//触摸状态0:直接滑动；1:按下后第一下滑动；2:按下后的持续滑动

    public NimbleFrameLayout(Context context) {
        super(context);
    }

    public NimbleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NimbleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NimbleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    /**********************************************************************************************/
    /******************************************方法重写*********************************************/
    /**********************************************************************************************/
    //拦截子控件的点击事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }


    //重写触摸监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下(0)
                this.touchState = 1;
                NimbleAnimationDown(this, event);
                break;
            case MotionEvent.ACTION_MOVE://手指滑动(2)
                switch (touchState) {
                    case 1://按下后开始滑动第一下
                    case 0://没有按下直接滑动
                        this.touchState = 2;
                        break;
                    case 2://按下后的持续滑动
                        //停止动画
                        if (animator.isRunning())
                            animator.end();
                        //保存坐标
                        float[] coor = {event.getX(), event.getY()};//触摸点坐标
                        Nimble(this, coor);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:// 手指抬起(1)
            case MotionEvent.ACTION_CANCEL:// 触摸动作取消
                if (this.touchState == 1) {//按下后后直接抬起
                    super.callOnClick();
                }
                NimbleAnimationUp(this);
                break;
            default:
                break;
        }
        return true;
    }
    /**********************************************************************************************/
    /******************************************私有方法*********************************************/
    /**********************************************************************************************/
    /**
     * 灵动控件动画(按下)
     *
     * @param view  控件
     * @param event 触摸位置信息
     */
    private void NimbleAnimationDown(View view, MotionEvent event) {
        //设置动画支点
        view.setPivotX((float) (view.getWidth() * 0.5));
        view.setPivotY((float) (view.getHeight() * 0.5));

        int w2 = view.getWidth() / 2;
        int h2 = view.getHeight() / 2;
        float x = event.getX();
        float y = event.getY();

        if (x > w2 * 4 / 5 && x < w2 * 8 / 5 && y > h2 * 4 / 5 && y < h2 * 8 / 5)
            Scale(view, true);//点到中间，直接缩放
        else {
            //偏移计算(懒得去深究为什么x和y反过来了，总之不反过来是不行的)
            float rotationy = (w2 - x) / w2 * -7f;
            float rotationx = (h2 - y) / h2 * 7f;

            if (rotationx > 7f) rotationx = 7f;
            else if (rotationx < -7f) rotationx = -7f;

            if (rotationy > 7f) rotationy = 7f;
            else if (rotationy < -7f) rotationy = -7f;


            PropertyValuesHolder rx = PropertyValuesHolder.ofFloat("rotationX", 0, rotationx);
            PropertyValuesHolder ry = PropertyValuesHolder.ofFloat("rotationY", 0, rotationy);

            PropertyValuesHolder sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), 0.95f);
            PropertyValuesHolder sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), 0.95f);

            animator = ObjectAnimator.ofPropertyValuesHolder(view, sx, sy, rx, ry)
                    .setDuration(300);
            OvershootInterpolator interpolator = new OvershootInterpolator(3f);
            animator.setInterpolator(interpolator);
            animator.start();
        }

    }

    /**
     * 灵动控件动画(回收)
     *
     * @param view 控件
     */
    private void NimbleAnimationUp(View view) {

        PropertyValuesHolder rx = PropertyValuesHolder.ofFloat("rotationX", view.getRotationX(), 0);
        PropertyValuesHolder ry = PropertyValuesHolder.ofFloat("rotationY", view.getRotationY(), 0);

        PropertyValuesHolder sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), 0.95f);
        PropertyValuesHolder sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), 0.95f);

        animator = ObjectAnimator.ofPropertyValuesHolder(view, sx, sy, rx, ry)
                .setDuration(300);
        OvershootInterpolator interpolator = new OvershootInterpolator(3f);
        animator.setInterpolator(interpolator);
        animator.start();
        Scale(view, false);
    }

    /**
     * 执行控件灵动变换
     *
     * @param view 控件
     * @param coor 本次的坐标
     */
    private void Nimble(View view, float[] coor) {
        int w2 = view.getWidth() / 2;//控件宽度一半
        int h2 = view.getHeight() / 2;//控件高度一半

        //偏移计算
        float rotationy = (w2 - coor[0]) / w2 * -7f;
        float rotationx = (h2 - coor[1]) / h2 * 7f;

        if (rotationx > 7f) rotationx = 7f;
        else if (rotationx < -7f) rotationx = -7f;

        if (rotationy > 7f) rotationy = 7f;
        else if (rotationy < -7f) rotationy = -7f;

        view.setRotationX(rotationx);
        view.setRotationY(rotationy);
    }

    /**
     * 控件缩放
     *
     * @param view    控件
     * @param isScale 是就缩不是就放
     */
    private void Scale(View view, boolean isScale) {
        PropertyValuesHolder sx, sy;
        if (isScale) {//缩
            sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), 0.95f);
            sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), 0.95f);
        } else {//放
            sx = PropertyValuesHolder.ofFloat("scaleX", view.getScaleX(), 1);
            sy = PropertyValuesHolder.ofFloat("scaleY", view.getScaleY(), 1);
        }
        animator = ObjectAnimator.ofPropertyValuesHolder(view, sx, sy)
                .setDuration(300);
        OvershootInterpolator interpolator = new OvershootInterpolator(3f);//变速器
        animator.setInterpolator(interpolator);
        animator.start();
    }


}
