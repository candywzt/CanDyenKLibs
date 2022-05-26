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

/**
 * CDK项目控件
 */
public class Item extends LinearLayout {
    private Context context;
    private TextView titleView, summaryView;
    private ImageView iconView;

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
    /*************************************重写方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
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

        ImageView iv = new ImageView(context);
        LinearLayout.LayoutParams lp6 = new LayoutParams(dp2px(20), dp2px(20));
        iv.setLayoutParams(lp6);
        iv.setImageResource(R.drawable.ic_right_arrow);
        addView(iv);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKItem);
        String title = typedArray.getString(R.styleable.CDKItem_title);
        String summary = typedArray.getString(R.styleable.CDKItem_summary);
        int icon = typedArray.getResourceId(R.styleable.CDKItem_icon, 0);
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
            lp.height = -2;
            iconView.setLayoutParams(lp);
        }
    }

    private void initEvents() {
        //用来触发点按效果的
        super.setOnClickListener(v -> {

        });

    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }

    /**********************************************************************************************/
    /******************************************公开方法*********************************************/
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
     * 获取Icon控件以便使用第三方图片加载器
     */
    public ImageView getImageView() {
        return iconView;
    }
}

