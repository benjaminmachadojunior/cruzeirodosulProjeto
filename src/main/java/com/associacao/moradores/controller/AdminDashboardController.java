package com.associacao.moradores.controller;

import com.associacao.moradores.model.*;
import com.associacao.moradores.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final MoradorRepository moradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDashboardController(MoradorRepository m, UsuarioRepository u, SolicitacaoRepository s, PasswordEncoder p) {
        this.moradorRepository = m;
        this.usuarioRepository = u;
        this.solicitacaoRepository = s;
        this.passwordEncoder = p;
    }

    @GetMapping("/solicitacoes/editar/{id}")
    public String exibirFormularioEditar(@PathVariable("id") Long id, Model model) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação inválida: " + id));

        model.addAttribute("solicitacao", solicitacao);
        return "admin/editar-solicitacao";
    }

    @PostMapping("/solicitacoes/editar/{id}")
    public String atualizarSolicitacao(@PathVariable("id") Long id, @ModelAttribute("solicitacao") Solicitacao dadosFormulario) {
        // 1. Busca o registro original completo do banco de dados
        Solicitacao solicitacaoOriginal = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação inválida: " + id));

        // 2. Transfere apenas as informações que o usuário pode editar na tela
        solicitacaoOriginal.setEndereco(dadosFormulario.getEndereco());
        solicitacaoOriginal.setCriticidade(dadosFormulario.getCriticidade());
        solicitacaoOriginal.setDescricao(dadosFormulario.getDescricao());
        solicitacaoOriginal.setStatus(dadosFormulario.getStatus());

        // 3. Salva a entidade original mantendo os relacionamentos e operador intactos
        solicitacaoRepository.save(solicitacaoOriginal);

        return "redirect:/admin/solicitacoes";
    }

    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {
        // Conta os totais diretamente no banco de dados baseado no status
        long totalPendente = solicitacaoRepository.countByStatus("PENDENTE");
        long totalEmAnalise = solicitacaoRepository.countByStatus("EM_ANÁLISE");
        long totalResolvido = solicitacaoRepository.countByStatus("RESOLVIDO");

        // Envia os totais para o arquivo HTML Thymeleaf
        model.addAttribute("totalPendente", totalPendente);
        model.addAttribute("totalEmAnalise", totalEmAnalise);
        model.addAttribute("totalResolvido", totalResolvido);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("usersList", moradorRepository.findAll());
        model.addAttribute("newMorador", new Morador());
        return "admin/users";
    }

    @PostMapping("/users/add")
    public String addMorador(@ModelAttribute Morador morador) {
        moradorRepository.save(morador);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteMorador(@PathVariable Long id) {
        moradorRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/system-users")
    public String systemUsers(Model model) {
        model.addAttribute("systemUsersList", usuarioRepository.findAll());
        model.addAttribute("newUsuario", new Usuario());
        return "admin/system-users";
    }

    @PostMapping("/system-users/add")
    public String addSystemUser(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Este nome de usuário já está em uso!");
            return "redirect:/admin/system-users";
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRole("OPERADOR");
        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("success", "Novo acesso criado com sucesso!");
        return "redirect:/admin/system-users";
    }

    @GetMapping("/system-users/delete/{id}")
    public String deleteSystemUser(@PathVariable Long id) {
        if(usuarioRepository.count() > 1) {
            usuarioRepository.deleteById(id);
        }
        return "redirect:/admin/system-users";
    }

    @GetMapping("/solicitacoes")
    public String solicitacoes(Model model) {
        model.addAttribute("solicitacoesList", solicitacaoRepository.findAll());
        model.addAttribute("moradoresList", moradorRepository.findAll());
        model.addAttribute("newSolicitacao", new Solicitacao());
        return "admin/solicitacoes";
    }

    @PostMapping("/solicitacoes/add")
    public String addSolicitacao(@ModelAttribute Solicitacao solicitacao, @AuthenticationPrincipal UserDetails currentUser) {
        // Busca o operador logado obrigatório
        Usuario operador = usuarioRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não foi encontrado no banco de dados."));

        solicitacao.setOperador(operador);
        solicitacao.setDataHora(LocalDateTime.now());

        // Define o status padronizado em maiúsculo para funcionar com a contagem do dashboard
        solicitacao.setStatus("PENDENTE");

        solicitacaoRepository.save(solicitacao);
        return "redirect:/admin/solicitacoes";
    }
}

@Controller
class LoginController {
    @GetMapping("/login")
    public String login() { return "login"; }
}
