package co.ciao.bluclub.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.Toast;

import co.ciao.bluclub.R;

public class ItemAnimation {

    /* animation type */
    public static final int BOTTOM_UP = 1;
    public static final int FADE_IN = 2;
    public static final int LEFT_RIGHT = 3;
    public static final int RIGHT_LEFT = 4;
    public static final int NONE = 0;

    /* animation duration */
    private static final long DURATION_IN_BOTTOM_UP = 150;
    private static final long DURATION_IN_FADE_ID = 500;
    private static final long DURATION_IN_LEFT_RIGHT = 150;
    private static final long DURATION_IN_RIGHT_LEFT = 150;

    public static void animate(View view, int position, int type) {
        switch (type) {
            case BOTTOM_UP:
                animateBottomUp(view, position);
                break;

            case FADE_IN:
                animateFadeIn(view, position);
                break;

            case LEFT_RIGHT:
                animateLeftRight(view, position);
                break;

            case RIGHT_LEFT:
                animateRightLeft(view, position);
                break;
        }
    }

    private static void animateBottomUp(View view, int position) {
        boolean not_first_item = position == -1;
        position = position + 1;
        view.setTranslationY(not_first_item ? 800 : 500);
        view.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", not_first_item ? 800 : 500, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1.f);
        animatorTranslateY.setStartDelay(not_first_item ? 0 : (position * DURATION_IN_BOTTOM_UP));
        animatorTranslateY.setDuration((not_first_item ? 3 : 1) * DURATION_IN_BOTTOM_UP);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }

    private static void animateFadeIn(View view, int position) {
        boolean not_first_item = position == -1;
        position = position + 1;
        view.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 0.f, 0.5f, 1.f);
        ObjectAnimator.ofFloat(view, "alpha", 0.f).start();
        animatorAlpha.setStartDelay(not_first_item ? DURATION_IN_FADE_ID / 2 : (position * DURATION_IN_FADE_ID / 3));
        animatorAlpha.setDuration(DURATION_IN_FADE_ID);
        animatorSet.play(animatorAlpha);
        animatorSet.start();
    }

    private static void animateLeftRight(View view, int position) {
        boolean not_first_item = position == -1;
        position = position + 1;
        view.setTranslationX(-400f);
        view.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view, "translationX", -400f, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1.f);
        ObjectAnimator.ofFloat(view, "alpha", 0.f).start();
        animatorTranslateY.setStartDelay(not_first_item ? DURATION_IN_LEFT_RIGHT : (position * DURATION_IN_LEFT_RIGHT));
        animatorTranslateY.setDuration((not_first_item ? 2 : 1) * DURATION_IN_LEFT_RIGHT);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }

    private static void animateRightLeft(View view, int position) {
        boolean not_first_item = position == -1;
        position = position + 1;
        view.setTranslationX(view.getX() + 400);
        view.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(view, "translationX", view.getX() + 400, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1.f);
        ObjectAnimator.ofFloat(view, "alpha", 0.f).start();
        animatorTranslateY.setStartDelay(not_first_item ? DURATION_IN_RIGHT_LEFT : (position * DURATION_IN_RIGHT_LEFT));
        animatorTranslateY.setDuration((not_first_item ? 2 : 1) * DURATION_IN_RIGHT_LEFT);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }


    public static void fadeOut(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f);
        animator.addUpdateListener(valueAnimator -> {
            float alpha = (float) valueAnimator.getAnimatedValue();
            view.setAlpha(alpha);
        });
        animator.setDuration(1000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    public static void fadeIn(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.addUpdateListener(valueAnimator -> {
            float alpha = (float) valueAnimator.getAnimatedValue();
            view.setAlpha(alpha);
        });
        animator.setDuration(1000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    @SuppressWarnings("unused")
    public static void scaleDownX(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        animator.addUpdateListener(valueAnimator -> {
            float scaleX = (float) valueAnimator.getAnimatedValue();
            view.setScaleX(scaleX);});
        animator.setDuration(1000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    public static void scaleDownY(View view) {
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                view, "scaleY", 0f, 1.2f, 1f);
        animator2.addUpdateListener(valueAnimator -> {
            float scaleY = (float) valueAnimator.getAnimatedValue();
            view.setScaleY(scaleY);
        });
        animator2.setDuration(1000);
        animator2.start();
    }

    public static void expandOnClick(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1.1f,1f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1.1f, 1f);
        animator1.addUpdateListener(valueAnimator -> {
            float scaleY = (float) valueAnimator.getAnimatedValue();
            view.setScaleY(scaleY);
        });
        animator.addUpdateListener(valueAnimator -> {
            float scaleX = (float) valueAnimator.getAnimatedValue();
            view.setScaleX(scaleX);
        });

        animator.setDuration(1000);
        animator1.setDuration(1000);
        animator.start();
        animator1.start();
    }
}
