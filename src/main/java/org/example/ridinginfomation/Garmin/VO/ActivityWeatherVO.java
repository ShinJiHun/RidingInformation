package org.example.ridinginfomation.Garmin.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityWeatherVO {
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String condition;
}
