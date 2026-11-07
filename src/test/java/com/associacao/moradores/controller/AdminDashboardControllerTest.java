package com.associacao.moradores.controller;

import com.associacao.moradores.model.Morador;
import com.associacao.moradores.model.Solicitacao;
import com.associacao.moradores.model.Usuario;
import com.associacao.moradores.repository.MoradorRepository;
import com.associacao.moradores.repository.SolicitacaoRepository;
import com.associacao.moradores.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminDashboardControllerTest {

    @Mock
    private MoradorRepository moradorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdminDashboardController adminDashboardController;

    private Solicitacao solicitacaoOriginal;
    private Usuario operador;

    @BeforeEach
    void setUp() {
        operador = new Usuario();
        operador.setUsername("admin");

        solicitacaoOriginal = new Solicitacao();
        solicitacaoOriginal.setId(1L);
        solicitacaoOriginal.setEndereco("Rua Original, 123");
        solicitacaoOriginal.setCriticidade(50);
        solicitacaoOriginal.setDescricao("Descrição Antiga");
        solicitacaoOriginal.setStatus("PENDENTE");
        solicitacaoOriginal.setOperador(operador);
    }

    @Test
    void testExibirFormularioEditar() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacaoOriginal));

        String viewName = adminDashboardController.exibirFormularioEditar(1L, model);

        assertEquals("admin/editar-solicitacao", viewName);
        verify(model).addAttribute("solicitacao", solicitacaoOriginal);
    }

    @Test
    void testExibirFormularioEditarInvalido() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            adminDashboardController.exibirFormularioEditar(1L, model);
        });
    }

    @Test
    void testAtualizarSolicitacao() {
        when(solicitacaoRepository.findById(1L)).thenReturn(Optional.of(solicitacaoOriginal));

        Solicitacao dadosFormulario = new Solicitacao();
        dadosFormulario.setEndereco("Rua Nova, 456");
        dadosFormulario.setCriticidade(80);
        dadosFormulario.setDescricao("Descrição Atualizada");
        dadosFormulario.setStatus("EM_ANÁLISE");

        String viewName = adminDashboardController.atualizarSolicitacao(1L, dadosFormulario);

        assertEquals("redirect:/admin/solicitacoes", viewName);
        assertEquals("Rua Nova, 456", solicitacaoOriginal.getEndereco());
        assertEquals(80, solicitacaoOriginal.getCriticidade());
        assertEquals("Descrição Atualizada", solicitacaoOriginal.getDescricao());
        assertEquals("EM_ANÁLISE", solicitacaoOriginal.getStatus());
        verify(solicitacaoRepository).save(solicitacaoOriginal);
    }

    @Test
    void testExibirDashboard() {
        when(solicitacaoRepository.countByStatus("PENDENTE")).thenReturn(5L);
        when(solicitacaoRepository.countByStatus("EM_ANÁLISE")).thenReturn(3L);
        when(solicitacaoRepository.countByStatus("RESOLVIDO")).thenReturn(10L);

        String viewName = adminDashboardController.exibirDashboard(model);

        assertEquals("admin/dashboard", viewName);
        verify(model).addAttribute("totalPendente", 5L);
        verify(model).addAttribute("totalEmAnalise", 3L);
        verify(model).addAttribute("totalResolvido", 10L);
    }

    @Test
    void testSolicitacoes() {
        List<Solicitacao> solicitacoes = new ArrayList<>();
        List<Morador> moradores = new ArrayList<>();

        when(solicitacaoRepository.findAll()).thenReturn(solicitacoes);
        when(moradorRepository.findAll()).thenReturn(moradores);

        String viewName = adminDashboardController.solicitacoes(model);

        assertEquals("admin/solicitacoes", viewName);
        verify(model).addAttribute("solicitacoesList", solicitacoes);
        verify(model).addAttribute("moradoresList", moradores);
        verify(model).addAttribute(eq("newSolicitacao"), any(Solicitacao.class));
    }

    @Test
    void testAddSolicitacao() {
        when(userDetails.getUsername()).thenReturn("admin");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(operador));

        Solicitacao novaSolicitacao = new Solicitacao();
        novaSolicitacao.setEndereco("Av Principal, 789");

        String viewName = adminDashboardController.addSolicitacao(novaSolicitacao, userDetails);

        assertEquals("redirect:/admin/solicitacoes", viewName);
        assertEquals(operador, novaSolicitacao.getOperador());
        assertEquals("PENDENTE", novaSolicitacao.getStatus());
        assertNotNull(novaSolicitacao.getDataHora());
        verify(solicitacaoRepository).save(novaSolicitacao);
    }

    @Test
    void testLogin_DeveRetornarNomeDoTemplateHtml() {
        // 1. Instancia o controlador de forma simples (não precisa de mocks pois não há dependências)
        LoginController loginController = new LoginController();

        // 2. Executa o método de mapeamento da rota GET
        String view = loginController.login();

        // 3. Valida se o retorno aponta exatamente para o arquivo "login.html" do Thymeleaf
        assertEquals("login", view);
    }

}
