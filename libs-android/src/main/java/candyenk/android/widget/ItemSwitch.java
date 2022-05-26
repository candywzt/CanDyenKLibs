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
import candyenk.android.view.NimbleSwitch;

/**
 * CDK项目Switch控件
 */
public class ItemSwitch extends LinearLayout {
    private Context context;
    private TextView titleView, summaryView;
    private ImageView iconView;
    private NimbleSwitch switchView;

    private CharSequence switchOn, switchOff, summary;


    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/

    public ItemSwitch(Context context) {
        this(context, null);
    }

    public ItemSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKItemSwitch);
        String title = typedArray.getString(R.styleable.CDKItemSwitch_title);
        summary = typedArray.getString(R.styleable.CDKItemSwitch_summary);
        switchOn = typedArray.getString(R.styleable.CDKItemSwitch_switchTextOn);
        switchOff = typedArray.getString(R.styleable.CDKItemSwitch_switchTextOff);
        boolean switchNF = typedArray.getBoolean(R.styleable.CDKItemSwitch_switchNF, false);
        int icon = typedArray.getResourceId(R.styleable.CDKItemSwitch_icon, 0);
        typedArray.recycle();


        if (title != null) {
            titleView.setText(title);
        }

        if (summary != null) {
            summaryView.setText(summary);
            summaryView.setVisibility(View.VISIBLE);
        } else if (switchOn != null && switchNF) {
            summaryView.setText(switchOn);
            summaryView.setVisibility(View.VISIBLE);
        } else if (switchOff != null && !switchNF) {
            summaryView.setText(switchOff);
            summaryView.setVisibility(View.VISIBLE);
        }

        if (icon != 0) {
            iconView.setImageResource(icon);
        } else {
            LayoutParams lp = (LayoutParams) iconView.getLayoutParams();
            lp.height = -2;
            iconView.setLayoutParams(lp);
        }
        switchView.setChecked(switchNF);
    }

    /**
     * 控件事件初始化
     */
    private void initEvents() {
        super.setOnClickListener((v) -> {
            switchView.setChecked(!switchView.isChecked());
        });
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && switchOn != null) {
                summaryView.setText(switchOn);
            } else if (!isChecked && switchOff != null) {
                summaryView.setText(switchOff);
            } else if (summary != null) {
                summaryView.setText(summary);
            }
        });
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
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
        this.summary = summary;
        summaryView.setText(summary);
        summaryView.setVisibility(View.VISIBLE);
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
     * 获取当前Switch开关状态
     */
    public boolean isChecked() {
        return switchView.isChecked();
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
    public void setOnCheckedChangeListener(NimbleSwitch.OnCheckedChangeListener swl) {
        switchView.setOnCheckedChangeListener((nimbleSwitch, isChecked) -> {
            if (swl != null) {
                swl.onCheckedChanged(nimbleSwitch, isChecked);
            }
            if (switchOn != null && switchOff != null) {
                summaryView.setText(isChecked ? switchOn : switchOff);
            }
        });
    }

    /**
     * 设置本控件点击事件监听
     */
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(v -> {
            switchView.setChecked(!switchView.isChecked());
            l.onClick(this);
        });
    }

    /**
     * 获取Icon控件以便使用第三方图片加载器
     */
    public ImageView getImageView() {
        return iconView;
    }
}
