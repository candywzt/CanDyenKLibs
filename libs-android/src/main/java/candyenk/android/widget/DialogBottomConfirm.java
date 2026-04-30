package candyenk.android.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import candyenk.android.tools.L;
import candyenk.java.utils.UData;

import java.util.function.Consumer;


/**
 * 底部弹窗-确认弹窗
 */
public class DialogBottomConfirm extends DialogBottomTips {
    protected long confirmTime;
    
    public DialogBottomConfirm(Context context) {
        this(context, null);
    }
    
    public DialogBottomConfirm(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomConfirm(Context context, View view) {
        super(context, view);
        setTimeOut(0);
    }
    
    @Override
    public void show() {
        if (!ok || isShowing()) return;
        if (leftButton.getVisibility() == View.GONE) {
            L.e(TAG, "尚未设置回调事件");
            return;
        }
        super.show();
        if (isShowing() && confirmTime != 0) {
            leftButton.setEnabled(false);
            Handler h = new Handler(Looper.getMainLooper());
            new Thread(() -> {
                CharSequence text = leftButton.getText();
                while (isShowing() && confirmTime > 0) {
                    try {
                        h.post(() -> {
                            if (isShowing()) leftButton.setText(UData.L2A(confirmTime / 1000, "s", "%d"));
                        });
                        Thread.sleep(1000);
                        confirmTime -= 1000;
                    } catch (Exception ignored) {}
                }
                if (isShowing()) {
                    h.post(() -> {
                        leftButton.setText(text);
                        leftButton.setEnabled(true);
                    });
                    
                }
            }).start();
        }
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setLeftButton(CharSequence text, Consumer<? extends DialogBottom> leftClick, Consumer<? extends DialogBottom> leftLong) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setRightButton(CharSequence text, Consumer<? extends DialogBottom> rightClick, Consumer<? extends DialogBottom> rightLong) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void dismiss(Consumer<? extends DialogBottom> dismissRun) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * 设置确认前等待时间
     * 时间结束前无法点击确认
     * 默认0
     *
     * @param time 等待时间
     */
    public void setConfirmTime(long time) {
        if (!isShowing()) confirmTime = time;
    }
    
    /**
     * 设置回应回调
     *
     * @param callback 回应回调
     */
    public void setOnEventCallBack(OnEventCallBack callback) {
        if (callback == null) {
            super.setLeftButton(leftButton.getText(), null, null);
            super.setRightButton(null, null, null);
            super.setOnCancelListener(null);
        } else {
            super.setLeftButton(leftButton.getText(), d -> {
                super.dismiss(d1 -> callback.onYse());
            }, null);
            super.setRightButton(rightButton.getText(), d -> {
                super.dismiss(d12 -> callback.onNo());
            }, null);
            super.setOnCancelListener(d -> callback.onNo());
        }
    }
    
    /**
     * 弹窗回应回调
     */
    public interface OnEventCallBack {
        void onYse();
        
        void onNo();
    }
    
    /**
     * 弹窗仅确认回调
     */
    public interface Yes extends OnEventCallBack {
        default void onNo() {}
    }
    
}

