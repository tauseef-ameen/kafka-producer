package com.medium.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medium.kafka.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The type Kafka producer service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    /**
     * The Topic name.
     */
    @Value("${spring.topic.name}")
    public String topicName;
    /**
     * The Kafka template.
     */
    public final KafkaTemplate<Integer, String> kafkaTemplate;
    /**
     * The Object mapper.
     */
    public final ObjectMapper objectMapper;

    /**
     * Send order event approach 1 completable future.
     *
     * @param orderEvent the order catalog event
     * @return the completable future
     * @throws JsonProcessingException the json processing exception
     */
    public CompletableFuture<SendResult<Integer, String>> sendOrderEventApproach1(OrderEvent orderEvent) throws JsonProcessingException {
        var key = orderEvent.orderId();
        var value = objectMapper.writeValueAsString(orderEvent);

        var future = kafkaTemplate.send(topicName, key, value);
        return future.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(throwable, key);
            } else {
                handleSuccess(sendResult, key);
            }
        });
    }

    // synchronous way of calling approach 2

    /**
     * Send order event approach 2 send result.
     *
     * @param orderEvent the order catalog event
     * @return the send result
     * @throws JsonProcessingException the json processing exception
     * @throws ExecutionException      the execution exception
     * @throws InterruptedException    the interrupted exception
     * @throws TimeoutException        the timeout exception
     */
    public SendResult<Integer, String> sendOrderEventApproach2(OrderEvent orderEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        var key = orderEvent.orderId();
        var value = objectMapper.writeValueAsString(orderEvent);

        var result = kafkaTemplate.send(topicName, key, value).get(3, TimeUnit.SECONDS);
        handleSuccess(result, key);

        return result;
    }


    /**
     * Send order event approach 3 completable future using producerRecord.
     *
     * @param orderEvent the order catalog event
     * @return the completable future
     * @throws JsonProcessingException the json processing exception
     */
    public CompletableFuture<SendResult<Integer, String>> sendOrderEventApproach3(OrderEvent orderEvent)
            throws JsonProcessingException {
        var key = orderEvent.orderId();
        var value = objectMapper.writeValueAsString(orderEvent);
        log.info("Key is: {} and value is: {}", key, value);
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(topicName, key, value);

        var future = kafkaTemplate.send(producerRecord);
        return future.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(throwable, key);
            } else {
                handleSuccess(sendResult, key);
            }
        });
    }

    private void handleFailure(Throwable throwable, Integer key) {
        log.error("Unable to send kafka message to topic: {} with key: {} and error is: {}", topicName, key, throwable.getMessage());
    }

    private void handleSuccess(SendResult<Integer, String> sendResult, Integer key) {
        log.info("Message sent to topic: {} with key: {} and partition: {}",
                sendResult.getRecordMetadata().topic(),
                key,
                sendResult.getRecordMetadata().partition());
    }
}
