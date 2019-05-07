package com.order_service.kafka.producer;
import java.util.HashMap;
import java.util.Map;

import com.order_service.model.LimitOrder;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.order_service.model.Order;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;



@Configuration
public class SenderConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        Thread.currentThread().setContextClassLoader(null);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    @Bean
    public ProducerFactory<String, Order> producerFactory() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, LimitOrder> producerFactoryLimit() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
    @Bean
    public KafkaTemplate<String,Order> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    @Bean
    public KafkaTemplate<String, LimitOrder> kafkaTemplateLimit() {
        return new KafkaTemplate<>(producerFactoryLimit());
    }
    @Bean
    public Sender sender() {
        return new Sender();
    }
}