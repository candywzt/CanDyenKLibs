package candyenk.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import candyenk.android.tools.V;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ShizukuHelp BottomBar
 * 底栏控件
 * 配合CDKActivity使用
 */
public class BottomBar extends LinearLayout {

    /*************************************静态变量**************************************************/

    /*************************************成员变量**************************************************/
    protected Context context;
    protected boolean isLand;//是不是纵向排布
    protected List<LinearLayout> itemList;
    private int[] sign = {0};//标志<控件是否初始化结束>

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
    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initLayout();
        initAttrs(attrs);
        initEvents();
        this.isLand = getOrientation() == VERTICAL;
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    protected int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }

    protected void initLayout() {}

    protected void initAttrs(AttributeSet attrs) {}

    protected void initEvents() {}

    @Override
    public void setOnClickListener(OnClickListener v) {}

    @Override
    public void onMeasure(int w, int h) {
        this.sign[0] = 0;
        super.onMeasure(w, h);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.sign[0] = 1;
    }

    @Override
    public void setOrientation(int orientation) {
        this.isLand = orientation == VERTICAL;
        super.setOrientation(orientation);
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 添加一个项目
     * 最多添加5个,多了爆炸
     */
    public void addItem(CharSequence title, Drawable icon, View.OnClickListener v) {
        if (itemList == null) itemList = new ArrayList<>(5);
        if (itemList.size() >= 5) throw new NullPointerException("支持最多5个Item");
        LinearLayout item = createItem(title, icon, vv -> {
            ItemChanged(itemList.size());
            v.onClick(vv);
        });
        removeView(item);
        addView(item);
        itemList.add(item);
        if (this.sign[0] == 1) requestLayout();
    }

    public void addItem(CharSequence title, Drawable icon) {
        addItem(title, icon, null);
    }

    /**
     * 移除Item
     */
    public void removeItem(int index) {
        if (itemList == null || itemList.get(index) == null) return;
        removeView(itemList.get(index));
        itemList.remove(index);
    }

    public void removeAllItem() {
        if (itemList == null) return;
        itemList.clear();
        removeAllViews();
    }

    /**
     * 绑定ViewPager2
     * 前提条件,创建足够的Item
     */
    public void bindViewPager2(ViewPager2 page) {
        RecyclerView.Adapter adp = page.getAdapter();
        if (adp.getItemCount() > 5) throw new NullPointerException("支持最多5个Item的ViewPager2");
        if (itemList.size() != Math.min(5, adp.getItemCount())) throw new NullPointerException("Item数量不符合");
        for (int i = 0; i < itemList.size(); i++) {
            int finalI = i;
            itemList.get(i).setOnClickListener(v -> page.setCurrentItem(finalI));
        }
        page.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //TODO:动画
            }
        });
    }

    /**
     * 获取Item
     * 没有就是NULL
     */
    public LinearLayout getItem(int index) {
        if (itemList == null) return null;
        return itemList.get(index);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建一个项目 ***/
    private LinearLayout createItem(CharSequence title, Drawable icon, View.OnClickListener v) {
        LinearLayout l1 = new LinearLayout(context);
        V.LL(l1).size(isLand ? -2 : -1, isLand ? -1 : -2).orientation(1).weight(1).refresh();
        if (v != null) l1.setOnClickListener(v);

        ImageView iv = new ImageView(context);
        V.LL(iv).sizeDP(-1, 40).parent(l1);
        if (icon == null) iv.setImageResource(android.R.drawable.ic_delete);
        else iv.setImageDrawable(icon);

        TextView tv = new MaterialTextView(context);
        V.LL(tv).size(-1, -2).gravity(Gravity.CENTER).text(title).parent(l1);
        return l1;
    }

    /*** 项目被切换 ***/
    private void ItemChanged(int index) {
        //TODO:动画展现
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
}

