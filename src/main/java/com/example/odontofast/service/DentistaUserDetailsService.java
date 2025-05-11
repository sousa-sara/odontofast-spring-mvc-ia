package com.example.odontofast.service;

import com.example.odontofast.model.Dentista;
import com.example.odontofast.model.Role;
import com.example.odontofast.repository.DentistaRepository;
import com.example.odontofast.repository.RoleRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class DentistaUserDetailsService implements UserDetailsService {

    private final DentistaRepository dentistaRepository;
    private final RoleRepository roleRepository;

    public DentistaUserDetailsService(DentistaRepository dentistaRepository, RoleRepository roleRepository) {
        this.dentistaRepository = dentistaRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("===== CARREGANDO USUÁRIO =====");
        System.out.println("Username solicitado: " + username);

        // Caso especial para testes com o usuário admin
        if (username.equals("ADMIN9998")) {
            System.out.println("Criando usuário admin para teste");

            // Criar um dentista admin temporário em memória (não salvo no banco)
            Dentista adminDentista = new Dentista();
            adminDentista.setIdDentista(999L); // ID fictício
            adminDentista.setCro("ADMIN9998");
            adminDentista.setNomeDentista("Administrador");
            adminDentista.setSenhaDentista("admin123"); // Senha em texto plano para teste

            // Adicionar ROLE_ADMIN
            Set<Role> roles = new HashSet<>();
            Role adminRole = new Role();
            adminRole.setId(1L);
            adminRole.setName("ADMIN");
            roles.add(adminRole);
            adminDentista.setRoles(roles);

            return adminDentista;
        }

        // Comportamento normal para outros usuários
        try {
            return dentistaRepository.findByCro(username)
                    .orElseThrow(() -> {
                        System.out.println("ERRO: Usuário não encontrado no banco: " + username);
                        return new UsernameNotFoundException("Usuário não encontrado: " + username);
                    });
        } catch (UsernameNotFoundException ex) {
            System.out.println("ERRO ao carregar usuário: " + ex.getMessage());
            throw ex;
        }
    }
}