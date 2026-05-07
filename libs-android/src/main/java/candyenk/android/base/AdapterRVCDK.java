package candyenk.android.base;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

/**
 * 数据集组件适配器
 */
public abstract class AdapterRVCDK<H extends HolderCDK> extends RecyclerView.Adapter<H> {
    protected View.OnClickListener l = v -> {};
    protected View.OnLongClickListener ll = v -> true;
    private H headH, footH;//头部,尾部元数据
    private boolean needHF;//是否激活头尾部
    
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
        int type = getItemViewType(p);
        if (type == Integer.MIN_VALUE || type == Integer.MAX_VALUE) return;
        if (getHeader() != null) p--;
        onBind(h, p);
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
        if (getHeader() != null && p == 0) return Integer.MIN_VALUE;
        if (getFootrt() != null && p == getItemCount() - 1) return Integer.MAX_VALUE;
        return getType(getHeader() == null ? p : p - 1);
    }
    
    @Override
    public final int getItemCount() {
        int count = getCount();
        if (getHeader() != null) count++;
        if (getFootrt() != null) count++;
        return count;
    }
    
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        if (rv.getLayoutManager() instanceof LinearLayoutManager lm) {
            this.needHF = (lm.getOrientation() == LinearLayoutManager.VERTICAL);
        }
    }
    
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
     * 设置数据集尾部
     * 仅适用于纵向列表
     *
     * @param holder 尾部的元数据
     */
    public void setFooter(H holder) {
        this.footH = holder;
    }
    
    /**
     * 获取适配器头部
     * 仅适用于纵向列表
     *
     * @return 头部
     */
    public H getHeader() {
        if (needHF) return headH;
        return null;
    }
    
    /**
     * 设置数据集头部
     * 仅适用于纵向列表
     *
     * @param holder 头部的元数据
     */
    public void setHeader(H holder) {
        this.headH = holder;
    }
    
    /**
     * 获取适配器尾部
     * 仅适用于纵向列表
     *
     * @return 尾部
     */
    public H getFootrt() {
        if (needHF) return footH;
        return null;
    }
}
