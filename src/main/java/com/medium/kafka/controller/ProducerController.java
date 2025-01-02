package com.medium.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.medium.kafka.dto.BookCatalogEvent;
import com.medium.kafka.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/v1/book")
    public ResponseEntity<BookCatalogEvent> postBookCatalogEvent
            (@RequestBody BookCatalogEvent bookCatalogEvent) throws JsonProcessingException {
        log.info("sendBookCatalogEvent via approach1: {}", bookCatalogEvent);
        kafkaProducerService.sendBookEvent_Approach1(bookCatalogEvent);
        log.info("After message sent in Async way approach 1: Message sent successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCatalogEvent);

    }

    @PostMapping("/v2/book")
    public ResponseEntity<BookCatalogEvent> postBookCatalogEvent2(@RequestBody BookCatalogEvent bookCatalogEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("sendBookCatalogEvent via approach2: {}", bookCatalogEvent);
        kafkaProducerService.sendBookEvent_Approach2(bookCatalogEvent);
        log.info("After message sent in sync way approach 2: Message sent successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCatalogEvent);

    }

    @PostMapping("/v3/book")
    public ResponseEntity<BookCatalogEvent> postBookCatalogEvent3(@RequestBody BookCatalogEvent bookCatalogEvent) throws JsonProcessingException {
        log.info("sendBookCatalogEvent via approach3: {}", bookCatalogEvent);
        kafkaProducerService.sendBookEvent_Approach3(bookCatalogEvent);
        log.info("After message sent in Async way approach 3: Message sent successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCatalogEvent);

    }
}
