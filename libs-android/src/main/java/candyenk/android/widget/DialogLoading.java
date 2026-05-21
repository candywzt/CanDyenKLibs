package candyenk.android.widget;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import candyenk.android.R;
import candyenk.android.graphics.Gradient;
import candyenk.android.tools.RC;
import candyenk.android.tools.V;
import candyenk.android.view.LoadView;
import candyenk.android.viewgroup.SmoothLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * <h1>呼吸加载弹窗</h1>
 */
public class DialogLoading extends AlertDialog {
    private static final String TAG = DialogLoading.class.getSimpleName();
    private static final HashSet<View> mList = new HashSet<>();//拉起的控件列表
    private final Context context;//拉起弹窗的Context
    private final View sign;//调用上拉弹窗的View对象
    private final boolean ok;//是否初始化成功
    private final AtomicReference<Consumer<DialogLoading>> dl = new AtomicReference<>();//结束监听
    private FrameLayout dialogView;//弹窗布局对象
    private TextView titleView; //标题文字控件
    private LoadView loadView;//加载动画控件
    private RC rc;//线程执行事件
    
    public DialogLoading(Context context) {
        this(context, null);
    }
    
    public DialogLoading(View view) {
        this(view.getContext(), view);
    }
    
    public DialogLoading(Context context, View view) {
        super(context, 0);
        this.context = context;
        this.sign = view;
        super.setOnDismissListener(d -> {
            if (dl.get() != null) dl.get().accept(DialogLoading.this);
        });
        this.ok = mList.add(view);
        if (this.ok) initLayout();
        else Log.e(TAG, "拦截重复调用");
    }
    
    @Override
    public void dismiss() {
        mList.remove(this.sign);
        loadView.dismiss(l -> DialogLoading.super.dismiss());
    }
    
    /**
     * 设置标题文本
     * 调用该方法将显示标题栏
     * 不调用则隐藏标题栏
     *
     * @param title 标题文本
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setCancelable(boolean flag) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    @Override
    public void show() {
        if (!ok | isShowing()) return;
        super.show();
        if (rc != null) rc.runThread();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        setContentView(dialogView);
        Window window = getWindow();
        if (window != null) window.setBackgroundDrawableResource(android.R.color.transparent);
        loadView.start();
    }
    
    /**
     * 关闭弹窗
     *
     * @param dismissRun 关闭后监听
     */
    public void dismiss(Consumer<DialogLoading> dismissRun) {
        this.dl.set(dismissRun);
        dismiss();
    }
    
    /**
     * 设置加载异步线程操作
     * 子线程代码运行结束后自动关闭弹窗并回调
     * 关闭弹窗之后才会回调!!!
     * sign返回ERROR时msg为Exception!!!
     *
     * @param rc 线程操作
     */
    public void setThreadRun(RC rc) {
        if (!ok || isShowing()) return;
        this.rc = rc;
        RC.ResultCB<Object> result = rc.result();
        rc.result(o -> dismiss(d -> result.call(o)));
    }
    
    /**
     * 设置弹窗外触关闭和返回键关闭
     * 默认 true,true
     *
     * @param touchOff 外触是否关闭
     * @param backOff  返回键是否关闭
     */
    public void setCanceledable(boolean touchOff, boolean backOff) {
        super.setCanceledOnTouchOutside(touchOff);
        super.setCancelable(backOff);
        dialogView.setOnClickListener(touchOff ? v -> dismiss() : null);
    }
    
    /*** 创建布局 ***/
    private void initLayout() {
        dialogView = new SmoothLayout(context);
        dialogView.setOnClickListener(v -> dismiss());
        V.FL(dialogView).sizeDP(180, 200).lGravity(Gravity.CENTER).background(Gradient.DEFAULT(context))
         .refresh();
        loadView = new LoadView(context);
        loadView.setOnClickListener(v -> {});
        V.FL(loadView).sizeDP(180).lGravity(Gravity.CENTER_HORIZONTAL).parent(dialogView);
        
        titleView = new MaterialTextView(context);
        V.FL(titleView).size(-2, -2).lGravity(Gravity.BOTTOM | Gravity.CENTER).paddingDP(0, 0, 0, 10)
         .textColorRes(R.color.text_main).textSize(18).hide().parent(dialogView);
        
    }
    
}

