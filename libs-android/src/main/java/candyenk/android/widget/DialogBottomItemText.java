package candyenk.android.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.R;
import candyenk.android.base.HolderCDK;
import candyenk.android.tools.V;
import candyenk.android.viewgroup.NoLinearLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 底部弹窗组件-Text项目展示
 */
public class DialogBottomItemText extends DialogBottomItem {
    private boolean itemCenter = true;//项目是否居中
    private boolean showSplitLine = true;//是否显示项目分割线
    private CharSequence[] items;
    
    public DialogBottomItemText(Context context) {
        this(context, null);
    }
    
    public DialogBottomItemText(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomItemText(Context context, View view) {
        super(context, view);
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence...)}
     */
    @Deprecated
    @Override
    public void setContent(int resId, int count) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence...)}
     */
    @Deprecated
    @Override
    public void setContent(View[] views) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence...)}
     */
    @Deprecated
    @Override
    public void setContent(int... resIds) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * 参数里的View是TextView
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
     * 参数里的View是TextView
     */
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        if (!ok || isShowing() || l == null) return;
        super.setOnItemLongClickListener((v, p) -> {
            RecyclerView.ViewHolder h = listView.getChildViewHolder(v);
            l.accept(V.getChild(h.itemView, 0), p);
        });
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * 设置文本项目是否居中
     *
     * @param isCenter 是否居中
     */
    public void setItemCenter(boolean isCenter) {
        this.itemCenter = isCenter;
    }
    
    /**
     * 设置是否显示项目分割线
     *
     * @param isShow 是否显示，默认true
     */
    public void setShowSplitLine(boolean isShow) {
        this.showSplitLine = isShow;
    }
    
    /**
     * 设置内容-文本列表
     *
     * @param stringList 文本列表
     */
    public void setContent(CharSequence... stringList) {
        if (!ok) return;
        this.items = stringList;
        adapter.setItems(this::createLayout, items.length);
        super.setOnBindViewHolder(h -> {
            int p = h.getAdapterPosition();
            CharSequence text = items[p];
            V.LL(V.getChild(h.itemView, 0)).text(text);//TextView
            if (showSplitLine)
                V.LL(V.getChild(h.itemView, 1)).visibility(p == items.length - 1 ? View.GONE : View.VISIBLE)
                 .refresh();//分割线
        });
    }
    
    /**
     * 获取文本列表
     *
     * @return 文本列表
     */
    public CharSequence[] getTextList() {
        return items;
    }
    
    /*** 创建Item布局 ***/
    private View createLayout() {
        NoLinearLayout nl = new NoLinearLayout(context);
        V.RV(nl).sizeDP(-1, 60).backgroundRes(R.drawable.bg_cdk).orientation(1).refresh();
        nl.setClickable(true);
        nl.setFocusable(true);
        
        TextView tv = new MaterialTextView(context);
        V.LL(tv).size(-1).textColorRes(R.color.text_main)
         .gravity(itemCenter ? Gravity.CENTER : Gravity.START | Gravity.CENTER).textSize(18)
         .paddingDP(itemCenter ? 0 : 20, 0, 0, 0).parent(nl);
        if (showSplitLine) {
            View v = new View(context);
            V.LL(v).sizeDP(-1, 2).marginDP(20, -1, 20, 0).backgroundRes(R.color.main_01).parent(nl);
        }
        return nl;
    }
}

