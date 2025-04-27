package com.greenpulse.data.data_ingestion_service.config;

import com.greenpulse.data.data_ingestion_service.model.SensorDataEvent;
import com.greenpulse.data.data_ingestion_service.model.WeatherDataEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name("weather-data-event-topic")
                .partitions(3)
                .replicas(3) //can't be more than brokers number
                .config("min.insync.replicas", "2")
                .build();
    }

    @Bean
    NewTopic createSensorDataTopic() {
        return TopicBuilder.name("sensor-data-event-topic")
                .partitions(3)
                .replicas(3)
                .config("min.insync.replicas", "2")
                .build();
    }

    @Bean
    public ProducerFactory<String, WeatherDataEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092,kafka-2:9094,kafka-3:9096");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // avoid classloader issues
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, WeatherDataEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, SensorDataEvent> sensorDataProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092,kafka-2:9094,kafka-3:9096");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, SensorDataEvent> sensorDataKafkaTemplate() {
        return new KafkaTemplate<>(sensorDataProducerFactory());
    }
}