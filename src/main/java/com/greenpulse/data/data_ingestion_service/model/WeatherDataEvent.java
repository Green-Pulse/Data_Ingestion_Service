package com.greenpulse.data.data_ingestion_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

//@RedisHash("weather-data")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDataEvent implements Serializable {

//    @Id
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
