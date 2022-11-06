package candyenk.android.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import candyenk.android.R;
import candyenk.android.tools.V;
import com.google.android.material.imageview.ShapeableImageView;


/**
 * 灵动动画卡片项目控件
 */
public final class ItemNimble extends FrameLayout {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private Context context;
    private TextView titleView; //文字控件
    private ImageView iconView; //图片控件
    private ShapeableImageView backView;//背景图图控件


    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    public ItemNimble(Context context) {
        this(context, null);
    }

    public ItemNimble(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemNimble(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemNimble(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    /**
     * 内部控件初始化
     */
    private void initLayout() {
        V.ML(this).size(-1, -2).backgroundRes(0).nimble().refresh();

        backView = new ShapeableImageView(context);
        V.FL(backView).size(-2, -2).marginDP(20).ele(0).scaleType(ImageView.ScaleType.CENTER_CROP).radiusDP(20).parent(this).refresh();

        iconView = new ImageView(context);
        V.FL(iconView).size(-2, -2).ele(1).scaleType(ImageView.ScaleType.FIT_START).parent(this).refresh();

        titleView = new TextView(context);
        V.FL(titleView).sizeDP(-2, -2).ele(2).textSize(20).textColorRes(R.color.text_title).parent(this).refresh();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CDKItemNimble);
        int l = a.getInt(R.styleable.CDKItemNimble_iconLocation, 0);
        String title = a.getString(R.styleable.CDKItemNimble_android_title);
        Drawable back = a.getDrawable(R.styleable.CDKItemNimble_android_background);
        Drawable image = a.getDrawable(R.styleable.CDKItemNimble_android_icon);
        a.recycle();

        setImageLocation(l);
        if (title != null) {
            titleView.setText(title);
        }
        if (image != null) {
            iconView.setImageDrawable(image);
        }
        if (back != null) {
            backView.setImageDrawable(back);
        }
    }

    private void initEvents() {
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
     * 设置上层图片
     */
    public void setImage(int resourceId) {
        iconView.setImageResource(resourceId);
    }

    public void setImage(Drawable drawable) {
        iconView.setImageDrawable(drawable);
    }

    public void setImage(Bitmap bitmap) {
        iconView.setImageBitmap(bitmap);
    }

    /**
     * 设置卡片背景图
     */
    public void setCardBackground(int resourceId) {
        backView.setImageResource(resourceId);
    }

    public void setCardBackground(Drawable drawable) {
        backView.setImageDrawable(drawable);
    }

    /**
     * 设置图片位置
     * 0:左
     * 1:右
     */
    public void setImageLocation(int l) {
        if (l == RIGHT) {
            V.FL(iconView).lGravity(21).refresh();
            V.FL(titleView).lGravity(21).marginDP(0, 0, 100, 0).refresh();
        } else {
            V.FL(iconView).lGravity(19).refresh();
            V.FL(titleView).lGravity(19).marginDP(200, 0, 0, 0).refresh();

        }
    }

    /**
     * 获取Icon控件以便使用第三方图片加载器
     */
    public ImageView getIconView() {
        return iconView;
    }

    /**
     * 获取卡片背景图控件以便使用第三方图片加载器
     */
    public ShapeableImageView getBackgroundView() {
        return backView;
    }
}

