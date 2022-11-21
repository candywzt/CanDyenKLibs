package candyenk.android.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import org.jetbrains.annotations.NotNull;


/**
 * 自定义View上拉弹窗
 */
public class DialogBottomView extends DialogBottom {
    /*************************************静态变量**************************************************/
    /*************************************成员变量**************************************************/
    protected View contentView;
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
    public DialogBottomView(Context context) {
        this(context, null);
    }

    public DialogBottomView(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottomView(Context context, View view) {
        super(context, view);
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    @Override
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(Adapter)");
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置内容-自定义布局
     * 拉起弹窗后不可修改
     */
    public void setContent(int viewid) {
        if (!ok) return;
        View view = LayoutInflater.from(viewContext).inflate(viewid, null, false);
        this.setContent(view);
    }

    /**
     * 设置内容-自定义View
     * 拉起弹窗后不可修改
     */
    public void setContent(View view) {
        if (!ok) return;
        this.contentView = view;
        adapter = new ViewAdapter(view);
        super.setContent(adapter);
    }

    /**
     * 获取设置的控件
     * 方便使用xml的控件设置事件监听
     */
    public <T extends View> T getContentView() {
        return (T) contentView;
    }
/**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
    /*** 上拉弹窗适配器 ***/
    private static class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final View view;//自定义View

        private ViewAdapter(View view) {
            this.view = view;
            V.RL(view).paddingDP(V.UN, V.UN, V.UN, 40).refresh();//增加底距
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(view) {};
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
}

