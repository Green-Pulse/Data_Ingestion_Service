package com.greenpulse.data.data_ingestion_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDataEvent {
    private String city;
    private double temperature;
    private double humidity;
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "WeatherDataEvent{" +
                "city='" + city + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", timestamp=" + timestamp +
                '}';
    }
}
