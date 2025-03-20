package com.example.odontofast.controller;

import com.example.odontofast.dto.DentistaCadastroDTO;
import com.example.odontofast.model.Dentista;
import com.example.odontofast.model.Especialidade;
import com.example.odontofast.repository.EspecialidadeRepository;
import com.example.odontofast.service.DentistaService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/dentista")
public class AuthController {

    // Declaração de uma variável final para armazenar a instância do serviço de
    // Dentista.
    // "final" significa que essa variável não poderá ser alterada após a
    // inicialização.
    private final DentistaService dentistaService;
    private final EspecialidadeRepository especialidadeRepository;

    // Construtor para injeção de dependência
    // Esse construtor recebe uma instância de DentistaService e a atribui à
    // variável dentistaService.
    // Isso permite que a injeção de dependência seja feita pelo Spring, sem
    // necessidade de usar @Autowired.
    public AuthController(DentistaService dentistaService, EspecialidadeRepository especialidadeRepository) {
        this.dentistaService = dentistaService;
        this.especialidadeRepository = especialidadeRepository;
    }

    private void adicionarToast(Model model, String mensagem, String tipo) {
        model.addAttribute("paramTxtMensagem", mensagem);
        model.addAttribute("paramTipoMensagem", tipo);
    }

    @GetMapping("/home")
    public String exibirHome(HttpSession session, Model model) {
        Long dentistaId = (Long) session.getAttribute("dentistaId");

        if (dentistaId == null) {
            return "redirect:/dentista/login"; // Redireciona para o login se não houver dentista na sessão
        }

        Optional<Dentista> dentista = dentistaService.buscarPorId(dentistaId);

        if (dentista.isPresent()) {
            model.addAttribute("nomeDentista", dentista.get().getNomeDentista()); // Adiciona o nome do dentista ao
                                                                                  // modelo
        } else {
            return "redirect:/dentista/login"; // Se o dentista não for encontrado, redireciona para o login
        }

        return "home"; // Retorna a página home.html
    }

    @GetMapping("/agendamentos")
    public String exibirAgendamentos(HttpSession session, Model model) {
        Long dentistaId = (Long) session.getAttribute("dentistaId");

        if (dentistaId == null) {
            return "redirect:/dentista/login"; // Redireciona para o login se não houver dentista na sessão
        }

        Optional<Dentista> dentista = dentistaService.buscarPorId(dentistaId);

        if (dentista.isPresent()) {
            model.addAttribute("nomeDentista", dentista.get().getNomeDentista()); // Adiciona o nome do dentista ao
                                                                                  // modelo
        } else {
            return "redirect:/dentista/login"; // Se o dentista não for encontrado, redireciona para o login
        }

        return "agendamentos"; // Retorna a página home.html
    }

    // Exibir a página de cadastro
    @GetMapping("/cadastro")
    public String exibirCadastroDentista() {
        return "cadastro-dentista"; // Retorna a página de cadastro
    }

    @PostMapping("/cadastro") // Mapeia a requisição HTTP POST para o endpoint "/cadastro"
    public String cadastrarDentista(@ModelAttribute DentistaCadastroDTO dentistaCadastroDTO, Model model) {
    try {
        // Criação de um novo objeto Dentista
        Dentista dentista = new Dentista();
        
        // Preenchendo os atributos do dentista com os dados do DTO recebido
        dentista.setNomeDentista(dentistaCadastroDTO.getNomeDentista());
        dentista.setSenhaDentista(dentistaCadastroDTO.getSenhaDentista());
        dentista.setCro(dentistaCadastroDTO.getCro());
        dentista.setTelefoneDentista(dentistaCadastroDTO.getTelefoneDentista());
        dentista.setEmailDentista(dentistaCadastroDTO.getEmailDentista());

        // Busca a especialidade informada no DTO no banco de dados
        Especialidade especialidade = especialidadeRepository.findById(dentistaCadastroDTO.getEspecialidadeId())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada")); // Lança uma exceção caso a especialidade não seja encontrada

        // Simula a exibição do nome do dentista cadastrado no console (útil para depuração)
        System.out.println("Dentista cadastrado (mock): " + dentista.getNomeDentista());

        // Associa a especialidade ao dentista
        dentista.setEspecialidade(especialidade);

        // Salva o dentista no banco de dados utilizando o serviço
        dentistaService.salvarDentista(dentista);

        // Adiciona uma mensagem de sucesso ao modelo para exibição na página
        adicionarToast(model, "Cadastro realizado com sucesso!", "text-bg-success");

        // Redireciona para a página de login do dentista após o cadastro bem-sucedido
        return "login-dentista";
       } catch (Exception e) {
        // Em caso de erro, adiciona uma mensagem de erro ao modelo
        adicionarToast(model, "Erro ao cadastrar dentista", "text-bg-danger");

        // Mantém os dados do formulário para que o usuário possa corrigir possíveis erros
        model.addAttribute("dentistaCadastroDTO", dentistaCadastroDTO);

        // Retorna para a página de cadastro do dentista
        return "cadastro-dentista";
       }
   }


    // Tela de login de dentista
    @GetMapping("/login")
    public String exibirLoginDentista() {
        return "login-dentista"; // A view será a página login.html
    }

    // Endpoint de login (o formulário será enviado via POST)
    @PostMapping("/login")
    public String loginDentista(@RequestParam String cro, @RequestParam String senha, Model model,
            HttpSession session) {
        Optional<Dentista> dentista = dentistaService.autenticarDentista(cro, senha);

        if (dentista.isPresent()) {
            session.setAttribute("dentistaId", dentista.get().getIdDentista()); // Armazena o ID na sessão
            return "redirect:/dentista/home"; // Redireciona para evitar reenvio do formulário
        } else {
            model.addAttribute("erro", "Credenciais inválidas!");
            return "login-dentista";
        }
    }

    @GetMapping("/perfil")
    public String exibirPerfil(HttpSession session, Model model) {
        Long dentistaId = (Long) session.getAttribute("dentistaId"); // Recupera o ID da sessão

        if (dentistaId == null) {
            return "redirect:/dentista/login"; // Redireciona se não estiver logado
        }

        Optional<Dentista> dentista = dentistaService.buscarPorId(dentistaId);

        if (dentista.isPresent()) {
            model.addAttribute("dentista", dentista.get());
            return "perfil"; // Retorna a página perfil.html
        } else {
            return "redirect:/dentista/home"; // Se não encontrar, redireciona para home
        }
    }

    @PostMapping("/perfil/atualizar") // Mapeia a requisição HTTP POST para o endpoint "/perfil/atualizar"
    public String atualizarPerfil(@ModelAttribute Dentista dentista, HttpSession session, Model model) {
    
    // Obtém o ID do dentista armazenado na sessão
    Long dentistaId = (Long) session.getAttribute("dentistaId");

    // Verifica se o ID do dentista na sessão é válido e corresponde ao ID do dentista que está sendo atualizado
    if (dentistaId == null || !dentistaId.equals(dentista.getIdDentista())) {
        // Se não corresponder, exibe uma mensagem de erro e retorna para a página de perfil
        adicionarToast(model, "Acesso negado. Faça login novamente.", "text-bg-danger");
        return "perfil";
    }

    try {
        // Simula a exibição do nome do dentista atualizado no console (útil para depuração)
        System.out.println("Dentista atualizado (mock): " + dentista.getNomeDentista());

        // Chama o serviço para atualizar os dados do dentista no banco de dados
        dentistaService.atualizarDentista(dentista);

        // Adiciona uma mensagem de sucesso ao modelo para exibição na página
        adicionarToast(model, "Perfil atualizado com sucesso!", "text-bg-success");
    } catch (Exception e) {
        // Em caso de erro, adiciona uma mensagem de erro ao modelo
        adicionarToast(model, "Erro ao atualizar o perfil.", "text-bg-danger");
    }

    // Adiciona o objeto dentista atualizado ao modelo para exibição na página de perfil
    model.addAttribute("dentista", dentista);

    // Retorna para a página de perfil
    return "perfil";
    }

}