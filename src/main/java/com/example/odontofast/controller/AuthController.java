package com.example.odontofast.controller;
import com.example.odontofast.dto.DentistaCadastroDTO;
import com.example.odontofast.model.Dentista;
import com.example.odontofast.model.Especialidade;
import com.example.odontofast.service.DentistaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/dentista")
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

    // Tela de login de dentista
    @GetMapping("/cadastro")
    public String exibirCadastroDentista() {
        return "cadastro-dentista";  // A view será a página cadastro.html
    }

    @PostMapping("/cadastro")
    public ModelAndView cadastrarDentista(@ModelAttribute("dentista") DentistaCadastroDTO dentistaCadastroDTO) {
        // Criação do objeto Dentista
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

        // Salva o dentista no banco de dados
        dentistaService.salvarDentista(dentista);

        // Redireciona para a página de login com mensagem de sucesso
        ModelAndView modelAndView = new ModelAndView("login-dentista");
        modelAndView.addObject("sucesso", "Cadastro bem-sucedido! Faça login.");
        return modelAndView;
    }

    // Tela de login de dentista
    @GetMapping("/login")
    public String exibirLoginDentista() {
        return "login-dentista";  // A view será a página login.html
    }

    // Endpoint de login (o formulário será enviado via POST)
    @PostMapping("/login")
    public String loginDentista(@RequestParam String cro, @RequestParam String senha, Model model) {
        Optional<Dentista> dentista = dentistaService.autenticarDentista(cro, senha);

        if (dentista.isPresent()) {
            return "home";  // Sucesso, redireciona para a página principal (home)
        } else {
            model.addAttribute("erro", "Credenciais inválidas!");
            return "login-dentista";  // Retorna para a tela de login com erro
        }
    }
}