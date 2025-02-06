package com.medium.kafka.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class AutoBeanConfiguration {

    @Value("${spring.topic.name}")
    public String topicName;

    @Bean
    public NewTopic createTopic() {
        log.info("Trying to create topic with name: {}", topicName);

        NewTopic newTopic = TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .build();
        log.info("topic created with name: {}", newTopic.name());
        return newTopic;
    }
}
