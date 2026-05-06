package candyenk.android.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.view.*;
import android.widget.*;
import androidx.annotation.StringRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatImageView;
import candyenk.android.R;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.android.utils.ULay;
import candyenk.android.viewgroup.SmoothLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.Consumer;

/**
 * 底部弹窗组件
 * 建议使用其下继承组件
 */
public class DialogBottom extends BottomSheetDialog {
    private static View lastSign;//重复标记
    protected final ListenerSave ls;//事件监听
    protected String TAG;
    protected Context context; //拉起弹窗的Activity
    protected Context viewContext;
    protected View targetView; //调用上拉弹窗的View对象
    protected boolean ok;//是否已经初始化成功
    protected FrameLayout dialogView;//弹窗布局对象
    protected LinearLayout parentView;//内容父级
    protected TextView titleView;  //标题控件
    protected ImageView closeView;  //关闭控件
    protected Button leftButton, rightButton;  //按钮控件
    protected View centerView;//按钮中间分割区域
    protected LinearLayout buttonGroup;//按钮父级控件
    
    /**
     * 通过上下文创建DialogBottom
     *
     * @param context 上下文
     */
    public DialogBottom(Context context) {
        this(context, null);
    }
    
    /**
     * 通过View创建DialogBottom
     * 单个View仅能创建一个DialogBottom,有效防止重复点击
     *
     * @param view 用于重复标记的View
     */
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
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setContentView(int layoutResId) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setContentView(View view) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        //直接撑开到最大状态
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    
    @Override
    public void show() {
        if (!ok || isShowing()) return;
        super.show();
    }
    
    /**
     * 设置标题文本
     * 默认不显示
     *
     * @param title 标题文本,为null则不显示标题栏
     */
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
     * 设置标题文本
     *
     * @param resId 标题资源ID
     */
    public void setTitle(@StringRes int resId) {
        this.setTitle(context.getText(resId));
    }
    
    @Override
    public void dismiss() {
        lastSign = null;
        super.dismiss();
    }
    
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
     * 设置弹窗底部左侧(积极)按钮
     * 默认不显示
     *
     * @param text      按钮文本,为null显示默认文本
     * @param leftClick 单击监听,为null不显示该按钮
     * @param leftLong  长按监听
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
     * 设置弹窗底部右侧(消极)按钮
     * 默认不显示
     *
     * @param text       按钮文本,为null显示默认文本
     * @param rightClick 单击监听,为null不显示该按钮
     * @param rightLong  长按监听
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
     * 设置弹窗关闭方式
     *
     * @param touchOff 是否允许外部触摸关闭,默认true
     * @param backOff  是否允许返回键关闭,默认true
     */
    public void setCancelable(boolean touchOff, boolean backOff) {
        if (!ok) return;
        setCanceledOnTouchOutside(touchOff);
        setCancelable(backOff);
    }
    
    /**
     * 设置标题文本居中还是居左
     *
     * @param isCenter 是否居中
     */
    public void setTitleCenter(boolean isCenter) {
        if (!ok) return;
        V.LL(titleView).gravity(isCenter ? Gravity.CENTER : Gravity.START).paddingDP(isCenter ? 0 : 20, 0, 0, 0)
         .refresh();
    }
    
    /**
     * 设置右上角关闭按钮是否显示
     *
     * @param isShow 是否显示,默认false
     */
    public void setShowClose(boolean isShow) {
        if (!ok) return;
        closeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
    
    /**
     * 设置内容
     * 拉起弹窗后不可修改
     *
     * @param view 自定义内容
     */
    public void setContent(View view) {
        if (!ok) return;
        V.LL(view).weight(1).parent(parentView, 1);
    }
    
    /**
     * 设置弹窗优先级最高
     * 弹窗将悬浮在所有Activity之上
     * 需要悬浮窗权限，没有就报错
     */
    public void setOverlay() {
        Window window = getWindow();
        if (window == null) return;
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
    }
    
    protected float dp2px(float dp) {
        return ULay.dp2px(context, dp);
    }
    
    /*** 创建弹窗根布局 ***/
    private void initLayout() {
        //根级控件,圆角卡片
        dialogView = new SmoothLayout(viewContext);
        V.LL(dialogView).size(-1, -2).backgroundRes(R.color.back_all).refresh();
        //顶栏背景
        ImageView iv = new AppCompatImageView(viewContext);
        V.FL(iv).sizeDP(-1, 120).drawable(R.drawable.bg_transparent_gradual_change)
         .scaleType(ImageView.ScaleType.FIT_XY).parent(dialogView);
        //内容父控件
        parentView = new LinearLayout(viewContext);
        V.FL(parentView).size(-1, -2).paddingDP(0, 20, 0, 0).orientation(1).parent(dialogView);
        //标题控件
        titleView = new MaterialTextView(viewContext);
        V.LL(titleView).size(-1, -2).textSize(20).textColorRes(R.color.text_title).hide().paddingDP(20, 0, 0, 0)
         .parent(parentView);
        //关闭按钮
        closeView = new ImageView(viewContext);
        V.FL(closeView).sizeDP(40, 40).lGravity(Gravity.TOP | Gravity.RIGHT).hide().drawable(R.drawable.ic_close)
         .paddingDP(0, 10, 10, 0).parent(dialogView);
        closeView.setOnClickListener(v -> dismiss());
        //底部按钮组
        buttonGroup = new LinearLayout(viewContext);
        V.LL(buttonGroup).size(-1, -2).orientation(0).paddingDP(20, 0, 20, 20).hide().parent(parentView);
        //左按钮
        leftButton = new MaterialButton(viewContext);
        V.LL(leftButton).sizeDP(-1, 50).weight(1).text(R.string.yes).hide().parent(buttonGroup);
        leftButton.setOnClickListener(this.ls::OnLeftClick);
        leftButton.setOnLongClickListener(this.ls::OnLiftLong);
        //中间隔层
        centerView = new View(viewContext);
        V.LL(centerView).sizeDP(40, 50).hide().parent(buttonGroup);
        //右按钮
        rightButton = new MaterialButton(viewContext, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        V.LL(rightButton).sizeDP(-1, 50).weight(1).text(R.string.no).hide().parent(buttonGroup);
        rightButton.setOnClickListener(this.ls::OnRightClick);
        rightButton.setOnLongClickListener(this.ls::OnRightLong);
        super.setContentView(dialogView);
        //删除预见式返回动画
        getOnBackInvokedDispatcher().registerOnBackInvokedCallback(0, this::dismiss);
    }
    
    /*** 检查是否合法 ***/
    private boolean checkSign() {
        if (lastSign == null || this.targetView != lastSign) {
            lastSign = this.targetView;
            return true;
        } else return false;
    }
    
    /*** 保存各种监听的类 ***/
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected class ListenerSave {
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

