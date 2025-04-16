package com.greenpulse.data.data_ingestion_service.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpulse.data.data_ingestion_service.model.WeatherDataEvent;
import com.greenpulse.data.data_ingestion_service.service.OpenWeatherService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class WeatherDataScheduler {

    private final OpenWeatherService weatherService;
    private final KafkaTemplate<String, WeatherDataEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");


    @Scheduled(fixedRate = 60000) //every 60 seconds
    public void fetchAndPublish() {
        WeatherDataEvent event = weatherService.fetchWeatherData();

        if (event != null) {

            // Сохраняем текущее время как timestamp, независимо от API
            event.setTimestamp(LocalDateTime.now());

            // 1. Сформировать уникальный ключ
            String key = "weather-data:" + event.getCity() + ":" + event.getTimestamp().format(formatter);

            try {
                // 2. Сериализовать объект в JSON
                String jsonValue = objectMapper.writeValueAsString(event);

                // 3. Сохранить в Redis
                redisTemplate.opsForValue().set(key, jsonValue);
                LOGGER.info("Saved event to Redis with key: {}", key);

            } catch (JsonProcessingException e) {
                LOGGER.error("Error serializing WeatherDataEvent to JSON", e);
            }

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