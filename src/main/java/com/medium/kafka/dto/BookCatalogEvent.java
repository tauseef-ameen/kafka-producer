package com.medium.kafka.dto;

public record BookCatalogEvent(
        Integer eventId, EventType eventType, Book book
) {
}
