package ru.vsu.cs.weatherforecast.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherHourBodyResponse {
    Integer dt;
    Double temp;
}
