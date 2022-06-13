package ru.vsu.cs.weatherforecast.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppUtils {

    public static final String WEATHER_API_KEY = "4ee0653bd41375d67d0527057889f757";
    public static final String BASE_API_URL = "https://api.openweathermap.org";
    public static final String PIC_API_URL = "https://openweathermap.org/img/wn/";

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder().build())
            .build();

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
