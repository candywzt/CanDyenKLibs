package candyenk.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import candyenk.android.R;
import candyenk.android.view.SeekBar;

/**
 * CDK项目拖动条控件
 */
public class ItemSeekBar extends LinearLayout {
    private Context context;
    private TextView titleView, summaryView, progressTextView;
    private ImageView iconView;
    private SeekBar seekBarView;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /******************************************构造方法*********************************************/
    /**********************************************************************************************/

    public ItemSeekBar(Context context) {
        this(context, null);
    }

    public ItemSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initLayout();
        initAttrs(attrs);
        initEvents();
    }
    /**********************************************************************************************/
    /******************************************重写方法*********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /******************************************私有方法*********************************************/
    /**********************************************************************************************/
    private void initLayout() {
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

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKItemSeekBar);
        String title = typedArray.getString(R.styleable.CDKItemSeekBar_title);
        String summary = typedArray.getString(R.styleable.CDKItemSeekBar_summary);
        int max = typedArray.getInt(R.styleable.CDKItemSeekBar_max, 100);
        int min = typedArray.getInt(R.styleable.CDKItemSeekBar_min, 0);
        int progress = typedArray.getInt(R.styleable.CDKItemSeekBar_progress, 0);
        int icon = typedArray.getResourceId(R.styleable.CDKItemSwitch_icon, 0);
        typedArray.recycle();

        if (title != null) {
            titleView.setText(title);
        }
        if (summary != null) {
            summaryView.setText(summary);
            summaryView.setVisibility(View.VISIBLE);
        }
        if (icon != 0) {
            iconView.setImageResource(icon);
        } else {
            LayoutParams lp = (LayoutParams) iconView.getLayoutParams();
            lp.height = -1;
            iconView.setLayoutParams(lp);
        }
        seekBarView.setMax(max);
        seekBarView.setMin(min);
        seekBarView.setProgress(progress);
        progressTextView.setText("" + progress);
    }


    private void initEvents() {
        super.setOnClickListener(v -> {

        });
        seekBarView.setOnSeekBarChangeListener((seekBar, progress, percent) -> {
            progressTextView.setText("" + progress);
        });
    }

    public int dp2px(double dpValue) {
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**********************************************************************************************/
    /******************************************公共方法*********************************************/
    /**********************************************************************************************/
    /**
     * 设置标题内容文字
     */
    public void setTitleText(CharSequence title) {
        titleView.setText(title);
    }

    /**
     * 设置副标题内容文字
     */
    public void setSummaryText(CharSequence summary) {
        summaryView.setText(summary);
        summaryView.setVisibility(View.GONE);
    }

    /**
     * 设置图标
     */
    public void setIconImage(int ResourceId) {
        iconView.setImageResource(ResourceId);
    }

    public void setIconImage(Drawable drawable) {
        iconView.setImageDrawable(drawable);
    }

    public void setIconImage(Bitmap bitmap) {
        iconView.setImageBitmap(bitmap);
    }

    /**
     * 获取Icon控件以便使用第三方图片加载器
     */
    public ImageView getImageView() {
        return iconView;
    }

    /**
     * 获取当前seekbar进度
     */
    public int getProgress() {
        return seekBarView.getProgress();
    }

    /**
     * 设置seekbar进度
     */
    public void setProgress(int progress) {
        seekBarView.setProgress(progress);
    }

    /**
     * 设置seekbar变动监听
     */
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        seekBarView.setOnSeekBarChangeListener((seekBar, progress, percent) -> {
            progressTextView.setText("" + progress);
            l.onSeekBarChangeListener(seekBar, progress, percent);
        });
    }
}
