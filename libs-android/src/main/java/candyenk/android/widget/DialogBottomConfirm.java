package candyenk.android.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.asbc.HolderCDK;
import candyenk.android.tools.L;
import candyenk.java.utils.UData;

import java.util.function.BiConsumer;
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
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int viewid) {
        L.e(TAG, "不支持的操作DialogBottomConfirm.setContent(int)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(View view) {
        L.e(TAG, "不支持的操作DialogBottomConfirm.setContent(View)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        L.e(TAG, "不支持的操作DialogBottomConfirm.setContent(RecyclerView.Adapter)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(View... views) {
        L.e("TAG", "不支持的操作DialogBottomConfirm.setContent(View...)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int... resIds) {
        L.e("TAG", "不支持的操作DialogBottomConfirm.setContent(int...)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int resId, int count) {
        L.e("TAG", "不支持的操作DialogBottomConfirm.setContent(int,int)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        L.e("TAG", "不支持的操作DialogBottomConfirm.setLayoutManager(RecyclerView.LayoutManager)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        L.e("TAG", "不支持的操作DialogBottomConfirm.setOnBindViewHolder(Consumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不支持的操作DialogBottomConfirm.setOnItemClickListener(BiConsumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不支持的操作DialogBottomConfirm.setOnItemLongClickListener(BiConsumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public <T extends View> T getContentView() {
        return L.e(TAG, "不支持的操作DialogBottomConfirm.getContentView()", null);
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnButtonClickListener(View.OnClickListener left, View.OnClickListener right) {
        L.e(TAG, "不支持的操作DialogBottomConfirm.setOnButtonClickListener(View.OnClickListener,View.OnClickListener)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setCancelable(boolean touchOff, boolean backOff) {
        L.e(TAG, "不支持的操作DialogBottomConfirm.setCancelable(boolean,boolean)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setTitleCenter(boolean isCenter) {
        L.e(TAG, "不支持的操作DialogBottomConfirm.setTitleCenter(boolean)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setShowClose(boolean isShow) {
        L.e(TAG, "不支持的操作DialogBottomConfirm.setShowClose(boolean)");
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
                dismiss();
                callback.onYse();
            }, v -> {
                dismiss();
                callback.onNo();
            });
            setOnCancelListener(d -> callback.onNo());
        }
    }

}

