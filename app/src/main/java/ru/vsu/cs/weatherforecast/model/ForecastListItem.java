package ru.vsu.cs.weatherforecast.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForecastListItem {
    private Bitmap weatherImage;
    private String description;
    private String temperature;
    private String date;

    public ForecastListItem(Bitmap weatherImage, String description, String temperature, String date) {
        this.weatherImage = weatherImage;
        this.description = description;
        this.temperature = temperature;
        this.date = date;
    }
}
