package candyenk.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.*;
import android.widget.LinearLayout;
import candyenk.android.tools.A;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.android.utils.ULay;

/**
 * 非常棒的ItemBar
 * 置于底栏,用来做菜单栏灰常棒
 */
public class ItemBar extends LinearLayout {
    /*************************************静态变量**************************************************/

    /*************************************成员变量**************************************************/
    protected static final String TAG = ItemBar.class.getSimpleName();
    protected Context context;
    protected ItemIcon[] items;//默认ItemIcon控件集
    protected ItemPackage[] packs;//当前显示的Item集

    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /**
     * Item包接口
     */
    public interface ItemPackage {
        CharSequence getTitle();

        Drawable getIcon();

        void onClick(View v);

        boolean onLongClick(View v);
    }
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public ItemBar(Context context) {
        this(context, null);
    }

    public ItemBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initLayout();
        initAnim();
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    protected float dp2px(float dp) {
        return ULay.dp2px(context, dp);
    }

    protected void initLayout() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        this.items = new ItemIcon[5];
        this.packs = new ItemPackage[5];
        for (int i = 0; i < items.length; i++) items[i] = createItem();
    }

    protected void initAnim() {

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        L.e(TAG, "不支持的操作");
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        L.e(TAG, "不支持的操作");
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 展示Item到指定位置
     *
     * @param item  已加载的Item
     * @param index 指定位置(0-4)
     */
    public void showItem(ItemPackage item, int index) {
        showItem(item, index, null);
    }

    /**
     * 展示Item到指定位置
     *
     * @param item     指定Item
     * @param index    指定位置
     * @param callback 展示完毕后的回调
     */
    public void showItem(ItemPackage item, int index, Runnable callback) {
        if (index > 4 || index < 0) return;//索引越界
        if (packs[index] == null) show(item, index, callback);//当前位置为空,直接显示
        else if (packs[index] != item) hide(index, () -> show(item, index, callback));//当前位置有其他Item
        else if (callback != null) callback.run();//当前位置就是当前Item,直接执行回调
    }

    /**
     * 隐藏指定位置的Item
     *
     * @param index 指定位置
     */
    public void hideItem(int index) {
        hideItem(index, null);
    }

    /**
     * 隐藏指定位置的Item
     *
     * @param index    指定位置
     * @param callback 隐藏完毕的回调
     */
    public void hideItem(int index, Runnable callback) {
        if (index > 4 || index < 0) return;//索引越界
        if (packs[index] != null) hide(index, callback);//当前位置不为空,直接隐藏
        else if (callback != null) callback.run();//当前位置为空,直接执行回调
    }

    /**
     * 隐藏所有Item
     * 记得在CallBack重新Show一个出来,不然就没得玩了hhh
     */
    public void hideAll(Runnable callback) {
        boolean b = true;
        for (int i = 0; i < packs.length; i++) {
            if (packs[i] == null) continue;
            hide(i, b ? callback : null);
            b = false;
        }
        if (b) callback.run();
    }

    /**
     * 启用剧中放大(默认false)
     */
    public void enableBigCenter(boolean enable) {
        V.LL(items[2].getIconView()).sizeDP(enable ? 64 : 48).refresh();
        items[2].getTitleView().setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    /**
     * 获取指定位置的Item
     */
    public ItemIcon getItem(int index) {
        if (index < 0 || index > 4) return null;
        return items[index];
    }

    /**
     * 在指定位置展示指定标题
     * 传参null则显示该位置Item的标题
     */
    public void showTitle(CharSequence title, int index) {
        new Popup(getItem(index))
                .setContent(title == null ? packs[index].getTitle() : title)
                .show();
    }

    public void showTitle(int titleId, int index) {
        showTitle(titleId == 0 ? null : context.getText(titleId), index);
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建Item ***/
    private ItemIcon createItem() {
        ItemIcon icon = new ItemIcon(context);
        V.LL(icon).size(-2, -2).marginDP(12, 0, 12, 0).invisible().parent(this).refresh();
        return icon;
    }

    /*** 创建中间动画(isShow,isCenter,X,Y) ***/
    private Animation createAnim(boolean s, int i) {
        float txy = items[i].getWidth() * (i == 0 ? 2 : i == 1 ? 1 : i == 3 ? -1 : i == 4 ? -2 : 0);//平移动画XY始末
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(100);
        set.addAnimation(new ScaleAnimation(s ? 0 : 1, s ? 1 : 0, s ? 0 : 1, s ? 1 : 0, items[i].getWidth() * 0.5f, items[i].getHeight() * 0.5f));
        if (i != 2) set.addAnimation(new TranslateAnimation(s ? txy : 0, s ? 0 : txy, 0, 0));
        return set;
    }

    /*** 展示Item具体实现 ***/
    private void show(ItemPackage item, int index, Runnable callback) {
        ItemIcon view = items[index];
        view.setTitleText(item.getTitle());
        view.setIconDrawable(item.getIcon());
        view.setOnClickListener(item::onClick);
        view.setOnLongClickListener(item::onLongClick);
        Animation anim = createAnim(true, index);
        anim.setAnimationListener((A.EndV) a -> {
            view.clearAnimation();
            if (callback != null) callback.run();
        });
        V.visible(view);
        packs[index] = item;
        view.startAnimation(anim);
    }

    /*** 隐藏Item具体实现 ***/
    private void hide(int index, Runnable callback) {
        ItemIcon view = items[index];
        Animation anim = createAnim(false, index);
        anim.setAnimationListener((A.EndV) a -> {
            V.invisible(view);
            view.clearAnimation();
            if (callback != null) callback.run();
        });
        packs[index] = null;
        view.startAnimation(anim);
    }
/**********************************************************************************************/
/**************************************内部类***************************************************/
/**********************************************************************************************/

    /**
     * Item包装器
     */
    public static class ItemWrapper implements ItemPackage {
        public CharSequence title;
        public Drawable icon;
        public View.OnClickListener onClick, onLongClick;

        public ItemWrapper(Context context, int titleId, int iconId, View.OnClickListener onClick, View.OnClickListener onLongClick) {
            this.title = context.getText(titleId);
            this.icon = context.getDrawable(iconId);
            this.onClick = onClick;
            this.onLongClick = onLongClick;
        }

        public ItemWrapper(CharSequence title, Drawable icon, View.OnClickListener onClick, View.OnClickListener onLongClick) {
            this.title = title;
            this.icon = icon;
            this.onClick = onClick;
            this.onLongClick = onLongClick;
        }

        @Override
        public CharSequence getTitle() {
            return title;
        }

        @Override
        public Drawable getIcon() {
            return icon;
        }

        @Override
        public void onClick(View v) {
            if (onClick != null) onClick.onClick(v);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onLongClick != null) onLongClick.onClick(v);
            return true;
        }
    }
}

