package com.medium.kafka.dto;

public record OrderDetails(
        int quantity,
        String customerName,
        double price
) {
}
