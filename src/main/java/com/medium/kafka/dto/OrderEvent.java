package com.medium.kafka.dto;

public record OrderEvent(
        Integer orderId, OrderType orderType, OrderDetails orderDetails
) {
}
