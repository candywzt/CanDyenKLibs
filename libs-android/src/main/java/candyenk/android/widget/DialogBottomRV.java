package candyenk.android.widget;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.base.AdapterRVCDK;
import candyenk.android.base.HolderCDK;
import candyenk.android.tools.V;

/**
 * 底部弹窗组件-数据集适配器
 */
public class DialogBottomRV extends DialogBottom {
    protected RecyclerView listView; //列表控件
    protected AdapterRVCDK<? extends HolderCDK> adapter;
    
    public DialogBottomRV(Context context) {
        this(context, null);
    }
    
    public DialogBottomRV(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomRV(Context context, View view) {
        super(context, view);
        if (ok) super.setContent(initLayout());
    }
    
    @Override
    public void show() {
        if (!ok || isShowing()) return;
        if (listView.getAdapter() == null) return;
        super.show();
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setContent(View view) {
        throw new UnsupportedOperationException("此方法在该类中已被禁用");
    }
    
    /**
     * 设置内容-自定义适配器
     * 拉起弹窗后不可修改
     *
     * @param adapter 适配器
     */
    public void setContent(AdapterRVCDK<? extends HolderCDK> adapter) {
        if (!ok) return;
        if (adapter == null) return;
        this.adapter = adapter;
        listView.setAdapter(adapter);
    }
    
    /**
     * 设置内容布局管理器
     *
     * @param lm 布局管理器 默认为线性布局
     */
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        if (!ok) return;
        if (lm == null) lm = new LinearLayoutManager(context);
        listView.setLayoutManager(lm);
    }
    
    /**
     * 获取当前适配器
     *
     * @return 当前适配器
     */
    public AdapterRVCDK<? extends HolderCDK> getAdapter() {
        return adapter;
    }
    
    /*** 创建弹窗根布局 ***/
    private View initLayout() {
        listView = new RecyclerView(context);
        V.LL(listView).size(-1, -2).weight(1).refresh();
        listView.setLayoutManager(new LinearLayoutManager(context));
        return listView;
    }
}

