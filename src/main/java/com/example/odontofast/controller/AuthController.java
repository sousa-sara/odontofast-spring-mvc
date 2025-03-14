package com.example.odontofast.controller;
import com.example.odontofast.dto.DentistaCadastroDTO;
import com.example.odontofast.model.Dentista;
import com.example.odontofast.model.Especialidade;
import com.example.odontofast.service.DentistaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/dentista")
public class AuthController {

    // Declaração de uma variável final para armazenar a instância do serviço de Dentista.
    // "final" significa que essa variável não poderá ser alterada após a inicialização.
    private final DentistaService dentistaService;

    // Construtor para injeção de dependência
    // Esse construtor recebe uma instância de DentistaService e a atribui à variável dentistaService.
    // Isso permite que a injeção de dependência seja feita pelo Spring, sem necessidade de usar @Autowired.
    public AuthController(DentistaService dentistaService) {
        this.dentistaService = dentistaService;
    }

    // Endpoint para cadastro de dentista
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrarDentista(@RequestBody DentistaCadastroDTO dentistaCadastroDTO) {
        // Convertendo o DTO para a entidade Dentista antes de salvar
        Dentista dentista = new Dentista();
        dentista.setNomeDentista(dentistaCadastroDTO.getNomeDentista());
        dentista.setSenhaDentista(dentistaCadastroDTO.getSenhaDentista());
        dentista.setCro(dentistaCadastroDTO.getCro());
        dentista.setTelefoneDentista(dentistaCadastroDTO.getTelefoneDentista());
        dentista.setEmailDentista(dentistaCadastroDTO.getEmailDentista());

        // Associa a especialidade
        Especialidade especialidade = new Especialidade();
        especialidade.setIdEspecialidade(dentistaCadastroDTO.getEspecialidade().getIdEspecialidade());
        especialidade.setTipoEspecialidade(dentistaCadastroDTO.getEspecialidade().getTipoEspecialidade());
        dentista.setEspecialidade(especialidade);

        // Salvar o dentista no banco de dados
        dentistaService.salvarDentista(dentista);

        return ResponseEntity.ok("Cadastro bem-sucedido!");
    }

    // Endpoint para login de dentista (autenticação)
    @PostMapping("/login")
    public ResponseEntity<String> loginDentista(@RequestParam String cro, @RequestParam String senha) {
        Optional<Dentista> dentista = dentistaService.autenticarDentista(cro, senha);

        if (dentista.isPresent()) {
            return ResponseEntity.ok("Login bem-sucedido!");
        } else {
            return ResponseEntity.status(401).body("Credenciais inválidas!");
        }
    }
}