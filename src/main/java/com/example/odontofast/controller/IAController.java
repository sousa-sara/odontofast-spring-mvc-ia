package com.example.odontofast.controller;

import com.example.odontofast.service.IAService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dentista/ia")
public class IAController {

    private final IAService iaService;

    public IAController(IAService iaService) {
        this.iaService = iaService;
    }

    // Novos métodos para o Assistente de Anamnese
    @GetMapping("/anamnese-assistente")
    public String paginaAnamneseAssistente() {
        return "anamnese-assistente";
    }

    @PostMapping("/gerar-perguntas-anamnese")
    public String gerarPerguntasAnamnese(@RequestParam String informacoesIniciais, Model model) {
        String perguntas = iaService.gerarPerguntasAnamnese(informacoesIniciais);
        model.addAttribute("informacoesIniciais", informacoesIniciais);
        model.addAttribute("perguntasGeradas", perguntas);
        return "anamnese-assistente";
    }
}