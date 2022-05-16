package ru.vsu.cs.weatherforecast.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    public static String firstUpperCase(String word){
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static List<Animator> getFadeInAnimatorsForViews(Integer duration, View... views) {
        List<Animator> animators = new ArrayList<>();
        for(View v: views) {
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                v.setAlpha(value);
            });
            animator.setDuration(duration);
            animators.add(animator);
        }
        return animators;
    }
}
