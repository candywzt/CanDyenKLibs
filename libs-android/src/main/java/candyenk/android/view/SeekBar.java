package candyenk.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SeekBar extends ProgressBar {
    public static final int SEEK_FREE = 0;//空闲状态
    public static final int SEEK_DOWN = -1;//按下瞬间
    public static final int SEEK_SLIDE = 2;//拖动中
    public static final int SEEK_UP = 1;//抬起瞬间

    private int state;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    /**********************************************************************************************/
    /*****************************************接口**************************************************/
    /**********************************************************************************************/

    public interface OnSeekBarChangeListener {
        void onSeekBarChangeListener(SeekBar seekBar, int progress, float percent, int state);
    }
    /**********************************************************************************************/
    /*****************************************构造方法***********************************************/
    /**********************************************************************************************/
    public SeekBar(Context context) {
        this(context, null);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**********************************************************************************************/
    /*****************************************重写方法***********************************************/
    /**********************************************************************************************/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下(0)
            case MotionEvent.ACTION_MOVE://手指滑动(2)

            case MotionEvent.ACTION_UP:// 手指抬起(1)
            case MotionEvent.ACTION_CANCEL:// 触摸动作取消
                float x = event.getX();
                if (x < super.startPoint) x = super.startPoint;
                else if (x > super.endPoint) x = super.endPoint;
                if (x != super.progressPoint)
                    super.setProgressPoint(x);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void changeProgress(int type, Object number) {
        super.changeProgress(type, number);
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onSeekBarChangeListener(this, super.progress, super.progressPercent, this.state);
        }
    }
    /**********************************************************************************************/
    /*****************************************私有方法***********************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*****************************************公共方法***********************************************/
    /**********************************************************************************************/
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        this.mOnSeekBarChangeListener = l;
    }
}
