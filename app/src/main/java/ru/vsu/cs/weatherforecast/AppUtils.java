package ru.vsu.cs.weatherforecast;

public class AppUtils {
    public static String firstUpperCase(String word){
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
