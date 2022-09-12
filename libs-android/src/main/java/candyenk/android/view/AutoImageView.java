package candyenk.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import candyenk.android.R;


/**
 * 自动滚动图片框架
 */
public class AutoImageView extends FrameLayout {
    private Context context;
    private ImageView mImageView;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/
    public AutoImageView(Context context) {
        this(context, null);
    }

    public AutoImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AutoImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initLayout();
        initAttrs(attrs);
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/
    private void initLayout() {
        FrameLayout.LayoutParams lp = new LayoutParams(-1, -1);
        setLayoutParams(lp);
        mImageView = new ImageView(context);
        FrameLayout.LayoutParams lp2 = new LayoutParams(-1, -1);
        lp.leftMargin = dp2px(-100);
        lp.rightMargin = dp2px(-300);
        mImageView.setLayoutParams(lp2);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(mImageView);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CDKAutoImageView);
        int resid = a.getResourceId(R.styleable.CDKAutoImageView_android_src, 0);
        setImage(resid);
    }

    private int dp2px(double dpValue) {
        float num = dpValue < 0 ? -1 : 1;
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + (0.5f * num));
    }
    /**********************************************************************************************/
    /*************************************公开方法**************************************************/
    /**********************************************************************************************/
    public void setImage(int resId) {
        mImageView.setImageResource(resId);
        new Handler().postDelayed(() -> {
            AnimationSet animationSet = new AnimationSet(true);
            Animation anim = new TranslateAnimation(-500, 0, 0, 0);
            anim.setDuration(20000);
            anim.setRepeatCount(1000);
            anim.setRepeatMode(Animation.REVERSE);
            Animation anim2 = new AlphaAnimation(1.0f, 0.9f);
            anim2.setDuration(20000);
            anim2.setRepeatCount(1000);
            anim2.setRepeatMode(Animation.REVERSE);
            animationSet.addAnimation(anim);
            animationSet.addAnimation(anim2);
            mImageView.startAnimation(animationSet);
        }, 200L);
    }
}
