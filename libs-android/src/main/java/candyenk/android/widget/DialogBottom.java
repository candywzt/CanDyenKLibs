package candyenk.android.widget;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.StringRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import candyenk.android.R;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.android.utils.ULay;
import candyenk.android.utils.USDK;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.Consumer;


/**
 * 自定义上拉弹窗
 * 拦截层叠创建
 * 四次重构
 */
public class DialogBottom extends BottomSheetDialog {
    /*************************************静态变量**************************************************/
    @SuppressLint("StaticFieldLeak")
    private static View lastSign;//重复标记
    /*************************************成员变量**************************************************/
    protected String TAG;
    protected Context context; //拉起弹窗的Activity
    protected Context viewContext;
    protected View targetView; //调用上拉弹窗的View对象
    protected boolean ok;//是否已经初始化成功
    protected CardView dialogView;//弹窗布局对象
    protected LinearLayout parentView;//内容父级
    protected TextView titleView;  //标题控件
    protected ImageView closeView;  //关闭控件
    protected Button leftButton, rightButton;  //左右按钮控件
    protected View centerView;//按钮中间分割区域
    protected LinearLayout buttonGroup;//按钮父级控件
    protected final ListenerSave ls;//事件监听
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 会自动阻止多重点击,不过避免使用null
     */
    public DialogBottom(Context context) {
        this(context, null);
    }

    public DialogBottom(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottom(Context context, View view) {
        super(context, R.style.Theme_CDK_Dialog);
        this.TAG = this.getClass().getSimpleName();
        this.context = context;
        this.viewContext = new ContextThemeWrapper(context, R.style.Theme_CDK);
        this.targetView = view;
        this.ls = new ListenerSave();
        super.setOnDismissListener(ls::OnDismissListener);
        this.ok = checkSign();
        if (!ok) L.e(TAG, "弹窗创建重复");
        if (ok) initLayout();
        if (USDK.O() && context instanceof Service) {//悬浮窗模式(API26)
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setContentView(View view) {
        L.e(TAG, "不支持的操作" + TAG + ".setContentView(View)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setContentView(int layoutResId) {
        L.e(TAG, "不支持的操作" + TAG + ".setContentView(int)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        L.e(TAG, "不支持的操作" + TAG + ".setContentView(View,ViewGroup.LayoutParams)");
    }


    @Override
    protected void onStart() {
        super.onStart();
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);//解决横屏显示不全
    }

    @Override
    public void show() {
        if (!ok || isShowing()) return;
        super.show();
    }

    @Override
    public void dismiss() {
        lastSign = null;
        super.dismiss();
    }

    protected float dp2px(float dp) {
        return ULay.dp2px(context, dp);
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 带监听关闭
     *
     * @param dismissRun 弹窗关闭后触发的事件
     */
    public void dismiss(Consumer<? extends DialogBottom> dismissRun) {
        this.ls.dismissRun = dismissRun;
        dismiss();
    }

    /**
     * 设置上拉弹窗底部左侧按钮
     * 监听事件为空则不显示按钮
     * text为null则显示默认文本
     */
    public void setLeftButton(CharSequence text, Consumer<? extends DialogBottom> leftClick, Consumer<? extends DialogBottom> leftLong) {
        if (!ok) return;
        this.ls.leftClick = leftClick;
        this.ls.leftLong = leftLong;
        this.leftButton.setText(text == null ? context.getText(R.string.yes) : text);
        if (leftClick == null && leftLong == null) {
            V.hide(leftButton, centerView);
            if (V.isHide(rightButton)) V.hide(buttonGroup);
        } else {
            V.visible(leftButton, buttonGroup);
            if (V.isVisible(rightButton)) V.visible(centerView);
        }
    }

    /**
     * 设置上拉弹窗底部右侧按钮
     * 监听事件为空则不显示按钮
     * text为null则显示默认文本
     */
    public void setRightButton(CharSequence text, Consumer<? extends DialogBottom> rightClick, Consumer<? extends DialogBottom> rightLong) {
        if (!ok) return;
        this.ls.rightClick = rightClick;
        this.ls.rightLong = rightLong;
        this.rightButton.setText(text == null ? context.getText(R.string.no) : text);
        if (rightClick == null && rightLong == null) {
            V.hide(rightButton, centerView);
            if (V.isHide(leftButton)) V.hide(buttonGroup);
        } else {
            V.visible(rightButton, buttonGroup);
            if (V.isVisible(leftButton)) V.visible(centerView);
        }
    }

    /**
     * 设置弹窗外触关闭和返回键关闭
     * 默认 true,true
     */
    public void setCancelable(boolean touchOff, boolean backOff) {
        if (!ok) return;
        setCanceledOnTouchOutside(touchOff);
        setCancelable(backOff);
    }

    /**
     * 设置标题文本
     * 调用该方法以显示标题栏
     */
    public void setTitle(@StringRes int resId) {
        this.setTitle(context.getText(resId));
    }

    public void setTitle(CharSequence title) {
        if (!ok) return;
        if (title == null) {
            titleView.setVisibility(View.GONE);
        } else {
            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置标题是否居中,默认false
     * 需设置标题内容,否则不显示
     */
    public void setTitleCenter(boolean isCenter) {
        if (!ok) return;
        V.LL(titleView).gravity(isCenter ? Gravity.CENTER : Gravity.START).paddingDP(isCenter ? 0 : 20, 0, 0, 0).refresh();
    }

    /**
     * 设置是否显示右上角关闭按钮,默认false
     */
    public void setShowClose(boolean isShow) {
        if (!ok) return;
        closeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置内容-自定义适配器
     * 拉起弹窗后不可修改
     */
    public void setContent(View view) {
        if (!ok) return;
        V.LL(view).weight(1).parent(parentView, 1);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建弹窗根布局 ***/
    @SuppressLint("RtlHardcoded")
    private void initLayout() {
        dialogView = new MaterialCardView(viewContext);
        V.LL(dialogView).size(-1, -2).backgroundRes(R.color.back_all).radiusDP(20).refresh();

        ImageView iv = new AppCompatImageView(viewContext);
        V.FL(iv).sizeDP(-1, 120).drawable(R.drawable.background_transparent_gradual_change).scaleType(ImageView.ScaleType.FIT_XY).parent(dialogView);

        parentView = new LinearLayout(viewContext);
        V.FL(parentView).size(-1, -2).paddingDP(0, 20, 0, 0).orientation(1).parent(dialogView);

        titleView = new MaterialTextView(viewContext);
        V.LL(titleView).size(-1, -2).textSize(20).textColorRes(R.color.text_title).hide().paddingDP(20, 0, 0, 0).parent(parentView);

        closeView = new ImageView(viewContext);
        V.FL(closeView).sizeDP(40, 40).lGravity(Gravity.TOP | Gravity.RIGHT).hide().drawable(R.drawable.ic_close).paddingDP(0, 10, 10, 0).parent(dialogView);
        closeView.setOnClickListener(v -> dismiss());


        buttonGroup = new LinearLayout(viewContext);
        V.LL(buttonGroup).size(-1, -2).orientation(0).paddingDP(20, 0, 20, 20).hide().parent(parentView);

        leftButton = new MaterialButton(viewContext);
        V.LL(leftButton).sizeDP(-1, 50).weight(1).text(R.string.yes).hide().parent(buttonGroup);
        leftButton.setOnClickListener(this.ls::OnLeftClick);
        leftButton.setOnLongClickListener(this.ls::OnLiftLong);

        centerView = new View(viewContext);
        V.LL(centerView).sizeDP(40, 50).hide().parent(buttonGroup);

        rightButton = new MaterialButton(viewContext, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        V.LL(rightButton).sizeDP(-1, 50).weight(1).text(R.string.no).hide().parent(buttonGroup);
        rightButton.setOnClickListener(this.ls::OnRightClick);
        rightButton.setOnLongClickListener(this.ls::OnRightLong);
        super.setContentView(dialogView);
    }

    /*** 检查是否合法 ***/
    private boolean checkSign() {
        if (lastSign == null || this.targetView != lastSign) {
            lastSign = this.targetView;
            return true;
        } else return false;
    }
    /**********************************************************************************************/
    /*******************************************内部类**********************************************/
    /**********************************************************************************************/
    /*** 保存各种监听的类 ***/
    private class ListenerSave {
        private Consumer dismissRun;
        private Consumer leftClick;
        private Consumer leftLong;
        private Consumer rightClick;
        private Consumer rightLong;

        /*** 弹窗结束监听 ***/
        private void OnDismissListener(DialogInterface di) {
            if (dismissRun != null) dismissRun.accept(DialogBottom.this);
        }

        /*** 左按钮点按监听 ***/
        private void OnLeftClick(View v) {
            if (leftClick != null) leftClick.accept(DialogBottom.this);
        }

        /*** 左按钮长按监听 ***/
        private boolean OnLiftLong(View v) {
            if (leftLong != null) leftLong.accept(DialogBottom.this);
            return true;
        }

        /*** 右按钮点按监听 ***/
        private void OnRightClick(View v) {
            if (rightClick != null) rightClick.accept(DialogBottom.this);
        }

        /*** 右按钮长按监听 ***/
        private boolean OnRightLong(View v) {
            if (rightLong != null) rightLong.accept(DialogBottom.this);
            return true;
        }


    }
}

