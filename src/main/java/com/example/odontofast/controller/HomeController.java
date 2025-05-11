package com.example.odontofast.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Retorna a página de seleção de perfil
        return "profiles";
    }
    @GetMapping("/select-profile")
    public String selectProfile() {
        return "profiles";
    }
}