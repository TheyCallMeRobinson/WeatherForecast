package ru.vsu.cs.weatherforecast.model.response.inner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeelsLike {
    Double day;
    Double night;
    Double eve;
    Double morn;
}
