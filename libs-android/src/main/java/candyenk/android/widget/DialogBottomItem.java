package candyenk.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import candyenk.android.base.AdapterRVCDK;
import candyenk.android.base.HolderCDK;
import candyenk.android.tools.V;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 底部弹窗组件-数据集项目
 */
public class DialogBottomItem extends DialogBottomRV {
    protected ItemAdapter adapter;
    
    public DialogBottomItem(Context context) {
        this(context, null);
    }
    
    public DialogBottomItem(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomItem(Context context, View view) {
        super(context, view);
        this.adapter = new ItemAdapter();
        this.adapter.setFooter(new HolderCDK(createFoot()));
        if (ok) super.setContent(this.adapter);
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(View...)}{@link #setContent(int...)}{@link #setContent(int, int)} .
     */
    @Deprecated
    @Override
    public void setContent(AdapterRVCDK<? extends HolderCDK> adapter) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * 设置内容-单布局多项目
     * 指定唯一布局,具体效果再做绑定
     * 弹窗显示后不可用
     *
     * @param resId 唯一xml布局
     * @param count 数量
     */
    public void setContent(@LayoutRes int resId, int count) {
        if (!ok || isShowing()) return;
        this.adapter.setItems(() -> LayoutInflater.from(context).inflate(resId, listView, false), count);
    }
    
    /**
     * 设置内容-多项目
     * 弹窗显示后不可用
     *
     * @param views 项目组
     */
    public void setContent(View[] views) {
        if (!ok || isShowing()) return;
        this.adapter.setItems(views);
    }
    
    /**
     * 设置内容-多布局
     *
     * @param resIds 布局ID列表
     */
    public void setContent(@LayoutRes int... resIds) {
        if (!ok || isShowing()) return;
        LayoutInflater li = LayoutInflater.from(context);
        View[] items = new View[resIds.length];
        for (int i = 0; i < resIds.length; i++) items[i] = li.inflate(resIds[i], listView, false);
        setContent(items);
    }
    
    /**
     * 绑定布局处理器
     * 弹窗显示后不可用
     *
     * @param c 布局处理器
     */
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        if (!ok || isShowing()) return;
        this.adapter.setOnBindViewHolder(c);
    }
    
    /**
     * 设置列表项目点击事件
     * 弹窗显示后不可用
     *
     * @param l 点击监听
     */
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        if (!ok || isShowing()) return;
        this.adapter.setOnLongClickListener(listView, h -> l.accept(h.itemView, h.getAdapterPosition()));
    }
    
    /**
     * 设置列表项目长按事件
     * 弹窗显示后不可用
     *
     * @param l 长按监听
     */
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        if (!ok || isShowing()) return;
        this.adapter.setOnClickListener(listView, h -> l.accept(h.itemView, h.getAdapterPosition()));
    }
    
    /*** 创建页脚 ***/
    private View createFoot() {
        View view = new View(context);
        V.RL(view).sizeDP(-1, 20).refresh();
        return view;
    }
    
    /*** Item适配器 ***/
    protected static class ItemAdapter extends AdapterRVCDK<HolderCDK> {
        protected View[] items;
        protected Supplier<View> supplier;
        private int count;
        private Consumer<HolderCDK> c;
        
        protected ItemAdapter() {
        }
        
        @Override
        public HolderCDK onCreate(ViewGroup p, int t) {
            View view = t == -1 ? supplier.get() : items[t];
            return new HolderCDK(view);
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
         * 获取指定位置的控件类型
         * 唯一布局为-1,其他的为p
         */
        @Override
        public int getType(int p) {
            return items == null ? -1 : p;
        }
        
        /*** 设置Adapter内容,控件组 ***/
        protected void setItems(View... items) {
            this.items = items;
            this.count = items.length;
        }
        
        /*** 设置Adapter内容,重复控件 ***/
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
    }
}

