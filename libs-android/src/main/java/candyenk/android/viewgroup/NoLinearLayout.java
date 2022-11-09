package candyenk.android.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * 拦截内部控件点击事件的LinearLayout
 */
public class NoLinearLayout extends LinearLayout {

    public NoLinearLayout(Context context) {
        super(context);
    }

    public NoLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //拦截子控件的点击事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
