package candyenk.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.*;
import android.widget.LinearLayout;
import candyenk.android.R;
import candyenk.android.tools.A;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import candyenk.android.utils.ULay;
import candyenk.java.utils.UArrays;

import java.util.ArrayList;
import java.util.List;

/**
 * 非常棒的ItemBar
 * 置于底栏,用来做菜单栏灰常棒
 */
public class ItemBar extends LinearLayout {
    /*************************************静态变量**************************************************/

    /*************************************成员变量**************************************************/
    private static final String TAG = ItemBar.class.getSimpleName();
    protected Context context;
    protected ItemIcon[] items;//默认ItemIcon控件集
    protected Animation[] showAnims;//默认展开动画集
    protected Animation[] hideAnims;//默认收起动画集
    protected final List<ItemPackage> list = new ArrayList<>();//ItemPackage List

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

        void onClick();

        void onLongClick();
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
        for (int i = 0; i < items.length; i++) {
            items[i] = createItem();
            super.addView(items[i]);
        }
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

    public void setItem(ItemPackage... items) {
        list.clear();
        UArrays.addArrays(list, items);
    }

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
     * @param item     已加载的Item
     * @param index    指定位置
     * @param callback 展示完毕后的回调
     */
    public void showItem(ItemPackage item, int index, Runnable callback) {
        if (!list.contains(item) || index > 4 || index < 0) return;
        if (V.isInvisible(items[index])) show(item, index, callback);
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
        if (index > 4 || index < 0) return;
        if (V.isVisible(items[index])) hide(index, callback);
    }

    /**
     * 隐藏所有Item
     * 记得在CallBack重新Show一个出来,不然就没得玩了hhh
     */
    public void hideAll(Runnable callback) {
        boolean b = true;
        for (int i = 0; i < items.length; i++) {
            if (V.isVisible(items[i])) {
                hide(i, b ? callback : null);
                b = false;
            }
        }
        if (b) callback.run();
    }

    /**
     * 启用剧中放大(默认true)
     */
    public void enableBigCenter(boolean enable) {
        V.LL(items[2].getIconView()).sizeDP(enable ? 64 : 40).refresh();
        items[2].getTitleView().setVisibility(enable ? View.GONE : View.VISIBLE);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建Item ***/
    private ItemIcon createItem() {
        ItemIcon icon = new ItemIcon(context);
        V.LL(icon).size(-2, -2).invisible().refresh();
        icon.setTitleText("标题");
        icon.setIconResource(R.drawable.ic_ok);
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
        view.setOnClickListener(v -> item.onClick());
        view.setOnLongClickListener(v -> {
            item.onLongClick();
            return true;
        });
        float x = V.getParent(view).getWidth() * 0.5f;
        float y = V.getParent(view).getHeight() * 0.5f;
        Animation anim = createAnim(true, index);
        anim.setAnimationListener((A.EAL) a -> {
            view.clearAnimation();
            if (callback != null) callback.run();
        });
        V.visible(view);
        view.startAnimation(anim);
    }

    /*** 隐藏Item具体实现 ***/
    private void hide(int index, Runnable callback) {
        ItemIcon view = items[index];
        Animation anim = createAnim(false, index);
        anim.setAnimationListener((A.EAL) a -> {
            V.invisible(view);
            view.clearAnimation();
            if (callback != null) callback.run();
        });
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
        public Runnable onClick, onLongClick;

        public ItemWrapper(CharSequence title, Drawable icon, Runnable onClick, Runnable onLongClick) {
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
        public void onClick() {
            if (onClick != null) onClick.run();
        }

        @Override
        public void onLongClick() {
            if (onLongClick != null) onLongClick.run();
        }
    }
}

