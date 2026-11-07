package com.associacao.moradores.model;

import jakarta.persistence.*;

@Entity
public class Morador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String status;

    public Morador() {}
    public Morador(String nome, String status) {
        this.nome = nome;
        this.status = status;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
