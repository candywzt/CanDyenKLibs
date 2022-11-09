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
import candyenk.android.tools.V;
import candyenk.android.view.LoadView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashSet;


/**
 * <h1>呼吸加载弹窗</h1>
 */
public class DialogLoading extends AlertDialog {
    private static final String TAG = DialogLoading.class.getSimpleName();
    private static final HashSet<View> mList = new HashSet<>();//拉起的控件列表

    private final Context context;//拉起弹窗的Context
    private final View parentView;//调用上拉弹窗的View对象
    private boolean ok;//是否初始化成功
    private FrameLayout dialogView;//弹窗布局对象
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
        this.ok = mList.add(view);
        if (this.ok) initLayout();
    }
    /**********************************************************************************************/
    /*************************************重写方法**************************************************/
    /**********************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(dialogView);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadView.start();
    }

    /**
     * 拉起弹窗
     */
    public void show() {
        if (ok) super.show();
        else Log.e(TAG, "拦截重复调用");
    }


    public void dismiss() {
        mList.remove(this.parentView);
        loadView.dismiss();
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    private void initLayout() {
        dialogView = new FrameLayout(context);
        dialogView.setOnClickListener(v -> dismiss());
        V.LP(dialogView).sizeDP(-1, -2).backgroundRes(android.R.color.transparent).refresh();

        CardView cv = new MaterialCardView(context);
        V.FL(cv).sizeDP(180, -1).lGravity(Gravity.CENTER).backgroundRes(R.color.back_view).radiusDP(20).parent(dialogView).refresh();

        loadView = new LoadView(context);
        loadView.setCloseListener(DialogLoading.super::dismiss);
        loadView.setOnClickListener(v -> {});
        V.FL(loadView).sizeDP(180).lGravity(Gravity.CENTER_HORIZONTAL).parent(cv).refresh();

        titleView = new MaterialTextView(context);
        V.FL(titleView).size(-2, -2).lGravity(Gravity.BOTTOM | Gravity.CENTER).marginDP(0, 180, 0, 10).textColorRes(R.color.text_main).textSize(18).parent(cv).hide().refresh();

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
}

