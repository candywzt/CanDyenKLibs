package candyenk.android.widget;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import candyenk.android.view.LoadView;

import java.util.HashSet;


/**
 * <h1>呼吸加载弹窗</h1>
 */
public class DialogLoading extends AlertDialog {
    private static final String TAG = DialogLoading.class.getSimpleName();
    private static final HashSet<View> mList = new HashSet<>();//拉起的控件列表

    private Context context;//拉起弹窗的Context
    private View parentView;//调用上拉弹窗的View对象
    private boolean isShow;//是否已经展示
    private LinearLayout dialogView;//弹窗布局对象
    private TextView titleView; //标题文字控件
    private LoadView loadView;//加载动画控件


    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public DialogLoading(Context context) {
        this(context, null);
    }

    /**
     * 构造函数
     * 无法使用同一View拉起多个Dialog,可使用null拉起一个dialog
     *
     * @param context 拉起弹窗的活动
     * @param view    拉起弹窗的View
     */
    public DialogLoading(Context context, View view) {
        super(context, 0);
        this.context = context;
        this.parentView = view;
        this.isShow = !mList.add(view);
        if (!this.isShow) {
            initLayout();
        }
    }
    /**********************************************************************************************/
    /*************************************重写方法**************************************************/
    /**********************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        this.isShow = true;
        //添加布局
        setContentView(dialogView);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadView.start();
    }

    /**
     * 拉起弹窗
     */
    public void show() {
        if (!isShow) {
            super.show();
        } else {
            Log.e(TAG, "拦截重复调用");
        }
    }

    /**
     * 结束并销毁弹窗
     */
    public void dismiss(OnDismissListener l) {
        if (isShow) {
            loadView.dismiss(() -> {
                if (l != null) {
                    setOnDismissListener(l);
                }
                mList.remove(this.parentView);
                super.dismiss();
            });
        }

    }

    public void dismiss() {
        this.dismiss(null);
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    private void initLayout() {
        dialogView = new LinearLayout(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp2px(220));
        dialogView.setLayoutParams(lp);
        dialogView.setGravity(Gravity.CENTER);
        dialogView.setBackgroundResource(android.R.color.transparent);

        CardView cv = new CardView(context);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(dp2px(180), -1);
        cv.setLayoutParams(lp2);
        cv.setRadius(dp2px(20));
        dialogView.addView(cv);

        loadView = new LoadView(context);
        FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(-1, -1);
        loadView.setLayoutParams(lp3);
        cv.addView(loadView);

        titleView = new TextView(context);
        FrameLayout.LayoutParams lp4 = new FrameLayout.LayoutParams(-1, -2);
        lp4.gravity = Gravity.BOTTOM;
        lp4.setMargins(0, 0, 0, dp2px(10));
        titleView.setLayoutParams(lp4);
        titleView.setTextSize(18);
        titleView.setGravity(Gravity.CENTER);
        titleView.setVisibility(View.GONE);
        cv.addView(titleView);
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }
    /**********************************************************************************************/
    /*************************************公开方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置弹窗外触关闭和返回键关闭
     * 默认 true,true
     *
     * @param touchOff 外触是否关闭
     * @param backOff  返回键是否关闭
     */
    public void setCancelable(boolean touchOff, boolean backOff) {
        if (!isShow) {
            //设置外触关闭
            setCanceledOnTouchOutside(touchOff);
            //设置返回键关闭
            setCancelable(backOff);
        }
    }

    /**
     * 设置标题文本
     * 调用该方法将显示标题栏
     * 不调用则隐藏标题栏
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        if (!isShow) {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }
    }


}

