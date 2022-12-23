package candyenk.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.*;
import android.util.AttributeSet;
import candyenk.android.R;
import candyenk.android.utils.USDK;
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
    private static int[] colorList1;
    private static int[] colorList5;
    private final Context context;
    private GradientDrawable drawable;//背景
    private int ss, st, se, sb, sh;//边距,左,上,右,下,高度
    private Drawable icon;//头图


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
        if (colorList1 == null) colorList1 = context.getResources().getIntArray(R.array.color_md100);
        if (colorList5 == null) colorList5 = context.getResources().getIntArray(R.array.color_md500);
        initAttrs(attrs);
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (icon != null) {
            icon.setBounds(getPaddingLeft() - sh - st, getPaddingTop(), getPaddingLeft() - st, getMeasuredHeight() - getPaddingBottom());
            icon.draw(canvas);
        }
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start + ss, top + st, end + se, bottom + sb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();//内容高度
        if (sh != height) {//高度改变
            sh = height;
            se = (int) (getMeasuredHeight() * 0.4);
            st = sb = (int) (getMeasuredHeight() * 0.2);
            setImageDrawable(icon);
        }
    }

    @Override
    public void setBackground(Drawable bg) {
        if (bg != getBG()) {
            if (bg instanceof ColorDrawable) {
                int color = ((ColorDrawable) bg).getColor();
                this.drawable.setColor(color);
            } else if (USDK.Q() && bg instanceof ColorStateListDrawable) {
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
        getBG().setStroke(strokeWidth, colors);
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
            Drawable bg = ta.getDrawable(R.styleable.CDKLabelView_android_background);
            Drawable tc = ta.getDrawable(R.styleable.CDKLabelView_android_textColor);
            this.icon = ta.getDrawable(R.styleable.CDKLabelView_android_src);
            boolean rc = ta.getBoolean(R.styleable.CDKLabelView_randomColor, true);
            int index = ta.getInt(R.styleable.CDKLabelView_colorIndex, -1);
            ta.recycle();
            setBackground(bg);
            if (USDK.Q() && tc instanceof ColorStateListDrawable)
                setTextColor(((ColorStateListDrawable) tc).getColorStateList());
            if (rc) randomColor();
            setColorIndex(index);
        } catch (Exception ignored) {}
    }

    private GradientDrawable getBG() {
        if (this.drawable == null) {
            this.drawable = new GradientDrawable();
            this.drawable.setCornerRadius(666);
        }
        return this.drawable;
    }
    /**********************************************************************************************/
    /*****************************************公开方法***********************************************/
    /**********************************************************************************************/
    /**
     * 启用随机色彩
     */
    public void randomColor() {
        int index = new Random().nextInt(colorList1.length);
        setColorIndex(index);
    }

    /**
     * 设置图标内容
     * 为空则删除图标
     */
    public void setImageDrawable(Drawable drawable) {
        this.icon = drawable;
        if (icon == null) ss = se;
        else if (icon instanceof BitmapDrawable) {
            Bitmap bm = ((BitmapDrawable) icon).getBitmap();
            ss = (bm.getWidth() / bm.getHeight() * sh) + se + st;
        } else ss = sh + se + st;
        setPaddingRelative(0, 0, 0, 0);
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
        this.drawable.setColor(colorList1[index]);
        setTextColor(colorList5[index]);
    }
}