package candyenk.android.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import java.util.function.Consumer;

/**
 * 布局绑定帮助类
 */
public abstract class VB implements Consumer<View> {
    public static final Class<TextView> TEXT = TextView.class;
    public static final Class<ImageView> ICON = ImageView.class;
    private View view;
    protected Context context;

    @Override
    public void accept(View view) {
        this.view = view;
        this.context = view.getContext();
        bindContent(view);
    }

    /**
     * 绑定View视图
     */
    public abstract void bindContent(View view);


    /**
     * 通过id获取控件
     */
    public final <T extends View> T id(@IdRes int id) {
        return view.findViewById(id);
    }

    public final <T extends View> T id(@IdRes int id, Class<T> c) {
        return id(id);
    }

    /**
     * 获取String
     */
    public final String string(@StringRes int id) {
        return context.getString(id);
    }

    /**
     * 获取text
     */
    public final CharSequence text(@StringRes int id) {
        return context.getText(id);
    }

    /**
     * 获取资源
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public final Drawable icon(@DrawableRes int id) {
        return context.getDrawable(id);
    }
}
