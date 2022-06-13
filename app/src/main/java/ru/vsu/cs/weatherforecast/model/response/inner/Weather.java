package ru.vsu.cs.weatherforecast.model.response.inner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weather {
    Integer id;
    String main;
    String description;
    String icon;
}
