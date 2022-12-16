package candyenk.android.asbc;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * RecycleView.Adapter
 * 不要使用getAdapterPosition了.请使用getPosition
 */
public abstract class AdapterRVCDK<VH extends HolderCDK> extends RecyclerView.Adapter<VH> {
    VH headH, footH;//头部,尾部
    protected View.OnClickListener l = v -> {};
    protected View.OnLongClickListener ll = v -> true;

    @NonNull
    @NotNull
    @Override
    public final VH onCreateViewHolder(@NonNull @NotNull ViewGroup p, int t) {
        if (t == Integer.MIN_VALUE) return headH;
        else if (t == Integer.MAX_VALUE) return footH;
        VH h = onCreate(p, t);
        h.onClick(l);
        h.OnLong(ll);
        h.adapter = this;
        return h;
    }

    public final void onBindViewHolder(@NotNull VH h, int p) {
        if (getItemViewType(p) == Integer.MIN_VALUE || getItemViewType(p) == Integer.MAX_VALUE) return;
        if (headH != null) p--;
        onBind(h, p);

    }

    public final int getItemCount() {
        int count = getCount();
        if (headH != null) count++;
        if (footH != null) count++;
        return count;
    }

    @Override
    public final int getItemViewType(int p) {
        if (headH != null && p == 0) return Integer.MIN_VALUE;
        if (footH != null && p == getItemCount() - 1) return Integer.MAX_VALUE;
        return getType(p);
    }

    /**
     * 创建ViewHolder
     */
    public abstract VH onCreate(ViewGroup p, int t);

    /**
     * 绑定数据
     */
    public abstract void onBind(@NotNull VH h, int p);

    /**
     * 数据总量
     */
    public abstract int getCount();

    /**
     * Item类型
     * 别用两个int极值
     */
    public int getType(int p) {
        return 0;
    }

    /**
     * 设置点按监听事件
     */
    public <VH extends HolderCDK> void setOnClickListener(RecyclerView list, Consumer<VH> c) {
        if (l == null) l = v -> {};
        else l = v -> c.accept((VH) list.getChildViewHolder(v));
    }

    /**
     * 设置长按监听事件
     */
    public <VH extends HolderCDK> void setOnLongClickListener(RecyclerView list, Consumer<VH> c) {
        if (ll == null) ll = v -> true;
        else ll = v -> {
            c.accept((VH) list.getChildViewHolder(v));
            return true;
        };
    }

    /**
     * 设置头部
     */
    public VH setHeader(VH holder) {
        return this.headH = holder;
    }

    /**
     * 设置尾部
     */
    public VH setFooter(VH holder) {
        return this.footH = holder;
    }
}
