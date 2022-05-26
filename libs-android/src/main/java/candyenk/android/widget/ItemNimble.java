package candyenk.android.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import candyenk.android.R;
import candyenk.android.viewgroup.NimbleFrameLayout;
import com.google.android.material.card.MaterialCardView;


/**
 * 灵动动画卡片项目控件
 */
public final class ItemNimble extends NimbleFrameLayout {
    private Context context;
    private TextView titleView; //文字控件
    private ImageView imageView; //图片控件
    private ImageView cardimage;//卡片图控件


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
        LayoutParams lp = new LayoutParams(-1, -2);
        setLayoutParams(lp);
        setPadding(dp2px(10), 0, dp2px(10), 0);

        MaterialCardView cv = new MaterialCardView(context);
        LayoutParams lp2 = new LayoutParams(-1, -1);
        lp2.setMargins(dp2px(20), dp2px(20), dp2px(20), dp2px(20));
        cv.setLayoutParams(lp2);
        cv.setElevation(dp2px(1));
        cv.setRadius(dp2px(20));
        addView(cv);

        cardimage = new ImageView(context);
        LayoutParams lp3 = new LayoutParams(-1, -1);
        cardimage.setLayoutParams(lp3);
        cv.addView(cardimage);

        imageView = new ImageView(context);
        LayoutParams lp4 = new LayoutParams(-2, -1);
        lp4.gravity = Gravity.LEFT;
        imageView.setLayoutParams(lp4);
        imageView.setElevation(dp2px(2));
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        addView(imageView);

        titleView = new TextView(context);
        LayoutParams lp5 = new LayoutParams(dp2px(200), -1);
        lp5.gravity = Gravity.LEFT;
        lp5.setMargins(dp2px(30), 0, dp2px(30), 0);
        titleView.setLayoutParams(lp5);
        titleView.setElevation(dp2px(3));
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(20);
        titleView.setTextColor(context.getResources().getColor(R.color.text_title));
        addView(titleView);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CDKItemNimble);
        int iconLocation = typedArray.getInt(R.styleable.CDKItemNimble_iconLocation, 0);
        String title = typedArray.getString(R.styleable.CDKItemNimble_title);
        int cardBackground = typedArray.getResourceId(R.styleable.CDKItemNimble_cardbackground, 0);
        int image = typedArray.getResourceId(R.styleable.CDKItemNimble_image, 0);
        typedArray.recycle();

        if (iconLocation == 1) {
            LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
            lp.gravity = Gravity.RIGHT;
            imageView.setLayoutParams(lp);

            LayoutParams lp2 = (LayoutParams) titleView.getLayoutParams();
            lp2.gravity = Gravity.RIGHT;
            titleView.setLayoutParams(lp2);
        }
        if (title != null) {
            titleView.setText(title);
        }
        if (image != 0) {
            imageView.setImageResource(image);
        }
        if (cardBackground != 0) {
            cardimage.setImageResource(cardBackground);
        }
    }

    private void initEvents() {
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
     * 设置上层图片
     */
    public void setImage(int resourceId) {
        imageView.setImageResource(resourceId);
    }

    public void setImage(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 设置卡片背景图
     */
    public void setCardBackground(int resourceId) {
        cardimage.setImageResource(resourceId);
    }

    public void setCardBackground(Drawable drawable) {
        cardimage.setImageDrawable(drawable);
    }

    public void setCardBackground(Bitmap bitmap) {
        cardimage.setImageBitmap(bitmap);
    }

    /**
     * 设置图片位置
     * 0:左
     * 1:右
     */
    public void setImageLocation(int imageLocation) {
        LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
        LayoutParams lp2 = (LayoutParams) titleView.getLayoutParams();
        if (imageLocation == 0) {
            lp.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
        } else if (imageLocation == 1) {
            lp.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
        }
        imageView.setLayoutParams(lp);
        titleView.setLayoutParams(lp2);
    }

    /**
     * 获取Icon控件以便使用第三方图片加载器
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 获取卡片背景图控件以便使用第三方图片加载器
     */
    public ImageView getCardBackgroundView() {
        return cardimage;
    }
}

