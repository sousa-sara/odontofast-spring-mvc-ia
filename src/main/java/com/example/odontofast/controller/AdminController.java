package com.example.odontofast.controller;

import com.example.odontofast.model.Dentista;
import com.example.odontofast.repository.DentistaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/login")
    public String loginAdmin() {
        return "admin-login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/auth-debug")
    @ResponseBody
    public String authDebug(Authentication authentication) {
        StringBuilder debug = new StringBuilder();
        debug.append("<h2>Informações de Autenticação</h2>");

        if (authentication == null) {
            debug.append("<p>Autenticação: NULL</p>");
            return debug.toString();
        }

        debug.append("<p>Nome: ").append(authentication.getName()).append("</p>");
        debug.append("<p>Autenticado: ").append(authentication.isAuthenticated()).append("</p>");
        debug.append("<p>Principal: ").append(authentication.getPrincipal().getClass().getName()).append("</p>");
        debug.append("<p>Authorities:</p><ul>");

        authentication.getAuthorities().forEach(auth ->
                debug.append("<li>").append(auth.getAuthority()).append("</li>")
        );

        debug.append("</ul>");
        return debug.toString();
    }

}
