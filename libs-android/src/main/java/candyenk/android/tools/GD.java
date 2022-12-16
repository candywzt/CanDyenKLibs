package candyenk.android.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import candyenk.java.utils.UArrays;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Android GestureDetector帮助类
 * 似乎不支持CardView???
 * 说实话这东西一点也不好用
 * <p>
 * 快按屏幕:onDown->onUp
 * 慢按屏幕：onDown–>onShowPress
 * 按下屏幕一段时间onDown–>onShowPress–>onLongPress
 * 拖动屏幕：onDown–>onShowPress–>onScroll(多个)–>onFling
 * 快速滑动：onDown–>onScroll(多个)–>onFling
 * 双击：onDown–>onSingleTapUp–>onDoubleTap–>onDoubleTapEvent–>onDown–>onDoubleTapEvent
 */
public class GD {
    private EventGener onDown;
    private EventGener onHold;
    private EventGener onUp;
    private EventGener onClick;
    private EventGener onLong;
    private EventGener onDouble;
    private EventScroll onScroll;
    private EventScroll onFling;

    /**
     * 一般事件发生接口
     */
    public interface EventGener {
        boolean event(View v, MotionEvent e);
    }

    /**
     * 两点区间事件发生接口
     */
    public interface EventScroll {
        boolean event(View v, MotionEvent e1, MotionEvent e2, float f1, float f2);
    }

    /**
     * 创建
     */
    public static GD create() {
        return new GD();
    }

    private GD() {

    }

    /**
     * 设置按下触发
     * 所有动作的起点事件
     */
    public GD onDown(EventGener eg) {
        this.onDown = eg;
        return this;
    }

    /**
     * 设置按下按住触发
     * 按下到长按之间的事件
     */
    public GD onHold(EventGener eg) {
        this.onHold = eg;
        return this;
    }

    /**
     * 设置按下抬起触发
     * 按下-按住-抬起触发
     */
    public GD onUp(EventGener eg) {
        this.onUp = eg;
        return this;
    }

    /**
     * 设置点按触发
     * 一次完整的点按触发
     * 仅推荐在设置双击事件后设置
     */
    public GD onClick(EventGener eg) {
        this.onClick = eg;
        return this;
    }

    /**
     * 设置长按触发
     * 一次完整的长按触发
     */
    public GD onLong(EventGener eg) {
        this.onLong = eg;
        return this;
    }

    /**
     * 设置双击触发
     */
    public GD onDouble(EventGener eg) {
        this.onDouble = eg;
        return this;
    }

    /**
     * 设置滑动触发
     * 按下后移动(多次)触发
     * e1:手势起点,OnDown
     * e2:当前位置的Event
     * x:x轴坐标差值(与上一次)
     * y:y轴坐标差值(与上一次)
     */
    public GD onScroll(EventScroll eg) {
        this.onScroll = eg;
        return this;
    }

    /**
     * 设置甩尾触发
     * 拖动结束后的甩尾,不甩的话不触发
     * e1:手势起点,OnDown
     * e2:当前位置的Event
     * x:x轴甩尾速度(px/s)
     * y:y轴甩尾速度(px/s)
     */
    public GD onFling(EventScroll eg) {
        this.onFling = eg;
        return this;
    }


    /**
     * 生成GestureDetectorCompat
     * 不会绑定到View
     */
    public GestureDetectorCompat build(View view) {
        return new GestureDetectorCompat(view.getContext(), createListener(view));
    }

    /**
     * 生成View.TouchListener
     */
    public View.OnTouchListener toListener() {
        return new View.OnTouchListener() {
            GestureDetectorCompat gd = null;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gd == null) gd = build(v);
                return gd.onTouchEvent(event);
            }
        };
    }

    /**
     * 生成RecycleView Item监听
     */
    public RecyclerView.OnItemTouchListener buildItemListener(Context context) {

        /*** 创建RecycleView Item监听 ***/
        return new RecyclerView.SimpleOnItemTouchListener() {
            GestureDetectorCompat gdc;

            @Override
            public boolean onInterceptTouchEvent(@NonNull @NotNull RecyclerView rv, @NonNull @NotNull MotionEvent e) {
                if (gdc == null) gdc = build(rv);
                return gdc.onTouchEvent(e);
            }

            @Override
            public void onTouchEvent(@NonNull @NotNull RecyclerView rv, @NonNull @NotNull MotionEvent e) {
                if (gdc == null) gdc = build(rv);
                gdc.onTouchEvent(e);
            }
        };
    }


    /*** 创建侦测器 ***/
    private GestureDetector.OnGestureListener createListener(View v) {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override//按下(一刹)
            public boolean onDown(MotionEvent e) {
                return onDown != null && onDown.event(v, e);
            }

            @Override//按下之后,长按之前(持续)
            public void onShowPress(MotionEvent e) {
                if (onHold != null) onHold.event(v, e);
            }

            @Override//抬起(一刹)
            public boolean onSingleTapUp(MotionEvent e) {
                return onUp != null && onUp.event(v, e);
            }

            /**
             * 拖动屏幕会多次触发
             * 屏幕拖动事件，如果按下的时间过长，调用了onLongPress，再拖动屏幕不会触发onScroll。
             * @param e1 开始拖动的第一次按下down操作,也就是第一个ACTION_DOWN
             * @param e2 触发当前onScroll方法的ACTION_MOVE
             * @param x 当前的x坐标与最后一次触发scroll方法的x坐标的差值。
             * @param y 当前的y坐标与最后一次触发scroll方法的y坐标的差值。
             */
            @Override//拖动(多次)
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float x, float y) {
                return onScroll != null && onScroll.event(v, e1, e2, x, y);
            }

            @Override//长按(一刹)
            public void onLongPress(MotionEvent e) {
                if (onLong != null) onLong.event(v, e);
            }

            /**
             * 滑动动作的最后一下甩尾
             * @param e1 滑动开始动作的第一个ACTION_DOWN(按下)
             * @param e2 滑动结束时的最后一个ACTION_MOVE(移动)
             * @param x：X轴上的移动速度，像素/秒
             * @param y：Y轴上的移动速度，像素/秒
             */
            @Override//滑动甩尾(最后一次)
            public boolean onFling(MotionEvent e1, MotionEvent e2, float x, float y) {
                return onFling != null && onFling.event(v, e1, e2, x, y);
            }

            /**
             * 单击事件
             * 用来判定该次点击是单纯的SingleTap而不是DoubleTap
             * 如果连续点击两次就触发DoubleTap
             * 如果只点击一次系统等待一段时间后没有收到第二次点击则触发SingleTapConfirmed
             * 触发顺序是：OnDown->OnsingleTapUp->OnsingleTapConfirmed
             */
            @Override//没有触发双击的单击
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return onClick != null && onClick.event(v, e);
            }

            @Override//双击
            public boolean onDoubleTap(MotionEvent e) {
                return onDouble != null && onDouble.event(v, e);
            }

            /**
             * 双击间隔中发生的动作
             * 指触发onDoubleTap以后，在双击之间发生的其它动作
             */
            @Override//双击间隔
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            @Override//上下文单击
            public boolean onContextClick(MotionEvent e) {
                return false;
            }

        };
    }

    /**
     * 控件触摸监听组
     */
    public static class TouchListeners implements View.OnTouchListener {
        private final List<View.OnTouchListener> tList;

        public TouchListeners() {
            this.tList = new ArrayList<>();
        }

        public TouchListeners add(View.OnTouchListener... ls) {
            UArrays.addArrays(tList, ls);
            return this;
        }

        @SuppressLint("ClickableViewAccessibility")
        public TouchListeners add(GestureDetectorCompat... ls) {
            tList.add((v, event) -> {
                boolean b = true;//只要有一个为false那就是false
                for (GestureDetectorCompat gd : ls) if (gd != null) b &= gd.onTouchEvent(event);
                return b;
            });
            return this;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (tList.isEmpty()) return false;
            boolean b = true;//只要有一个为false那就是false
            for (View.OnTouchListener l : tList) if (l != null) b &= l.onTouch(v, event);
            return b;
        }
    }
}
