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
    public static final int SEEK_FREE = 0;//空闲状态
    public static final int SEEK_DOWN = -1;//按下瞬间
    public static final int SEEK_SLIDE = 2;//拖动中
    public static final int SEEK_UP = 1;//抬起瞬间
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
                float x = event.getX();
                if (x < super.startPoint) x = super.startPoint;
                else if (x > super.endPoint) x = super.endPoint;
                if (x != super.progressPoint)
                    super.setProgressPoint(x);
                break;
            case MotionEvent.ACTION_UP:// 手指抬起(1)
            case MotionEvent.ACTION_CANCEL:// 触摸动作取消
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
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
