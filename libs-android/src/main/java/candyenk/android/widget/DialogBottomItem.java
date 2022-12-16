package candyenk.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import candyenk.android.asbc.AdapterRVCDK;
import candyenk.android.asbc.HolderCDK;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * 自定义项目展示上拉弹窗
 * 自动拦截重复调用
 * 这个东西没人用吧
 */
public class DialogBottomItem extends DialogBottomRV {
    /*************************************成员变量**************************************************/
    protected ItemAdapter adapter;
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
    public DialogBottomItem(Context context) {
        this(context, null);
    }

    public DialogBottomItem(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottomItem(Context context, View view) {
        super(context, view);
        this.TAG = this.getClass().getSimpleName();
        this.adapter = new ItemAdapter(context);
        this.adapter.setFooter(new HolderCDK(createFoot()));
        if (ok) super.setContent(this.adapter);
    }

    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    /**
     * @deprecated Use {@link #setContent(View...)}{@link #setContent(int...)}{@link #setContent(int, int)} instead.
     */
    @Override
    public void setContent(AdapterRVCDK<? extends HolderCDK> adapter) {
        L.e("TAG", "不支持的操作" + TAG + ".setContent(Adapter)");
    }

    @Override
    public ItemAdapter getAdapter() {
        return adapter;
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置内容
     * 指定唯一布局,具体效果再做绑定
     * 弹窗显示后不可用
     *
     * @param resId 唯一xml布局
     * @param count 数量
     */
    public void setContent(@LayoutRes int resId, int count) {
        if (!ok || isShowing()) return;
        this.adapter.setItems(() -> LayoutInflater.from(context).inflate(resId, null), count);
    }

    /**
     * 设置内容-项目列表
     * 弹窗显示后不可用
     */
    public void setContent(View[] views) {
        if (!ok || isShowing()) return;
        this.adapter.setItems(views);
    }

    public void setContent(@LayoutRes int... resIds) {
        if (!ok || isShowing()) return;
        LayoutInflater li = LayoutInflater.from(context);
        View[] items = new View[resIds.length];
        for (int i = 0; i < resIds.length; i++) items[i] = li.inflate(resIds[i], null);
        setContent(items);
    }

    /**
     * 绑定布局处理器
     * 弹窗显示后不可用
     */
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        if (!ok || isShowing()) return;
        this.adapter.setOnBindViewHolder(c);
    }

    /**
     * 设置列表项目点击事件
     * 弹窗显示后不可用
     */
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        if (!ok || isShowing()) return;
        this.adapter.setOnLongClickListener(listView, h -> l.accept(h.itemView, h.getAdapterPosition()));
    }

    /**
     * 设置列表项目长按事件
     * 弹窗显示后不可用
     */
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        if (!ok || isShowing()) return;
        this.adapter.setOnClickListener(listView, h -> l.accept(h.itemView, h.getAdapterPosition()));
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建页脚 ***/
    private View createFoot() {
        View view = new View(context);
        V.RL(view).sizeDP(-1, 20).refresh();
        return view;
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
    /*** 上拉弹窗适配器 ***/
    public static class ItemAdapter extends AdapterRVCDK<HolderCDK> {
        private final Context context;
        protected View[] items;
        protected Supplier<View> supplier;
        private int count;
        private Consumer<HolderCDK> c;

        private ItemAdapter(Context context) {
            this.context = context;
        }

        /*** 设置Adapter内容 ***/
        protected void setItems(View... items) {
            this.items = items;
            this.count = items.length;
        }

        /*** 设置Adapter内容 ***/
        protected void setItems(Supplier<View> supplier, int count) {
            this.supplier = supplier;
            this.count = count;
        }

        /*** 设置布局绑定 ***/
        protected void setOnBindViewHolder(Consumer<HolderCDK> c) {
            this.c = c;
        }

        /*** 清空Item事件监听 ***/
        protected void clearListener() {
            l = null;
            ll = null;
        }

        @Override
        public HolderCDK onCreate(ViewGroup p, int t) {
            View view = t == -1 ? supplier.get() : items[t];
            HolderCDK h = new HolderCDK(view);
            return h;
        }

        @Override
        public void onBind(@NotNull HolderCDK h, int p) {
            if (c != null) c.accept(h);
        }

        @Override
        public int getCount() {
            return count;
        }

        /**
         * 唯一res为-1,其他的为p
         */
        @Override
        public int getType(int p) {
            return items == null ? -1 : p;
        }
    }
}

