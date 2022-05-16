package ru.vsu.cs.weatherforecast.model;

import android.graphics.drawable.Drawable;

public class ForecastListItem {
    private Drawable weatherImage;
    private String description;
    private String temperature;
    private String date;

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

    public void setWeatherImage(Drawable weatherImage) {
        this.weatherImage = weatherImage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
