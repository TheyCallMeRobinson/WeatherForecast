package ru.vsu.cs.weatherforecast.model.response.inner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Temp {
    Double day;
    Double min;
    Double max;
    Double night;
    Double eve;
    Double morn;
}
