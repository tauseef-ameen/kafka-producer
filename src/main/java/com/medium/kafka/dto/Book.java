package com.medium.kafka.dto;

import org.springframework.stereotype.Component;


public record Book(
        int bookId,
        String name,
        String author
) {
}
