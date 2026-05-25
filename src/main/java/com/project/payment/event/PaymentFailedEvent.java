package com.project.payment.event;

import java.io.Serializable;

public record PaymentFailedEvent(
    Long orderId,
    String reason
) implements Serializable {}