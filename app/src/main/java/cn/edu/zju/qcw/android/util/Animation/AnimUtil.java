package cn.edu.zju.qcw.android.util.Animation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;

import cn.edu.zju.qcw.android.base.BaseAnimationListener;

/**
 * Created by SQ on 2017/5/9.
 */

public class AnimUtil {

    public static final int MOVE_LEFT = 0;
    public static final int MOVE_RIGHT = 1;
    public static final int MOVE_UP = 2;
    public static final int MOVE_DOWN = 3;


    public static void alphaAnimation(final View view, float fromAlpha, final float toAlpha, long duration, Animation.AnimationListener listener) {
        view.setVisibility(View.VISIBLE);

        final AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        if (toAlpha == 0) {
            alphaAnimation.setAnimationListener(new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setAlpha(toAlpha);
                    view.setVisibility(View.GONE);
                    view.clearAnimation();
                }
            });
        } else if (toAlpha == 1) {
            Log.i("TAG", view.getVisibility() + "");
            view.setAlpha(toAlpha);
            alphaAnimation.setAnimationListener(new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    view.clearAnimation();
                }
            });
        }
        if (listener != null) {
            alphaAnimation.setAnimationListener(listener);
        }

        view.startAnimation(alphaAnimation);

    }

    public static void moveDistanceAnimation(final View view, int direction, int distance, long duration, Animation.AnimationListener listener) {
        int x = 0;
        int y = 0;

        TranslateAnimation translateAnimation;
        switch (direction) {
            case MOVE_LEFT:
                translateAnimation = new TranslateAnimation(0, -distance, 0, 0);
                x = -distance;
                break;
            case MOVE_DOWN:
                translateAnimation = new TranslateAnimation(0, 0, 0, distance);
                y = distance;
                break;
            case MOVE_UP:
                translateAnimation = new TranslateAnimation(0, 0, 0, -distance);
                y = -distance;
                break;
            case MOVE_RIGHT:
                translateAnimation = new TranslateAnimation(0, distance, 0, 0);
                x = distance;
                break;
            default:
                translateAnimation = new TranslateAnimation(0, 0, 0, 0);
                break;
        }
        final int xx = x;
        final int yy = y;

        translateAnimation.setDuration(duration);
        translateAnimation.setFillAfter(true);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                int left = view.getLeft() + xx;
                int top = view.getTop() + yy;
                int width = view.getWidth();
                int height = view.getHeight();
                view.layout(left, top, left + width, top + height);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (listener != null){
            translateAnimation.setAnimationListener(listener);
        }
            view.startAnimation(translateAnimation);
    }


}
