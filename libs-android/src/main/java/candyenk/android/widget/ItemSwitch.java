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
import candyenk.android.view.NimbleSwitch;

/**
 * CDKSwitch项目控件
 * 属性:
 * title(String):标题文本
 * summary(String):副标题文本
 * icon(Reference):图标,默认无
 * onSummary(String):开关打开时的副标题
 * offSummary(String):开关关闭时的副标题
 * checked(boolean):开关初始状态
 */
public class ItemSwitch extends Item {
    protected CharSequence summary, onSummary, offSummary;
    protected NimbleSwitch switchView;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/

    public ItemSwitch(Context context) {
        super(context);
    }

    public ItemSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    /**********************************************************************************************/
    /******************************************重写方法*********************************************/
    /**********************************************************************************************/
    @Override
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

        switchView = new NimbleSwitch(context);
        addView(switchView);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKItemSwitch);
        String title = typedArray.getString(R.styleable.CDKItemSwitch_android_title);
        summary = typedArray.getString(R.styleable.CDKItemSwitch_android_summary);
        onSummary = typedArray.getString(R.styleable.CDKItemSwitch_android_summaryOn);
        offSummary = typedArray.getString(R.styleable.CDKItemSwitch_android_summaryOff);
        boolean checked = typedArray.getBoolean(R.styleable.CDKItemSwitch_android_checked, false);
        int icon = typedArray.getResourceId(R.styleable.CDKItemSwitch_android_icon, 0);
        typedArray.recycle();

        setTitleText(title);
        setChecked(checked);
        setIconResource(icon);
        setSummary(0, summary);
        setSummary(-1, offSummary);
        setSummary(1, onSummary);
        setSummaryText();
    }

    @Override
    protected void initEvents() {
        super.setOnClickListener((v) -> {
            setChecked(!isChecked());
        });
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setSummaryText();
        });
    }

    protected void setSummaryText() {
        setSummaryText(isChecked() ? (onSummary == null ? summary : onSummary) : (offSummary == null ? summary : offSummary));
    }

    /**
     * 设置本控件点击事件监听
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(v -> {
            setChecked(!isChecked());
            l.onClick(this);
        });
    }
    /**********************************************************************************************/
    /******************************************公共方法*********************************************/
    /**********************************************************************************************/
    /**
     * 设置副标题内容
     *
     * @param state -1:关闭状态
     *              0:普通副标题
     *              1:打开状态
     */
    public void setSummary(int state, CharSequence summary) {
        if (state == 0) this.summary = summary;
        else if (state == -1) this.offSummary = summary;
        else if (state == 1) this.onSummary = summary;
    }

    /**
     * 设置当前Switch开关状态
     */
    public void setChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    /**
     * 设置本控件Switch开关变动监听
     */
    public void setOnCheckedChangeListener(NimbleSwitch.OnCheckedChangeListener l) {
        switchView.setOnCheckedChangeListener((nimbleSwitch, isChecked) -> {
            setSummaryText();
            if (l != null) {
                l.onCheckedChanged(nimbleSwitch, isChecked);
            }
        });
    }

    /**
     * 获取当前Switch开关状态
     */
    public boolean isChecked() {
        return switchView.isChecked();
    }

    /**
     * 获取Switch控件
     */
    public NimbleSwitch getSwitchView() {
        return switchView;
    }
    /**********************************************************************************************/
    /******************************************私有方法*********************************************/
    /**********************************************************************************************/
}
