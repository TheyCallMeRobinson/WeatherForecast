package ru.vsu.cs.weatherforecast.model.response;

import lombok.Getter;
import lombok.Setter;
import ru.vsu.cs.weatherforecast.model.response.inner.Temp;
import ru.vsu.cs.weatherforecast.model.response.inner.Weather;

@Getter
@Setter
public class ForecastListDtoResponse {
    Integer datetime;
    Temp temp;
    Weather weather;
}
