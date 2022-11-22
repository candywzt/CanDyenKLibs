package candyenk.android.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.tools.L;
import candyenk.java.utils.UData;


/**
 * 确认弹窗
 */
public class DialogBottomConfirm extends DialogBottomTips {
    /*************************************静态变量**************************************************/
    /*************************************成员变量**************************************************/
    protected long confirmTime;
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

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

    public interface OnYseCallBack extends OnEventCallBack {
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

    @Override
    public void setContent(int viewid) {
        L.e(TAG, "不支持的操作DialogBottomTips.setContent(int)");
    }

    @Override
    public void setContent(View view) {
        L.e(TAG, "不支持的操作DialogBottomTips.setContent(View)");
    }

    @Override
    public <T extends View> T getContentView() {
        return L.e(TAG, "不支持的操作DialogBottomTips.getContentView()", null);
    }

    @Override
    public void setOnButtonClickListener(View.OnClickListener left, View.OnClickListener right) {
        L.e(TAG, "不支持的操作DialogBottomTips.setOnButtonClickListener(View.OnClickListener,View.OnClickListener)");
    }

    @Override
    public void setCancelable(boolean touchOff, boolean backOff) {
        L.e(TAG, "不支持的操作DialogBottomTips.setCancelable(boolean,boolean)");
    }

    @Override
    public void setTitleCenter(boolean isCenter) {
        L.e(TAG, "不支持的操作DialogBottomTips.setTitleCenter(boolean)");
    }

    @Override
    public void setShowClose(boolean isShow) {
        L.e(TAG, "不支持的操作DialogBottomTips.setShowClose(boolean)");
    }

    @Override
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        L.e(TAG, "不支持的操作DialogBottomTips.setContent(RecyclerView.Adapter)");
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
            super.setOnButtonClickListener(null, null);
            setOnCancelListener(null);
        } else {
            super.setOnButtonClickListener(v -> {
                callback.onYse();
                dismiss();
            }, v -> {
                callback.onNo();
                dismiss();
            });
            setOnCancelListener(d -> callback.onNo());
        }
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/

}

