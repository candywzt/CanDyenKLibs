package candyenk.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.base.HolderCDK;
import candyenk.android.tools.L;

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
    public void setContent(View[] views) {
        L.e("TAG", "不支持的操作" + TAG + ".setContent(View[])");
    }

    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Override
    public void setContent(int... resIds) {
        L.e("TAG", "不支持的操作" + TAG + ".setContent(int...)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Override
    public void setContent(int resId, int count) {
        L.e("TAG", "不支持的操作" + TAG + ".setContent(int,int)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        L.e("TAG", "不支持的操作" + TAG + ".setLayoutManager(RecyclerView.LayoutManager)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        L.e("TAG", "不支持的操作" + TAG + ".setOnBindViewHolder(Consumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不支持的操作" + TAG + ".setOnItemClickListener(BiConsumer)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不支持的操作" + TAG + ".setOnItemLongClickListener(BiConsumer)");
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
        this.setContent(LayoutInflater.from(context).inflate(viewid, listView, false));
    }

    /**
     * 设置内容-自定义View
     * 拉起弹窗后不可修改
     */
    public void setContent(View view) {
        if (!ok) return;
        super.setContent(new View[]{view});
        adapter.clearListener();
    }

    /**
     * 内容绑定
     */
    public void bindContent(Consumer<View> binder) {
        binder.accept(adapter.items[0]);
    }

    /**
     * 获取设置的View
     */
    public View getContent() {
        if (adapter.items != null) return adapter.items[0];
        else return null;
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/

}

