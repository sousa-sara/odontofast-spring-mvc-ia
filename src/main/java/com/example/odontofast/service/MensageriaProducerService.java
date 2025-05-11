// Serviço Produtor
package com.example.odontofast.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MensageriaProducerService {

    private final RabbitTemplate rabbitTemplate;

    public MensageriaProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarNotificacaoAgendamento(Object mensagem) {
        rabbitTemplate.convertAndSend("agendamento.queue", mensagem);
    }

    public void enviarNotificacaoEmail(Object mensagem) {
        rabbitTemplate.convertAndSend("notificacao.queue", mensagem);
    }
}