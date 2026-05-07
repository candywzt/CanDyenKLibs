package candyenk.android.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import candyenk.android.tools.V;

/**
 * DialogBottomItem-Item可横向滚动的
 * listView的父级变为FrameLayout
 */
public class DialogBottomItemHScroll extends DialogBottomItem {
    protected HorizontalScrollView hs;
    
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
    
    /**
     * 设置头部
     * 独立于横向滚动Item的内容
     *
     * @param view 独立头部View
     */
    public void setHeader(View view) {
        ViewGroup parent = V.getParent(hs);
        int index = parent.indexOfChild(hs);
        parent.addView(view, index);
    }
    
    /*** 修改RV继承关系 ***/
    private void reviseListView() {
        ViewGroup parent = V.getParent(listView);
        int index = parent.indexOfChild(listView);
        parent.removeView(listView);
        hs = new HorizontalScrollView(context);
        V.LL(hs).size(-1, -2).parent(parent, index);
        hs.addView(listView);
    }
    
}

