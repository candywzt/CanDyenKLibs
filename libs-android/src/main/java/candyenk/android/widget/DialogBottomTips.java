package candyenk.android.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.R;
import candyenk.android.asbc.HolderCDK;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * 提示弹窗
 */
public class DialogBottomTips extends DialogBottomView {
    /*************************************成员变量**************************************************/
    protected LinearLayout layout;
    protected ImageView iconView;
    protected TextView contentView;
    protected long timeout = 3000;
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
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
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
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
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int viewid) {
        L.e(TAG, "不支持的操作DialogBottomTips.setContent(int)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(View view) {
        L.e(TAG, "不支持的操作DialogBottomTips.setContent(View)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        L.e(TAG, "不支持的操作DialogBottomTips.setContent(RecyclerView.Adapter)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(View... views) {
        L.e("TAG", "不支持的操作DialogBottomTips.setContent(View...)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int... resIds) {
        L.e("TAG", "不支持的操作DialogBottomTips.setContent(int...)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int resId, int count) {
        L.e("TAG", "不支持的操作DialogBottomTips.setContent(int,int)");
    }


    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        L.e("TAG", "不支持的操作DialogBottomTips.setLayoutManager(RecyclerView.LayoutManager)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        L.e("TAG", "不支持的操作DialogBottomTips.setOnBindViewHolder(Consumer)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不支持的操作DialogBottomTips.setOnItemClickListener(BiConsumer)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不支持的操作DialogBottomTips.setOnItemLongClickListener(BiConsumer)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public <T extends View> T getContentView() {
        return L.e(TAG, "不支持的操作DialogBottomTips.getContentView()", null);
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setCancelable(boolean touchOff, boolean backOff) {
        L.e(TAG, "不支持的操作DialogBottomTips.setCancelable(boolean,boolean)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setTitleCenter(boolean isCenter) {
        L.e(TAG, "不支持的操作DialogBottomTips.setTitleCenter(boolean)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setShowClose(boolean isShow) {
        L.e(TAG, "不支持的操作DialogBottomTips.setShowClose(boolean)");
    }

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置内容文本
     */
    public void setContent(CharSequence text) {
        if (!ok) return;
        if (text == null) contentView.setVisibility(View.GONE);
        else {
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(text);
        }
    }

    /**
     * 设置图标样式
     */
    public void setIconStyle(int style) {
        //TODO:还没做
    }

    /**
     * 设置超时时间
     * 在show之前设置,否则无效
     * 1000-30000之间,或者0其余无效
     */
    public void setTimeOut(long time) {
        if (!ok || isShowing()) return;
        if (time == 0 || (time >= 1000 && time <= 30000)) timeout = time;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 初始化布局 ***/
    private void initLayout() {
        this.layout = new LinearLayout(viewContext);
        V.RL(layout).size(-1, -2).orientation(1).paddingDP(20).refresh();

        LinearLayout l1 = new LinearLayout(viewContext);
        V.LL(l1).size(-1, -2).orientation(0).gravity(Gravity.CENTER).parent(layout).refresh();

        iconView = new ImageView(viewContext);
        V.LL(iconView).sizeDP(80).drawable(R.drawable.ic_ok).parent(l1).refresh();

        titleView = new MaterialTextView(viewContext);
        V.LL(titleView).size(-1, -1).gravity(Gravity.CENTER).textSize(24).parent(l1).hide().refresh();

        contentView = new MaterialTextView(viewContext);
        V.LL(contentView).size(-1, -2).paddingDP(0, 20, 0, 0).parent(layout).hide().refresh();
        super.setContent(layout);
    }

}

