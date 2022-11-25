package candyenk.android.asbc;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

/***
 * RecyclerView 的 ViewHolder
 * 方便一点啦
 */
public class HolderCDK extends RecyclerView.ViewHolder {
    public final Context context;

    public HolderCDK(@NonNull @NotNull View itemView) {
        super(itemView);
        this.context = itemView.getContext();
    }

    public <T extends View> T findViewById(int id) {
        return itemView.findViewById(id);
    }

    public <T extends View> T getChildAt(int index) {
        return itemView instanceof ViewGroup ? (T) ((ViewGroup) itemView).getChildAt(index) : null;
    }

    public int getChildCount() {
        return itemView instanceof ViewGroup ? ((ViewGroup) itemView).getChildCount() : 0;
    }

    public void setOnClickListener(View.OnClickListener l) {
        itemView.setOnClickListener(l);
    }

    public void setOnLongClickListener(View.OnLongClickListener l) {
        itemView.setOnLongClickListener(l);
    }
}
