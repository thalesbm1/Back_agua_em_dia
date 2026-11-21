package com.aguaemdia.repository;

import com.aguaemdia.model.Vazamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VazamentoRepository extends JpaRepository<Vazamento, Long> {

    List<Vazamento> findByStatus(String status);

    List<Vazamento> findByBairro(String bairro);

    List<Vazamento> findAllByOrderByCriadoEmDesc();
}
