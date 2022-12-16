package candyenk.android.widget;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import candyenk.android.R;
import candyenk.android.tools.L;
import candyenk.android.tools.RC;
import candyenk.android.tools.V;
import candyenk.android.view.LoadView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


/**
 * <h1>呼吸加载弹窗</h1>
 */
public class DialogLoading extends AlertDialog {
    /*************************************静态变量**************************************************/
    private static final String TAG = DialogLoading.class.getSimpleName();
    private static final HashSet<View> mList = new HashSet<>();//拉起的控件列表
    /*************************************成员变量**************************************************/
    private final Context context;//拉起弹窗的Context
    private final View parentView;//调用上拉弹窗的View对象
    private final boolean ok;//是否初始化成功
    private final AtomicReference<Consumer> dl = new AtomicReference<>();//结束监听
    private FrameLayout dialogView;//弹窗布局对象
    private TextView titleView; //标题文字控件
    private LoadView loadView;//加载动画控件
    private RC rc;//线程执行事件
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public DialogLoading(Context context) {
        this(context, null);
    }

    public DialogLoading(View view) {
        this(view.getContext(), view);
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
        super.setOnDismissListener(d -> {
            if (dl.get() != null) dl.get().accept(DialogLoading.this);
        });
        this.ok = mList.add(view);
        if (this.ok) initLayout();
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        setContentView(dialogView);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadView.start();
    }


    @Override
    public void show() {
        if (ok) {
            super.show();
            if (rc != null) rc.runThread();
        } else Log.e(TAG, "拦截重复调用");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        L.e(TAG, "不支持的操作" + TAG + ".setOnDismissListener(OnDismissListener)");
    }

    @Override
    public void dismiss() {
        mList.remove(this.parentView);
        loadView.dismiss(l -> DialogLoading.super.dismiss());
    }

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 带监听关闭
     */
    public void dismiss(Consumer<? extends DialogLoading> dismissRun) {
        this.dl.set(dismissRun);
        dismiss();
    }

    /**
     * 设置加载中线程操作
     * 子线程代码运行结束后自动关闭弹窗并回调
     * 关闭弹窗之后才会回调!!!
     * sign返回ERROR时msg为Exception!!!
     */
    public void setThreadRun(RC rc) {
        if (!ok || isShowing()) return;
        this.rc = new RC(rc.run, (sign, msg) -> {
            if (sign == RC.SIGN_RETURN) dismiss(d -> rc.runCall(sign, msg));
            else rc.runCall(sign, msg);
        });
    }

    /**
     * 设置弹窗外触关闭和返回键关闭
     * 默认 true,true
     *
     * @param touchOff 外触是否关闭
     * @param backOff  返回键是否关闭
     */
    public void setCanceledable(boolean touchOff, boolean backOff) {
        setCanceledOnTouchOutside(touchOff);
        setCancelable(backOff);
        dialogView.setOnClickListener(touchOff ? v -> dismiss() : null);
    }

    /**
     * 设置标题文本
     * 调用该方法将显示标题栏
     * 不调用则隐藏标题栏
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    private void initLayout() {
        dialogView = new FrameLayout(context);
        dialogView.setOnClickListener(v -> dismiss());
        V.LP(dialogView).sizeDP(-1, -2).backgroundRes(android.R.color.transparent).refresh();

        CardView cv = new MaterialCardView(context);
        V.FL(cv).sizeDP(180, 200).lGravity(Gravity.CENTER).backgroundRes(R.color.back_view).radiusDP(20).parent(dialogView);

        loadView = new LoadView(context);
        loadView.setOnClickListener(v -> {});
        V.FL(loadView).sizeDP(180).lGravity(Gravity.CENTER_HORIZONTAL).parent(cv);

        titleView = new MaterialTextView(context);
        V.FL(titleView).size(-2, -2).lGravity(Gravity.BOTTOM | Gravity.CENTER).paddingDP(0, 0, 0, 10).textColorRes(R.color.text_main).textSize(18).hide().parent(cv);

    }

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
}

