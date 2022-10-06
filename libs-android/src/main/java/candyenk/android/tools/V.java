package candyenk.android.tools;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Android View的帮助类
 * 获取控件的LayoutParams
 * 设置控件尺寸
 * 设置控件内外距
 * 设置控件的重力
 * 设置控件的重量
 * 获取指定类型子控件
 */
public class V<T extends View> {
    /*************************************静态变量**************************************************/

    /*************************************成员变量**************************************************/
    private T view;
    private ViewGroup.LayoutParams lp;
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 获取控件的Margin布局参数
     */
    public static ViewGroup.MarginLayoutParams getMLP(View v) {
        return (ViewGroup.MarginLayoutParams) v.getLayoutParams();
    }

    /**
     * 获取控件的LinearLayout布局参数
     */
    public static LinearLayout.LayoutParams getLLLP(View v) {
        return (LinearLayout.LayoutParams) v.getLayoutParams();
    }

    /**
     * 获取控件的FrameLayout布局参数
     */
    public static FrameLayout.LayoutParams getFLLP(View v) {
        return (FrameLayout.LayoutParams) v.getLayoutParams();
    }

    /**
     * 获取控件的RelativeLayout布局参数
     */
    public static RelativeLayout.LayoutParams getRLLP(View v) {
        return (RelativeLayout.LayoutParams) v.getLayoutParams();
    }

    /**
     * 获取控件的RecyclerView布局参数
     */
    public static RecyclerView.LayoutParams getRVLP(View v) {
        return (RecyclerView.LayoutParams) v.getLayoutParams();
    }

    /**
     * 创建LinearLayout.LayoutParams
     */
    public static V createLL(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new LinearLayout.LayoutParams(-2, -2);
        else view.lp = new LinearLayout.LayoutParams(view.lp);
        return view;
    }

    /**
     * 创建FrameLayout.LayoutParams
     */
    public static V createFL(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new FrameLayout.LayoutParams(-2, -2);
        else view.lp = new FrameLayout.LayoutParams(view.lp);
        return view;
    }

    /**
     * 创建RelativeLayout.LayoutParams
     */
    public static V createRL(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new RelativeLayout.LayoutParams(-2, -2);
        else view.lp = new RelativeLayout.LayoutParams(view.lp);
        return view;
    }

    /**
     * 创建RecyclerView.LayoutParams
     */
    public static V createRV(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new RecyclerView.LayoutParams(-2, -2);
        else view.lp = new RecyclerView.LayoutParams(view.lp);
        return view;
    }

    /**
     * 获取子控件
     */
    public static <T extends View> T getChild(View v, int i, Class<T> c) {
        if (!(v instanceof ViewGroup)) return null;
        ViewGroup vg = (ViewGroup) v;
        return (T) vg.getChildAt(i);
    }

    /**
     * 设置控件宽高
     */
    public static V setSize(View v, int w, int h) {
        return new V(v).setSize(w, h);
    }

    public static V setSizeDP(View v, int w, int h) {
        return new V(v).setSizeDP(w, h);
    }


    /**
     * 设置控件外边距
     */
    public static V setMargin(View v, int l, int t, int r, int b) {
        return new V(v).setMargin(l, t, r, b);
    }

    public static V setMarginDP(View v, int l, int t, int r, int b) {
        return new V(v).setMarginDP(l, t, r, b);
    }

    /**
     * 设置控件Weight属性
     */
    public static V setWeight(View v, double w) {
        return new V(v).setWeight(w);
    }

    /**
     * 设置控件Layout_Gravity属性
     */
    public static V setLGravity(View v, int g) {
        return new V(v).setLGravity(g);
    }

    /**
     * 设置控件Padding属性
     */
    public static V setPaddingDP(View v, int l, int t, int r, int b) {
        return new V(v).setPaddingDP(l, t, r, b);
    }

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/
    private double dp2px(double dp) {
        float num = dp < 0 ? -1 : 1;
        final double scale = view.getContext().getResources().getDisplayMetrics().density;
        return (dp * scale + (0.5f * num));
    }

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    private V(T v) {
        this.view = v;
        this.lp = view.getLayoutParams();
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置控件宽高
     */
    public V setSize(int w, int h) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        lp.width = w;
        lp.height = h;
        return this;
    }

    public V setSizeDP(int w, int h) {
        setSize(w < 0 ? w : (int) dp2px(w),
                h < 0 ? h : (int) dp2px(h));
        return this;
    }

    /**
     * 设置控件外边距
     */
    public V setMargin(int l, int t, int r, int b) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) lp).setMargins(l, t, r, b);
        }
        return this;
    }

    public V setMarginDP(int l, int t, int r, int b) {
        setMargin((int) dp2px(l),
                (int) dp2px(t),
                (int) dp2px(r),
                (int) dp2px(b));
        return this;
    }

    /**
     * 设置控件Weight属性
     */
    public V setWeight(double w) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        if (lp instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) lp).weight = (float) w;
        }
        return this;
    }

    /**
     * 设置控件Layout_Gravity属性
     */
    public V setLGravity(int g) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        if (lp instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) lp).gravity = g;
        } else if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).gravity = g;
        }
        return this;
    }

    /**
     * 设置控件Padding属性
     */
    public V setPaddingDP(int l, int t, int r, int b) {
        view.setPadding((int) dp2px(l),
                (int) dp2px(t),
                (int) dp2px(r),
                (int) dp2px(b));
        return this;
    }

    /**
     * 刷新控件的布局参数
     */
    public V refresh() {
        view.setLayoutParams(this.lp);
        return this;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/


}
