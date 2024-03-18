package candyenk.android.tools;

import android.animation.Animator;
import android.view.animation.Animation;

/**
 * Android动画预设类
 * Android View动画预设类
 * A结尾是Animator
 * V结尾是Animation
 */
public class A {
    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /*** 只保留结束监听 ***/
    public interface EndV extends Animation.AnimationListener {
        @Override
        default void onAnimationStart(Animation a) {}

        @Override
        default void onAnimationRepeat(Animation a) {}
    }

    /*** 只保留开始监听 ***/
    public interface StartV extends Animation.AnimationListener {
        @Override
        default void onAnimationRepeat(Animation a) {}

        @Override
        default void onAnimationEnd(Animation a) {}
    }

    /*** 只保留结束监听 ***/
    public interface EndA extends Animator.AnimatorListener {
        /**
         * @param a 当前动画
         * @param b 是否反向播放
         */
        @Override
        void onAnimationEnd(Animator a, boolean b);

        @Override
        default void onAnimationStart(Animator a, boolean b) {}

        @Override
        default void onAnimationStart(Animator a) {}

        @Override
        default void onAnimationEnd(Animator a) {}

        @Override
        default void onAnimationCancel(Animator a) {}

        @Override
        default void onAnimationRepeat(Animator a) {}
    }

    /*** 只保留开始监听 ***/
    public interface StartA extends Animator.AnimatorListener {
        /**
         * @param a 当前动画
         * @param b 是否反向播放
         */
        @Override
        void onAnimationStart(Animator a, boolean b);

        @Override
        default void onAnimationEnd(Animator a, boolean b) {}

        @Override
        default void onAnimationStart(Animator a) {}

        @Override
        default void onAnimationEnd(Animator a) {}

        @Override
        default void onAnimationCancel(Animator a) {}

        @Override
        default void onAnimationRepeat(Animator a) {}
    }
}
