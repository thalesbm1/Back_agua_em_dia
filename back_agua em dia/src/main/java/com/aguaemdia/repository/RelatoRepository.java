package com.aguaemdia.repository;

import com.aguaemdia.model.Relato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelatoRepository extends JpaRepository<Relato, Long> {

    Optional<Relato> findByProtocolo(String protocolo);

    List<Relato> findByUsuarioId(Long usuarioId);

    List<Relato> findByVazamentoId(Long vazamentoId);

    List<Relato> findAllByOrderByEnviadoEmDesc();
}
