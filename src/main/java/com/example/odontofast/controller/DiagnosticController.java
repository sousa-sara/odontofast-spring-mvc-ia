package com.example.odontofast.controller;

import com.example.odontofast.model.Dentista;
import com.example.odontofast.repository.DentistaRepository;
import com.example.odontofast.service.MensageriaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DiagnosticController {

    private final DentistaRepository dentistaRepository;
    private final MensageriaProducerService mensageriaProducerService;  // Adicione este campo

    @Autowired
    public DiagnosticController(DentistaRepository dentistaRepository, MensageriaProducerService mensageriaProducerService) {
        this.dentistaRepository = dentistaRepository;
        this.mensageriaProducerService = mensageriaProducerService;
    }

    @GetMapping("/check-roles")
    @ResponseBody
    public String checkRoles(@RequestParam String cro) {
        try {
            Dentista dentista = dentistaRepository.findByCro(cro)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            StringBuilder result = new StringBuilder();
            result.append("<h3>Informações do usuário: ").append(cro).append("</h3>");
            result.append("<p>Nome: ").append(dentista.getNomeDentista()).append("</p>");

            result.append("<h4>Roles no banco:</h4><ul>");
            dentista.getRoles().forEach(role ->
                    result.append("<li>").append(role.getName()).append("</li>")
            );
            result.append("</ul>");

            result.append("<h4>Authorities geradas:</h4><ul>");
            dentista.getAuthorities().forEach(auth ->
                    result.append("<li>").append(auth.getAuthority()).append("</li>")
            );
            result.append("</ul>");

            return result.toString();
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }
    @GetMapping("/test-rabbitmq")
    @ResponseBody
    public String testRabbitMQ() {
        // Testar notificação
        Map<String, Object> mensagem = new HashMap<>();
        mensagem.put("tipo", "TESTE");
        mensagem.put("mensagem", "Teste de mensageria");
        mensagem.put("data", new Date().toString());

        mensageriaProducerService.enviarNotificacaoEmail(mensagem);
        return "Mensagem enviada para a fila de notificação!";
    }

}