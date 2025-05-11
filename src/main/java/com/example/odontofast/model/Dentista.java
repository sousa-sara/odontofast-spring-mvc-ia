package com.example.odontofast.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "c_op_dentista")
public class Dentista implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dentista_seq")
    @SequenceGenerator(name = "dentista_seq", sequenceName = "c_op_dentista_seq", allocationSize = 1)
    private Long idDentista;

    private String nomeDentista;

    private String senhaDentista;

    private String cro;

    private long telefoneDentista;

    private String emailDentista;

    // Relação com especialidade
    @ManyToOne
    @JoinColumn(name = "id_especialidade")
    private Especialidade especialidade;

    // Relação com roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "c_op_dentista_roles",
            joinColumns = @JoinColumn(name = "dentista_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>(); // Inicializado como HashSet vazio

    // Métodos UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return senhaDentista;
    }

    @Override
    public String getUsername() {
        return cro;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getters e Setters
    public Long getIdDentista() {
        return idDentista;
    }

    public void setIdDentista(Long idDentista) {
        this.idDentista = idDentista;
    }

    public String getNomeDentista() {
        return nomeDentista;
    }

    public void setNomeDentista(String nomeDentista) {
        this.nomeDentista = nomeDentista;
    }

    public String getSenhaDentista() {
        return senhaDentista;
    }

    public void setSenhaDentista(String senhaDentista) {
        this.senhaDentista = senhaDentista;
    }

    public String getCro() {
        return cro;
    }

    public void setCro(String cro) {
        this.cro = cro;
    }

    public long getTelefoneDentista() {
        return telefoneDentista;
    }

    public void setTelefoneDentista(long telefoneDentista) {
        this.telefoneDentista = telefoneDentista;
    }

    public String getEmailDentista() {
        return emailDentista;
    }

    public void setEmailDentista(String emailDentista) {
        this.emailDentista = emailDentista;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    // Adicionar getters e setters para roles
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
