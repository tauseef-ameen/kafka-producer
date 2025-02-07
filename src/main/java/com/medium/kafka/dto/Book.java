package com.medium.kafka.dto;

public record Book(
        int bookId,
        String name,
        String author
) {
}
