package candyenk.android.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.R;
import candyenk.android.tools.V;
import candyenk.android.viewgroup.NoLinearLayout;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;


/**
 * Text项目展示上拉弹窗
 * 自动拦截重复调用
 * 三次重构
 */
public class DialogBottomItemText extends DialogBottom {
    /*************************************静态变量**************************************************/
    /*************************************成员变量**************************************************/
    protected CharSequence[] items;
    protected boolean itemCenter = true;//项目是否居中
    protected boolean showSplitLine = true;//是否显示项目分割线
    protected RecyclerView.OnItemTouchListener l, ll;//项目事件监听
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

    public DialogBottomItemText(Context context) {
        this(context, null);
    }

    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
    public DialogBottomItemText(Context context, View view) {
        super(context, view);
        this.TAG = this.getClass().getSimpleName();
    }


    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置列表项目点击事件
     * 没有列表则不起作用
     * 弹窗显示后不可用
     */
    public void setOnItemClickListener(BiConsumer<TextView, Integer> l) {
        if (!ok) return;
        if (l == null) return;
        if (this.l != null) listView.removeOnItemTouchListener(this.l);
        this.l = createListener(holder -> l.accept(V.getChild(holder.itemView, 0), holder.getLayoutPosition()), null, null);
        listView.addOnItemTouchListener(this.l);
    }

    /**
     * 设置列表项目长按事件
     * 没有列表则不起作用
     * 弹窗显示后不可用
     */
    public void setOnItemLongClickListener(BiConsumer<TextView, Integer> l) {
        if (!ok) return;
        if (l == null) return;
        if (this.ll != null) listView.removeOnItemTouchListener(this.ll);
        this.ll = createListener(null, holder -> l.accept(V.getChild(holder.itemView, 0), holder.getLayoutPosition()), null);
        listView.addOnItemTouchListener(this.ll);
    }

    /**
     * 设置字符数组项目是否居中,默认true
     * 只有在内容设置为文本列表时有效
     * 弹窗显示后不可用
     */

    public void setItemCenter(boolean isCenter) {
        this.itemCenter = isCenter;
    }

    /**
     * 设置是否显示Item分割线,默认true
     * 只有在内容设置为文本列表时有效
     * 弹窗显示后不可用
     */
    public void setShowSplitLine(boolean isShow) {
        this.showSplitLine = isShow;
    }

    /**
     * 设置内容-文本项目列表
     * 调用该方法建议不要显示底栏按钮
     * 弹窗显示后不可用
     */
    public void setContent(CharSequence... stringList) {
        if (!ok) return;
        this.adapter = new ItemAdapter(stringList);
        listView.setAdapter(adapter);
    }

    /**
     * 获取Text列表
     */
    public CharSequence[] getTextList() {
        return items;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
    /*** 上拉弹窗适配器 ***/
    private class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final CharSequence[] items;//文本列表

        private ItemAdapter(CharSequence[] items) {
            this.items = items;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(initLayoutStrings()) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CharSequence text = ((CharSequence[]) items)[position];
            V.getChild(holder.itemView, TextView.class, 0).setText(text);
            V.getChild(holder.itemView, 1).setVisibility(position + 1 == getItemCount() ? 8 : 0);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        /*** 创建文本列表控件 ***/
        private View initLayoutStrings() {
            NoLinearLayout nl = new NoLinearLayout(viewContext);
            V.RV(nl).sizeDP(-1, 60).backgroundRes(R.drawable.bg_cdk).orientation(1).refresh();
            nl.setClickable(true);
            nl.setFocusable(true);

            TextView tv = new TextView(viewContext);
            V.LL(tv).size(-1).parent(nl).textColorRes(R.color.text_main).refresh();
            if (itemCenter) tv.setGravity(Gravity.CENTER);
            else {
                tv.setGravity(Gravity.CENTER | Gravity.LEFT);
                V.paddingDP(tv, 10, 0, 0, 0);
            }
            tv.setTextSize(18);
            if (showSplitLine) {
                View v = new View(viewContext);
                V.LL(v).sizeDP(-1, 2).marginDP(30, -1, 30, 0).backgroundRes(R.color.main_01).parent(nl).refresh();
            }
            return nl;
        }
    }
}

