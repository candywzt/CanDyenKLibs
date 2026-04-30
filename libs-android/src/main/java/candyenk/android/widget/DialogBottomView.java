package candyenk.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.base.HolderCDK;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * 底部弹窗-自定义View
 */
public class DialogBottomView extends DialogBottomItem {
    
    public DialogBottomView(Context context) {
        this(context, null);
    }
    
    public DialogBottomView(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomView(Context context, View view) {
        super(context, view);
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Deprecated
    @Override
    public void setContent(int resId, int count) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * 获取设置的View
     * 如果是铜鼓LayoutId设置的布局,可通过该方法获取视图
     *
     * @return 自定义View
     */
    public <T extends View> T getContent() {
        if (adapter.items != null) return (T) adapter.items[0];
        else return null;
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Deprecated
    @Override
    public void setContent(View[] views) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(View)}{@link #setContent(int)}
     */
    @Deprecated
    @Override
    public void setContent(int... resIds) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * 设置内容-自定义布局
     *
     * @param layoutId 布局资源ID
     */
    public void setContent(int layoutId) {
        if (!ok) return;
        this.setContent(LayoutInflater.from(context).inflate(layoutId, listView, false));
    }
    
    /**
     * 设置内容-自定义View
     *
     * @param view 自定义View
     */
    public void setContent(View view) {
        if (!ok) return;
        super.setContent(new View[]{view});
        adapter.clearListener();
    }
    
}

