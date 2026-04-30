package candyenk.android.base;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

/**
 * 数据集组件适配器
 */
public abstract class AdapterRVCDK<H extends HolderCDK> extends RecyclerView.Adapter<H> {
    protected View.OnClickListener l = v -> {};
    protected View.OnLongClickListener ll = v -> true;
    protected H headH, footH;//头部,尾部元数据
    
    @NonNull
    @Override
    public final H onCreateViewHolder(@NonNull ViewGroup p, int t) {
        if (t == Integer.MIN_VALUE) return headH;
        else if (t == Integer.MAX_VALUE) return footH;
        H h = onCreate(p, t);
        h.onClick(l);
        h.OnLong(ll);
        h.adapter = this;
        return h;
    }
    
    @Override
    public final void onBindViewHolder(@NonNull H h, int p) {
        if (getItemViewType(p) == Integer.MIN_VALUE || getItemViewType(p) == Integer.MAX_VALUE) return;
        if (headH != null) p--;
        onBind(h, p);
        
    }
    
    @Override
    public final int getItemCount() {
        int count = getCount();
        if (headH != null) count++;
        if (footH != null) count++;
        return count;
    }
    
    /**
     * 获取指定位置的视图的类型
     * 如果是Head,则返回{@link Integer#MIN_VALUE}
     * 如果是Foot则返回{@link Integer#MAX_VALUE}
     *
     * @param p 视图所处的位置
     * @return 视图类型
     */
    @Override
    public final int getItemViewType(int p) {
        if (headH != null && p == 0) return Integer.MIN_VALUE;
        if (footH != null && p == getItemCount() - 1) return Integer.MAX_VALUE;
        return getType(p);
    }
    
    /**
     * 在该方法内创建元数据
     *
     * @param p 视图将要加入的父控件
     * @param t 当前视图类型
     * @return 视图元数据
     */
    public abstract H onCreate(ViewGroup p, int t);
    
    /**
     * 在该方法内绑定视图
     *
     * @param h 视图元数据
     * @param p 视图所处的位置
     */
    public abstract void onBind(@NonNull H h, int p);
    
    /**
     * 返回当前适配器所持有的视图数量
     *
     * @return 视图数量
     */
    public abstract int getCount();
    
    /**
     * 获取指定位置的视图类型
     * 不要使用{@link Integer#MIN_VALUE}和{@link Integer#MAX_VALUE}已经被Head和Foot占用了
     *
     * @param p 视图所处的位置
     * @return 视图类型
     */
    public abstract int getType(int p);
    
    
    /**
     * 设置点按监听事件
     *
     * @param list 适配器绑定的数据集组件
     * @param c    适配器项目的点击监听
     */
    public void setOnClickListener(RecyclerView list, Consumer<H> c) {
        if (l == null) l = v -> {};
        else l = v -> c.accept((H) list.getChildViewHolder(v));
    }
    
    /**
     * 设置长按监听事件
     *
     * @param list 适配器绑定的数据集组件
     * @param c    适配器项目的长按监听
     */
    public void setOnLongClickListener(RecyclerView list, Consumer<H> c) {
        if (ll == null) ll = v -> true;
        else ll = v -> {
            c.accept((H) list.getChildViewHolder(v));
            return true;
        };
    }
    
    /**
     * 设置数据集头部
     *
     * @param holder 头部的元数据
     */
    public H setHeader(H holder) {
        return this.headH = holder;
    }
    
    /**
     * 设置数据集尾部
     *
     * @param holder 尾部的元数据
     */
    public H setFooter(H holder) {
        return this.footH = holder;
    }
}
