package candyenk.android.widget;


import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.base.AdapterRVCDK;
import candyenk.android.base.HolderCDK;
import candyenk.android.tools.L;
import candyenk.android.tools.V;


/**
 * 自定义Adapter上拉弹窗
 * 拦截层叠创建
 * 四次重构
 */
public class DialogBottomRV extends DialogBottom {
    /*************************************成员变量**************************************************/
    protected RecyclerView listView; //列表控件
    protected AdapterRVCDK<? extends HolderCDK> adapter;
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * 构造方法
     * 会自动阻止多重点击,不过避免使用null
     */
    public DialogBottomRV(Context context) {
        this(context, null);
    }

    public DialogBottomRV(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottomRV(Context context, View view) {
        super(context, view);
        if (ok) super.setContent(initLayout());
        //TODO:这特么怎么回事???
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public void setContent(View view) {
        L.e(TAG, "不支持的操作" + TAG + ".setContentView(View)");
    }

    @Override
    public void show() {
        if (!ok || isShowing()) return;
        if (listView.getAdapter() == null) return;
        super.show();
    }

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置内容-自定义适配器
     * 拉起弹窗后不可修改
     */
    public void setContent(AdapterRVCDK<? extends HolderCDK> adapter) {
        if (!ok) return;
        if (adapter == null) return;
        this.adapter = adapter;
        listView.setAdapter(adapter);
    }

    /**
     * 设置内容布局管理器
     * 默认 LinearLayoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        if (!ok) return;
        if (lm == null) lm = new LinearLayoutManager(context);
        listView.setLayoutManager(lm);
    }

    /**
     * 获取当前Adapter
     */
    public AdapterRVCDK<? extends HolderCDK> getAdapter() {
        return adapter;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建弹窗根布局 ***/
    private View initLayout() {
        listView = new RecyclerView(viewContext);
        V.LL(listView).size(-1, -2).weight(1).refresh();
        listView.setLayoutManager(new LinearLayoutManager(context));
        return listView;
    }
}

