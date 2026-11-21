package com.aguaemdia.service;

import com.aguaemdia.dto.LoginDTO;
import com.aguaemdia.dto.UsuarioDTO;
import com.aguaemdia.dto.UsuarioResponseDTO;
import com.aguaemdia.model.Usuario;
import com.aguaemdia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com o e-mail: " + dto.getEmail());
        }

        String senhaCriptografada = (dto.getSenha() != null && !dto.getSenha().isBlank())
                ? passwordEncoder.encode(dto.getSenha())
                : passwordEncoder.encode("123456");

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome() != null ? dto.getNome().trim() : "Cidadão Colaborador");
        usuario.setEmail(dto.getEmail().trim().toLowerCase());
        usuario.setTelefone(dto.getTelefone());
        usuario.setSenhaHash(senhaCriptografada);
        usuario.setSituacao(dto.getSituacao() != null ? dto.getSituacao() : "ATIVO");

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
        return toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO autenticar(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com este e-mail."));

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenhaHash())) {
            throw new IllegalArgumentException("Senha incorreta.");
        }

        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO toResponseDTO(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTelefone(),
                u.getSituacao(),
                u.getCriadoEm()
        );
    }
}
