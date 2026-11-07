package com.associacao.moradores.repository;
import com.associacao.moradores.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    // Adicione esta linha aqui para permitir a contagem por status no banco de dados
    long countByStatus(String status);
}
