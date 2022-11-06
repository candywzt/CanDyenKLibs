package candyenk.android.tools;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.core.view.GestureDetectorCompat;

import java.util.function.Consumer;

/**
 * Android GestureDetector帮助类
 * 手势侦测器
 * <p>
 * 快按屏幕:onDown
 * 慢按屏幕：onDown–>onShowPress
 * 按下屏幕等待一段时间onDown–>onShowPress–>onLongPress
 * 拖动屏幕：onDown–>onShowPress–>onScroll(多个)–>onFling
 * 快速滑动：onDown–>onScroll(多个)–>onFling
 * 双击：onDown–>onSingleTapUp–>onDoubleTap–>onDoubleTapEvent–>onDown–>onDoubleTapEvent
 */
public class GD {
    private final Context context;
    private Consumer<MotionEvent> click;
    private Consumer<MotionEvent> doubleClick;
    private Consumer<MotionEvent> longClick;

    /**
     * 创建
     */
    public static GD create(Context context) {
        return new GD(context);
    }

    private GD(Context context) {
        this.context = context;
    }

    /**
     * 设置单击事件
     */
    public GD click(Consumer<MotionEvent> e) {
        this.click = e;
        return this;
    }

    /**
     * 设置双击事件
     */
    public GD doubleClick(Consumer<MotionEvent> e) {
        this.doubleClick = e;
        return this;
    }

    /**
     * 设置长按事件
     */
    public GD longClick(Consumer<MotionEvent> e) {
        this.longClick = e;
        return this;
    }

    /**
     * 生成GestureDetectorCompat
     */
    public GestureDetectorCompat buildCompat() {
        return new GestureDetectorCompat(context, createListener());
    }

    /**
     * 生成GestureDetector
     */
    public GestureDetector build() {
        return new GestureDetector(context, createListener());
    }

    /*** 执行返回器 ***/
    private boolean er(Consumer<MotionEvent> c, MotionEvent e) {
        if (c == null) return false;
        c.accept(e);
        return true;
    }

    /*** 创建侦测器 ***/
    private GestureDetector.OnGestureListener createListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override//按下即触发
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override//按住触发
            public void onShowPress(MotionEvent e) {
            }

            @Override//轻触抬起触发(单击)
            public boolean onSingleTapUp(MotionEvent e) {
                return er(click, e);
            }

            /*
             * 屏幕拖动事件，如果按下的时间过长，调用了onLongPress，再拖动屏幕不会触发onScroll。拖动屏幕会多次触发
             * @param e1 开始拖动的第一次按下down操作,也就是第一个ACTION_DOWN
             * @parem e2 触发当前onScroll方法的ACTION_MOVE
             * @param distanceX 当前的x坐标与最后一次触发scroll方法的x坐标的差值。
             * @param diastancY 当前的y坐标与最后一次触发scroll方法的y坐标的差值。
             */
            @Override//屏幕拖动触发(多次触发)
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override//长按触发
            public void onLongPress(MotionEvent e) {
                er(longClick, e);
            }

            /*
             * 按下屏幕，在屏幕上快速滑动后松开，由一个down,多个move,一个up触发
             * @param e1 开始快速滑动的第一次按下down操作,也就是第一个ACTION_DOWN
             * @parem e2 触发当前onFling方法的move操作,也就是最后一个ACTION_MOVE
             * @param velocityX：X轴上的移动速度，像素/秒
             * @parram velocityY：Y轴上的移动速度，像素/秒
             */
            @Override//快速滑动触发
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            /*
             * 单击事件。用来判定该次点击是单纯的SingleTap而不是DoubleTap，如果连续点击两次就是DoubleTap手势，
             * 如果只点击一次，系统等待一段时间后没有收到第二次点击则判定该次点击为SingleTap而不是DoubleTap，
             * 然后触发SingleTapConfirmed事件。触发顺序是：OnDown->OnsingleTapUp->OnsingleTapConfirmed
             * 关于onSingleTapConfirmed和onSingleTapUp的一点区别：二者的区别是：onSingleTapUp，只要手抬起就会执行，
             * 而对于onSingleTapConfirmed来说，如果双击的话，则onSingleTapConfirmed不会执行。
             */
            @Override//单击
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override//双击
            public boolean onDoubleTap(MotionEvent e) {
                return er(doubleClick, e);
            }

            /*
             * 双击间隔中发生的动作。指触发onDoubleTap以后，在双击之间发生的其它动作，包含down、up和move事件
             */
            @Override//双击间隔
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            /**/
            @Override//上下文单击
            public boolean onContextClick(MotionEvent e) {
                return false;
            }
        };
    }
}
