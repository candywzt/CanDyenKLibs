package candyenk.android.tools;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.*;
import android.view.View;
import androidx.annotation.RequiresApi;


/**
 * 文本处理器
 */
public class TX {

    /*************************************静态变量**************************************************/
    private static final String TAG = TX.class.getSimpleName();
    /*************************************成员变量**************************************************/
    private final Spannable sp;
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 便捷修改颜色
     */
    public static Spannable color(CharSequence text, int color) {
        return create(text).setColor(0, text.length(), color).out();
    }

    /**
     * 便捷修改大小(sp)
     */
    public static Spannable size(CharSequence text, int size) {
        return create(text).setSize(0, text.length(), size).out();
    }

    /**
     * 便捷修改背景色
     */
    public static Spannable backColor(CharSequence text, int color) {
        return create(text).setBackColor(0, text.length(), color).out();
    }

    /**
     * 创建帮助类
     */
    public static TX create(CharSequence text) {
        if (text == null) text = "";
        return new TX(text);
    }
    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/
    /*** 创建Spannable ***/
    private static Spannable createSB(CharSequence text) {
        return text instanceof Spannable ? (Spannable) text : new SpannableString(text);
    }
    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /**
     * URL点击监听接口
     */
    public interface URLListener {
        void onClick(View view, String url);
    }
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    private TX(CharSequence text) {
        this.sp = createSB(text);
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    //输出
    public Spannable out() {
        return sp;
    }

    /**
     * 给文本换个颜色(十六进制颜色值)
     */
    public TX setColor(int s, int e, int c) {
        if (s == e || s < 0 || e > sp.length()) return this;
        sp.setSpan(new ForegroundColorSpan(c), s, e, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 给文本换个背景颜色(十六进制颜色值)
     */
    public TX setBackColor(int s, int e, int c) {
        if (s == e || s < 0 || e > sp.length()) return this;
        sp.setSpan(new BackgroundColorSpan(c), s, e, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 给文本换个字体
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public TX setFont(int s, int e, Typeface tf) {
        if (s == e || s < 0 || e > sp.length()) return this;
        sp.setSpan(new TypefaceSpan(tf), s, e, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 给文本设置大小
     * sp
     */
    public TX setSize(int s, int e, int size) {
        if (s == e || s < 0 || e > sp.length()) return this;
        sp.setSpan(new AbsoluteSizeSpan(size), s, e, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }


    /**
     * 在前面加个标点
     * 类似列表那种
     *
     * @param gw 点与文字的间距px
     * @param c  标点颜色(如果颜色为透明,则不显示标点)
     * @param r  标点半径px
     */
    public TX setBullet(int s, int e, int gw, int c, int r) {
        if (s == e || s < 0 || e > sp.length()) return this;
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(gw);
        parcel.writeInt(c);
        parcel.writeInt(c);
        parcel.writeInt(r);
        parcel.setDataPosition(0);
        sp.setSpan(new BulletSpan(parcel), s, e, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * 设置URL监听
     * 记得给TextView控件设置
     * setMovementMethod(LinkMovementMethod.getInstance())
     */
    public TX setURLClick(URLListener l) {
        URLSpan[] urls = sp.getSpans(0, sp.length(), URLSpan.class);
        for (URLSpan url : urls) {
            sp.setSpan(new URLSpan(url, l), sp.getSpanStart(url), sp.getSpanEnd(url), sp.getSpanFlags(url));
            sp.removeSpan(url);
        }
        return this;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
    static class URLSpan extends android.text.style.URLSpan {
        private final URLListener l;

        public URLSpan(android.text.style.URLSpan url, URLListener l) {
            super(url.getURL());
            this.l = l;
        }

        @Override
        public void onClick(View widget) {
            l.onClick(widget, getURL());
        }
    }
}
