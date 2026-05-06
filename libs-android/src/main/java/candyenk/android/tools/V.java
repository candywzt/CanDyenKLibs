package candyenk.android.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.utils.ULay;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.Shapeable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Android View的帮助类
 */
public class V<T extends View> {
    /** 在多参数方法中指代不改变当前项 */
    public static final int UN = Integer.MIN_VALUE;
    /** 在获取嵌套父级方法中指代最顶级父控件 */
    public static final int SUPER = Integer.MAX_VALUE;
    private static final String TAG = "ViewHelper";
    private final T view;
    private final Context context;
    private ViewGroup.LayoutParams lp;
    
    private V(T v) {
        this.view = v;
        this.context = v.getContext();
        this.lp = view.getLayoutParams();
    }
    
    /**
     * 获取边距布局参数
     * 请注意类型，不符会报错
     *
     * @param v 指定控件
     * @return 布局信息
     */
    public static ViewGroup.MarginLayoutParams getMLP(View v) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp == null) throw new NullPointerException("该控件暂未设置布局参数");
        else if (lp instanceof ViewGroup.MarginLayoutParams mlp) return mlp;
        throw new ClassCastException("该控件的布局参数类型不符");
    }
    
    /**
     * 获取线性布局参数
     * 请注意类型，不符会报错
     *
     * @param v 指定控件
     * @return 布局信息
     */
    public static LinearLayout.LayoutParams getLLP(View v) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp == null) throw new NullPointerException("该控件暂未设置布局参数");
        else if (lp instanceof LinearLayout.LayoutParams llp) return llp;
        throw new ClassCastException("该控件的布局参数类型不符");
    }
    
    /**
     * 获取框架布局参数
     * 请注意类型，不符会报错
     *
     * @param v 指定控件
     * @return 布局信息
     */
    public static FrameLayout.LayoutParams getFLP(View v) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp == null) throw new NullPointerException("该控件暂未设置布局参数");
        else if (lp instanceof FrameLayout.LayoutParams llp) return llp;
        throw new ClassCastException("该控件的布局参数类型不符");
    }
    
    /**
     * 获取相对布局参数
     * 请注意类型，不符会报错
     *
     * @param v 指定控件
     * @return 布局信息
     */
    public static RelativeLayout.LayoutParams getRLP(View v) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp == null) throw new NullPointerException("该控件暂未设置布局参数");
        else if (lp instanceof RelativeLayout.LayoutParams rlp) return rlp;
        throw new ClassCastException("该控件的布局参数类型不符");
    }
    
    /**
     * 获取数据集布局参数
     * 请注意类型，不符会报错
     *
     * @param v 指定控件
     * @return 布局信息
     */
    public static RecyclerView.LayoutParams getRVL(View v) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if (lp == null) throw new NullPointerException("该控件暂未设置布局参数");
        else if (lp instanceof RecyclerView.LayoutParams rvl) return rvl;
        throw new ClassCastException("该控件的布局参数类型不符");
    }
    
    /**
     * 通过触摸事件获取元数据视图
     *
     * @param v   数据集控件
     * @param e   触摸事件
     * @param <T> 元数据类型
     * @return 指定元数据
     */
    public static <T extends RecyclerView.ViewHolder> T getHolder(RecyclerView v, MotionEvent e) {
        return getHolder(v, e.getX(), e.getY());
    }
    
    /**
     * 通过坐标值获取元数据视图
     *
     * @param v   数据集控件
     * @param x   X轴坐标值
     * @param y   Y轴坐标值
     * @param <T> 元数据类型
     * @return 指定元数据
     */
    @SuppressWarnings("unchecked")
    public static <T extends RecyclerView.ViewHolder> T getHolder(RecyclerView v, float x, float y) {
        View c = v.findChildViewUnder(x, y);
        return c == null ? null : (T) v.getChildViewHolder(c);
    }
    
    /**
     * 获取指定ID的子控件
     *
     * @param v   父控件
     * @param id  控件ID
     * @param c   控件类型
     * @param <T> 控件类型
     * @return 指定ID的控件
     */
    public static <T extends View> T findId(View v, @IdRes int id, Class<T> c) {
        if (v == null) return null;
        return v.findViewById(id);
    }
    
    /**
     * 获取指定索引的子控件
     *
     * @param v   父控件
     * @param i   控件索引
     * @param c   控件类型
     * @param <T> 控件类型
     * @return 指定索引的控件
     */
    public static <T extends View> T getChild(View v, int i, Class<T> c) {
        return getChild(v, i);
    }
    
    /**
     * 获取指定索引的子控件
     *
     * @param v   父控件
     * @param i   控件索引
     * @param <T> 控件类型
     * @return 指定索引的控件
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getChild(View v, int i) {
        if (v instanceof ViewGroup vg) return (T) vg.getChildAt(i);
        return null;
    }
    
    /**
     * 获取第一个指定类型的子控件
     * 类型必须完全相等
     *
     * @param v   父控件
     * @param c   指定类型
     * @param <T> 控件类型
     * @return 指定类型的控件
     */
    public static <T extends View> T getChild(View v, Class<T> c) {
        return getChild(v, c, 0);
    }
    
    /**
     * 获取第i个指定类型的子控件
     * 类型必须完全相等
     * 从0开始
     *
     * @param v   父控件
     * @param c   指定类型
     * @param <T> 控件类型
     * @return 指定类型的控件
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getChild(View v, Class<T> c, int i) {
        for (View view : getChilds(v)) if (view.getClass() == c) if (i-- == 0) return (T) view;
        return null;
    }
    
    /**
     * 获取所有子控件
     *
     * @param v 父控件
     * @return 子控件数组
     */
    public static View[] getChilds(View v) {
        if (v instanceof ViewGroup vg) {
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
     * 获取嵌套子控件
     *
     * @param v   父控件
     * @param c   控件类型
     * @param i   嵌套位置数组，例：【0,2,3】：获取该控件的0号子控件的2号子控件的3号子控件
     * @param <T> 控件类型
     * @return 指定控件
     */
    public static <T extends View> T getChild(View v, Class<T> c, int... i) {
        return getChild(v, i);
    }
    
    /**
     * 获取嵌套子控件
     *
     * @param v   父控件
     * @param i   嵌套位置数组，例：【0,2,3】：获取该控件的0号子控件的2号子控件的3号子控件
     * @param <T> 控件类型
     * @return 指定控件
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T getChild(View v, int... i) {
        View view = v;
        for (int ii : i) {
            if (v == null || ii < 0) break;
            view = getChild(view, ii);
        }
        return (T) view;
    }
    
    /**
     * 获取父级控件
     *
     * @param v   子控件
     * @param <T> 控件类型
     * @return 指定控件
     */
    public static <T extends ViewGroup> T getParent(View v, Class<T> c) {
        return getParent(v);
    }
    
    /**
     * 获取父级控件
     *
     * @param v   子控件
     * @param <T> 控件类型
     * @return 指定控件
     */
    public static <T extends ViewGroup> T getParent(View v) {
        return getParent(v, 0);
    }
    
    /**
     * 获取嵌套父级控件
     * 有一层为null则返回null
     *
     * @param v   子控件
     * @param i   嵌套层数，例.0层就是自己的父级，1层就是父级的父级
     *            {{@link #SUPER}}代表获取最顶级ViewGroup
     * @param <T> 控件类型
     * @return 指定控件
     */
    public static <T extends ViewGroup> T getParent(View v, Class<T> c, int i) {
        return getParent(v, i);
    }
    
    /**
     * 获取嵌套父级控件
     * 有一层为null则返回null
     *
     * @param v   子控件
     * @param i   嵌套层数，例.0层就是自己的父级，1层就是父级的父级
     *            {{@link #SUPER}}代表获取最顶级ViewGroup
     * @param <T> 控件类型
     * @return 指定控件
     */
    @SuppressWarnings("unchecked")
    public static <T extends ViewGroup> T getParent(View v, int i) {
        if (i < 0 || v == null) return null;
        ViewParent vp = v.getParent();
        if (i == SUPER) while (vp != null && vp.getParent() != null) vp = vp.getParent();
        else while (i-- > 0 && vp != null) vp = vp.getParent();
        return (T) vp;
    }
    
    /**
     * 批量添加子控件
     *
     * @param v 父控件
     * @param c 子控件组
     */
    public static void addView(View v, View... c) {
        if (v instanceof ViewGroup vg) for (View view : c) vg.addView(view);
    }
    
    /**
     * 批量设置控件可见性
     *
     * @param i 控件可见性
     * @param v 控件组
     */
    public static void visibility(@Visibility int i, View... v) {
        for (View view : v) if (view != null) view.setVisibility(i);
    }
    
    /**
     * 批量设置控件不占位
     *
     * @param v 控件组
     */
    public static void hide(View... v) {
        for (View view : v) if (view != null && !V.isHide(view)) view.setVisibility(View.GONE);
    }
    
    /**
     * 批量设置控件不可见
     *
     * @param v 控件组
     */
    public static void visible(View... v) {
        for (View view : v)
            if (view != null && !V.isVisible(view)) view.setVisibility(View.VISIBLE);
    }
    
    /**
     * 批量设置控件可见
     *
     * @param v 控件组
     */
    public static void invisible(View... v) {
        for (View view : v)
            if (view != null && !V.isInvisible(view)) view.setVisibility(View.INVISIBLE);
    }
    
    /**
     * 检查控件是否不占位
     *
     * @param v 控件
     * @return 控件是否不占位
     */
    public static boolean isHide(View v) {
        return v != null && v.getVisibility() == View.GONE;
    }
    
    /**
     * 检查控件是否不可见
     *
     * @param v 控件
     * @return 控件是否不可见
     */
    public static boolean isInvisible(View v) {
        return v != null && v.getVisibility() == View.INVISIBLE;
    }
    
    /**
     * 检查控件是否可见
     *
     * @param v 控件
     * @return 控件是否可见
     */
    public static boolean isVisible(View v) {
        return v != null && v.getVisibility() == View.VISIBLE;
    }
    
    /**
     * 批量设置控件开关状态
     * 仅限于Checkable控件
     *
     * @param c 开关状态
     * @param v 控件组
     */
    public static void check(boolean c, View... v) {
        for (View view : v) if (view instanceof Checkable ca) ca.setChecked(c);
    }
    
    /**
     * 批量设置控件打开
     *
     * @param v 控件组
     */
    public static void checkTrue(View... v) {
        check(true, v);
    }
    
    /**
     * 批量设置控件关闭
     *
     * @param v 控件组
     */
    public static void checkFalse(View... v) {
        check(false, v);
    }
    
    /**
     * 批量设置控件点击监听
     *
     * @param l 点按监听
     * @param v 控件组
     */
    public static void click(View.OnClickListener l, View... v) {
        for (View view : v) if (view != null) view.setOnClickListener(l);
    }
    
    /**
     * 批量设置控件长按监听
     * 自动返回true
     *
     * @param l 长按监听
     * @param v 控件组
     */
    public static void lClick(View.OnClickListener l, View... v) {
        View.OnLongClickListener ol = v1 -> {
            l.onClick(v1);
            return true;
        };
        for (View view : v) if (view != null) view.setOnLongClickListener(ol);
    }
    
    /**
     * 从基础布局信息开始创建调用链
     * 包含属性：宽，高
     *
     * @param v   作用控件
     * @param <T> 控件类型
     * @return 调用链对象
     */
    public static <T extends View> V<T> LP(T v) {
        V<T> vh = new V<>(v);
        if (vh.lp == null) vh.lp = new ViewGroup.LayoutParams(-2, -2);
        return vh;
    }
    
    /**
     * 从边距布局信息开始创建调用链
     * 包含属性：宽，高，四外边距
     *
     * @param v   作用控件
     * @param <T> 控件类型
     * @return 调用链对象
     */
    public static <T extends View> V<T> ML(T v) {
        V<T> vh = new V<>(v);
        if (vh.lp == null) vh.lp = new ViewGroup.MarginLayoutParams(-2, -2);
        return vh;
    }
    
    /**
     * 从框架布局信息开始创建调用链
     * 包含属性：宽，高，四外边距，自身重力
     *
     * @param v   作用控件
     * @param <T> 控件类型
     * @return 调用链对象
     */
    public static <T extends View> V<T> FL(T v) {
        V<T> vh = new V<>(v);
        if (vh.lp == null) vh.lp = new FrameLayout.LayoutParams(-2, -2);
        return vh;
    }
    
    /**
     * 从线性布局信息开始创建调用链
     * 包含属性：宽，高，四外边距，自身重力，权重
     *
     * @param v   作用控件
     * @param <T> 控件类型
     * @return 调用链对象
     */
    public static <T extends View> V<T> LL(T v) {
        V<T> vh = new V<>(v);
        if (vh.lp == null) vh.lp = new LinearLayout.LayoutParams(-2, -2);
        return vh;
    }
    
    /**
     * 从相对布局信息开始创建调用链
     * 包含属性：LEFT_OF、RIGHT_OF、ALIGN_PARENT_XXX 等
     *
     * @param v   作用控件
     * @param <T> 控件类型
     * @return 调用链对象
     * @deprecated 已过时
     */
    @Deprecated
    public static <T extends View> V<T> RL(T v) {
        V<T> vh = new V<>(v);
        if (vh.lp == null) vh.lp = new RelativeLayout.LayoutParams(-2, -2);
        return vh;
    }
    
    /**
     * 从数据集布局信息开始创建调用链
     * 无多余属性
     *
     * @param v   作用控件
     * @param <T> 控件类型
     * @return 调用链对象
     */
    public static <T extends View> V<T> RV(T v) {
        V<T> vh = new V<>(v);
        if (vh.lp == null) vh.lp = new RecyclerView.LayoutParams(-2, -2);
        return vh;
    }
    
    /**
     * 设置宽高
     * 如果没有布局信息，将自动创建
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param w 宽px
     * @param h 高px
     * @return 调用链
     */
    public V<T> size(int w, int h) {
        if (lp == null) lp = new ViewGroup.LayoutParams(-2, -2);
        lp.width = w == UN ? lp.width : w;
        lp.height = h == UN ? lp.height : h;
        return this;
    }
    
    /**
     * 设置宽高
     * 如果没有布局信息，将自动创建
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param v 宽高px
     * @return 调用链
     */
    public V<T> size(int v) {
        return size(v, v);
    }
    
    /**
     * 设置宽高
     * 如果没有布局信息，将自动创建
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param w 宽dp
     * @param h 高dp
     * @return 调用链
     */
    public V<T> sizeDP(int w, int h) {
        return size(w < 0 ? w : (int) DP(w), h < 0 ? h : (int) DP(h));
    }
    
    /**
     * 设置宽高
     * 如果没有布局信息，将自动创建
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param v 宽高dp
     * @return 调用链
     */
    public V<T> sizeDP(int v) {
        return sizeDP(v, v);
    }
    
    /**
     * 设置外边距
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param l 左边距
     * @param t 上边距
     * @param r 右边距
     * @param b 下边距
     * @return 调用链
     */
    public V<T> margin(int l, int t, int r, int b) {
        if (lp instanceof ViewGroup.MarginLayoutParams lp1) {
            l = l == UN ? lp1.leftMargin : l;
            t = t == UN ? lp1.topMargin : t;
            r = r == UN ? lp1.rightMargin : r;
            b = b == UN ? lp1.bottomMargin : b;
            lp1.setMargins(l, t, r, b);
            return this;
        } else throw new ClassCastException("该控件的布局参数类型不符");
    }
    
    /**
     * 设置外边距
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param vv 边距
     * @return 调用链
     */
    public V<T> margin(int vv) {
        return margin(vv, vv, vv, vv);
    }
    
    /**
     * 设置外边距
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param l 左边距dp
     * @param t 上边距dp
     * @param r 右边距dp
     * @param b 下边距dp
     * @return 调用链
     */
    public V<T> marginDP(int l, int t, int r, int b) {
        return margin((int) DP(l), (int) DP(t), (int) DP(r), (int) DP(b));
    }
    
    /**
     * 设置外边距
     * 如果传入{@link #UN},则不改变
     * 需要刷新{@link #refresh()}
     *
     * @param vv 边距dp
     * @return 调用链
     */
    public V<T> marginDP(int vv) {
        return marginDP(vv, vv, vv, vv);
    }
    
    /**
     * 设置权重
     * 仅支持线性布局， LinearLayout
     * 需要刷新{@link #refresh()}
     *
     * @param w 权重
     * @return 调用链
     */
    public V<T> weight(int w) {
        if (lp instanceof LinearLayout.LayoutParams lp1) lp1.weight = (float) w;
        else throw new ClassCastException("该控件的布局参数类型不符");
        return this;
    }
    
    /**
     * 设置自身重力
     * 仅支持线性布局和框架布局，LinearLayout And FrameLayout
     * 需要刷新{@link #refresh()}
     *
     * @param g 重力
     * @return 调用链
     */
    public V<T> lGravity(@GravityInt int g) {
        if (lp instanceof LinearLayout.LayoutParams lp1) lp1.gravity = g;
        else if (lp instanceof FrameLayout.LayoutParams lp1) lp1.gravity = g;
        else throw new ClassCastException("该控件的布局参数类型不符");
        return this;
    }
    
    /**
     * 设置内距
     *
     * @param l 左内距px
     * @param t 上内距px
     * @param r 右内距px
     * @param b 下内距px
     * @return 调用链
     */
    public V<T> padding(int l, int t, int r, int b) {
        l = l == UN ? view.getPaddingLeft() : l;
        t = t == UN ? view.getPaddingTop() : t;
        r = r == UN ? view.getPaddingRight() : r;
        b = b == UN ? view.getPaddingBottom() : b;
        if (view instanceof CardView cv) {
            cv.setContentPadding(l, t, r, b);
        } else view.setPadding(l, t, r, b);
        return this;
    }
    
    /**
     * 设置内距
     *
     * @param vv 内距
     * @return 调用链
     */
    public V<T> padding(int vv) {
        return padding(vv, vv, vv, vv);
    }
    
    /**
     * 设置内距
     *
     * @param l 左内距dp
     * @param t 上内距dp
     * @param r 右内距pdp
     * @param b 下内距dp
     * @return 调用链
     */
    public V<T> paddingDP(int l, int t, int r, int b) {
        return padding((int) DP(l), (int) DP(t), (int) DP(r), (int) DP(b));
    }
    
    /**
     * 设置内距
     *
     * @param vv 内距dp
     * @return 调用链
     */
    public V<T> paddingDP(int vv) {
        return paddingDP(vv, vv, vv, vv);
    }
    
    /**
     * 设置厚度
     *
     * @param e 厚度px
     * @return 调用链
     */
    public V<T> ele(float e) {
        if (view instanceof CardView cv) {
            cv.setCardElevation(e);
            cv.setMaxCardElevation(e);
        } else view.setElevation(e);
        return this;
    }
    
    /**
     * 设置厚度
     *
     * @param e 厚度dp
     * @return 调用链
     */
    public V<T> eleDP(int e) {
        return ele(DP(e));
    }
    
    /**
     * 设置圆角
     *
     * @param tl 左上圆角px
     * @param tr 右上圆角px
     * @param bl 右下圆角px
     * @param br 左下圆角px
     * @return 调用链
     */
    public V<T> radius(float tl, float tr, float bl, float br) {
        if (view instanceof Shapeable v) {
            ShapeAppearanceModel.Builder b = ShapeAppearanceModel.builder();
            v.setShapeAppearanceModel(b.setTopLeftCorner(CornerFamily.ROUNDED, tl)
                                       .setTopRightCorner(CornerFamily.ROUNDED, tr)
                                       .setBottomLeftCorner(CornerFamily.ROUNDED, bl)
                                       .setBottomRightCorner(CornerFamily.ROUNDED, br).build());
        } else throw new UnsupportedOperationException("该控件不支持设置独立圆角属性");
        return this;
    }
    
    /**
     * 设置圆角
     *
     * @param r 圆角px
     * @return 调用链
     */
    public V<T> radius(float r) {
        if (view instanceof CardView v) v.setRadius(r);
        else if (view instanceof Shapeable) return radius(r, r, r, r);
        else throw new UnsupportedOperationException("该控件不支持设置圆角属性");
        return this;
    }
    
    /**
     * 设置圆角
     *
     * @param tl 左上圆角dp
     * @param tr 右上圆角dp
     * @param bl 右下圆角dp
     * @param br 左下圆角dp
     * @return 调用链
     */
    public V<T> radiusDP(int tl, int tr, int bl, int br) {
        return radius(DP(tl), DP(tr), DP(bl), DP(br));
    }
    
    /**
     * 设置圆角
     *
     * @param r 圆角dp
     * @return 调用链
     */
    public V<T> radiusDP(int r) {
        return radius(DP(r));
    }
    
    /**
     * 设置ID
     *
     * @param id id属性值
     * @return 调用链
     */
    public V<T> id(int id) {
        view.setId(id);
        return this;
    }
    
    /**
     * 设置灵动效果
     * 详细查看{@link NV#apply(View)}
     *
     * @return 调用链
     */
    public V<T> nimble() {
        NV.apply(view);
        return this;
    }
    
    /**
     * 设置可见性属性
     *
     * @param v 可见性
     * @return 调用链
     */
    public V<T> visibility(@Visibility int v) {
        view.setVisibility(v);
        return this;
    }
    
    /**
     * 设置可见性-不占位
     *
     * @return 调用链
     */
    public V<T> hide() {
        return visibility(View.GONE);
    }
    
    /**
     * 设置可见性-可见
     *
     * @return 调用链
     */
    public V<T> visible() {
        return visibility(View.VISIBLE);
    }
    
    /**
     * 设置可见性-不可见
     *
     * @return 调用链
     */
    public V<T> invisible() {
        return visibility(View.INVISIBLE);
    }
    
    /**
     * 设置开关状态
     *
     * @param checked 开关状态
     * @return 调用链
     */
    public V<T> check(boolean checked) {
        if (view instanceof Checkable c) c.setChecked(checked);
        else throw new UnsupportedOperationException("该控件不支持设置开关属性");
        return this;
    }
    
    /**
     * 设置开关状态-开
     *
     * @return 调用链
     */
    public V<T> checkTrue() {
        return check(true);
    }
    
    /**
     * 设置开关状态-关
     *
     * @return 调用链
     */
    public V<T> checkFalse() {
        return check(false);
    }
    
    /**
     * 设置背景资源ID
     * 支持图像资源和颜色资源
     *
     * @param resID 资源ID,0等于null
     * @return 调用链
     */
    public V<T> backgroundRes(int resID) {
        if (resID == 0) return background(null);
        if (isColor(resID)) return background(context.getColor(resID));
        else return background(context.getDrawable(resID));
    }
    
    /**
     * 设置背景图像
     * CardView 采用setForeground方法
     *
     * @param drawable 图像
     * @return 调用链
     */
    public V<T> background(Drawable drawable) {
        if (view instanceof CardView) view.setForeground(drawable);
        else view.setBackground(drawable);
        return this;
    }
    
    /**
     * 设置背景颜色
     * CardView采用setCardBackgroundColor方法
     *
     * @param color 颜色值，不是资源值
     * @return 调用链
     */
    public V<T> background(@ColorInt int color) {
        if (view instanceof CardView c) c.setCardBackgroundColor(color);
        else view.setBackgroundColor(color);
        return this;
    }
    
    /**
     * 设置控件提示词
     * 仅支持TextView
     *
     * @param resID 文本资源ID
     * @return 调用链
     */
    public V<T> hint(@StringRes int resID) {
        if (view instanceof TextView tv) tv.setHint(resID);
        else throw new UnsupportedOperationException("该控件不支持设置Hint属性");
        return this;
    }
    
    /**
     * 设置控件提示词文本
     * 仅支持TextView
     *
     * @param s 提示词
     * @return 调用链
     */
    public V<T> hint(CharSequence s) {
        if (view instanceof TextView tv) tv.setHint(s);
        else throw new UnsupportedOperationException("该控件不支持设置Hint属性");
        return this;
    }
    
    /**
     * 设置控件提示词颜色
     * 仅支持TextView
     *
     * @param resId 颜色资源ID
     * @return 调用链
     */
    public V<T> hintColorRes(@ColorRes int resId) {
        return hintColor(context.getColor(resId));
    }
    
    /**
     * 设置控件提示词颜色
     * 仅支持TextView
     *
     * @param color 颜色值，不是资源值
     * @return 调用链
     */
    public V<T> hintColor(@ColorInt int color) {
        if (view instanceof TextView tv) tv.setHintTextColor(color);
        else throw new UnsupportedOperationException("该控件不支持设置HintColor属性");
        return this;
    }
    
    /**
     * 设置控件文本
     * 仅支持TextView
     *
     * @param resID 文本资源ID
     * @return 调用链
     */
    public V<T> text(@StringRes int resID) {
        if (view instanceof TextView tv) tv.setText(resID);
        else throw new UnsupportedOperationException("该控件不支持设置Text属性");
        return this;
    }
    
    /**
     * 设置控件文本
     * 仅支持TextView
     *
     * @param s 文本
     * @return 调用链
     */
    public V<T> text(CharSequence s) {
        if (view instanceof TextView tv) tv.setText(s);
        else throw new UnsupportedOperationException("该控件不支持设置Text属性");
        return this;
    }
    
    /**
     * 设置文本颜色
     * 仅支持TextView
     *
     * @param resId 颜色资源值
     * @return 调用链
     */
    public V<T> textColorRes(@ColorRes int resId) {
        return textColor(context.getColor(resId));
    }
    
    /**
     * 设置文本颜色
     * 仅支持TextView
     *
     * @param color 颜色值，不是资源ID
     * @return 调用链
     */
    public V<T> textColor(@ColorInt int color) {
        if (view instanceof TextView tv) tv.setTextColor(color);
        else throw new UnsupportedOperationException("该控件不支持设置HintColor属性");
        return this;
    }
    
    /**
     * 设置文本大小
     * 仅支持TextView
     *
     * @param sp 文本大小sp
     * @return 调用链
     */
    public V<T> textSize(float sp) {
        if (view instanceof TextView tv) tv.setTextSize(sp);
        else throw new UnsupportedOperationException("该控件不支持设置HintColor属性");
        return this;
    }
    
    /**
     * 设置图像内容
     * 仅支持ImageView
     *
     * @param resID 图像资源ID
     * @return 调用链
     */
    public V<T> drawable(@DrawableRes int resID) {
        if (view instanceof ImageView iv) iv.setImageResource(resID);
        else throw new UnsupportedOperationException("该控件不支持设置ImageDradable属性");
        return this;
    }
    
    /**
     * 设置图像内容
     * 仅支持ImageView
     *
     * @param drawable 图像
     * @return 调用链
     */
    public V<T> drawable(Drawable drawable) {
        if (view instanceof ImageView iv) iv.setImageDrawable(drawable);
        else throw new UnsupportedOperationException("该控件不支持设置ImageDradable属性");
        return this;
    }
    
    /**
     * 设置图像显示方式
     * 仅支持ImageView
     *
     * @param type 显示方式
     * @return 调用链
     */
    public V<T> scaleType(ImageView.ScaleType type) {
        if (view instanceof ImageView iv) iv.setScaleType(type);
        else throw new UnsupportedOperationException("该控件不支持设置ScaleType属性");
        return this;
    }
    
    /**
     * 设置布局方向Orientation
     * 仅支持线性布局，LinearLayout
     * 0水平，1垂直
     *
     * @param o 方向
     * @return 调用链
     */
    public V<T> orientation(int o) {
        if (view instanceof LinearLayout ll) ll.setOrientation(o);
        else throw new UnsupportedOperationException("该控件不支持设置Orientation属性");
        return this;
    }
    
    /**
     * 设置子控件重力
     *
     * @param g 重力
     * @return 调用链
     */
    public V<T> gravity(@GravityInt int g) {
        if (view instanceof LinearLayout ll) ll.setGravity(g);
        else if (view instanceof TextView tv) tv.setGravity(g);
        else throw new UnsupportedOperationException("该控件不支持设置Gravity属性");
        return this;
    }
    
    /**
     * 添加子控件
     *
     * @param v 控件组
     * @return 调用链
     */
    public V<T> add(View... v) {
        if (view instanceof ViewGroup vg) for (View vv : v) vg.addView(vv);
        return this;
    }
    
    /**
     * 设置控件父级
     * 自带刷新效果
     * 最终方法，后面不能跟着调用链
     *
     * @param p 父级控件
     * @return 当前控件
     */
    public T parent(ViewGroup p) {
        return parent(p, -1);
    }
    
    /**
     * 设置控件父级
     * 自带刷新效果
     * 最终方法，后面不能跟着调用链
     *
     * @param p 父级控件
     * @param i 在父控件中的位置
     * @return 当前控件
     */
    public T parent(ViewGroup p, int i) {
        if (p != null) p.addView(view, i, lp);
        return view;
    }
    
    /**
     * 刷新属性设置
     * 最终方法，后面不能跟着调用链
     *
     * @return 当前控件
     */
    public T refresh() {
        if (this.lp != null) view.setLayoutParams(this.lp);
        return view;
    }
    
    /** dp转px */
    private float DP(float dp) {
        return ULay.dp2px(context, dp);
    }
    
    /** 判断该资源ID是不是颜色 **/
    private boolean isColor(@ColorRes int resId) {
        try {
            context.getColor(resId);
            return true;
        } catch (Resources.NotFoundException ignored) {}
        return false;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({View.VISIBLE, View.INVISIBLE, View.GONE})
    public @interface Visibility {}
    
    @IntDef(flag = true, value = {Gravity.FILL, Gravity.FILL_HORIZONTAL, Gravity.FILL_VERTICAL, Gravity.START, Gravity.END, Gravity.LEFT, Gravity.RIGHT, Gravity.TOP, Gravity.BOTTOM, Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL, Gravity.DISPLAY_CLIP_HORIZONTAL, Gravity.DISPLAY_CLIP_VERTICAL, Gravity.CLIP_HORIZONTAL, Gravity.CLIP_VERTICAL, Gravity.NO_GRAVITY})
    public @interface GravityInt {}
}
