package candyenk.android.widget;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.android.tools.L;
import candyenk.android.tools.TH;
import candyenk.android.tools.V;
import candyenk.android.utils.UShare;
import com.google.android.material.textview.MaterialTextView;


/**
 * Text展示上拉弹窗
 * 自动拦截重复调用
 * 三次重构
 */
public class DialogBottomText extends DialogBottomView {
    /*************************************静态变量**************************************************/
    /*************************************成员变量**************************************************/
    protected CharSequence text;
    protected TextView tv;
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
    @Override
    public void setContent(int viewid) {
        L.e("TAG", "不允许使用setContent(int)");
    }

    @Override
    public void setContent(View view) {
        L.e("TAG", "不允许使用setContent(View)");
    }

    @Override
    public void setContent(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        L.e("TAG", "不允许使用setContent(Adapter)");
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
        this.text = TH.create(text).setURLClick((v, url) -> UShare.startBrowser(v.getContext(), url)).out();
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
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/

}

