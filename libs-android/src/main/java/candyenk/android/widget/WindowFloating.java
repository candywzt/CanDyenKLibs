package candyenk.android.widget;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import candyenk.android.R;
import com.google.android.material.imageview.ShapeableImageView;

import candyenk.android.utils.UWeb;
import candyenk.android.viewgroup.WindowLayout2;

import java.util.HashSet;

/**
 * 悬浮窗组件
 */
public class WindowFloating {
    private static final String TAG = WindowFloating.class.getSimpleName();
    private static final HashSet<View> mList = new HashSet<>();
    private Context context;
    private View parentView;//调用悬浮窗的父级控件
    private WindowManager wm;//窗口管理器
    private WindowManager.LayoutParams lp; //窗口数据

    private ShapeableImageView minView;
    private WindowLayout2 mainView;

    private WebView webView;//悬浮窗WebView控件

    private int minWidth, mainWidth, mainHeight;
    private int windowState;
    private boolean isShow;
    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************构造方法**********************************************/
    /**********************************************************************************************/
    public WindowFloating(Context context) {
        this(context, null);
    }

    public WindowFloating(Context context, View view) {
        this.context = context;
        this.parentView = view;
        this.isShow = !mList.add(view);
        if (!isShow) {
            initWindow();
            initLayout();
            initContent();
            initEvents();
        }

    }
    /**********************************************************************************************/
    /*****************************************重写方法**********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************私有方法**********************************************/
    /**********************************************************************************************/
    private void initWindow() {
        wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        lp.format = PixelFormat.RGBA_8888;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        lp.gravity = Gravity.LEFT | Gravity.TOP;
    }

    private void initLayout() {
        minView = new ShapeableImageView(context);
        minView.setImageDrawable(context.getDrawable(R.drawable.aaa));
        mainView = new WindowLayout2(context);

        webView = new WebView(context);
        mainView.addView(webView);
        UWeb.initWebView((Activity) context, webView);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1 miHoYoBBS/2.24.2");
    }

    private void initContent() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int[] px = new int[]{dm.widthPixels, dm.heightPixels};
        int displayWidth = Math.min(px[0], px[1]);
        int displayheight = Math.max(px[0], px[1]);
        minWidth = dp2px(50);
        lp.x = displayWidth / 5;
        lp.y = displayheight / 6;
        mainWidth = (int) (displayWidth / 1.5);
        mainHeight = (int) (displayheight / 2.5);
    }

    private void initEvents() {
        View.OnTouchListener positionListener = new View.OnTouchListener() {
            private float lastX, lastY, wmX, wmY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        wmX = lp.x;
                        wmY = lp.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) (wmX + event.getRawX() - lastX);
                        int y = (int) (wmY + event.getRawY() - lastY);
                        changeWindowPosition(x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        };
        minView.setOnTouchListener(positionListener);
        minView.setOnClickListener(v -> showMainWindow());//放大
        mainView.setOnWindowSizeChangedListener((width, height) -> changeWindowSize(width, height));
        mainView.setOnWindowPositionChangedListener((x, y) -> changeWindowPosition(x, y));
        mainView.setOnFocusChangeListener((v, hasFocus) -> getInput(hasFocus));
        /*
        barView.setOnClickListener(v -> {
            LinearLayout ll = new LinearLayout(context);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
            ll.setLayoutParams(lp);
            ll.setOrientation(LinearLayout.VERTICAL);
            EditText et = new EditText(context);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -2);
            et.setLayoutParams(lp2);
            Button b1 = new MaterialButton(context);
            b1.setText("米游社");
            b1.setOnClickListener(v1 -> {
                et.setText("https://webstatic.mihoyo.com/ys/app/interactive-map/index.html");
            });
            Button b2 = new MaterialButton(context);
            b2.setText("观测枢");
            b2.setOnClickListener(v1 -> {
                et.setText("https://www.ip138.com/useragent/");
            });
            ll.addView(et);
            ll.addView(b1);
            ll.addView(b2);
            DialogBottom db = new DialogBottom(context, v);
            db.setWindowMode();
            db.setTitle("请输入网址");
            db.setContent(ll);
            db.setButtonText("加载", "缩小");
            db.setOnButtonClickListener(v1 -> {
                if (TextUtils.isEmpty(et.getText())) return;
                webView.loadUrl(et.getText().toString());
            }, v2 -> showMinWindow());
            db.show();

        });
        */
    }

    //改变大窗口的大小
    private void changeWindowSize(int w, int h) {
        if (windowState == 0 || windowState == 1) return;
        if (w < dp2px(70) || h < dp2px(100)) return;
        if (w == lp.width && h == lp.height) return;
        lp.width = mainWidth = w;
        lp.height = mainHeight = h;
        wm.updateViewLayout(mainView, lp);
    }

    //改变大小窗口位置
    private void changeWindowPosition(int x, int y) {
        if (windowState == 0) return;
        if (x == lp.x && y == lp.y) return;
        lp.x = x;
        lp.y = y;
        wm.updateViewLayout(windowState == 1 ? minView : mainView, lp);
    }

    //获取输入法焦点
    private void getInput(boolean isGet) {
        if (isGet) {
            Log.e(TAG, "获取焦点");
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        } else {
            Log.e(TAG, "失去焦点");
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        mainView.setLayoutParams(lp);
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }
    /**********************************************************************************************/
    /*****************************************公开方法**********************************************/
    /**********************************************************************************************/
    public void show() {
        if (isShow) {
            Log.e(TAG, "拦截重复调用,控件详细:" + this.parentView);
        } else {
            showMinWindow();
        }
    }

    /**
     * 切换悬浮球
     */
    public void showMinWindow() {
        Log.e(TAG, "展示悬浮球");
        if (windowState != 2) return;
        wm.removeView(mainView);
        lp.width = lp.height = minWidth;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wm.addView(minView, lp);
        windowState = 1;
    }

    /**
     * 切换悬浮窗
     */
    public void showMainWindow() {
        Log.e(TAG, "展示主悬浮窗");
        if (windowState != 1) return;
        wm.removeView(minView);
        lp.width = mainWidth;
        lp.height = mainHeight;
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wm.addView(mainView, lp);
        windowState = 2;
    }

    /**
     * 关闭悬浮窗和悬浮球
     */
    public void dismiss() {
        Log.e(TAG, "关闭悬浮窗");
        if (windowState == 1) wm.removeView(minView);
        if (windowState == 2) wm.removeView(mainView);
        windowState = 0;
        mList.remove(this.parentView);
    }

}
