package candyenk.android.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import candyenk.android.tools.V;


/**
 * Item可横向滚动的DialogBottomItem
 * listView的父级变为FrameLayout4
 */
public class DialogBottomItemHScroll extends DialogBottomItem {
    protected HorizontalScrollView hs;
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public DialogBottomItemHScroll(Context context) {
        this(context, null);
    }

    public DialogBottomItemHScroll(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottomItemHScroll(Context context, View view) {
        super(context, view);
        if (ok) reviseListView();
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 头部
     * 独立于横向滚动Item的内容
     */
    public void setHeader(View view) {
        ViewGroup parent = V.getParent(hs);
        int index = parent.indexOfChild(hs);
        parent.addView(view, index);
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 修改RV继承关系 ***/
    private void reviseListView() {
        ViewGroup parent = V.getParent(listView);
        int index = parent.indexOfChild(listView);
        parent.removeView(listView);
        hs = new HorizontalScrollView(viewContext);
        V.LL(hs).size(-1, -2).parent(parent, index);
        hs.addView(listView);
    }

}

