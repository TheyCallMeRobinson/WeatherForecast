package ru.vsu.cs.weatherforecast.util;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
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

    @GET
    Call<ResponseBody> getListPicture(@Url String url);
}
