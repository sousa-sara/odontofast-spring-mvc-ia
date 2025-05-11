// Atualizando o AuthController para utilizar Spring Security
package com.example.odontofast.controller;

import com.example.odontofast.dto.DentistaCadastroDTO;
import com.example.odontofast.model.Dentista;
import com.example.odontofast.model.Especialidade;
import com.example.odontofast.model.Role;
import com.example.odontofast.repository.EspecialidadeRepository;
import com.example.odontofast.repository.RoleRepository;
import com.example.odontofast.service.DentistaService;
import com.example.odontofast.service.MensageriaProducerService;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/dentista")
public class AuthController {

    private final DentistaService dentistaService;
    private final EspecialidadeRepository especialidadeRepository;
    private final RoleRepository roleRepository;
    private final MensageriaProducerService mensageriaService;

    public AuthController(DentistaService dentistaService,
                          EspecialidadeRepository especialidadeRepository,
                          RoleRepository roleRepository,
                          MensageriaProducerService mensageriaService) {
        this.dentistaService = dentistaService;
        this.especialidadeRepository = especialidadeRepository;
        this.roleRepository = roleRepository;
        this.mensageriaService = mensageriaService;
    }

    private void adicionarToast(Model model, String mensagem, String tipo) {
        model.addAttribute("paramTxtMensagem", mensagem);
        model.addAttribute("paramTipoMensagem", tipo);
    }

    @GetMapping("/home")
    public String exibirHome(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/dentista/login";
        }

        Dentista dentista = (Dentista) authentication.getPrincipal();
        model.addAttribute("nomeDentista", dentista.getNomeDentista());
        return "home";
    }

    @GetMapping("/agendamentos")
    public String exibirAgendamentos(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/dentista/login";
        }

        Dentista dentista = (Dentista) authentication.getPrincipal();
        model.addAttribute("nomeDentista", dentista.getNomeDentista());
        return "agendamentos";
    }

    @GetMapping("/cadastro")
    public String exibirCadastroDentista(Model model) {
        model.addAttribute("especialidades", especialidadeRepository.findAll());
        return "cadastro-dentista";
    }

    @PostMapping("/cadastro")
    public String cadastrarDentista(@ModelAttribute DentistaCadastroDTO dentistaCadastroDTO, Model model) {
        try {
            Dentista dentista = new Dentista();

            dentista.setNomeDentista(dentistaCadastroDTO.getNomeDentista());
            // Usar senha em texto plano para testes
            dentista.setSenhaDentista(dentistaCadastroDTO.getSenhaDentista());
            dentista.setCro(dentistaCadastroDTO.getCro());
            dentista.setTelefoneDentista(dentistaCadastroDTO.getTelefoneDentista());
            dentista.setEmailDentista(dentistaCadastroDTO.getEmailDentista());

            Especialidade especialidade = especialidadeRepository.findById(dentistaCadastroDTO.getEspecialidadeId())
                    .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

            dentista.setEspecialidade(especialidade);

            // Adicionar role padrão (DENTISTA)
            Role roleDentista = roleRepository.findByName("DENTISTA")
                    .orElseThrow(() -> new RuntimeException("Role não encontrada"));
            dentista.setRoles(Collections.singleton(roleDentista));

            Dentista dentistaSalvo = dentistaService.salvarDentista(dentista);

            // Enviar notificação via mensageria
            Map<String, Object> mensagem = new HashMap<>();
            mensagem.put("tipo", "NOVO_CADASTRO");
            mensagem.put("dentista", dentistaSalvo.getNomeDentista());
            mensagem.put("email", dentistaSalvo.getEmailDentista());
            mensageriaService.enviarNotificacaoEmail(mensagem);

            adicionarToast(model, "Cadastro realizado com sucesso!", "text-bg-success");
            return "login-dentista";
        } catch (Exception e) {
            adicionarToast(model, "Erro ao cadastrar dentista: " + e.getMessage(), "text-bg-danger");
            model.addAttribute("dentistaCadastroDTO", dentistaCadastroDTO);
            model.addAttribute("especialidades", especialidadeRepository.findAll());
            return "cadastro-dentista";
        }
    }

    @GetMapping("/login")
    public String exibirLoginDentista() {
        return "login-dentista";
    }

    @GetMapping("/perfil")
    public String exibirPerfil(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/dentista/login";
        }

        Dentista dentista = (Dentista) authentication.getPrincipal();
        model.addAttribute("dentista", dentista);
        return "perfil";
    }

    @PostMapping("/perfil/atualizar")
    public String atualizarPerfil(@ModelAttribute Dentista dentista, Authentication authentication, Model model) {
        if (authentication == null) {
            adicionarToast(model, "Acesso negado. Faça login novamente.", "text-bg-danger");
            return "redirect:/dentista/login";
        }

        Dentista dentistaAtual = (Dentista) authentication.getPrincipal();

        if (!dentistaAtual.getIdDentista().equals(dentista.getIdDentista())) {
            adicionarToast(model, "Acesso negado. Você não pode editar este perfil.", "text-bg-danger");
            return "redirect:/dentista/perfil";
        }

        try {
            // Manter a senha atual e roles, apenas atualizar outros dados
            dentista.setSenhaDentista(dentistaAtual.getSenhaDentista());
            dentista.setRoles(dentistaAtual.getRoles());

            dentistaService.atualizarDentista(dentista);

            // Enviar notificação via mensageria
            Map<String, Object> mensagem = new HashMap<>();
            mensagem.put("tipo", "PERFIL_ATUALIZADO");
            mensagem.put("dentista", dentista.getNomeDentista());
            mensagem.put("email", dentista.getEmailDentista());
            mensageriaService.enviarNotificacaoEmail(mensagem);

            // Adicionar mensagem de sucesso
            adicionarToast(model, "Perfil atualizado com sucesso!", "text-bg-success");
        } catch (Exception e) {
            // Bloco catch adicionado para capturar exceções
            adicionarToast(model, "Erro ao atualizar o perfil: " + e.getMessage(), "text-bg-danger");
        }

        model.addAttribute("dentista", dentista);
        return "perfil";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/dentista/login?logout=true";
    }
}