// Serviço Consumidor
package com.example.odontofast.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MensageriaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(MensageriaConsumerService.class);

    @RabbitListener(queues = "agendamento.queue")
    public void receberAgendamento(Object mensagem) {
        logger.info("Recebida mensagem de agendamento: {}", mensagem);
        // Processar o agendamento recebido
    }

    @RabbitListener(queues = "notificacao.queue")
    public void receberNotificacao(Object mensagem) {
        logger.info("Recebida mensagem de notificação: {}", mensagem);
        // Processar a notificação a ser enviada
    }
}