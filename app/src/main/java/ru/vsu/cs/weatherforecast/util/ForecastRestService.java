package ru.vsu.cs.weatherforecast.util;

import android.graphics.drawable.Drawable;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.vsu.cs.weatherforecast.model.response.WeatherApiFullResponse;

public interface ForecastRestService {
    @GET("/data/2.5/onecall")
    Call<WeatherApiFullResponse> getForecast(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query(value = "exclude", encoded = true) String exclude,
            @Query("appid") String key,
            @Query("units") String units,
            @Query("lang") String language
    );

    @GET("/img/wn/{pic_name}@2x.png")
    Call<Drawable> getDrawable(@Path("pic_name") String picName);
}
