package candyenk.android.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import candyenk.android.tools.L;
import candyenk.java.utils.UData;

import java.util.function.Consumer;


/**
 * 确认弹窗
 */
public class DialogBottomConfirm extends DialogBottomTips {
    /*************************************成员变量**************************************************/
    protected long confirmTime;
    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /**
     * 操作回应回调
     */
    public interface OnEventCallBack {
        void onYse();

        void onNo();
    }

    public interface Yes extends OnEventCallBack {
        default void onNo() {}
    }
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
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
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
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
        L.e(TAG, "不支持的操作" + TAG + ".setLeftButton(CharSequence,Consumer,Consumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setRightButton(CharSequence text, Consumer<? extends DialogBottom> rightClick, Consumer<? extends DialogBottom> rightLong) {
        L.e(TAG, "不支持的操作" + TAG + ".setRightButton(CharSequence,Consumer,Consumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        L.e(TAG, "不支持的操作" + TAG + ".setOnCancelListener(OnCancelListener)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void dismiss(Consumer<? extends DialogBottom> dismissRun) {
        L.e(TAG, "不支持的操作" + TAG + ".dismiss(Consumer)");
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置确认时间
     * 时间结束前无法点击确认
     * show之后无效
     */
    public void setConfirmTime(long time) {
        if (!isShowing()) confirmTime = time;
    }

    /**
     * 设置确认回应回调
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

}

