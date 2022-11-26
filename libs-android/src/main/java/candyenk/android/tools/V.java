package candyenk.android.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.*;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.utils.ULay;
import candyenk.java.utils.UArrays;
import candyenk.java.utils.UReflex;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Android View的帮助类
 * 功能挺多的,不列举了
 * 只有LayoutParame里的变量和多参数方法才会有对应静态方法
 * 非得用一下那就随便创建个LayoutParame别刷新就好了
 */
public class V<T extends View> {
    /*************************************静态变量**************************************************/
    private static final String TAG = "ViewHelper";
    private static final Map<View, V> map = new HashMap<>();
    public static final int UN = Integer.MIN_VALUE;
    public static final int SUPER = Integer.MAX_VALUE;
    /*************************************成员变量**************************************************/
    private final T view;
    private final Context context;
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
    public static LinearLayout.LayoutParams getLLP(View v) {
        return (LinearLayout.LayoutParams) v.getLayoutParams();
    }

    /**
     * 获取控件的FrameLayout布局参数
     */
    public static FrameLayout.LayoutParams getFLP(View v) {
        return (FrameLayout.LayoutParams) v.getLayoutParams();
    }

    /**
     * 获取控件的RelativeLayout布局参数
     */
    public static RelativeLayout.LayoutParams getRLP(View v) {
        return (RelativeLayout.LayoutParams) v.getLayoutParams();
    }

    /**
     * 获取控件的RecyclerView布局参数
     */
    public static RecyclerView.LayoutParams getRVL(View v) {
        return (RecyclerView.LayoutParams) v.getLayoutParams();
    }

    /**
     * 获取子控件
     * i:子控件索引
     */
    public static <T extends View> T getChild(View v, int i) {
        if (i < 0 || !(v instanceof ViewGroup)) return null;
        return (T) ((ViewGroup) v).getChildAt(i);
    }

    /**
     * 获取子控件
     * 强转为指定类型,方便链式调用
     * 这个判定条件是int i
     */
    public static <T extends View> T getChild(View v, int i, Class<T> c) {
        return getChild(v, i);
    }

    /**
     * 获取子控件
     * 获取第一个指定类型的控件
     */
    public static <T extends View> T getChild(View v, Class<T> c) {
        return getChild(v, c, 0);
    }

    /**
     * 获取子控件
     * 获取第i个指定类型的控件
     * 从0开始
     * 这个判定条件是Class
     */
    public static <T extends View> T getChild(View v, Class<T> c, int i) {
        for (View view : getChilds(v)) {
            if (view.getClass() == c) {
                if (i == 0) return (T) view;
                i--;
            }
        }
        return null;
    }

    /**
     * 获取嵌套子控件
     * i.length==1为子控件
     * i.length==2为子控件的子控件
     * ...
     */
    public static <T extends View> T getChild(View v, int... i) {
        View view = v;
        for (int ii : i) {
            if (v == null || ii < 0) break;
            view = getChild(view, ii);
        }
        return (T) view;
    }

    /**
     * 获取子孙控件
     * 强转为指定类型,方便链式调用
     */
    public static <T extends View> T getChild(View v, Class<T> c, int... i) {
        return getChild(v, i);
    }

    /**
     * 获取所有子控件
     */
    public static View[] getChilds(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            View[] vs = new View[vg.getChildCount()];
            for (int i = 0; i < vs.length; i++) {
                vs[i] = vg.getChildAt(i);
            }
            return vs;
        } else {
            return new View[0];
        }
    }

    /**
     * 获取父控件
     */
    public static <T extends ViewGroup> T getParent(View v) {
        return getParent(v, 0);
    }

    public static <T extends ViewGroup> T getParent(View v, Class<T> c) {
        return getParent(v);
    }

    /**
     * 获取祖宗控件
     * 0为父级
     * 1为父级的父级
     * 2为父级的父级的父级
     * SUPER为最顶级
     * i过大会返回最顶级
     */
    public static <T extends ViewGroup> T getParent(View v, Class<T> c, int i) {
        return getParent(v, i);
    }

    public static <T extends ViewGroup> T getParent(View v, int i) {
        if (i < -1 || v == null) return null;
        ViewParent vp = v.getParent();
        while (i-- > 0 && vp != null && vp.getParent() != null) vp = vp.getParent();
        return (T) vp;
    }


    /**
     * 获取ViewHolder
     */
    public static <T extends RecyclerView.ViewHolder> T getHolder(RecyclerView v, MotionEvent e) {
        return getHolder(v, e.getX(), e.getY());
    }

    public static <T extends RecyclerView.ViewHolder> T getHolder(RecyclerView v, float x, float y) {
        View c = v.findChildViewUnder(x, y);
        return c == null ? null : (T) v.getChildViewHolder(c);
    }

    /**
     * 添加控件
     */
    public static void addView(View v, View... c) {
        if (v instanceof ViewGroup) for (View view : c) ((ViewGroup) v).addView(view);
    }

    /**
     * 设置控件隐藏
     */
    public static void hide(View... v) {
        for (View view : v) if (view != null) view.setVisibility(View.GONE);
    }

    /**
     * 设置控件可见
     */
    public static void visible(View... v) {
        for (View view : v) if (view != null) view.setVisibility(View.VISIBLE);
    }

    /**
     * 设置控件不可见
     */
    public static void invisible(View... v) {
        for (View view : v) if (view != null) view.setVisibility(View.INVISIBLE);
    }

    /**
     * 判断控件隐藏
     */
    public static boolean isHide(View v) {
        return v != null && v.getVisibility() == View.GONE;
    }

    /**
     * 判断控件可见
     */
    public static boolean isVisible(View v) {
        return v != null && v.getVisibility() == View.VISIBLE;
    }

    /**
     * 判断控件不可见
     */
    public static boolean isInvisible(View v) {
        return v != null && v.getVisibility() == View.INVISIBLE;
    }

    /**
     * 设置控件打开
     */
    public static void checkTrue(View... v) {
        check(true, v);
    }

    /**
     * 设置控件关闭
     */
    public static void checkFalse(View... v) {
        check(false, v);
    }

    /**
     * 设置控件开关状态
     */
    public static void check(boolean c, View... v) {
        for (View view : v) if (view instanceof Checkable) ((Checkable) view).setChecked(c);
    }


    /**
     * 创建LinearLayout.LayoutParams
     */
    public static V LL(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new LinearLayout.LayoutParams(-2, -2);
        return view;
    }

    /**
     * 创建FrameLayout.LayoutParams
     */
    public static V FL(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new FrameLayout.LayoutParams(-2, -2);
        return view;
    }

    /**
     * 创建RelativeLayout.LayoutParams
     */
    public static V RL(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new RelativeLayout.LayoutParams(-2, -2);
        return view;
    }

    /**
     * 创建RecyclerView.LayoutParams
     */
    public static V RV(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new RecyclerView.LayoutParams(-2, -2);
        return view;
    }

    /**
     * 创建ViewGroup.MarginLayoutParams
     */
    public static V ML(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new ViewGroup.MarginLayoutParams(-2, -2);
        return view;
    }

    /**
     * 创建ViewGroup.LayoutParams
     */
    public static V LP(View v) {
        V view = new V(v);
        if (view.lp == null) view.lp = new ViewGroup.LayoutParams(-2, -2);
        return view;
    }


    /**
     * 设置控件宽高
     */
    public static V size(View v, int w, int h) {
        return new V(v).size(w, h);
    }

    public static V size(View v, int vv) {
        return new V(v).size(vv);
    }

    public static V sizeDP(View v, int w, int h) {
        return new V(v).sizeDP(w, h);
    }

    public static V sizeDP(View v, int vv) {
        return new V(v).sizeDP(vv);
    }


    /**
     * 设置控件外边距
     */
    public static V margin(View v, int l, int t, int r, int b) {
        return new V(v).margin(l, t, r, b);
    }

    public static V margin(View v, int vv) {
        return new V(v).margin(vv);
    }

    public static V marginDP(View v, int l, int t, int r, int b) {
        return new V(v).marginDP(l, t, r, b);
    }

    public static V marginDP(View v, int vv) {
        return new V(v).marginDP(vv);
    }

    /**
     * 设置控件Weight属性
     */
    public static V weight(View v, int w) {
        return new V(v).weight(w);
    }

    /**
     * 设置控件Layout_Gravity属性
     */
    public static V lGravity(View v, int g) {
        return new V(v).lGravity(g);
    }

    /**
     * 设置控件Padding属性
     * 无需刷新
     */
    public static V padding(View v, int s, int t, int e, int b) {
        return new V(v).padding(s, t, e, b);
    }

    public static V padding(View v, int vv) {
        return new V(v).padding(vv);
    }

    public static V paddingDP(View v, int s, int t, int e, int b) {
        return new V(v).paddingDP(s, t, e, b);
    }

    public static V paddingDP(View v, int vv) {
        return new V(v).paddingDP(vv);
    }

    /**
     * 设置控件Elevation属性(厚度)
     * 无需刷新
     */
    public static V eleDP(View v, int e) {
        return new V(v).eleDP(e);
    }

    /**
     * 设置控件Radius属性(圆角)
     * 无需刷新
     */
    public static V radiusDP(View v, int r) {
        return new V(v).radiusDP(r);
    }

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    private V(T v) {
        this.view = v;
        this.context = v.getContext();
        this.lp = view.getLayoutParams();
        map.put(v, this);
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
    public V size(int w, int h) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        lp.width = w == UN ? lp.width : w;
        lp.height = h == UN ? lp.height : h;
        return this;
    }

    public V size(int v) {
        return size(v, v);
    }

    public V sizeDP(int w, int h) {
        return size(w < 0 ? w : (int) DP(w), h < 0 ? h : (int) DP(h));
    }

    public V sizeDP(int v) {
        return sizeDP(v, v);
    }

    /**
     * 设置控件外边距
     */
    public V margin(int s, int t, int e, int b) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp1 = (ViewGroup.MarginLayoutParams) lp;
            s = s == UN ? lp1.getMarginStart() : s;
            t = t == UN ? lp1.topMargin : t;
            e = e == UN ? lp1.rightMargin : e;
            b = b == UN ? lp1.bottomMargin : b;
            lp1.setMarginsRelative(s, t, e, b);
        }
        return this;
    }

    public V margin(int v) {
        return margin(v, v, v, v);
    }

    public V marginDP(int s, int t, int e, int b) {
        return margin((int) DP(s),
                (int) DP(t),
                (int) DP(e),
                (int) DP(b));
    }

    public V marginDP(int v) {
        return marginDP(v, v, v, v);
    }

    /**
     * 设置控件Weight属性
     */
    public V weight(int w) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        if (lp instanceof LinearLayout.LayoutParams) ((LinearLayout.LayoutParams) lp).weight = (float) w;
        return this;
    }

    /**
     * 设置控件Layout_Gravity属性
     */
    public V lGravity(int g) {
        if (lp == null) throw new NullPointerException("控件没有LayoutParams,请先创建");
        if (lp instanceof LinearLayout.LayoutParams) ((LinearLayout.LayoutParams) lp).gravity = g;
        else if (lp instanceof FrameLayout.LayoutParams) ((FrameLayout.LayoutParams) lp).gravity = g;
        return this;
    }

    /*** 从这里开始就是无需刷新的了 ***/
    /**
     * 设置控件ID
     */
    public V id(int id) {
        view.setId(id);
        return this;
    }

    /**
     * 设置灵动效果
     */
    public V nimble() {
        NV.apply(view);
        return this;
    }

    /**
     * 设置控件Padding属性
     * 无需刷新
     */
    public V padding(int s, int t, int e, int b) {
        s = s == UN ? view.getPaddingLeft() : s;
        t = t == UN ? view.getPaddingTop() : t;
        e = e == UN ? view.getPaddingRight() : e;
        b = b == UN ? view.getPaddingBottom() : b;
        if (view instanceof CardView) {
            ((CardView) view).setContentPadding(s, t, e, b);
        } else view.setPaddingRelative(s, t, e, b);
        return this;
    }

    public V padding(int v) {
        return padding(v, v, v, v);
    }

    public V paddingDP(int s, int t, int e, int b) {
        return padding((int) DP(s),
                (int) DP(t),
                (int) DP(e),
                (int) DP(b));
    }

    public V paddingDP(int v) {
        return paddingDP(v, v, v, v);
    }

    /**
     * 设置控件Elevation属性(厚度)
     * 无需刷新
     */
    public V ele(float e) {
        if (view instanceof CardView) {
            ((CardView) view).setCardElevation(e);
            ((CardView) view).setMaxCardElevation(e);
        } else view.setElevation(e);
        return this;
    }

    public V eleDP(int e) {
        return ele((float) DP(e));
    }

    /**
     * 设置控件父级
     * 无需刷新
     */
    public V parent(ViewGroup p) {
        return parent(p, -1);
    }

    public V parent(ViewGroup p, int i) {
        if (p != null) p.addView(view, i);
        return this;
    }

    /**
     * 设置控件圆角Radius
     * 支持ShapeableImageView
     * 无需刷新
     */
    public V radius(float r) {
        if (view instanceof CardView) ((CardView) view).setRadius(r);
        else if (view instanceof ShapeableImageView) {
            ((ShapeableImageView) view).setShapeAppearanceModel(ShapeAppearanceModel
                    .builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, r)
                    .setTopRightCorner(CornerFamily.ROUNDED, r)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, r)
                    .setBottomRightCorner(CornerFamily.ROUNDED, r)
                    .build());
        } else L.e(TAG, "控件:" + view + "无法设置Radius");
        return this;
    }

    public V radiusDP(int r) {
        return radius((float) DP(r));
    }

    /**
     * 设置控件显隐
     * 无需刷新
     */
    public V visibility(int v) {
        view.setVisibility(v);
        return this;
    }

    public V hide() {
        return visibility(View.GONE);
    }

    public V visible() {
        return visibility(View.VISIBLE);
    }

    public V invisible() {
        return visibility(View.INVISIBLE);
    }

    /**
     * 设置控件开关
     */
    public V checkTrue() {
        return check(true);
    }

    public V checkFalse() {
        return check(false);
    }

    public V check(boolean checked) {
        if (view instanceof Checkable) ((Checkable) view).setChecked(checked);
        else L.e(TAG, "控件:" + view + "不允许使用setChecked(boolean)");
        return this;
    }

    /**
     * 设置控件背景
     * CardView只能设置颜色
     * 无需刷新
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public V backgroundRes(int resID) {
        if (resID == 0) return background(null);
        if (isColor(resID)) return background(context.getColor(resID));
        else return background(context.getDrawable(resID));
    }

    /**
     * 设置控件背景色
     */
    public V background(@ColorInt int color) {
        if (view instanceof CardView) ((CardView) view).setCardBackgroundColor(color);
        else view.setBackgroundColor(color);
        return this;
    }

    public V background(Drawable drawable) {
        if (view instanceof CardView) view.setForeground(drawable);
        else view.setBackground(drawable);
        return this;
    }
/*** 从这里开始就是使用反射实现的属性了 ***/
    /*** 反射Find属性 ***/
    public UReflex.UM find(String m, Class<?>... c) {
        return UReflex.findM(view.getClass(), m, c);
    }

    /**
     * 设置控件Hint
     * TextView
     * 无需刷新
     */

    public V hint(@StringRes int resID) {
        return hint(context.getText(resID));
    }

    public V hint(CharSequence s) {
        String name = "setHint";
        UReflex.UM um = find(name, CharSequence.class);
        um.invoke(view, s);
        return log(um, name, CharSequence.class);
    }


    /**
     * 设置控件文本
     * TextView
     * 无需刷新
     */
    public V text(@StringRes int resID) {
        return text(context.getText(resID));
    }

    public V text(CharSequence s) {
        String name = "setText";
        UReflex.UM um = find(name, CharSequence.class);
        um.invoke(view, s);
        return log(um, name, CharSequence.class);
    }


    /**
     * 设置文本颜色资源值
     * Textiew
     * 无需刷新
     */
    public V textColorRes(@ColorRes int resId) {
        return textColor(context.getColor(resId));
    }

    /**
     * 设置文本颜色
     * 不是资源值
     * TextView
     * 无需刷新
     */
    public V textColor(@ColorInt int color) {
        String name = "setTextColor";
        UReflex.UM um = find(name, int.class);
        um.invoke(view, color);
        return log(um, name, int.class);
    }


    /**
     * 设置文本大小
     * 无需刷新
     */
    public V textSize(float sp) {
        String name = "setTextSize";
        UReflex.UM um = find(name, float.class);
        um.invoke(view, sp);
        return log(um, name, float.class);
    }

    /**
     * 设置显示Drawable
     * 可以设置颜色资源值
     * ImageView
     * 无需刷新
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public V drawable(@DrawableRes int resID) {
        if (resID == 0) return drawable((Drawable) null);
        return drawable(context.getDrawable(resID));
    }

    public V drawable(Drawable drawable) {
        String name = "setImageDrawable";
        UReflex.UM um = find(name, Drawable.class);
        um.invoke(view, drawable);
        return log(um, name, Drawable.class);
    }


    /**
     * 设置图片显示方式
     * 无需刷新
     */
    public V scaleType(ImageView.ScaleType type) {
        String name = "setScaleType";
        UReflex.UM um = find(name, ImageView.ScaleType.class);
        um.invoke(view, type);
        return log(um, name, ImageView.ScaleType.class);
    }

    /**
     * 设置布局方向(0横1纵)
     * 无需刷新
     */
    public V orientation(int o) {
        String name = "setOrientation";
        UReflex.UM um = find(name, int.class);
        um.invoke(view, o);
        return log(um, name, int.class);
    }

    /**
     * 设置控件Gravity属性
     * hhh我是不是应该把所有属性都用反射弄啊
     */
    public V gravity(int g) {
        String name = "setGravity";
        UReflex.UM um = find(name, int.class);
        um.invoke(view, g);
        return log(um, name, int.class);
    }

    /**
     * 设置控件Max属性
     */
    public V max(int m) {
        String name = "setMax";
        UReflex.UM um = find(name, int.class);
        um.invoke(view, m);
        return log(um, name, int.class);
    }

    /**
     * 设置控件Min属性
     */
    public V min(int m) {
        String name = "setMin";
        UReflex.UM um = find(name, int.class);
        um.invoke(view, m);
        return log(um, name, int.class);
    }

    /**
     * 刷新控件的布局参数
     */
    public void refresh() {
        if (this.lp != null) view.setLayoutParams(this.lp);
        map.remove(view, this);
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    private float DP(float dp) {
        return ULay.dp2px(context, dp);
    }

    private boolean isColor(@ColorRes int resId) {
        try {
            context.getColor(resId);
            return true;
        } catch (Resources.NotFoundException ignored) {}
        return false;
    }

    private V log(UReflex.UM um, String m, Class<?>... c) {
        if (um.isNull() || um.e != null) {
            String log = String.format("控件[%s].%s(%s)失败", view.getClass().getName(), m, UArrays.toString(c, Class::getSimpleName));
            L.e(TAG, um.e, log);
        }
        return this;
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/


}
