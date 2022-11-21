package candyenk.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.CompoundButton;
import candyenk.android.R;
import candyenk.android.view.SwitchCDK;

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
public class ItemSwitch extends Item implements Checkable {
    protected CharSequence summary, onSummary, offSummary;
    protected SwitchCDK switchView;

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
        super.initLayout();
        removeView(arrowView);
        switchView = new SwitchCDK(context);
        addView(switchView);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        try {
            @SuppressLint({"CustomViewStyleable", "Recycle"})
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CDKItemSwitch);
            summary = ta.getString(R.styleable.CDKItemSwitch_android_summary);
            onSummary = ta.getString(R.styleable.CDKItemSwitch_android_summaryOn);
            offSummary = ta.getString(R.styleable.CDKItemSwitch_android_summaryOff);
            setTitleText(ta.getString(R.styleable.CDKItemSwitch_android_title));
            setChecked(ta.getBoolean(R.styleable.CDKItemSwitch_android_checked, false));
            setIconDrawable(ta.getDrawable(R.styleable.CDKItemSwitch_android_icon));
            updateSummary(0, summary);
            updateSummary(-1, offSummary);
            updateSummary(1, onSummary);
            updateSummary();
            ta.recycle();
        } catch (Exception ignored) {}
    }

    @Override
    protected void initEvents() {
        super.setOnClickListener((v) -> {
            setChecked(!isChecked());
        });
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSummary();
        });
    }

    /*** 刷新副标题文本 ***/
    protected void updateSummary() {
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

    @Override
    public void setChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return switchView.isChecked();
    }

    @Override
    public void toggle() {
        switchView.toggle();
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
    public void updateSummary(int state, CharSequence summary) {
        if (state == 0) this.summary = summary;
        else if (state == -1) this.offSummary = summary;
        else if (state == 1) this.onSummary = summary;
    }

    /**
     * 设置本控件Switch开关变动监听
     */
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener l) {
        switchView.setOnCheckedChangeListener((aSwitch, isChecked) -> {
            updateSummary();
            if (l != null) {
                l.onCheckedChanged(aSwitch, isChecked);
            }
        });
    }

    /**
     * 获取Switch控件
     */
    public SwitchCDK getSwitchView() {
        return switchView;
    }
    /**********************************************************************************************/
    /******************************************私有方法*********************************************/
    /**********************************************************************************************/
}
