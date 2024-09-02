package com.candyenk.demo.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import candyenk.android.tools.V;
import com.google.android.material.card.MaterialCardView;

import java.util.function.BiConsumer;

public class WV extends MaterialCardView {
    private static final String TAG = WV.class.getSimpleName();
    private final Context context;
    private final ImageView dxView;//大小调整按钮
    private final View bgView;//边框控件
    private final OnTouchListener sizeChangeL;//大小改变监听
    private final OnTouchListener positionChangeL;//位置改变监听
    private View mView;//主图标控件
    private int state;//大小调整按钮状态:0默认;1调整中;2调整完毕
    private Animation barAnimStart, barAnimEnd;

    private BiConsumer<Integer, Integer> onSizeC;//请求大小改变
    private BiConsumer<Integer, Integer> onPosC;//请求位置改变

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/
    private interface EndAnimationListener extends Animation.AnimationListener {

        @Override
        default void onAnimationStart(Animation animation) {

        }

        @Override
        void onAnimationEnd(Animation animation);

        @Override
        default void onAnimationRepeat(Animation animation) {

        }
    }
    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/
    public WV(Context context) {
        this(context, null);
    }

    public WV(Context context, AttributeSet attrs) {
        this(context, attrs, candyenk.android.R.style.Theme_CDK);
    }

    public WV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.sizeChangeL = createSizeChangeL();
        this.positionChangeL = createPositionChangeL();
        this.dxView = createSizeView();
        this.mView = createMainView();
        this.bgView = createBackgroundView();
        setRadius(48);

    }


    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (state == 1) {//拖动过程中的变换
            int width = dxView.getWidth();
            int height = dxView.getHeight();
            int mX = ((LayoutParams) dxView.getLayoutParams()).rightMargin;
            int mY = ((LayoutParams) dxView.getLayoutParams()).bottomMargin;
            dxView.layout(right - mX - width, bottom - mY - height, right, bottom);
            bgView.layout(left, top, right, bottom);
        } else {//其他形式的变换
            super.onLayout(changed, left, top, right, bottom);
        }
    }


    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/


    private View createBackgroundView() {
        View v = new View(context);
        V.FL(v).ele(99).hide().backgroundRes(candyenk.android.R.drawable.bg_window).parent(this);
        return v;
    }

    private ImageView createMainView() {
        ImageView v = new ImageView(context);
        V.FL(v).sizeDP(32).ele(98).paddingDP(4).drawable(candyenk.android.R.drawable.bg_window_bar).parent(this);
        v.setOnTouchListener(positionChangeL);
        return v;
    }

    private ImageView createSizeView() {
        ImageView iv = new ImageView(context);
        V.FL(iv).sizeDP(32).lGravity(Gravity.BOTTOM | Gravity.END).paddingDP(5).ele(100)
                .drawable(candyenk.android.R.drawable.ic_window_horw).parent(this);
        iv.setOnTouchListener(sizeChangeL);
        return iv;
    }

    private OnTouchListener createSizeChangeL() {
        return new OnTouchListener() {
            //按下时的位置
            private float lastX, lastY;
            //按下时的控件大小
            private int oldX, oldY;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下
                        state = 1;
                        V.visible(bgView);
                        lastX = e.getRawX();
                        lastY = e.getRawY();
                        oldX = getWidth();
                        oldY = getHeight();
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        int w = (int) (oldX + e.getRawX() - lastX);//X轴上的坐标
                        int h = (int) (oldY + e.getRawY() - lastY);//Y轴上的坐标
                        if (onSizeC != null) onSizeC.accept(w, h);
                        break;
                    case MotionEvent.ACTION_UP://抬起
                        state = 2;
                        V.hide(bgView);
                        int w2 = (int) (oldX + e.getRawX() - lastX);
                        int h2 = (int) (oldY + e.getRawY() - lastY);
                        if (onSizeC != null) onSizeC.accept(w2, h2);
                        break;
                }
                return true;
            }
        };
    }

    private OnTouchListener createPositionChangeL() {
        return new OnTouchListener() {
            private float lastX, lastY;
            private int oldX, oldY;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        state = 1;
                        V.visible(bgView);
                        lastX = e.getRawX();
                        lastY = e.getRawY();
                        oldX = ((WindowManager.LayoutParams) getLayoutParams()).x;
                        oldY = ((WindowManager.LayoutParams) getLayoutParams()).y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = Math.max(0, (int) (oldX + e.getRawX() - lastX));
                        int y = Math.max(0, (int) (oldY + e.getRawY() - lastY));
                        if (onPosC != null) onPosC.accept(x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        state = 2;
                        V.hide(bgView);
                        int x2 = (int) (oldX + e.getRawX() - lastX);
                        int y2 = (int) (oldY + e.getRawY() - lastY);
                        if (onPosC != null) onPosC.accept(x2, y2);
                        break;
                }
                return true;
            }
        };
    }
    /**********************************************************************************************/
    /*****************************************公共方法***********************************************/
    /**********************************************************************************************/
    /**
     * 设置主图标控件
     *
     * @param view
     */
    public void setMainView(View view) {
        removeView(mView);
        this.mView = view;
        addView(view);
        view.setOnTouchListener(positionChangeL);
    }

    /**
     * 设置控件大小改变监听
     * 在该接口内改变WindowManager.LayoutParams的大小
     * 否则无法实现窗口大小调整功能
     */
    public void setOnWindowSizeChanged(BiConsumer<Integer, Integer> onWindowSizeChanged) {
        this.onSizeC = onWindowSizeChanged;
    }

    /**
     * 设置控件位置改变监听
     * 在该接口内改变WindowManager.LayoutParams的位置
     * 否则无法实现窗口位置调整功能
     */
    public void setOnWindowPositionChanged(BiConsumer<Integer, Integer> onWindowPositionChanged) {
        this.onPosC = onWindowPositionChanged;
    }
}

