package candyenk.android.widget;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import candyenk.android.tools.V;
import com.google.android.material.textview.MaterialTextView;


/**
 * 底部弹窗-文本展示
 */
public class DialogBottomText extends DialogBottomView {
    protected CharSequence text;
    protected TextView tv;
    
    public DialogBottomText(Context context) {
        this(context, null);
    }
    
    public DialogBottomText(View view) {
        this(view.getContext(), view);
    }
    
    protected DialogBottomText(Context context, View view) {
        super(context, view);
    }
    
    /**
     * @deprecated 不允许使用
     */
    @Deprecated
    @Override
    public <T extends View> T getContent() {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Deprecated
    @Override
    public void setContent(@LayoutRes int viewid) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * @deprecated 请使用 {@link #setContent(CharSequence)}
     */
    @Deprecated
    @Override
    public void setContent(View view) {
        throw new UnsupportedOperationException("改方法在该类中已被禁用");
    }
    
    /**
     * 设置内容-纯文本
     *
     * @param text 展示文本,可在show后修改
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
     *
     * @return 文本内容
     */
    public CharSequence getText() {
        return text;
    }
    
    /*** 创建纯文本控件 ***/
    private View createTextView() {
        tv = new MaterialTextView(viewContext);
        V.RV(tv).size(-1, -2).paddingDP(10).refresh();
        tv.setTextIsSelectable(true);
        return tv;
    }
}

