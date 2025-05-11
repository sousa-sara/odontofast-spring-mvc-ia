// Atualização do DentistaService
package com.example.odontofast.service;

import com.example.odontofast.model.Dentista;
import com.example.odontofast.model.Especialidade;
import com.example.odontofast.model.Role;
import com.example.odontofast.repository.DentistaRepository;
import com.example.odontofast.repository.EspecialidadeRepository;
import com.example.odontofast.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class DentistaService {

    private final DentistaRepository dentistaRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public DentistaService(DentistaRepository dentistaRepository,
                           EspecialidadeRepository especialidadeRepository,
                           PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.dentistaRepository = dentistaRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Dentista salvarDentista(Dentista dentista) {
        Optional<Especialidade> especialidadeOptional = especialidadeRepository
                .findById(dentista.getEspecialidade().getIdEspecialidade());

        if (especialidadeOptional.isEmpty()) {
            throw new RuntimeException("Especialidade não encontrada!");
        }

        dentista.setEspecialidade(especialidadeOptional.get());

        // Verificar se o dentista tem roles e adicionar a role DENTISTA se não tiver
        if (dentista.getRoles() == null || dentista.getRoles().isEmpty()) {
            Optional<Role> roleDentista = roleRepository.findByName("DENTISTA");
            if (roleDentista.isPresent()) {
                dentista.setRoles(Collections.singleton(roleDentista.get()));
            } else {
                // Fallback: se a role não existir, criar uma nova
                Role newRole = new Role();
                newRole.setName("DENTISTA");
                Role savedRole = roleRepository.save(newRole);
                dentista.setRoles(Collections.singleton(savedRole));
            }
        }

        return dentistaRepository.save(dentista);
    }

    public Optional<Dentista> autenticarDentista(String cro, String senha) {
        Optional<Dentista> dentista = dentistaRepository.findByCro(cro);
        if (dentista.isPresent() && passwordEncoder.matches(senha, dentista.get().getSenhaDentista())) {
            return dentista;
        }
        return Optional.empty();
    }

    public Optional<Dentista> buscarPorId(Long id) {
        return dentistaRepository.findById(id);
    }

    @Transactional
    public Dentista atualizarDentista(Dentista dentistaAtualizado) {
        Optional<Dentista> dentistaExistente = dentistaRepository.findById(dentistaAtualizado.getIdDentista());

        if (dentistaExistente.isPresent()) {
            Dentista dentista = dentistaExistente.get();

            // Atualiza apenas os campos que precisam ser atualizados
            dentista.setNomeDentista(dentistaAtualizado.getNomeDentista());
            dentista.setCro(dentistaAtualizado.getCro());
            dentista.setTelefoneDentista(dentistaAtualizado.getTelefoneDentista());
            dentista.setEmailDentista(dentistaAtualizado.getEmailDentista());

            // Se o campo de senha foi atualizado, use-o, caso contrário mantenha o original
            if (dentistaAtualizado.getSenhaDentista() != null &&
                    !dentistaAtualizado.getSenhaDentista().equals(dentista.getSenhaDentista())) {
                dentista.setSenhaDentista(dentistaAtualizado.getSenhaDentista());
            }

            // Se a especialidade for atualizada
            if (dentistaAtualizado.getEspecialidade() != null) {
                Optional<Especialidade> especialidade = especialidadeRepository
                        .findById(dentistaAtualizado.getEspecialidade().getIdEspecialidade());
                especialidade.ifPresent(dentista::setEspecialidade);
            }

            return dentistaRepository.save(dentista);
        } else {
            throw new RuntimeException("Dentista não encontrado!");
        }
    }
}