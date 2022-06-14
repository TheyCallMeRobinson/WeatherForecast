package ru.vsu.cs.weatherforecast.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.vsu.cs.weatherforecast.model.response.inner.FeelsLike;
import ru.vsu.cs.weatherforecast.model.response.inner.Temp;
import ru.vsu.cs.weatherforecast.model.response.inner.Weather;

@Getter
@Setter
public class WeatherMainBodyResponse {
    @SerializedName("dt")
    @Expose
    Integer datetime;

    @SerializedName("sunrise")
    @Expose
    Integer sunrise;

    @SerializedName("sunset")
    @Expose
    Integer sunset;

    @SerializedName("temp")
    @Expose
    Temp temp;

    @SerializedName("feels_like")
    @Expose
    FeelsLike feelsLike;

    @SerializedName("pressure")
    @Expose
    Double pressure;

    @SerializedName("wind_speed")
    @Expose
    Double windSpeed;

    @SerializedName("weather")
    @Expose
    List<Weather> weather;

    @SerializedName("clouds")
    @Expose
    Double clouds;
}
