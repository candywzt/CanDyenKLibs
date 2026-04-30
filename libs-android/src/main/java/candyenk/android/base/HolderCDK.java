package candyenk.android.base;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

/***
 * 数据集项目控件-元数据视图
 */
public class HolderCDK extends RecyclerView.ViewHolder {
    protected final Context context;
    protected AdapterRVCDK<? extends HolderCDK> adapter;
    
    /**
     * 通过View控件创建元数据
     *
     * @param itemView 控件
     */
    public HolderCDK(@NonNull View itemView) {
        super(itemView);
        this.context = itemView.getContext();
    }
    
    /**
     * 获取文本内容
     *
     * @param resId 文本资源ID
     * @return 文本内容
     */
    public CharSequence text(@StringRes int resId) {
        return context.getText(resId);
    }
    
    /**
     * 获取字符串内容
     *
     * @param resId 字符串资源ID
     * @return 字符串内容
     */
    public String string(@StringRes int resId) {
        return context.getString(resId);
    }
    
    /**
     * 获取图像内容
     *
     * @param resId 图像资源ID
     * @return 图像内容
     */
    public Drawable icon(@DrawableRes int resId) {
        return context.getDrawable(resId);
    }
    
    /**
     * 通过资源ID获取子控件
     *
     * @param id  控件资源ID
     * @param <T> 控件类型
     * @return 控件
     */
    public <T extends View> T findViewById(int id) {
        return itemView.findViewById(id);
    }
    
    /**
     * 通过索引获取当前项目控件的子控件
     *
     * @param index 索引值
     * @param <T>   控件类型
     * @return 控件内容
     */
    public <T extends View> T getChildAt(int index) {
        return itemView instanceof ViewGroup ? (T) ((ViewGroup) itemView).getChildAt(index) : null;
    }
    
    /**
     * 获取当前项目控件的子控件数量
     *
     * @return 子控件数量
     */
    public int getChildCount() {
        return itemView instanceof ViewGroup ? ((ViewGroup) itemView).getChildCount() : 0;
    }
    
    /**
     * 设置当前项目点击监听
     * 使用AdapterCDK将自动绑定
     *
     * @param l 点击监听
     */
    public void onClick(View.OnClickListener l) {
        itemView.setOnClickListener(l);
    }
    
    /**
     * 设置当前项目长按监听
     * 使用AdapterCDK将自动绑定
     *
     * @param l 长按监听
     */
    public void OnLong(View.OnLongClickListener l) {
        itemView.setOnLongClickListener(l);
    }
    
    /**
     * 获取Item所在位置
     * 如果使用了AdapterCDK的setHead,则只能使用改方法,不能用{@link #getPosition()}
     */
    public int getItemPosition() {
        int index = super.getAdapterPosition();
        if (adapter.headH != null) index--;
        return index;
    }
}
