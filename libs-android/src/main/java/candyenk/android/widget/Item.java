package candyenk.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import candyenk.android.R;

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
        LinearLayout.LayoutParams lp = new LayoutParams(-1, -2);
        setLayoutParams(lp);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        setPadding(dp2px(10), dp2px(10), dp2px(10), dp2px(10));
        setBackgroundResource(R.drawable.bg_pressed);

        iconView = new ImageView(context);
        LinearLayout.LayoutParams lp2 = new LayoutParams(dp2px(60), dp2px(60));
        iconView.setLayoutParams(lp2);
        addView(iconView);

        LinearLayout ll2 = new LinearLayout(context);
        LinearLayout.LayoutParams lp3 = new LayoutParams(-1, -2);
        lp3.leftMargin = lp3.rightMargin = dp2px(8);
        lp3.weight = 1;
        ll2.setLayoutParams(lp3);
        ll2.setOrientation(LinearLayout.VERTICAL);
        addView(ll2);

        titleView = new TextView(context);
        LinearLayout.LayoutParams lp4 = new LayoutParams(-1, -2);
        titleView.setLayoutParams(lp4);
        titleView.setTextSize(18);
        ll2.addView(titleView);

        summaryView = new TextView(context);
        LinearLayout.LayoutParams lp5 = new LayoutParams(-1, -2);
        summaryView.setLayoutParams(lp5);
        summaryView.setTextSize(12);
        summaryView.setVisibility(View.GONE);
        ll2.addView(summaryView);

        arrowView = new ImageView(context);
        LinearLayout.LayoutParams lp6 = new LayoutParams(dp2px(20), dp2px(20));
        arrowView.setLayoutParams(lp6);
        arrowView.setImageResource(R.drawable.ic_right_arrow);
        addView(arrowView);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKItem);
        String title = typedArray.getString(R.styleable.CDKItem_android_title);
        String summary = typedArray.getString(R.styleable.CDKItem_android_summary);
        int icon = typedArray.getResourceId(R.styleable.CDKItem_android_icon, 0);
        typedArray.recycle();

        setTitleText(title);
        setSummaryText(summary);
        setIconResource(icon);
    }

    protected void initEvents() {
        //用来触发点按效果的
        super.setOnClickListener(v -> {});
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
     * 设置图标
     * 为0则不显示Icon控件(Invisible)
     * 为-1则隐藏控件(Gone)
     */
    public void setIconResource(int rec) {
        if (iconView == null) return;
        if (rec > 0) {
            iconView.setImageResource(rec);
        } else {
            LayoutParams lp = (LayoutParams) iconView.getLayoutParams();
            lp.height = LayoutParams.WRAP_CONTENT;
            iconView.setLayoutParams(lp);
            iconView.setVisibility(rec == 0 ? View.INVISIBLE : View.GONE);
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

