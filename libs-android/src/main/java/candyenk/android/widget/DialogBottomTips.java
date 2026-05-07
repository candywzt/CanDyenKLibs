package candyenk.android.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import candyenk.android.R;
import candyenk.android.tools.V;
import com.google.android.material.textview.MaterialTextView;

/**
 * 底部弹窗-提示弹窗
 * 3秒后自动关闭
 */
public class DialogBottomTips extends DialogBottomView {
    private TextView textView;
    private long timeout = 3000;
    
    public DialogBottomTips(Context context) {
        this(context, null);
    }
    
    public DialogBottomTips(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomTips(Context context, View view) {
        super(context, view);
        if (ok) initLayout();
    }
    
    @Override
    public void show() {
        if (!ok || isShowing()) return;
        super.show();
        if (isShowing() && timeout != 0) {
            new Thread(() -> {
                try {
                    Thread.sleep(timeout);
                } catch (Exception ignored) {}
                if (isShowing()) dismiss();
            }).start();
        }
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public <T extends View> T getContent() {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Deprecated
    @Override
    public void setContent(int viewid) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Deprecated
    @Override
    public void setContent(View view) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * 设置内容文本
     *
     * @param text 提示文本
     */
    public void setContent(CharSequence text) {
        if (!ok) return;
        if (text == null) textView.setVisibility(View.GONE);
        else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setCancelable(boolean touchOff, boolean backOff) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setTitleCenter(boolean isCenter) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setShowClose(boolean isShow) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * 设置图标样式
     *
     * @param style 样式ID
     */
    public void setIconStyle(int style) {
        //TODO:还没做
    }
    
    /**
     * 设置自动关闭
     * 在show之前设置,否则无效
     * 1000-30000之间,或者0
     *
     * @param time 关闭时间
     */
    public void setTimeOut(long time) {
        if (!ok || isShowing()) return;
        if (time == 0 || (time >= 1000 && time <= 30000)) timeout = time;
    }
    
    /*** 初始化布局 ***/
    private void initLayout() {
        LinearLayout layout = new LinearLayout(context);
        V.RV(layout).size(-1, -2).orientation(1).paddingDP(20).refresh();
        
        LinearLayout l1 = new LinearLayout(context);
        V.LL(l1).size(-1, -2).orientation(0).gravity(Gravity.CENTER).parent(layout);
        
        ImageView iconView = new ImageView(context);
        V.LL(iconView).sizeDP(80).drawable(R.drawable.ic_ok).parent(l1);
        
        TextView titleView = new MaterialTextView(context);
        V.LL(titleView).size(-1, -1).gravity(Gravity.CENTER).textSize(24).hide().parent(l1);
        
        textView = new MaterialTextView(context);
        V.LL(textView).size(-1, -2).paddingDP(0, 20, 0, 0).hide().parent(layout);
        super.setContent(layout);
    }
    
}

