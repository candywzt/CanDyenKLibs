package candyenk.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import candyenk.android.R;
import candyenk.android.tools.V;
import com.google.android.material.textview.MaterialTextView;

/**
 * CDK项目控件
 * 属性:
 * title(String):标题文本
 * summary(String):副标题文本
 * icon(Reference):图标,默认无
 */
public class Item extends LinearLayout {
    protected Context context;
    protected TextView titleView, summaryView;
    protected ImageView iconView, arrowView;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public Item(Context context) {
        this(context, null);
    }

    public Item(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Item(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Item(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initLayout();
        initAttrs(attrs);
        initEvents();
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/
    protected int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }

    protected void initLayout() {
        V.paddingDP(this, 10).orientation(0).gravity(Gravity.CENTER).backgroundRes(R.drawable.bg_cdk).refresh();

        iconView = new ImageView(context);
        V.LL(iconView).sizeDP(60).parent(this).refresh();

        LinearLayout ll = new LinearLayout(context);
        V.LL(ll).size(-1, -2).orientation(1).paddingDP(8, 0, 8, 0).weight(1).parent(this).refresh();

        titleView = new MaterialTextView(context);
        V.LL(titleView).size(-1, -2).textSize(18).textColorRes(R.color.text_main).parent(ll).refresh();

        summaryView = new MaterialTextView(context);
        V.LL(summaryView).size(-1, -2).textSize(12).textColorRes(R.color.text_deputy).hide().parent(ll).refresh();

        arrowView = new ImageView(context);
        V.LL(arrowView).sizeDP(20).parent(this).drawable(R.drawable.ic_arrow_right).refresh();
    }

    protected void initAttrs(AttributeSet attrs) {
        try {
            @SuppressLint("CustomViewStyleable")
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CDKItem);
            setTitleText(ta.getString(R.styleable.CDKItem_android_title));
            setSummaryText(ta.getString(R.styleable.CDKItem_android_summary));
            setIconDrawable(ta.getDrawable(R.styleable.CDKItem_android_icon));
            ta.recycle();
        } catch (Exception ignored) {}

    }

    protected void initEvents() {
        super.setOnClickListener(v -> {}); //用来触发点按效果的
    }

    @Override
    public void setOnClickListener(View.OnClickListener v) {
        if (v == null && arrowView != null) arrowView.setVisibility(View.GONE);
        super.setOnClickListener(v);
    }

    /**********************************************************************************************/
    /******************************************公共方法*********************************************/
    /**********************************************************************************************/
    /**
     * 设置标题内容文字
     */
    public void setTitleText(CharSequence title) {
        if (titleView == null) return;
        titleView.setText(title);
    }

    /**
     * 设置副标题内容文字
     */
    public void setSummaryText(CharSequence summary) {
        if (summaryView == null) return;
        if (summary == null) {
            summaryView.setVisibility(View.GONE);
        } else {
            summaryView.setText(summary);
            summaryView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置图标(默认0)
     * 为0则不显示Icon控件(Invisible)
     * 为-1则隐藏控件(Gone)
     */
    public void setIconResource(int rec) {
        if (iconView == null) return;
        if (rec > 0) {
            iconView.setImageResource(rec);
        } else {
            V.LL(iconView).sizeDP(60, -2).refresh();
            iconView.setVisibility(rec == 0 ? View.INVISIBLE : View.GONE);
        }
    }

    /**
     * 设置图标
     * 为null则不显示Icon控件(Invisible)
     */
    public void setIconDrawable(Drawable drawable) {
        if (iconView == null) return;
        if (drawable != null) {
            iconView.setImageDrawable(drawable);
        } else {
            V.LL(iconView).sizeDP(60, -2).refresh();
            iconView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 获取标题文本
     */
    public CharSequence getTitle() {
        if (titleView == null) return "";
        return titleView.getText();
    }

    /**
     * 获取副标题文本
     */
    public CharSequence getSummary() {
        if (summaryView == null) return "";
        return summaryView.getText();
    }

    /**
     * 获取标题控件
     */
    public TextView getTitleView() {
        return titleView;
    }

    /**
     * 获取副标题控件
     */
    public TextView getSummaryView() {
        return summaryView;
    }

    /**
     * 获取图标控件
     */
    public ImageView getIconView() {
        return iconView;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/


}

