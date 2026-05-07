package candyenk.android.widget;

import android.content.Context;
import android.view.View;

import java.util.function.Consumer;

/**
 * 底部弹窗-提示弹窗
 * 3秒后自动关闭
 */
public class DialogBottomTips extends DialogBottomConfirm {
    
    public DialogBottomTips(Context context) {
        this(context, null);
    }
    
    public DialogBottomTips(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomTips(Context context, View view) {
        super(context, view);
        super.time = 3000;
        super.setCancelable(false, true);
    }
    
    @Override
    public void show() {
        if (!ok || isShowing()) return;
        super.show();
        if (isShowing() && time != 0) {
            new Thread(() -> {
                try {
                    Thread.sleep(time);
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
    public void setLeftButton(CharSequence text, Consumer<? extends DialogBottom> leftClick, Consumer<? extends DialogBottom> leftLong) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setRightButton(CharSequence text, Consumer<? extends DialogBottom> rightClick, Consumer<? extends DialogBottom> rightLong) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
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
}

