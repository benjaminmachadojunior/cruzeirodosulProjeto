package com.associacao.moradores.repository;
import com.associacao.moradores.model.Morador;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MoradorRepository extends JpaRepository<Morador, Long> {}
