package com.greenpulse.data.data_ingestion_service.scheduler;

import com.greenpulse.data.data_ingestion_service.model.WeatherDataEvent;
import com.greenpulse.data.data_ingestion_service.service.OpenWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class WeatherDataScheduler {

    private final OpenWeatherService weatherService;
    private final KafkaTemplate<String, WeatherDataEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public WeatherDataScheduler(OpenWeatherService weatherService, KafkaTemplate<String, WeatherDataEvent> kafkaTemplate) {
        this.weatherService = weatherService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 60000) //every 60 seconds
    public void fetchAndPublish() {
        WeatherDataEvent event = weatherService.fetchWeatherData();
        if (event != null) {

            WeatherDataEvent weatherDataEvent = new WeatherDataEvent(event.getCity(), event.getTemperature(), event.getHumidity(), event.getTimestamp());
            CompletableFuture<SendResult<String, WeatherDataEvent>> future
                    = kafkaTemplate.send("weather-data-event-topic", weatherDataEvent.getCity(), weatherDataEvent);

            future.whenComplete((result, exception) -> {
                if (exception != null) {
                    LOGGER.error("Failed to send weather data event: " + exception.getMessage());
                } else {
                    LOGGER.info("weather data event sent successfully: " + result.getRecordMetadata());
                }

                LOGGER.info(("Partition: " + result.getRecordMetadata().partition()));
                LOGGER.info(("Topic: " + result.getRecordMetadata().topic()));
                LOGGER.info(("Offset: " + result.getRecordMetadata().offset()));

            });

//        future.join(); for sync but we don't need that I think
        }
    }
}