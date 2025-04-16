package com.greenpulse.data.data_ingestion_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

//@RedisHash("sensor-data")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataEvent implements Serializable {
//    @Id
    private String city;
    private double temp;
    private double pres;
    private double dewp;
    private double rain;
    private double windSpeed;
    private boolean wd_NE;
    private boolean wd_NW;
    private boolean wd_SE;
    private LocalDateTime timestamp;
}

