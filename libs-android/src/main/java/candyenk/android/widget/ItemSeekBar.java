package candyenk.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import candyenk.android.R;
import candyenk.android.tools.V;
import candyenk.android.view.ProgressBarCDK;
import candyenk.android.view.SeekBarCDK;
import com.google.android.material.textview.MaterialTextView;

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
    protected SeekBarCDK seekBarView;

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
        super.initLayout();
        View ll = getChildAt(1);
        removeView(iconView);
        removeView(ll);
        removeView(arrowView);
        setOrientation(VERTICAL);

        LinearLayout ll2 = new LinearLayout(context);
        V.LL(ll2).size(-1, -2).orientation(0).gravity(Gravity.CENTER).parent(this).refresh();
        ll2.addView(iconView);
        ll2.addView(ll);

        progressTextView = new MaterialTextView(context);
        V.LL(progressTextView).size(-2, -2).lGravity(Gravity.TOP).margin(0, 5, 0, 0).textColorRes(R.color.text_main).parent(ll2).refresh();

        seekBarView = new SeekBarCDK(context);
        V.LL(seekBarView).size(-1, -2).margin(0, 10, 0, 0).parent(this).refresh();
        seekBarView.setDisplayMode(SeekBarCDK.DM_EXTREMUM | SeekBarCDK.DM_PERCENT);

    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CDKItemSeekBar);
        String title = ta.getString(R.styleable.CDKItemSeekBar_android_title);
        String summary = ta.getString(R.styleable.CDKItemSeekBar_android_summary);
        int max = ta.getInt(R.styleable.CDKItemSeekBar_android_max, 100);
        int min = ta.getInt(R.styleable.CDKItemSeekBar_android_min, 0);
        int progress = ta.getInt(R.styleable.CDKItemSeekBar_android_progress, min);
        Drawable icon = ta.getDrawable(R.styleable.CDKItemSeekBar_android_icon);
        ta.recycle();

        setTitleText(title);
        setSummaryText(summary);
        setIconDrawable(icon);
        setMax(max);
        setMin(min);
        progressTextView.setText(String.valueOf(progress));
    }

    @Override
    protected void initEvents() {
        super.setOnClickListener(v -> {});
        seekBarView.setOnProgressChangedListener((progress, percent) -> {
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
    public void setOnSeekBarChangeListener(ProgressBarCDK.OnProgressChangedListener l) {
        if (l != null) seekBarView.setOnProgressChangedListener(l);
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
    public SeekBarCDK getSeekBarView() {
        return seekBarView;
    }


    /**********************************************************************************************/
    /******************************************私有方法*********************************************/
    /**********************************************************************************************/

}
