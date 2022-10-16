package candyenk.android.tools;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import candyenk.android.utils.ULay;

/**
 * Android AutoImageView 帮助类
 */
public class AIF {
    /*************************************静态变量**************************************************/
    /*************************************成员变量**************************************************/
    private Context context;
    private ImageView imageView;
    private Drawable image;
    private int ori;//滚动方向
    private int margin;//偏移量(负数)
    private int w, h;//控件尺寸
    private Animation autoAnim;//自动动画
    private Animation showAnim;//显现动画
    private Animation hideAnim;//消失动画

    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 创建
     */
    public static AIF create() {
        return new AIF();
    }
    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /*** 只保留结束监听 ***/
    private interface EAL extends Animation.AnimationListener {
        @Override
        default void onAnimationStart(Animation animation) {}

        @Override
        default void onAnimationRepeat(Animation animation) {}
    }
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/

    private AIF() {
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置需要加载的ImageView
     * ImageView尺寸请设置为match
     */
    public AIF setImageView(ImageView imageView) {
        this.imageView = imageView;
        this.context = imageView.getContext();
        return setImage(imageView.getDrawable());
    }

    /**
     * 设置要显示的图片
     */
    public AIF setImage(Drawable drawable) {
        if (drawable != null) this.image = drawable;
        return this;
    }

    /**
     * 开始滚动
     */
    public void start() {
        createDrawable();
        measureMargin();
        createAnimation();
        V.createML(imageView).setSize(w, h).setMargin(0, 0, ori < 0 ? margin : 0, ori > 0 ? margin : 0).refresh();
        imageView.setImageDrawable(image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (ori != 0) imageView.startAnimation(autoAnim);
    }

    /**
     * 开始滚动
     * 附带启动动画
     */
    public void showStart() {
        createDrawable();
        measureMargin();
        createAnimation();
        V.createML(imageView).setSize(w, h).setMargin(0, 0, ori < 0 ? margin : 0, ori > 0 ? margin : 0).refresh();
        imageView.setImageDrawable(image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.startAnimation(showAnim);
    }

    /**
     * 重新滚动
     * 附带切换动画动画
     */
    public void updateStart() {
        createDrawable();
        measureMargin();
        createAnimation();
        imageView.startAnimation(hideAnim);
    }

    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/
    /*** 获取图片内容 ***/
    private void createDrawable() {
        if (imageView == null) throw new NullPointerException("尚未设置ImageView,请使用setImageView");
        if (image == null) image = imageView.getDrawable();
        if (image == null) throw new NullPointerException("尚未设置Image图片,请使用setImage");
    }

    /*** 创建动画 ***/
    private void createAnimation() {
        autoAnim = new TranslateAnimation(0, ori > 0 ? 0 : margin, 0, ori < 0 ? 0 : margin);
        autoAnim.setDuration(-30L * margin);
        autoAnim.setRepeatCount(Animation.INFINITE);
        autoAnim.setRepeatMode(Animation.REVERSE);

        showAnim = new AlphaAnimation(0, 1);//显现
        showAnim.setDuration(500);
        showAnim.setAnimationListener((EAL) a -> {
            if (ori != 0) imageView.startAnimation(autoAnim);
        });
        hideAnim = new AlphaAnimation(1, 0);//隐退
        hideAnim.setDuration(500);
        hideAnim.setAnimationListener((EAL) a -> {
            V.createML(imageView).setSize(w, h).setMargin(0, 0, ori < 0 ? margin : 0, ori > 0 ? margin : 0).refresh();
            imageView.setImageDrawable(image);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.startAnimation(showAnim);
        });
    }

    /*** 计算偏移方向及偏移量 ***/
    private void measureMargin() {
        Rect ls = ULay.getSize(context);
        w = ls.height() / image.getIntrinsicHeight() * image.getIntrinsicWidth();//同比宽
        h = ls.width() / image.getIntrinsicWidth() * image.getIntrinsicHeight();//同比高
        if (w > ls.width()) {
            h = ls.height();
            margin = ls.width() - w;
            ori = -1;
        } else if (h > ls.height()) {
            w = ls.width();
            margin = ls.height() - h;
            ori = 1;
        } else {
            w = ls.width();
            h = ls.height();
            margin = 0;
            ori = 0;
        }
    }
    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/


}
