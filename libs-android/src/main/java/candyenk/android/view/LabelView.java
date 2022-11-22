package candyenk.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import candyenk.android.R;
import candyenk.android.tools.L;
import com.google.android.material.textview.MaterialTextView;

import java.util.Random;

/**
 * 小标签控件
 * 背景色无法设置图片,只能设置Color
 * 不能设置Padding属性,无效
 * 启用随机色彩将覆盖原有色彩,不可逆
 */
public class LabelView extends MaterialTextView {
    private static final String TAG = LabelView.class.getSimpleName();
    private static final int strokeWidth = 4;
    private final Context context;
    private GradientDrawable drawable;//背景
    private final int[] sign = {0, 0, 0, 0, 0};//边距和高
    private Drawable icon;//头图
    private static final int[][] colorList = {
            {0xFFFFCDD2, 0xFFF44336}, {0xFFF8BBD0, 0xFFE91E63},
            {0xFFE1BEE7, 0xFF9C27B0}, {0xFFD1C4E9, 0xFF673AB7},
            {0xFFC5CAE9, 0xFF3F51B5}, {0xFFBBDEFB, 0xFF2196F3},
            {0xFFB3E5FC, 0xFF03A9F4}, {0xFFB2EBF2, 0xFF00BCD4},
            {0xFFB2DFDB, 0xFF009688}, {0xFFC8E6C9, 0xFF4CAF50},
            {0xFFDCEDC8, 0xFF8BC34A}, {0xFFF0F4C3, 0xFFCDDC39},
            {0xFFFFF9C4, 0xFFFFEB3B}, {0xFFFFECB3, 0xFFFFC107},
            {0xFFFFE0B2, 0xFFFF9800}, {0xFFFFCCBC, 0xFFFF5722},
            {0xFFD7CCC8, 0xFF795548}, {0xFFF5F5F5, 0xFF9E9E9E},
            {0xFFCFD8DC, 0xFF607D8B}
    };//预制颜色表

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/
    public LabelView(Context context) {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initAttrs(attrs);

    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (icon != null) {
            icon.setBounds(getPaddingLeft() - getMeasuredHeight() + getPaddingTop() + getPaddingBottom(), getPaddingTop(), getPaddingLeft(), getMeasuredHeight() - getPaddingBottom());
            icon.draw(canvas);
        }
    }

    @Override
    public void setPadding(int l, int t, int r, int b) {
        L.e(TAG, "不许动");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        if (sign[0] == 0 || height != sign[4]) {
            sign[1] = sign[3] = (int) ((sign[0] = sign[2] = (int) (getMeasuredHeight() * 0.4)) * 0.5);
        }
        sign[0] = icon == null ? sign[2] : (height + sign[2]);
        sign[4] = height;
        super.setPadding(sign[0], sign[1], sign[2], sign[3]);
        setMeasuredDimension(sign[0] + width + sign[2], sign[1] + height + sign[3]);
        if (this.drawable == null) this.drawable = new GradientDrawable();
        this.drawable.setCornerRadius((float) (Math.min(getMeasuredHeight(), getMeasuredWidth()) * 0.5));
        setBackground(this.drawable);
    }

    @Override
    public void setBackground(Drawable bg) {
        if (this.drawable == null) this.drawable = new GradientDrawable();
        if (bg != this.drawable) {
            if (bg instanceof ColorDrawable) {
                int color = ((ColorDrawable) bg).getColor();
                this.drawable.setColor(color);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && bg instanceof ColorStateListDrawable) {
                ColorStateList csl = ((ColorStateListDrawable) bg).getColorStateList();
                this.drawable.setColor(csl);
            }
        }
        super.setBackground(this.drawable);
    }

    @Override
    public void setTextColor(int color) {
        this.setTextColor(ColorStateList.valueOf(color));
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        if (this.drawable == null) this.drawable = new GradientDrawable();
        this.drawable.setStroke(strokeWidth, colors);
        this.setBackgroundDrawable(this.drawable);
        super.setTextColor(colors);
    }
    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/
    private void initAttrs(AttributeSet attrs) {
        try {
            @SuppressLint({"CustomViewStyleable", "Recycle"})
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CDKLabelView);
            this.icon = ta.getDrawable(R.styleable.CDKLabelView_android_src);
            boolean rc = ta.getBoolean(R.styleable.CDKLabelView_randomColor, true);
            int index = ta.getInt(R.styleable.CDKLabelView_colorIndex, -1);
            ta.recycle();
            if (rc) randomColor();
            setColorIndex(index);
        } catch (Exception ignored) {}
    }
    /**********************************************************************************************/
    /*****************************************公开方法***********************************************/
    /**********************************************************************************************/
    /**
     * 启用随机色彩
     */
    public void randomColor() {
        int index = new Random().nextInt(colorList.length);
        setColorIndex(index);
    }

    /**
     * 设置图标内容
     * 为空则删除图标
     */
    public void setImageDrawable(Drawable drawable) {
        this.icon = drawable;
        requestLayout();
    }

    /**
     * 设置图标内容
     * 为0则删除图标
     */
    @SuppressLint({"UseCompatLoadingForDrawables"})
    public void setImageDrawableRes(int resId) {
        if (resId <= 0) setImageDrawable(null);
        else setImageDrawable(context.getDrawable(resId));
    }

    /**
     * 设置色彩
     * 色彩索引(0-18)
     */
    public void setColorIndex(int index) {
        if (index < 0 || index > 18) return;
        this.drawable.setColor(colorList[index][0]);
        setTextColor(colorList[index][1]);
    }
}