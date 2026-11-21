package com.aguaemdia.service;

import com.aguaemdia.dto.RelatoDTO;
import com.aguaemdia.model.Relato;
import com.aguaemdia.model.Usuario;
import com.aguaemdia.model.Vazamento;
import com.aguaemdia.repository.RelatoRepository;
import com.aguaemdia.repository.UsuarioRepository;
import com.aguaemdia.repository.VazamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatoService {

    private final RelatoRepository relatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VazamentoRepository vazamentoRepository;

    @Autowired
    public RelatoService(RelatoRepository relatoRepository,
                         UsuarioRepository usuarioRepository,
                         VazamentoRepository vazamentoRepository) {
        this.relatoRepository = relatoRepository;
        this.usuarioRepository = usuarioRepository;
        this.vazamentoRepository = vazamentoRepository;
    }

    @Transactional
    public RelatoDTO registrarRelato(RelatoDTO dto) {
        // 1. Identifica ou recupera o Usuário
        Usuario usuario;
        if (dto.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário informado não encontrado (ID: " + dto.getUsuarioId() + ")"));
        } else if (dto.getUsuarioEmail() != null && !dto.getUsuarioEmail().isBlank()) {
            usuario = usuarioRepository.findByEmail(dto.getUsuarioEmail())
                    .orElseGet(() -> {
                        Usuario novo = new Usuario();
                        novo.setNome(dto.getUsuarioNome() != null ? dto.getUsuarioNome() : "Cidadão Anônimo");
                        novo.setEmail(dto.getUsuarioEmail());
                        novo.setSenhaHash("$2a$10$defaultHashAcademicPlaceholder");
                        novo.setSituacao("ATIVO");
                        return usuarioRepository.save(novo);
                    });
        } else {
            // Se nenhum usuário for informado, associa ao primeiro usuário padrão ou cria um
            List<Usuario> usuarios = usuarioRepository.findAll();
            if (!usuarios.isEmpty()) {
                usuario = usuarios.get(0);
            } else {
                Usuario padrao = new Usuario("Cidadão Anônimo", "cidadao@aguaemdia.com", "81999990000", "$2a$10$defaultHashPlaceholder", "ATIVO");
                usuario = usuarioRepository.save(padrao);
            }
        }

        // 2. Identifica ou cria o Vazamento correspondente
        Vazamento vazamento;
        if (dto.getVazamentoId() != null) {
            vazamento = vazamentoRepository.findById(dto.getVazamentoId())
                    .orElseThrow(() -> new IllegalArgumentException("Vazamento informado não encontrado (ID: " + dto.getVazamentoId() + ")"));
        } else {
            // Cria um novo Vazamento associado automaticamente
            vazamento = new Vazamento();
            vazamento.setStatus("ABERTO");
            vazamento.setBairro(dto.getBairro() != null ? dto.getBairro() : "Boa Vista");
            vazamento.setReferencia(dto.getReferencia() != null ? dto.getReferencia() : "Ponto informado pelo cidadão");
            vazamento.setTipo(dto.getTipo() != null ? dto.getTipo() : "REDE");
            vazamento.setPorte(dto.getPorte() != null ? dto.getPorte() : "MEDIO");
            vazamento.setLatitude(dto.getLatitude() != null ? dto.getLatitude() : -8.0570);
            vazamento.setLongitude(dto.getLongitude() != null ? dto.getLongitude() : -34.8880);
            vazamento.setVazaoEstimada(calcularVazaoEstimada(dto.getTipo(), dto.getPorte()));
            vazamento.setCriadoEm(LocalDateTime.now());
            vazamento.setPrimeiroRelatoValidoEm(LocalDateTime.now());
            vazamento = vazamentoRepository.save(vazamento);
        }

        // 3. Gera Protocolo amigável sequencial (ex: RE-2026-0001)
        long count = relatoRepository.count() + 1;
        String protocolo = "RE-2026-" + String.format("%04d", count);

        // 4. Cria e persiste o Relato
        Relato relato = new Relato();
        relato.setProtocolo(protocolo);
        relato.setDescricao(dto.getDescricao() != null ? dto.getDescricao() : "Relato de vazamento registrado pelo cidadão.");
        relato.setEnviadoEm(LocalDateTime.now());
        relato.setUsuario(usuario);
        relato.setVazamento(vazamento);
        relato.setBairro(dto.getBairro() != null ? dto.getBairro() : vazamento.getBairro());
        relato.setReferencia(dto.getReferencia() != null ? dto.getReferencia() : vazamento.getReferencia());
        relato.setTipo(dto.getTipo() != null ? dto.getTipo() : vazamento.getTipo());
        relato.setLatitude(dto.getLatitude() != null ? dto.getLatitude() : vazamento.getLatitude());
        relato.setLongitude(dto.getLongitude() != null ? dto.getLongitude() : vazamento.getLongitude());

        Relato salvo = relatoRepository.save(relato);
        return toDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<RelatoDTO> listarTodos() {
        return relatoRepository.findAllByOrderByEnviadoEmDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RelatoDTO buscarPorId(Long id) {
        Relato relato = relatoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Relato não encontrado com ID: " + id));
        return toDTO(relato);
    }

    @Transactional(readOnly = true)
    public RelatoDTO buscarPorProtocolo(String protocolo) {
        Relato relato = relatoRepository.findByProtocolo(protocolo)
                .orElseThrow(() -> new IllegalArgumentException("Relato não encontrado com protocolo: " + protocolo));
        return toDTO(relato);
    }

    @Transactional(readOnly = true)
    public List<RelatoDTO> listarPorVazamento(Long vazamentoId) {
        return relatoRepository.findByVazamentoId(vazamentoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private int calcularVazaoEstimada(String tipo, String porte) {
        int basePorte = 600;
        if ("GOTEJAMENTO".equalsIgnoreCase(porte)) basePorte = 3;
        else if ("FIO".equalsIgnoreCase(porte)) basePorte = 60;
        else if ("MEDIO".equalsIgnoreCase(porte)) basePorte = 600;
        else if ("FORTE".equalsIgnoreCase(porte)) basePorte = 2400;
        else if ("JORRO".equalsIgnoreCase(porte)) basePorte = 9000;

        double multiplicador = 1.6;
        if ("ADUTORA".equalsIgnoreCase(tipo)) multiplicador = 3.0;
        else if ("REDE".equalsIgnoreCase(tipo)) multiplicador = 1.6;
        else if ("RAMAL".equalsIgnoreCase(tipo)) multiplicador = 0.7;
        else if ("CLANDESTINA".equalsIgnoreCase(tipo)) return 800;
        else if ("DESPERDICIO".equalsIgnoreCase(tipo)) multiplicador = 0.5;

        return (int) Math.round(basePorte * multiplicador);
    }

    public RelatoDTO toDTO(Relato r) {
        RelatoDTO dto = new RelatoDTO();
        dto.setId(r.getId());
        dto.setProtocolo(r.getProtocolo());
        dto.setDescricao(r.getDescricao());
        dto.setEnviadoEm(r.getEnviadoEm());
        if (r.getUsuario() != null) {
            dto.setUsuarioId(r.getUsuario().getId());
            dto.setUsuarioNome(r.getUsuario().getNome());
            dto.setUsuarioEmail(r.getUsuario().getEmail());
        }
        if (r.getVazamento() != null) {
            dto.setVazamentoId(r.getVazamento().getId());
        }
        dto.setBairro(r.getBairro());
        dto.setReferencia(r.getReferencia());
        dto.setTipo(r.getTipo());
        dto.setLatitude(r.getLatitude());
        dto.setLongitude(r.getLongitude());
        return dto;
    }
}
