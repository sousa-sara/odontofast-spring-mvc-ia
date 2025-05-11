package com.example.odontofast.config;

import com.example.odontofast.model.Dentista;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/select-profile", "/assets/**", "/error", "/check-roles").permitAll()
                        .requestMatchers("/admin/login", "/admin/process-login").permitAll()
                        .requestMatchers("/dentista/login", "/dentista/cadastro").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/dentista/**").hasAnyRole("DENTISTA", "ADMIN")
                        .anyRequest().authenticated()
                )
                // Configuração de autenticação para dentistas
                .formLogin(dentista -> dentista
                        .loginPage("/dentista/login")
                        .loginProcessingUrl("/dentista/login")
                        .defaultSuccessUrl("/dentista/home", true)
                        .failureUrl("/dentista/login?error=true")
                        .usernameParameter("cro")
                        .passwordParameter("senha")
                        .permitAll()
                )
                // Configuração adicional para admin
                .addFilterAfter(adminAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Filtro personalizado para autenticação de admin com logs
    @Bean
    public UsernamePasswordAuthenticationFilter adminAuthenticationFilter() throws Exception {
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setFilterProcessesUrl("/admin/process-login");

        // Definir os nomes dos parâmetros do formulário
        filter.setUsernameParameter("cro");
        filter.setPasswordParameter("senha");

        // Resto do código permanece o mesmo...
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            // Importante: Armazenar a autenticação na sessão de forma explícita
            HttpSession session = request.getSession(true);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            System.out.println("\n======== DIAGNÓSTICO DE AUTENTICAÇÃO ADMIN ========");
            System.out.println("Nome do usuário: " + authentication.getName());
            System.out.println("Classe do Principal: " + authentication.getPrincipal().getClass().getName());

            System.out.println("\nTodas as Authorities:");
            authentication.getAuthorities().forEach(auth ->
                    System.out.println("  - " + auth.getAuthority())
            );

            // Verificar com diferentes formatos de role
            boolean hasRoleAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean hasAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ADMIN"));

            System.out.println("\nVerificações de Role:");
            System.out.println("  Tem 'ROLE_ADMIN'? " + hasRoleAdmin);
            System.out.println("  Tem 'ADMIN'? " + hasAdmin);

            // Verificar o dentista e suas roles diretamente
            if (authentication.getPrincipal() instanceof Dentista) {
                Dentista dentista = (Dentista) authentication.getPrincipal();
                System.out.println("\nRoles diretas do dentista:");
                dentista.getRoles().forEach(role ->
                        System.out.println("  - Nome: " + role.getName() + ", ID: " + role.getId())
                );
            }

            System.out.println("\nDecisão:");
            if (hasRoleAdmin) {
                System.out.println("  ✅ Usuário tem ROLE_ADMIN, redirecionando para /admin/dashboard");
                response.sendRedirect("/admin/dashboard");
            } else {
                System.out.println("  ❌ Usuário NÃO tem ROLE_ADMIN, redirecionando para /admin/login?error=true");
                SecurityContextHolder.clearContext();
                response.sendRedirect("/admin/login?error=true");
            }
            System.out.println("=================================================\n");
        });

        // Também adicionar logs no failure handler
        filter.setAuthenticationFailureHandler((request, response, exception) -> {
            System.out.println("\n======== FALHA NA AUTENTICAÇÃO ADMIN ========");
            System.out.println("Causa: " + exception.getClass().getName());
            System.out.println("Mensagem: " + exception.getMessage());
            System.out.println("==============================================\n");
            response.sendRedirect("/admin/login?error=true");
        });

        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return new ProviderManager(Collections.singletonList(
                new DaoAuthenticationProvider() {{
                    setUserDetailsService(userDetailsService);
                    setPasswordEncoder(passwordEncoder());
                }}
        ));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Custom plain text password encoder for testing only - NOT FOR PRODUCTION
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}