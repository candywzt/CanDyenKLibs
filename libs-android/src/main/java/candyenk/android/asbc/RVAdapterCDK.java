package candyenk.android.asbc;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * RecycleView.Adapter
 */
public abstract class RVAdapterCDK<VH extends HolderCDK> extends RecyclerView.Adapter<VH> {
    protected View.OnClickListener l = v -> {};
    protected View.OnLongClickListener ll = v -> true;

    @NonNull
    @NotNull
    @Override
    public final VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        VH h = onCreate(parent, viewType);
        h.setOnClickListener(l);
        h.setOnLongClickListener(ll);
        return h;
    }

    public abstract VH onCreate(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(@NotNull VH holder, int position);

    public int getItemCount() {return 1;}

    /*** 设置点按监听事件 ***/
    public<VH extends HolderCDK> void setOnClickListener(RecyclerView list, Consumer<VH> c) {
        if (l == null) l = v -> {};
        else l = v -> c.accept((VH) list.getChildViewHolder(v));
    }

    /*** 设置长按监听事件 ***/
    public <VH extends HolderCDK> void setOnLongClickListener(RecyclerView list, Consumer<VH> c) {
        if (ll == null) ll = v -> true;
        else ll = v -> {
            c.accept((VH) list.getChildViewHolder(v));
            return true;
        };
    }
}
