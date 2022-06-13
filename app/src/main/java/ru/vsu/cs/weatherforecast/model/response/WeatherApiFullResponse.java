package ru.vsu.cs.weatherforecast.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherApiFullResponse {
    @SerializedName("lat")
    @Expose
    Double latitude;

    @SerializedName("lon")
    @Expose
    Double longitude;

    @SerializedName("daily")
    @Expose
    List<WeatherDailyResponse> dailyList;
}
