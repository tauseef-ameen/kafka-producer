package com.medium.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.medium.kafka.dto.OrderEvent;
import com.medium.kafka.dto.OrderType;
import com.medium.kafka.service.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class ProducerController {
    private final KafkaProducerService kafkaProducerService;

    public ProducerController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/v1/order")
    @Operation(summary="produce kafka message in asynchronous way", description="Approach 1 to send kafka message")
    public ResponseEntity<OrderEvent> postOrderEvent
            (@RequestBody OrderEvent orderEvent) throws JsonProcessingException {
        log.info("send order details asynchronously using key value serializer and completable future: {}", orderEvent);
        kafkaProducerService.sendOrderEventApproach1(orderEvent);
        log.info("Message sent successfully using /v1/order api");
        return ResponseEntity.status(HttpStatus.CREATED).body(orderEvent);

    }

    @PostMapping("/v2/order")
    @Operation(summary="produce kafka message in synchronous way", description="Approach 2 to send kafka message")
    public ResponseEntity<OrderEvent> postOrderEvent2(@RequestBody OrderEvent orderEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("send order details synchronously: {}", orderEvent);
        SendResult<Integer, String> result = kafkaProducerService.sendOrderEventApproach2(orderEvent);
        log.info("Message sent using /v2/order api to topic : {}", result.getRecordMetadata().topic());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderEvent);

    }

    @PostMapping("/v3/order")
    @Operation(summary="produce kafka message in asynchronous way using Producer Record", description="Approach 3 to send kafka message")
    public ResponseEntity<OrderEvent> postOrderEvent3(@RequestBody OrderEvent orderEvent) throws JsonProcessingException {
        log.info("send order details asynchronously using producer record: {}", orderEvent);
        kafkaProducerService.sendOrderEventApproach3(orderEvent);
        log.info("Message sent successfully using /v3/order api");
        return ResponseEntity.status(HttpStatus.CREATED).body(orderEvent);

    }

    @PutMapping("/v1/order")
    @Operation(summary="produce kafka message using valid key and event type", description="send kafka message using PUT request")
    public ResponseEntity<?> putOrderEvent(@RequestBody OrderEvent orderEvent) throws JsonProcessingException {
        log.info("validating order event via put endpoint: {}", orderEvent);
        final ResponseEntity<String> response = validateOrderEvent(orderEvent);
        if (response != null) {
            return response;
        }
        kafkaProducerService.sendOrderEventApproach3(orderEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderEvent);
    }

    private static ResponseEntity<String> validateOrderEvent(OrderEvent orderEvent) {
        if (orderEvent.orderId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide order id");
        }
        if (!orderEvent.orderType().equals(OrderType.UPDATE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide event Type as update");
        }
        return null;
    }
}
