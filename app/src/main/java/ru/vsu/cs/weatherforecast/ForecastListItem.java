package ru.vsu.cs.weatherforecast;

import android.graphics.drawable.Drawable;

public class ForecastListItem {
    private final Drawable weatherImage;
    private final String description;
    private final String temperature;
    private final String date;

    public ForecastListItem(Drawable weatherImage, String description, String temperature, String date) {
        this.weatherImage = weatherImage;
        this.description = description;
        this.temperature = temperature;
        this.date = date;
    }

    public Drawable getWeatherImage() {
        return weatherImage;
    }

    public String getDescription() {
        return description;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getDate() {
        return date;
    }
}
