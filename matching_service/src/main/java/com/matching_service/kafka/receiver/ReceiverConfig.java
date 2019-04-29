package com.matching_service.kafka.receiver;

import java.util.HashMap;
import java.util.Map;


import com.matching_service.model.Order;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;


@Configuration
@EnableKafka
public class ReceiverConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;


        @Bean
        public ConsumerFactory<String, Order> consumerFactory(){
            JsonDeserializer<Order> deserializer = new JsonDeserializer<>(Order.class);
            deserializer.setRemoveTypeHeaders(false);
            deserializer.addTrustedPackages("*");
            deserializer.setUseTypeMapperForKey(true);

            Map<String, Object> config = new HashMap<>();

            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
            config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

            return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, Order> kafkaListenerContainerFactory(){
            ConcurrentKafkaListenerContainerFactory<String, Order> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            return factory;

        }
    @Bean
    public Receiver receiver() {
        return new Receiver();
    }
    }
