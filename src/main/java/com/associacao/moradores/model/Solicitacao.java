package com.associacao.moradores.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Solicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario operador;

    @ManyToOne
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador;

    // Altera a coluna no MySQL para o tipo TEXT (que suporta até 65.535 caracteres)
    @Column(name = "descricao", columnDefinition = "TEXT", nullable = false)
    private String descricao;


    @Column(nullable = false)
    private String endereco;

    private LocalDateTime dataHora;
    private Integer criticidade;
    private String status;

    public Solicitacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getOperador() { return operador; }
    public void setOperador(Usuario operador) { this.operador = operador; }
    public Morador getMorador() { return morador; }
    public void setMorador(Morador morador) { this.morador = morador; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Integer getCriticidade() { return criticidade; }
    public void setCriticidade(Integer criticidade) { this.criticidade = criticidade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
