package candyenk.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import candyenk.android.R;
import candyenk.android.tools.V;
import candyenk.java.utils.UData;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.Consumer;

/**
 * 底部弹窗-确认弹窗
 */
public class DialogBottomConfirm extends DialogBottomView {
    protected long time;
    private TextView textView;
    private ImageView imageView;
    
    public DialogBottomConfirm(Context context) {
        this(context, null);
    }
    
    public DialogBottomConfirm(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomConfirm(Context context, View view) {
        super(context, view);
        if (ok) initLayout();
    }
    
    @Override
    public void show() {
        if (!ok || isShowing()) return;
        super.show();
        if (isShowing() && time != 0) {
            leftButton.setEnabled(false);
            Handler h = new Handler(Looper.getMainLooper());
            new Thread(() -> {
                CharSequence text = leftButton.getText();
                while (isShowing() && time > 0) {
                    try {
                        h.post(() -> {
                            if (isShowing()) leftButton.setText(UData.L2A(time / 1000, "s", "%d"));
                        });
                        Thread.sleep(1000);
                        time -= 1000;
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
    public void setOnCancelListener(OnCancelListener listener) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void dismiss(Consumer<? extends DialogBottom> dismissRun) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * 设置图标
     *
     * @param resId 样式ID
     */
    public void setIcon(@DrawableRes int resId) {
        imageView.setImageResource(resId);
    }
    
    /**
     * 设置图标
     *
     * @param drawable 图标
     */
    public void setIcon(Drawable drawable) {
        imageView.setImageDrawable(drawable);
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
     * 设置等待时间
     * 时间结束前无法继续操作
     * 默认0
     *
     * @param time 等待时间
     */
    public void setTime(long time) {
        if (!isShowing()) this.time = time;
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
    
    /*** 初始化布局 ***/
    private void initLayout() {
        LinearLayout layout = new LinearLayout(context);
        V.RV(layout).size(-1, -2).orientation(1).gravity(Gravity.CENTER).paddingDP(0).refresh();
        
        imageView = new ImageView(context);
        V.LL(imageView).sizeDP(128).paddingDP(12).drawable(R.drawable.ic_ok).parent(layout);
        
        textView = new MaterialTextView(context);
        V.LL(textView).size(-1, -2).paddingDP(12).textSize(16).hide().parent(layout);
        super.setContent(layout);
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

