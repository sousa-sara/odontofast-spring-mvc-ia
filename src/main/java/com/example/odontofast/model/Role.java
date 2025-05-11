// Entidade Role para definir perfis de segurança
package com.example.odontofast.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "c_op_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<Dentista> dentistas;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Dentista> getDentistas() {
        return dentistas;
    }

    public void setDentistas(Set<Dentista> dentistas) {
        this.dentistas = dentistas;
    }
}