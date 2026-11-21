package com.aguaemdia.service;

import com.aguaemdia.dto.VazamentoDTO;
import com.aguaemdia.model.Vazamento;
import com.aguaemdia.repository.VazamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VazamentoService {

    private final VazamentoRepository vazamentoRepository;

    @Autowired
    public VazamentoService(VazamentoRepository vazamentoRepository) {
        this.vazamentoRepository = vazamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<VazamentoDTO> listarTodos(String status) {
        List<Vazamento> lista;
        if (status != null && !status.isBlank()) {
            lista = vazamentoRepository.findByStatus(status.toUpperCase());
        } else {
            lista = vazamentoRepository.findAllByOrderByCriadoEmDesc();
        }
        return lista.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VazamentoDTO buscarPorId(Long id) {
        Vazamento v = vazamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vazamento não encontrado com o ID: " + id));
        return toDTO(v);
    }

    @Transactional(readOnly = true)
    public Vazamento buscarEntidadePorId(Long id) {
        return vazamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vazamento não encontrado com o ID: " + id));
    }

    @Transactional
    public VazamentoDTO criar(VazamentoDTO dto) {
        Vazamento v = new Vazamento();
        v.setStatus(dto.getStatus() != null ? dto.getStatus().toUpperCase() : "ABERTO");
        v.setBairro(dto.getBairro());
        v.setReferencia(dto.getReferencia());
        v.setTipo(dto.getTipo() != null ? dto.getTipo() : "REDE");
        v.setPorte(dto.getPorte() != null ? dto.getPorte() : "MEDIO");
        v.setLatitude(dto.getLatitude());
        v.setLongitude(dto.getLongitude());
        v.setVazaoEstimada(dto.getVazaoEstimada() != null ? dto.getVazaoEstimada() : 600);
        v.setCriadoEm(LocalDateTime.now());
        v.setPrimeiroRelatoValidoEm(LocalDateTime.now());

        Vazamento salvo = vazamentoRepository.save(v);
        return toDTO(salvo);
    }

    @Transactional
    public VazamentoDTO atualizarStatus(Long id, String novoStatus) {
        Vazamento v = vazamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vazamento não encontrado com o ID: " + id));

        String statusFormatado = novoStatus.toUpperCase().trim();
        v.setStatus(statusFormatado);

        if ("CONCLUIDO".equals(statusFormatado) || "RESOLVIDO".equals(statusFormatado)) {
            v.setResolvidoEm(LocalDateTime.now());
        } else {
            v.setResolvidoEm(null);
        }

        Vazamento atualizado = vazamentoRepository.save(v);
        return toDTO(atualizado);
    }

    public VazamentoDTO toDTO(Vazamento v) {
        VazamentoDTO dto = new VazamentoDTO();
        dto.setId(v.getId());
        dto.setStatus(v.getStatus());
        dto.setPrimeiroRelatoValidoEm(v.getPrimeiroRelatoValidoEm());
        dto.setCriadoEm(v.getCriadoEm());
        dto.setResolvidoEm(v.getResolvidoEm());
        dto.setBairro(v.getBairro());
        dto.setReferencia(v.getReferencia());
        dto.setTipo(v.getTipo());
        dto.setPorte(v.getPorte());
        dto.setLatitude(v.getLatitude());
        dto.setLongitude(v.getLongitude());
        dto.setVazaoEstimada(v.getVazaoEstimada());
        dto.setTotalRelatos(v.getRelatos() != null ? v.getRelatos().size() : 0);
        return dto;
    }
}
