package candyenk.android.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.R;
import candyenk.android.asbc.HolderCDK;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.android.viewgroup.NoLinearLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * Text项目展示上拉弹窗
 * 自动拦截重复调用
 * 三次重构
 */
public class DialogBottomItemText extends DialogBottomItem {
    /*************************************成员变量**************************************************/
    protected CharSequence[] items;
    protected boolean itemCenter = true;//项目是否居中
    protected boolean showSplitLine = true;//是否显示项目分割线
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
    public DialogBottomItemText(Context context) {
        this(context, null);
    }

    public DialogBottomItemText(View view) {
        this(view.getContext(), view);
    }


    protected DialogBottomItemText(Context context, View view) {
        super(context, view);
    }

    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence...)}
     */
    @Override
    public void setContent(View... views) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(View...)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence...)}
     */
    @Override
    public void setContent(int... resIds) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(int...)");
    }

    /**
     * @deprecated 请使用 {@link #setContent(CharSequence...)}
     */
    @Override
    public void setContent(int resId, int count) {
        L.e("TAG", "不允许使用DialogBottomView.setContent(int,int)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        L.e("TAG", "不允许使用DialogBottomView.setLayoutManager(RecyclerView.LayoutManager)");
    }

    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        L.e("TAG", "不允许使用DialogBottomView.setOnBindViewHolder(Consumer)");
    }

    /**
     * 设置列表项目点击事件
     * 参数里的View是TextView
     * 弹窗显示后不可用
     */
    @Override
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        if (!ok || isShowing() || l == null) return;
        super.setOnItemLongClickListener((v, p) -> {
            RecyclerView.ViewHolder h = listView.getChildViewHolder(v);
            l.accept(V.getChild(h.itemView, 0), p);
        });
    }

    /**
     * 设置列表项目长按事件
     * 参数里的View是TextView
     * 弹窗显示后不可用
     */
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        if (!ok || isShowing() || l == null) return;
        super.setOnItemLongClickListener((v, p) -> {
            RecyclerView.ViewHolder h = listView.getChildViewHolder(v);
            l.accept(V.getChild(h.itemView, 0), p);
        });
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置字符数组项目是否居中(默认true)
     * 弹窗显示后不可用
     */

    public void setItemCenter(boolean isCenter) {
        this.itemCenter = isCenter;
    }

    /**
     * 设置是否显示Item分割线(默认true)
     * 弹窗显示后不可用
     */
    public void setShowSplitLine(boolean isShow) {
        this.showSplitLine = isShow;
    }

    /**
     * 设置内容-文本项目列表
     * 弹窗显示后不可用
     */
    public void setContent(CharSequence... stringList) {
        if (!ok) return;
        this.items = stringList;
        adapter.setItems(this::createLayout, items.length);
        super.setOnBindViewHolder(h -> {
            int p = h.getAdapterPosition();
            CharSequence text = items[p];
            boolean l = p == items.length - 1;
            V.RL(h.itemView).marginDP(0, 0, 0, l ? 20 : 0).refresh();//ItemView
            V.LL(V.getChild(h.itemView, 0)).text(text);//TextView
            if (showSplitLine) V.LL(V.getChild(h.itemView, 1)).visibility(l ? View.GONE : View.VISIBLE).refresh();//分割线
        });
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
    /*** 创建Item布局 ***/
    private View createLayout() {
        NoLinearLayout nl = new NoLinearLayout(viewContext);
        V.RV(nl).sizeDP(-1, 60).backgroundRes(R.drawable.bg_cdk).orientation(1).refresh();
        nl.setClickable(true);
        nl.setFocusable(true);

        TextView tv = new MaterialTextView(viewContext);
        V.LL(tv).size(-1).textColorRes(R.color.text_main).gravity(itemCenter ? Gravity.CENTER : Gravity.START | Gravity.CENTER).textSize(18).paddingDP(itemCenter ? 0 : 20, 0, 0, 0).parent(nl).refresh();
        if (showSplitLine) {
            View v = new View(viewContext);
            V.LL(v).sizeDP(-1, 2).marginDP(20, -1, 20, 0).backgroundRes(R.color.main_01).parent(nl).refresh();
        }
        return nl;
    }
}

