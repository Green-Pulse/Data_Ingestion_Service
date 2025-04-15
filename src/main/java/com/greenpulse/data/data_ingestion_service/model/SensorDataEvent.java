package com.greenpulse.data.data_ingestion_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataEvent {
    private String city;
    private double temp;
    private double pres;
    private double dewp;
    private double rain;
    private double windSpeed;
    private boolean wd_NE;
    private boolean wd_NW;
    private boolean wd_SE;
}

