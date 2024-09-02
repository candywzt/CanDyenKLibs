package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import candyenk.android.tools.V;
import candyenk.android.utils.USys;
import com.candyenk.demo.R;

import java.util.HashMap;
import java.util.Map;

public class WM {
    private static final String TAG = WM.class.getSimpleName();
    private static final Map<String, WM> map = new HashMap<>();
    private final Context context;
    private final WindowManager wm;
    private final WindowManager.LayoutParams lp;
    private final View.OnTouchListener minTouchL;//小窗口触摸事件
    private final WV mainView;//主窗口状态控件
    private int[] px;//屏幕尺寸
    private View minView;//小窗口状态控件
    private int state;//状态0未打开1最小化2最大化
    private int mainWidth, mainHeight;//大窗口尺寸
    private int mainMinWidth, mainMinHeight;//大窗口限制最小尺寸
    private int minVSize;//小窗口尺寸

    /**
     * 一个 Context只能创建一个悬浮窗
     */
    public static WM create(Context context) {
        WM wm = map.get(context.getClass().getName());
        if (wm != null) return wm.flushWindows();
        wm = new WM(context);
        map.put(context.getClass().getName(), wm);
        return wm;
    }

    private WM(Context context) {
        this.context = context;
        this.wm = USys.getSystemService(context, Context.WINDOW_SERVICE);
        this.lp = createWMLP();
        this.minTouchL = createMinTouchL();
        initContent();
        this.minView = defaultMinView();
        this.mainView = new WV(context);
        this.mainView.addView(defaultMainView());
        this.mainView.setOnWindowSizeChanged(this::changeWindowSize);
        this.mainView.setOnWindowPositionChanged(this::changeWindowPosition);
    }

    private WindowManager.LayoutParams createWMLP() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        lp.format = PixelFormat.RGBA_8888;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.gravity = Gravity.START | Gravity.TOP;
        return lp;
    }

    //创建窗口移动事件
    private View.OnTouchListener createMinTouchL() {
        return new View.OnTouchListener() {
            //按下的坐标X,Y,窗口当前的位置X,Y
            private float lastX, lastY, wmX, wmY;
            //是否移动过的标记
            private int move;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下
                        lastX = e.getRawX();
                        lastY = e.getRawY();
                        wmX = lp.x;
                        wmY = lp.y;
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        int x = (int) (wmX + e.getRawX() - lastX);
                        int y = (int) (wmY + e.getRawY() - lastY);
                        changeWindowPosition(x, y);
                        move = 1;
                        break;
                    case MotionEvent.ACTION_UP://抬起
                        if (move == 0) v.performClick();
                        move = 0;
                        break;
                }
                return true;//不允许后续事件触发
            }
        };
    }

    //初始化一些数据
    private void initContent() {
        mainMinWidth = dp2px(70);
        mainMinHeight = dp2px(100);
        minVSize = dp2px(50);//小窗口尺寸50DP
        this.px = getDisplayPixels(context);
        lp.x = px[0] / 5;//默认X位置:1/5宽
        lp.y = px[1] / 6;//默认Y位置:1/6高
        mainWidth = (int) (px[0] / 1.5);
        mainHeight = px[1] / 2;
    }


    //改变主视图大小
    private void changeWindowSize(int w, int h) {
        if (state == 1) wm.updateViewLayout(minView, lp);
        if (state != 2) return;
        if (w < mainMinWidth || h < mainMinHeight) return;//过小
        if (w + lp.x > px[0]) w = px[0] - lp.x;//横向超限
        if (h + lp.y > px[1]) h = px[1] - lp.y;//纵向超限
        lp.width = mainWidth = w;
        lp.height = mainHeight = h;
        wm.updateViewLayout(mainView, lp);
    }

    //改变视图位置
    private void changeWindowPosition(int x, int y) {
        if (state == 0) return;//视图未展示
        if (lp.width + x > px[0]) x = px[0] - lp.width;//限制横向最大值
        if (lp.height + y > px[1]) y = px[1] - lp.height;//限制纵向最大值
        lp.x = x;
        lp.y = y;
        wm.updateViewLayout(state == 1 ? minView : mainView, lp);
        Log.e(TAG, "改位置:" + lp.x + "-" + lp.y);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private View defaultMainView() {
        TextView tv = new TextView(context);
        tv.setText(candyenk.android.R.string.error);
        return tv;
    }

    @SuppressLint("ClickableViewAccessibility")
    private View defaultMinView() {
        ImageView iv = new ImageView(context);
        V.FL(iv).sizeDP(10).drawable(R.drawable.icon).refresh();
        iv.setOnClickListener(v -> showMainWindow());
        iv.setOnTouchListener(minTouchL);
        return iv;
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }

    //获取屏幕尺寸
    private int[] getDisplayPixels(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new int[]{dm.widthPixels, dm.heightPixels};
    }

    /**********************************************************************************************/
    /*****************************************公开方法**********************************************/
    /**********************************************************************************************/

    /**
     * 当需要视图刷新时调用
     * 例如屏幕显示比例变化时
     */
    public WM flushWindows() {
        float p1 = (float) lp.x / px[0];//原来的悬浮窗的轴心点的横向占比
        float p2 = (float) lp.y / px[1];//纵向占比
        this.px = getDisplayPixels(context);
        lp.x = (int) (px[0] * p1);//更正横向位置
        lp.y = (int) (px[1] * p2);//更正纵向位置
        changeWindowSize(lp.width, lp.height);//更正大小信息
        return this;
    }

    /**
     * 设置主界面视图
     */
    public WM setContent(View view) {
        this.mainView.removeAllViews();
        this.mainView.addView(view);
        return this;
    }

    /**
     * 设置小视图控件
     * 不要设置OnTouchListener,会被顶掉替换成拖动,非要设置可在show后设置
     * 可设置OnClickListener,建议设置为展示主视图
     */
    public WM setMinView(View view) {
        this.minView = view;
        this.minView.setOnTouchListener(minTouchL);
        return this;
    }

    /**
     * 设置主视图的标题图标
     * 不要设置OnTouchListener,会被顶掉替换成拖动,非要设置可在show后设置
     *
     * @param view
     * @return
     */
    public WM setMainBarView(View view) {
        //mainView.s
        return this;
    }

    /**
     * 设置小图标的大小,需要showMinWindows生效
     *
     * @param size 不可为负
     */
    public WM setMinViewSize(int size) {
        if (size > 0) this.minVSize = size;
        return this;
    }

    /**
     * 设置主视图尺寸
     * 该方法不受最小尺寸限制
     * 但不能小于等于0
     * 需要showMainView生效
     *
     * @param width  宽度px
     * @param height 高度px
     */
    public WM setMainViewSize(int width, int height) {
        if (width > 0) this.mainWidth = width;
        if (height > 0) this.mainHeight = height;
        return this;
    }

    /**
     * 设置主视图最小可拖动尺寸
     * 不可小于等于0
     *
     * @param width  宽度px
     * @param height 高度px
     */
    public WM setMainViewMinSize(int width, int height) {
        if (width > 0) this.mainMinWidth = width;
        if (height > 0) this.mainMinHeight = height;
        return this;
    }

    /**
     * 切换小图标
     */
    public void showMinWindow() {
        if (state == 1) return;
        if (state == 2) wm.removeView(mainView);
        lp.width = lp.height = minVSize;
        Log.e(TAG, "位置:" + lp.x + "-" + lp.y);
        Log.e(TAG, "大小:" + lp.width + "-" + lp.height);
        wm.addView(minView, lp);
        state = 1;
    }

    /**
     * 切换主视图
     */
    public void showMainWindow() {
        if (state == 2) return;
        if (state == 1) wm.removeView(minView);
        lp.width = mainWidth;
        lp.height = mainHeight;
        Log.e(TAG, "位置:" + lp.x + "-" + lp.y);
        Log.e(TAG, "大小:" + lp.width + "-" + lp.height);
        wm.addView(mainView, lp);
        state = 2;
        flushWindows();
    }

    /**
     * 关闭悬浮窗和悬浮球
     */
    public void closeWindow() {
        if (state == 1) wm.removeView(minView);
        if (state == 2) wm.removeView(mainView);
        state = 0;
    }

}
