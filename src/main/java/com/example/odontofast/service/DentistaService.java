package com.example.odontofast.service;
import com.example.odontofast.model.Dentista;
import com.example.odontofast.model.Especialidade;
import com.example.odontofast.repository.DentistaRepository;
import com.example.odontofast.repository.EspecialidadeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DentistaService {

    private final DentistaRepository dentistaRepository;
    private final EspecialidadeRepository especialidadeRepository;

    public DentistaService(DentistaRepository dentistaRepository, EspecialidadeRepository especialidadeRepository) {
        this.dentistaRepository = dentistaRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    public Dentista salvarDentista(Dentista dentista) {
        // Buscar a especialidade pelo ID
        Optional<Especialidade> especialidadeOptional = especialidadeRepository.findById(dentista.getEspecialidade().getIdEspecialidade());

        if (especialidadeOptional.isEmpty()) {
            throw new RuntimeException("Especialidade não encontrada!");
        }

        // Associar a especialidade ao dentista
        dentista.setEspecialidade(especialidadeOptional.get());

        // Salvar o dentista no repositório
        return dentistaRepository.save(dentista);
    }


    // Método para autenticar o dentista (login)
    public Optional<Dentista> autenticarDentista(String cro, String senha) {
        return dentistaRepository.findByCroAndSenhaDentista(cro, senha);
    }

}
