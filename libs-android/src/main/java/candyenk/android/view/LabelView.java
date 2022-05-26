package candyenk.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;

import candyenk.android.R;

/**
 * 小标签控件
 * 必须要有文字
 * 可以没有图
 * BUG:暂时不能改文字内容,不然会被截断
 */
public class LabelView extends View {
    private static final String TAG = LabelView.class.getSimpleName();
    private Context context;
    private Paint backgroundPaint;
    private Paint titlePaint;
    private Paint imagePaint;

    private int backgroundColor;
    private int titleColor;
    private Drawable titleImage;
    private CharSequence titleText;

    private int width, height;//控件尺寸
    private int titleWidth, titleHeight;//内容尺寸
    private float startX, endX, startY, endY;//背景四相坐标
    private float centerY;//中心点y坐标
    private float textX;//文字起始点x坐标
    private float imageStartX, imageStartY, imageEndX, imageEndY;//图标四相坐标


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
        initPaints();
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);//背景
        drawImage(canvas);//图标
        drawTitle(canvas);//文字
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initLayout();
    }

    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/

    private void initLayout() {
        Rect rect = new Rect();
        if (titleText != null) {
            titlePaint.getTextBounds((String) titleText, 0, titleText.length(), rect);
        }
        int textWidth = rect.width();//文字宽度(图标尺寸)
        int textHeight = this.titleText == null ? 33 : rect.height();//文字高度

        int imageWidth = this.titleImage == null ? 0 : textHeight + dp2px(6);

        this.titleWidth = this.titleImage == null ? textWidth : textWidth + imageWidth + dp2px(2);
        this.titleHeight = textHeight + dp2px(10);

        this.height = this.titleHeight + dp2px(10);
        this.width = this.titleWidth + this.height;

        this.startX = (this.width - this.titleWidth) * 0.5f;
        this.endX = this.startX + this.titleWidth;
        this.startY = (this.height - this.titleHeight) * 0.5f;
        this.endY = this.startY + this.titleHeight;

        this.textX = this.endX - textWidth;
        this.centerY = this.height * 0.5f;

        if (this.titleImage != null) {
            this.imageStartX = this.startX;
            this.imageStartY = dp2px(7);
            this.imageEndX = this.imageStartX + imageWidth;
            this.imageEndY = this.imageStartY + imageWidth;
        }
        setMeasuredDimension(this.width, this.height);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKLabelView);
        this.titleText = typedArray.getString(R.styleable.CDKLabelView_android_text);
        this.titleColor = typedArray.getColor(R.styleable.CDKLabelView_android_textColor, context.getResources().getColor(R.color.text_title));
        int res = typedArray.getResourceId(R.styleable.CDKLabelView_android_src, 0);
        if (res != 0) {
            this.titleImage = context.getResources().getDrawable(res);
        }
        this.backgroundColor = typedArray.getColor(R.styleable.CDKLabelView_android_background, context.getResources().getColor(R.color.mainGC4_1));
    }

    private void initPaints() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeWidth(1);

        imagePaint = new Paint();
        imagePaint.setColor(titleColor);
        imagePaint.setStyle(Paint.Style.FILL);
        imagePaint.setAntiAlias(true);
        imagePaint.setStrokeWidth(1);

        titlePaint = new Paint();
        titlePaint.setColor(titleColor);
        titlePaint.setStyle(Paint.Style.FILL);
        titlePaint.setTextSize(sp2px(12));
        titlePaint.setAntiAlias(true);
        titlePaint.setStrokeWidth(1);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(this.startX, this.centerY, this.titleHeight * 0.5f, backgroundPaint);
        canvas.drawCircle(this.endX, this.centerY, this.titleHeight * 0.5f, backgroundPaint);
        RectF rectF = new RectF(this.startX, this.startY, this.endX, this.endY);
        canvas.drawRect(rectF, backgroundPaint);
    }

    private void drawImage(Canvas canvas) {
        if (this.titleImage != null) {
            Rect rect = new Rect((int) this.imageStartX, (int) this.imageStartY, (int) this.imageEndX, (int) this.imageEndY);
            this.titleImage.setBounds(rect);
            this.titleImage.draw(canvas);
        }
    }

    private void drawTitle(Canvas canvas) {
        if (this.titleText == null) return;
        Paint.FontMetrics f = titlePaint.getFontMetrics();
        float y = this.centerY + (f.bottom - f.top) * 0.5f - f.bottom;
        canvas.drawText(this.titleText, 0, this.titleText.length(), this.textX, y, titlePaint);
    }

    public int sp2px(float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }
    /**********************************************************************************************/
    /*****************************************公开方法***********************************************/
    /**********************************************************************************************/
    /**
     * 设置文字内容
     */
    public void setText(CharSequence text) {
        if (TextUtils.isEmpty(text)) return;
        this.titleText = text;
        initLayout();
        invalidate();
    }

    /**
     * 设置图标内容
     * 为空则删除图标
     */
    public void setImageDrawable(Drawable drawable) {
        if (drawable == null) return;
        this.titleImage = drawable;
        initLayout();
        invalidate();
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(@ColorInt int textColor) {
        if (textColor == 0) return;
        this.titleColor = textColor;
        this.titlePaint.setColor(textColor);
        initLayout();
        invalidate();
    }

    /**
     * 设置背景色
     */
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        if (backgroundColor == 0) return;
        this.backgroundColor = backgroundColor;
        this.backgroundPaint.setColor(backgroundColor);
        invalidate();
    }
}
