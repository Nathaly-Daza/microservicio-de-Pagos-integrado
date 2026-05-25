package com.project.payment.listener;

import com.project.payment.event.OrderCreatedEvent;
import com.project.payment.event.PaymentApprovedEvent;
import com.project.payment.event.PaymentFailedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key.payment-result}")
    private String paymentResultRoutingKey;

    @RabbitListener(queues = "${app.rabbitmq.queue.payment}")
    public void processPayment(OrderCreatedEvent event) {
        System.out.println("💳 Procesando pago para Orden #" + event.orderId());
        System.out.println("   Usuario: " + event.username());
        System.out.println("   Total: $" + event.total());

        // Simular procesamiento
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simular 70% éxito, 30% fallo
        boolean paymentSuccess = Math.random() > 0.3;

        if (paymentSuccess) {
            PaymentApprovedEvent approvedEvent = new PaymentApprovedEvent(event.orderId());
            rabbitTemplate.convertAndSend(exchange, paymentResultRoutingKey, approvedEvent);
            System.out.println("✅ Pago APROBADO para Orden #" + event.orderId());
        } else {
            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                event.orderId(),
                "Fondos insuficientes o tarjeta rechazada"
            );
            rabbitTemplate.convertAndSend(exchange, paymentResultRoutingKey, failedEvent);
            System.out.println("❌ Pago RECHAZADO para Orden #" + event.orderId());
        }
    }
}