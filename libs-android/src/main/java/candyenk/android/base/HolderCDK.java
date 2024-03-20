package candyenk.android.base;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

/***
 * RecyclerView 的 ViewHolder
 * 方便一点啦
 */
public class HolderCDK extends RecyclerView.ViewHolder {
    public final Context context;
    AdapterRVCDK adapter;

    public HolderCDK(@NonNull @NotNull View itemView) {
        super(itemView);
        this.context = itemView.getContext();
    }

    /**
     * 便捷的文本内容获取
     */
    public CharSequence text(@StringRes int resId) {
        return context.getText(resId);
    }

    /**
     * 便捷的字符串获取
     */
    public String string(@StringRes int resId) {
        return context.getString(resId);
    }

    /**
     * 便捷的Drawable对象获取
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable icon(@DrawableRes int resId) {
        return context.getDrawable(resId);
    }

    /**
     * 便捷的通过ID获取View
     */
    public <T extends View> T findViewById(int id) {
        return itemView.findViewById(id);
    }

    /**
     * 便捷的通过索引获取子控件
     */
    public <T extends View> T getChildAt(int index) {
        return itemView instanceof ViewGroup ? (T) ((ViewGroup) itemView).getChildAt(index) : null;
    }

    /**
     * 便捷的获取子控件数量
     */
    public int getChildCount() {
        return itemView instanceof ViewGroup ? ((ViewGroup) itemView).getChildCount() : 0;
    }

    /**
     * 设置点按监听
     * 使用AdapterCDK会自动绑定在Adapter
     */
    public void onClick(View.OnClickListener l) {
        itemView.setOnClickListener(l);
    }

    /**
     * 设置长按监听
     * 使用AdapterCDK会自动绑定在Adapter
     */
    public void OnLong(View.OnLongClickListener l) {
        itemView.setOnLongClickListener(l);
    }

    /**
     * 获取Item所在位置
     * 添加了Head的只能使用这个
     * 没办法,别的被final了,没法重写
     */
    public int getItemPosition() {
        int index = super.getAdapterPosition();
        if (adapter.headH != null) index--;
        return index;
    }
}
