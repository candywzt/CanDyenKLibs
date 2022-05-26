package candyenk.android.viewgroup;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import candyenk.android.R;
import com.google.android.material.card.MaterialCardView;

/**
 * 悬浮窗根控件
 * <p>
 * 自带拖动按钮
 * 自带缩放按钮
 * 需设定拖动监听
 * 需设定缩放监听
 */
public class WindowLayout2 extends FrameLayout {
    private static final String TAG = WindowLayout2.class.getSimpleName();
    private Context context;

    private MaterialCardView cardView;//内容控件
    private ImageView horwView;//缩放控件
    private View backView;//边框控件

    private LinearLayout toolbar;//悬浮窗控制栏

    private int horwState;//大小调整按钮状态0未调整1调整中2调整完毕


    private OnWindowSizeChangedListener onWindowSizeChangedListener;//控件尺寸改变监听
    private OnWindowPositionChangedListener onWindowPositionChangedListener;//控件位置改变监听

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/
    public interface OnWindowSizeChangedListener {
        void onViewSizeChanged(int width, int height);
    }

    public interface OnWindowPositionChangedListener {
        void onViewPositionChanged(int x, int y);
    }

    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/
    public WindowLayout2(Context context) {
        this(context, null);
    }

    public WindowLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.Theme_CDKActivity);
    }

    public WindowLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initLayout();
        initAttrs(attrs);
        initEvents();
    }

    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (horwState == 1) {
            //调整边框尺寸和缩放控件位置
            int width = horwView.getWidth();
            int height = horwView.getHeight();
            int mX = ((LayoutParams) horwView.getLayoutParams()).rightMargin;
            int mY = ((LayoutParams) horwView.getLayoutParams()).bottomMargin;
            horwView.layout(right - mX - width, bottom - mY - height, right, bottom);
            backView.layout(left, top, right, bottom);
        } else {
            //调整所有子控件尺寸
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/
    private void initLayout() {
        //悬浮窗内容卡片
        cardView = new MaterialCardView(context);
        LayoutParams lp = new LayoutParams(-1, -1);
        cardView.setLayoutParams(lp);
        cardView.setElevation(0);
        cardView.setRadius(20);
        addView(cardView);

        //悬浮窗缩放控件
        horwView = new ImageView(context);
        lp = new LayoutParams(dp2px(30), dp2px(30));
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        horwView.setLayoutParams(lp);
        horwView.setElevation(100);//设置层级为最高
        horwView.setImageDrawable(context.getResources().getDrawable(R.drawable.window_horw));
        addView(horwView);

        //悬浮窗控制栏
        toolbar = new LinearLayout(context);
        lp = new LayoutParams(-2, -2);
        lp.setMargins(dp2px(5), dp2px(5), 0, 0);//默认处于内部左侧
        toolbar.setLayoutParams(lp);
        toolbar.setOrientation(LinearLayout.VERTICAL);//默认垂直布局
        toolbar.setElevation(99);//设置层级99
        toolbar.setBackgroundColor(Color.GREEN);
        addView(toolbar);

        //悬浮窗边框控件
        backView = new View(context);
        lp = new LayoutParams(dp2px(20), dp2px(20));
        //lp.setMargins(0, 0, dp2px(5), dp2px(5));
        backView.setLayoutParams(lp);
        backView.setElevation(99);//设置层级99
        backView.setBackgroundResource(R.drawable.window_back);
        backView.setVisibility(GONE);
        addView(backView);
        //初始化工具栏控件
        createToolbar();

    }

    private void initAttrs(AttributeSet attrs) {
    }

    private void initEvents() {
        setWindowSize(horwView);
    }

    //创建工具栏
    private void createToolbar() {
        //拖动控件
        ImageView dragView = createToolbarView();
        dragView.setImageDrawable(context.getResources().getDrawable(R.drawable.window_bar));
        setWindowPosition(dragView);//设置拖动控件的触摸事件
        toolbar.addView(dragView);
        //停靠
        //关闭
        //锁定
        //挂件
        //透明度
        //焦点
        //设置
        //快捷设置

    }

    //创建工具栏控件
    private ImageView createToolbarView() {
        ImageView view = new ImageView(context);
        LayoutParams lp = new LayoutParams(dp2px(20), dp2px(20));
        view.setLayoutParams(lp);
        return view;
    }

    //悬浮窗停靠
    private void setWindowDock(ImageView view) {

    }

    //悬浮窗关闭
    private void setWindowClose(ImageView view) {

    }

    //悬浮窗锁定
    private void setWindowLock(ImageView view) {

    }

    //悬浮窗挂件
    private void setWindowPendant(ImageView view) {

    }

    //悬浮窗透明度
    private void setWindowAlpha(ImageView view) {

    }

    //悬浮窗焦点
    private void setWindowFocus(ImageView view) {

    }

    //悬浮窗设置
    private void setWindowSetting(ImageView view) {

    }

    //悬浮窗快捷设置
    private void setWindowQuickSetting(ImageView view) {

    }

    //悬浮窗缩放
    private void setWindowSize(View view) {
        view.setOnTouchListener(new OnTouchListener() {
            //按下位置的坐标和按下时控件的尺寸
            private float lastX, lastY, viewW, viewH;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e(TAG, "按下");
                        horwState = 1;
                        backView.setVisibility(VISIBLE);
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        viewW = getWidth();
                        viewH = getHeight();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e(TAG, "滑动");
                        int w = (int) (viewW + event.getRawX() - lastX);
                        int h = (int) (viewH + event.getRawY() - lastY);
                        if (onWindowSizeChangedListener != null) {
                            onWindowSizeChangedListener.onViewSizeChanged(w, h);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e(TAG, "抬起");
                        horwState = 2;
                        backView.setVisibility(GONE);
                        int w2 = (int) (viewW + event.getRawX() - lastX);
                        int h2 = (int) (viewH + event.getRawY() - lastY);
                        if (onWindowSizeChangedListener != null) {
                            onWindowSizeChangedListener.onViewSizeChanged(w2, h2);
                        }
                        break;
                }
                return true;
            }
        });
    }

    //悬浮窗拖动
    private void setWindowPosition(View view) {
        view.setOnTouchListener(new OnTouchListener() {
            //按下位置的坐标和按下时控件的位置坐标
            private float lastX, lastY, viewX, viewY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        horwState = 1;
                        backView.setVisibility(VISIBLE);
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        viewX = ((WindowManager.LayoutParams) getLayoutParams()).x;
                        viewY = ((WindowManager.LayoutParams) getLayoutParams()).y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) (viewX + event.getRawX() - lastX);
                        int y = (int) (viewY + event.getRawY() - lastY);
                        if (onWindowPositionChangedListener != null) {
                            onWindowPositionChangedListener.onViewPositionChanged(x, y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        horwState = 2;
                        backView.setVisibility(GONE);
                        int x2 = (int) (viewX + event.getRawX() - lastX);
                        int y2 = (int) (viewY + event.getRawY() - lastY);
                        if (onWindowPositionChangedListener != null) {
                            onWindowPositionChangedListener.onViewPositionChanged(x2, y2);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }
    /**********************************************************************************************/
    /*****************************************公共方法***********************************************/
    /**********************************************************************************************/
    /**
     * 设置控件大小改变监听
     * 在该接口内改变WindowManager.LayoutParams的大小
     * 否则无法实现窗口大小调整功能
     */
    public void setOnWindowSizeChangedListener(OnWindowSizeChangedListener onWindowSizeChanged) {
        this.onWindowSizeChangedListener = onWindowSizeChanged;
    }

    /**
     * 设置控件位置改变监听
     * 在该接口内改变WindowManager.LayoutParams的位置
     * 否则无法实现窗口位置调整功能
     */
    public void setOnWindowPositionChangedListener(OnWindowPositionChangedListener onWindowPositionChanged) {
        this.onWindowPositionChangedListener = onWindowPositionChanged;
    }


}
