package candyenk.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import candyenk.android.R;
import candyenk.android.tools.A;
import candyenk.android.tools.V;
import candyenk.android.utils.ULay;
import com.google.android.material.textview.MaterialTextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 气泡弹窗控件
 */
public class Popup extends PopupWindow {
    /*************************************静态变量**************************************************/
    public static final int STARTOUT = 1;//置于起始点外
    public static final int START = 2;//对齐起始点
    public static final int CENTER = 3;//居中
    public static final int END = 4;//对齐结束点
    public static final int ENDOUT = 5;//置于结束点外

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STARTOUT, START, CENTER, END, ENDOUT})
    public @interface ViewLocation {}

    @SuppressLint("StaticFieldLeak")
    private static View lastSign;//上一个标记
    private static final String TAG = Popup.class.getSimpleName();
    /*************************************成员变量**************************************************/
    private final Context context;
    private final View parentView;//弹窗目标View
    private final FrameLayout rootView;//弹窗根布局
    private final int[] location = {CENTER, STARTOUT};//位置
    private final int[] deviation = {0, 0};//偏移
    private final float[] animD = {0.5f, 1};//动画参数
    private int margin = 16;//与目标控件的距离
    private View contentView;//内容布局
    private boolean ok;//是否已经初始化成功
    private boolean overSign;//结束标记

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public Popup(Context context) {
        this(context, null);
    }

    public Popup(View targetView) {
        this(targetView.getContext(), targetView);
    }

    private Popup(Context context, View view) {
        super(-2, -2);
        this.context = context;
        this.parentView = view;
        this.ok = checkSign();
        this.rootView = createLayout();
        if (ok) setContentView(this.rootView);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true, true);
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    @Override
    public void dismiss() {
        if (!ok) return;
        endAnim();
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 显示气泡弹窗，并将箭头指向目标
     */
    public void show() {
        if (!ok || isShowing()) dismiss();
        if (parentView != null) {
            calculation();
            setAnimationStyle(0);
            startAnim();
            showAsDropDown(parentView, deviation[0], deviation[1]);
        }
        showAtLocation((IBinder) null, Gravity.CENTER, 0, (int) (ULay.getHeight(context) * 0.25f));
    }

    /**
     * 设置弹窗位置(水平,垂直)
     * show后无效
     */
    public void setLocation(@ViewLocation int hLocation, @ViewLocation int vLocation) {
        if (!ok || isShowing()) return;
        this.location[0] = hLocation;
        this.location[1] = vLocation;
    }

    /**
     * 设置与目标控件的距离
     * 位置为out才有效
     * show后无效
     */
    public void setMargin(int margin) {
        if (!ok || isShowing()) return;
        this.margin = margin;
    }


    /**
     * 设置关闭方式
     * 气泡点击,气泡外点击
     * 设置自定义控件事件监听请关闭气泡触摸
     */
    public void setCancelable(boolean touchOff, boolean outTouchOff) {
        if (!ok) return;
        this.rootView.setOnClickListener(touchOff ? v -> dismiss() : null);
        setOutsideTouchable(outTouchOff);
        setFocusable(outTouchOff);
    }

    /**
     * 设置内容布局
     */
    public void setContent(@LayoutRes int resId) {
        this.setContent(LayoutInflater.from(context).inflate(resId, null));
    }

    /**
     * 设置内容布局
     */
    public void setContent(View contentView) {
        if (!ok) return;
        this.contentView = contentView;
        rootView.removeAllViews();
        rootView.addView(contentView);
        super.setContentView(this.rootView);
    }

    /**
     * 设置纯文本内容布局
     * 方便连调
     */
    public Popup setContent(CharSequence text) {
        TextView tv = new MaterialTextView(context);
        tv.setText(text);
        setContent(tv);
        return this;
    }

    /**
     * 获取设置内容
     */
    public <T extends View> T getContent() {
        return (T) this.contentView;
    }

    /**
     * 设置背景色
     */
    public void setBackground(@ColorInt int color) {
        rootView.setBackgroundColor(color);
    }

    /**
     * 设置背景图
     */
    public void setBackground(Drawable drawable) {
        rootView.setBackground(drawable);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /*** 创建弹窗根布局 ***/
    private FrameLayout createLayout() {
        FrameLayout cv = new FrameLayout(context);
        V.FL(cv).size(-2, -2).paddingDP(8).backgroundRes(R.drawable.bg_popup).refresh();
        return cv;
    }


    /*** 检查是否合法 ***/
    private boolean checkSign() {
        if (lastSign == null || this.parentView != lastSign) {
            lastSign = this.parentView;
            return true;
        } else return false;
    }

    /*** 计算偏移 ***/
    private void calculation() {
        rootView.measure(getWidth(), getHeight());
        int w = rootView.getMeasuredWidth();
        int h = rootView.getMeasuredHeight();
        int tw = parentView.getWidth();
        int th = parentView.getHeight();
        switch (location[0]) {
            case STARTOUT:
                deviation[0] = w * -1 - margin;
                animD[0] = w;
                break;
            case START:
                deviation[0] = 0;
                animD[0] = 0f;
                break;
            case CENTER:
                deviation[0] = (int) ((tw - w) * 0.5f);
                animD[0] = w * 0.5f;
                break;
            case END:
                deviation[0] = tw - w;
                animD[0] = w;
                break;
            case ENDOUT:
                deviation[0] = tw + margin;
                animD[0] = 0f;
                break;
        }
        switch (location[1]) {
            case STARTOUT:
                deviation[1] = (th + h) * -1 - margin;
                animD[1] = h;
                break;
            case START:
                deviation[1] = th * -1;
                animD[1] = 0f;
                break;
            case CENTER:
                deviation[1] = (int) ((th + h) * -0.5);
                animD[1] = h * 0.5f;
                break;
            case END:
                deviation[1] = h * -1;
                animD[1] = h;
                break;
            case ENDOUT:
                deviation[1] = margin;
                animD[1] = 0f;
                break;
        }
    }

    private void startAnim() {
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, animD[0], animD[1]);
        sa.setDuration(100);
        sa.setInterpolator(new AccelerateDecelerateInterpolator());
        sa.setAnimationListener((A.EndV) animation -> rootView.clearAnimation());
        rootView.startAnimation(sa);
    }

    private void endAnim() {
        if (overSign) return;
        overSign = true;
        ScaleAnimation ea = new ScaleAnimation(1, 0, 1, 0, animD[0], animD[1]);
        ea.setDuration(100);
        ea.setInterpolator(new AccelerateDecelerateInterpolator());
        ea.setAnimationListener((A.EndV) animation -> {
            super.dismiss();
            lastSign = null;
            rootView.clearAnimation();
        });
        rootView.startAnimation(ea);
    }
}
