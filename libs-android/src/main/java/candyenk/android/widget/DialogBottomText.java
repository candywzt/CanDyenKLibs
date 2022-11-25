package candyenk.android.widget;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.asbc.HolderCDK;
import candyenk.android.tools.L;
import candyenk.android.tools.V;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * Text展示上拉弹窗
 * 自动拦截重复调用
 * 三次重构
 */
public class DialogBottomText extends DialogBottomView {
    /*************************************成员变量**************************************************/
    protected CharSequence text;
    protected TextView tv;
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/

    /**
     * 构造方法
     * 无法使用同一View拉起多个Dialog,但可使用null拉起一个多余的dialog
     */
    public DialogBottomText(Context context) {
        this(context, null);
    }

    public DialogBottomText(View view) {
        this(view.getContext(), view);
    }

    protected DialogBottomText(Context context, View view) {
        super(context, view);
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(@LayoutRes int viewid) {
        L.e("TAG", "不允许使用DialogBottomText.setContent(int)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(View view) {
        L.e("TAG", "不允许使用DialogBottomText.setContent(View)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        L.e("TAG", "不允许使用DialogBottomText.setContent(RecyclerView.Adapter)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(View... views) {
        L.e("TAG", "不允许使用DialogBottomText.setContent(View...)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int... resIds) {
        L.e("TAG", "不允许使用DialogBottomText.setContent(int...)");
    }
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Override
    public void setContent(int resId, int count) {
        L.e("TAG", "不允许使用DialogBottomText.setContent(int,int)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        L.e("TAG", "不允许使用DialogBottomText.setLayoutManager(RecyclerView.LayoutManager)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnBindViewHolder(Consumer<HolderCDK> c) {
        L.e("TAG", "不允许使用DialogBottomText.setOnBindViewHolder(Consumer)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不允许使用DialogBottomText.setOnItemClickListener(BiConsumer)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public void setOnItemLongClickListener(BiConsumer<View, Integer> l) {
        L.e("TAG", "不允许使用DialogBottomText.setOnItemLongClickListener(BiConsumer)");
    }
    /**
     * @deprecated 不允许使用
     */
    @Override
    public <T extends View> T getContentView() {
        return super.getContentView();
    }
    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置内容-纯文本
     * 可重复调用以修改文本内容
     */
    public void setContent(CharSequence text) {
        if (!ok) return;
        if (this.tv == null) super.setContent(createTextView());
        this.text = text;
        this.tv.setText(this.text);
        this.tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 获取设置的内容
     */
    public CharSequence getText() {
        return text;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 创建纯文本控件 ***/
    private View createTextView() {
        tv = new MaterialTextView(viewContext);
        V.RV(tv).size(-1, -2).paddingDP(10).refresh();
        tv.setTextIsSelectable(true);
        return tv;
    }
}

