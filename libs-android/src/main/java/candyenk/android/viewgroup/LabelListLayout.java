package candyenk.android.viewgroup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import candyenk.android.view.LabelView;

import java.util.ArrayList;

/**
 * 自动换行的小标签框架
 * 待优化，太卡了！！！跟我LUA写的一样卡
 * TODO:算了往后再说
 * TODO:又不是不能用.jpg
 */
public class LabelListLayout extends ViewGroup {
    private Context context;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/
    public LabelListLayout(Context context) {
        this(context, null);
    }

    public LabelListLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LabelListLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    /**********************************************************************************************/
    /******************************************方法重写*********************************************/
    /**********************************************************************************************/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int x = 0;
        int y = 0;
        int row = 0;

        for (int index = 0; index < childCount; index++) {
            final View child = getChildAt(index);
            if (child.getVisibility() != View.GONE) {
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width;
                y = row * height + height;
                if (x > maxWidth) {
                    x = width;
                    row++;
                    y = row * height + height;
                }
            }
        }
        // 设置容器所需的宽度和高度
        setMeasuredDimension(maxWidth, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        int maxWidth = r - l;
        int x = 0, y, row = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width;
                y = row * height + height;
                if (x > maxWidth) {
                    x = width;
                    row++;
                    y = row * height + height;
                }
                child.layout(x - width, y - height, x, y);
                child.measure(width, height);
            }
        }
    }

    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************公开方法**************************************************/
    /**********************************************************************************************/
    public void addLabel(LabelView lv) {
        addView(lv);
    }

    public void setLabelList(ArrayList<LabelView> mList) {
        for (LabelView lv : mList) {
            if (lv != null) {
                addLabel(lv);
            }
        }
    }

    public void setLabelList(LabelView[] mList) {
        for (LabelView lv : mList) {
            if (lv != null) {
                addLabel(lv);
            }
        }
    }

    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            removeAllViews();
            for (int i = 0; i < adapter.getItemCount(); i++) {
                LabelView lv = new LabelView(context);
                lv.setBackgroundColor(adapter.getBackgroundColor(i));
                lv.setImageDrawable(adapter.getImageDrawable(i));
                lv.setText(adapter.getTitleText(i));
                lv.setTextColor(adapter.getTextColor(i));
                addLabel(lv);
            }
        }
    }
    /**********************************************************************************************/
    /***************************************适配器*************************************************/
    /**********************************************************************************************/
    public abstract static class Adapter {

        protected abstract int getItemCount();

        protected abstract Drawable getImageDrawable(int position);

        protected abstract CharSequence getTitleText(int position);

        @ColorInt
        protected abstract int getTextColor(int position);

        @ColorInt
        protected abstract int getBackgroundColor(int position);
    }
}
