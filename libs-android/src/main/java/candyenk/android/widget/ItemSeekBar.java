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
import candyenk.android.view.SeekBar;

/**
 * CDKSeekBar项目控件
 * 属性:
 * title(String):标题文本
 * summary(String):副标题文本
 * icon(Reference):图标,默认无
 * progress(Integer):显示进度,默认min
 * max:最大(右侧)值,默认0
 * min:最小(左侧)值,默认100
 */
public class ItemSeekBar extends Item {
    protected TextView progressTextView;
    protected SeekBar seekBarView;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /******************************************构造方法*********************************************/
    /**********************************************************************************************/

    public ItemSeekBar(Context context) {
        super(context);
    }

    public ItemSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ItemSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    /**********************************************************************************************/
    /******************************************重写方法*********************************************/
    /**********************************************************************************************/
    @Override
    protected void initLayout() {
        LayoutParams lp = new LayoutParams(-1, -2);
        setLayoutParams(lp);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        setPadding(dp2px(10), dp2px(10), dp2px(10), dp2px(10));
        setBackgroundResource(R.drawable.bg_pressed);

        LinearLayout ll2 = new LinearLayout(context);
        LayoutParams lp2 = new LayoutParams(-1, -2);
        ll2.setLayoutParams(lp2);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll2.setGravity(Gravity.CENTER);
        addView(ll2);

        iconView = new ImageView(context);
        LayoutParams lp3 = new LayoutParams(dp2px(60), dp2px(60));
        iconView.setLayoutParams(lp3);
        ll2.addView(iconView);

        LinearLayout ll3 = new LinearLayout(context);
        LayoutParams lp4 = new LayoutParams(-1, -2);
        lp4.leftMargin = lp4.rightMargin = dp2px(8);
        lp4.weight = 1;
        ll3.setLayoutParams(lp4);
        ll3.setOrientation(LinearLayout.VERTICAL);
        ll2.addView(ll3);

        titleView = new TextView(context);
        LayoutParams lp5 = new LayoutParams(-1, -2);
        titleView.setLayoutParams(lp5);
        titleView.setTextSize(18);
        titleView.setText("标题");
        ll3.addView(titleView);

        summaryView = new TextView(context);
        LayoutParams lp6 = new LayoutParams(-1, -2);
        summaryView.setLayoutParams(lp6);
        summaryView.setTextSize(12);
        summaryView.setVisibility(View.GONE);
        ll3.addView(summaryView);

        progressTextView = new TextView(context);
        LayoutParams lp7 = new LayoutParams(-2, -2);
        lp7.gravity = Gravity.TOP;
        lp7.setMargins(0, dp2px(5), 0, 0);
        progressTextView.setLayoutParams(lp7);
        ll2.addView(progressTextView);

        seekBarView = new SeekBar(context);
        LayoutParams lp8 = new LayoutParams(-1, -2);
        lp8.setMargins(0, dp2px(10), 0, 0);
        seekBarView.setLayoutParams(lp8);
        seekBarView.setDisplayMode(SeekBar.DISPLAY_MODE_EXTREMUM | SeekBar.DISPLAY_MODE_PERCENT);
        addView(seekBarView);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKItemSeekBar);
        String title = typedArray.getString(R.styleable.CDKItemSeekBar_android_title);
        String summary = typedArray.getString(R.styleable.CDKItemSeekBar_android_summary);
        int max = typedArray.getInt(R.styleable.CDKItemSeekBar_android_max, 100);
        int min = typedArray.getInt(R.styleable.CDKItemSeekBar_android_min, 0);
        int progress = typedArray.getInt(R.styleable.CDKItemSeekBar_android_progress, min);
        int icon = typedArray.getResourceId(R.styleable.CDKItemSeekBar_android_icon, 0);
        typedArray.recycle();

        setTitleText(title);
        setSummaryText(summary);
        setIconResource(icon);
        setMax(max);
        setMin(min);
        progressTextView.setText(String.valueOf(progress));
    }

    @Override
    protected void initEvents() {
        super.setOnClickListener(v -> {});
        seekBarView.setOnSeekBarChangeListener((seekBar, progress, percent, state) -> {
            progressTextView.setText(String.valueOf(progress));
        });
    }

    /**********************************************************************************************/
    /******************************************公共方法*********************************************/
    /**********************************************************************************************/
    /**
     * 设置seekbar进度
     */
    public void setProgress(int progress) {
        seekBarView.setProgress(progress);
    }

    /**
     * 设置SeekBar最大值
     */
    public void setMax(int max) {
        seekBarView.setMax(max);
    }

    /**
     * 设置最小值
     */
    public void setMin(int min) {
        seekBarView.setMin(min);
    }

    /**
     * 设置进度显示内容
     * 为null则显示当前进度
     */
    public void setProgressText(CharSequence text) {
        if (text == null) text = String.valueOf(getProgress());
        progressTextView.setText(text);
    }

    /**
     * 设置seekbar变动监听
     * 会重置进度显示内容
     * 需手动setProressText
     */
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        if (l != null) seekBarView.setOnSeekBarChangeListener(l);
    }

    /**
     * 获取SeekBar最大值
     */
    public int getMax() {
        return seekBarView.getMax();
    }

    /**
     * 获取最小值
     */
    public int getMin() {
        return seekBarView.getMin();
    }

    /**
     * 获取当前seekbar进度
     */
    public int getProgress() {
        return seekBarView.getProgress();
    }

    /**
     * 获取SeekBar控件
     */
    public SeekBar getSeekBarView() {
        return seekBarView;
    }


    /**********************************************************************************************/
    /******************************************私有方法*********************************************/
    /**********************************************************************************************/

}
