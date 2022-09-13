package candyenk.android.handle;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;


/**
 * 文本处理器
 * <br><font color=red>线程不安全</font>
 * 默认使用[)Spanned.SPAN_INCLUSIVE_EXCLUSIVE
 */
public class TextHandler {
    private static TextHandler INSTANCE;
    private SpannableStringBuilder span;

    private TextHandler() {
        INSTANCE = this;
    }

    //输出
    public Spannable out() {
        INSTANCE = null;
        return span;
    }

    /**
     * <br>给文本换个颜色(十六进制颜色值)
     */
    public TextHandler addColor(int start, int end, int color) {
        if (span.length() == 0) {
            return this;
        } else if (end == 0) {
            end = span.length();
        } else if (start > end) {
            //写这个东西的意义何在?
            int num = start;
            start = end;
            end = num;
        }
        span.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * <br>给文本换个背景颜色(十六进制颜色值)
     */
    public TextHandler addBackgroundColor(int start, int end, int color) {
        if (span.length() == 0) {
            return this;
        } else if (end == 0) {
            end = span.length();
        } else if (start > end) {
            //写这个东西的意义何在?
            int num = start;
            start = end;
            end = num;
        }
        span.setSpan(new BackgroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * <br>给文本换个字体
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public TextHandler addTypeFont(int start, int end, Typeface typeface) {
        if (span.length() == 0) {
            return this;
        } else if (end == 0) {
            end = span.length();
        } else if (start > end) {
            //写这个东西的意义何在?
            int num = start;
            start = end;
            end = num;
        }
        span.setSpan(new TypefaceSpan(typeface), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * <br>给文本设置大小
     *
     * @param dip true:输入px;false:输入dp
     */
    public TextHandler addSize(int start, int end, int size, boolean dip) {
        if (span.length() == 0) {
            return this;
        } else if (end == 0) {
            end = span.length();
        } else if (start > end) {
            //写这个东西的意义何在?
            int num = start;
            start = end;
            end = num;
        }
        span.setSpan(new AbsoluteSizeSpan(size, dip), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * <br>设置内容可点击
     * <br>记得给TextView控件设置
     * <br>setMovementMethod(LinkMovementMethod.getInstance())
     */
    public TextHandler addClickable(int start, int end, View.OnClickListener l) {
        if (span.length() == 0) {
            return this;
        } else if (end == 0) {
            end = span.length();
        } else if (start > end) {
            //写这个东西的意义何在?
            int num = start;
            start = end;
            end = num;
        }
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Log.e("aaa", "bbb");
                l.onClick(widget);
            }
        }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * <br>在前面加个标点
     * <br>类似列表那种
     * <br><font color=yellow>这个end有必要吗？？？</font>
     *
     * @param gapWidth 点与文字的间距px
     * @param color    标点颜色(如果颜色为透明,则不显示标点)
     * @param radius   标点半径px
     */
    public TextHandler addBulletSpan(int start, int end, int gapWidth, int color, int radius) {
        if (span.length() == 0) {
            return this;
        } else if (end == 0) {
            end = span.length();
        } else if (start > end) {
            //写这个东西的意义何在?
            int num = start;
            start = end;
            end = num;
        }
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(gapWidth);
        parcel.writeInt(color);
        parcel.writeInt(color);
        parcel.writeInt(radius);
        parcel.setDataPosition(0);
        span.setSpan(new BulletSpan(parcel), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }
    /**********************************************************************************************/
    /***************************************私有方法************************************************/
    /**********************************************************************************************/


    /**********************************************************************************************/
    /***************************************静态方法************************************************/
    /**********************************************************************************************/
    //创建
    public static TextHandler CREARE(CharSequence text) {
        new TextHandler();
        INSTANCE.span = new SpannableStringBuilder(text);
        return INSTANCE;
    }

    /**
     * 给文本换个颜色(十六进制颜色值)
     */
    public static Spannable setTextColor(CharSequence text, int color) {
        Spannable span = new SpannableString(text);
        span.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 给文本换个背景颜色(十六进制颜色值)
     */
    public static Spannable setTextBackgroundColor(CharSequence text, int color) {
        Spannable span = new SpannableString(text);
        span.setSpan(new BackgroundColorSpan(color), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 给文本换个字体
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Spannable setTextTypeFont(CharSequence text, Typeface typeface) {
        Spannable span = new SpannableString(text);
        span.setSpan(new TypefaceSpan(typeface), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 给文本设置大小
     *
     * @param dip true:输入px;false:输入dp
     */
    public static Spannable setTextSize(CharSequence text, int size, boolean dip) {
        Spannable span = new SpannableString(text);
        span.setSpan(new AbsoluteSizeSpan(size, dip), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 设置内容可点击
     * 记得给TextView控件设置
     * setMovementMethod(LinkMovementMethod.getInstance())
     */
    public static Spannable setTextClickable(CharSequence text, View.OnClickListener l) {
        Spannable span = new SpannableString(text);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Log.e("aaa", "bbb");
                l.onClick(widget);
            }
        }, 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 设置内容可点击
     * 记得给TextView控件设置
     * setMovementMethod(LinkMovementMethod.getInstance())
     */
    public static Spannable setTextURLSpan(CharSequence text) {
        Spannable span = new SpannableString(text);
        span.setSpan(new URLSpan("https:www.baidu.com"), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 在前面加个标点
     * 类似列表那种
     *
     * @param gapWidth 点与文字的间距px
     * @param color    标点颜色(如果颜色为透明,则不显示标点)
     * @param radius   标点半径px
     */
    public static Spannable setTextBulletSpan(CharSequence text, int gapWidth, int color, int radius) {
        Spannable span = new SpannableString(text);
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(gapWidth);
        parcel.writeInt(color);
        parcel.writeInt(color);
        parcel.writeInt(radius);
        parcel.setDataPosition(0);
        span.setSpan(new BulletSpan(parcel), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static Spannable setTextDrawableMarginSpan(Drawable drawable) {
        Spannable span = new SpannableString(" aaa");
        span.setSpan(new DrawableMarginSpan(drawable), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static Spannable setTextImageSpan(Drawable drawable, int verticalAlignment) {
        Spannable span = new SpannableString(" aaa");
        span.setSpan(new DrawableMarginSpan(drawable, verticalAlignment), 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }


}
