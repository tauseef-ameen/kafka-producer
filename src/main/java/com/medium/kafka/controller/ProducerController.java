package com.medium.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.medium.kafka.dto.BookCatalogEvent;
import com.medium.kafka.dto.EventType;
import com.medium.kafka.service.KafkaProducerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/v1/book")
    @Operation(summary="produce kafka message in asynchronous way", description="Approach 1 to send kafka message")
    public ResponseEntity<BookCatalogEvent> postBookCatalogEvent
            (@RequestBody BookCatalogEvent bookCatalogEvent) throws JsonProcessingException {
        log.info("sendBookCatalogEvent via approach1: {}", bookCatalogEvent);
        kafkaProducerService.sendBookEvent_Approach1(bookCatalogEvent);
        log.info("Message sent successfully via /v1/book api");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCatalogEvent);

    }

    @PostMapping("/v2/book")
    @Operation(summary="produce kafka message in synchronous way", description="Approach 2 to send kafka message")
    public ResponseEntity<BookCatalogEvent> postBookCatalogEvent2(@RequestBody BookCatalogEvent bookCatalogEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("sendBookCatalogEvent via approach2: {}", bookCatalogEvent);
        kafkaProducerService.sendBookEvent_Approach2(bookCatalogEvent);
        log.info("Message sent successfully via /v2/book api");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCatalogEvent);

    }

    @PostMapping("/v3/book")
    @Operation(summary="produce kafka message in asynchronous way using Producer Record", description="Approach 3 to send kafka message")
    public ResponseEntity<BookCatalogEvent> postBookCatalogEvent3(@RequestBody BookCatalogEvent bookCatalogEvent) throws JsonProcessingException {
        log.info("sendBookCatalogEvent via approach3: {}", bookCatalogEvent);
        kafkaProducerService.sendBookEvent_Approach3(bookCatalogEvent);
        log.info("Message sent successfully via /v3/book api");
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCatalogEvent);

    }

    @PutMapping("/v1/book")
    @Operation(summary="produce kafka message using valid key and event type", description="send kafka message using PUT request")
    public ResponseEntity<?> putBookCatalogEvent(@RequestBody BookCatalogEvent bookCatalogEvent) throws JsonProcessingException {
        log.info("validating book event via put endpoint: {}", bookCatalogEvent);
        final ResponseEntity<String> response = validateBookEvent(bookCatalogEvent);
        if (response != null) {
            return response;
        }
        kafkaProducerService.sendBookEvent_Approach3(bookCatalogEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCatalogEvent);
    }

    private static ResponseEntity<String> validateBookEvent(BookCatalogEvent bookCatalogEvent) {
        if (bookCatalogEvent.eventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide event id");
        }
        if (!bookCatalogEvent.eventType().equals(EventType.UPDATE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide event Type as update");
        }
        return null;
    }
}
