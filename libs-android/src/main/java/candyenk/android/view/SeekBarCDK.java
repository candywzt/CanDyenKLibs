package candyenk.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 这个东西不是继承自SeekBar
 * SeekBar那套我不吃
 */
public class SeekBarCDK extends ProgressBarCDK {
    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/
    public SeekBarCDK(Context context) {
        this(context, null);
    }

    public SeekBarCDK(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarCDK(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下(0)
                getParent().requestDisallowInterceptTouchEvent(true);
            case MotionEvent.ACTION_MOVE://手指滑动(2)
                super.setProgressPoint(event.getX());
                break;
            case MotionEvent.ACTION_UP:// 手指抬起(1)
            case MotionEvent.ACTION_CANCEL:// 触摸动作取消
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }
    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************公共方法***********************************************/
    /**********************************************************************************************/
}
