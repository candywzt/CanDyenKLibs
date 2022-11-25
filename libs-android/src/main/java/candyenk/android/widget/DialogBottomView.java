package candyenk.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.asbc.HolderCDK;
import candyenk.android.tools.L;
import candyenk.android.tools.V;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * 自定义View上拉弹窗
 */
public class DialogBottomView extends DialogBottomItem {
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
    public DialogBottomView(Context context) {
        this(context, null);
    }

    public DialogBottomView(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottomView(Context context, View view) {
        super(context, view);
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Override
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(Adapter)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Override
    public void setContent(View... views) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(View...)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Override
    public void setContent(int... resIds) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(int...)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Override
    public void setContent(int resId, int count) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(int,int)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        L.e("TAG", "不允许使用DialogBottomView.setLayoutManager(RecyclerView.LayoutManager)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        L.e("TAG", "不允许使用DialogBottomView.setOnBindViewHolder(Consumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不允许使用DialogBottomView.setOnItemClickListener(BiConsumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不允许使用DialogBottomView.setOnItemLongClickListener(BiConsumer)");
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置内容-自定义布局
     * 拉起弹窗后不可修改
     */
    public void setContent(int viewid) {
        if (!ok) return;
        this.setContent(LayoutInflater.from(context).inflate(viewid, null));
    }

    /**
     * 设置内容-自定义View
     * 拉起弹窗后不可修改
     */
    public void setContent(View view) {
        if (!ok) return;
        V.RL(view).padding(V.UN, V.UN, V.UN, (int) (view.getPaddingBottom() + dp2px(20)));
        super.setContent(view);
        adapter.clearListener();
    }

    /**
     * 获取设置的控件
     * 方便使用xml的控件设置事件监听
     */
    public <T extends View> T getContentView() {
        return (T) adapter.items[0];
    }


}

