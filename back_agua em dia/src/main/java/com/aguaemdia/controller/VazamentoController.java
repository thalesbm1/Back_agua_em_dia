package com.aguaemdia.controller;

import com.aguaemdia.dto.VazamentoDTO;
import com.aguaemdia.service.VazamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vazamentos")
@CrossOrigin(origins = "*")
public class VazamentoController {

    private final VazamentoService vazamentoService;

    @Autowired
    public VazamentoController(VazamentoService vazamentoService) {
        this.vazamentoService = vazamentoService;
    }

    @GetMapping
    public ResponseEntity<List<VazamentoDTO>> listar(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(vazamentoService.listarTodos(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(vazamentoService.buscarPorId(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<VazamentoDTO> criar(@RequestBody VazamentoDTO dto) {
        VazamentoDTO criado = vazamentoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusPatch(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "O campo 'status' é obrigatório."));
        }
        try {
            VazamentoDTO atualizado = vazamentoService.atualizarStatus(id, status);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusPut(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return atualizarStatusPatch(id, body);
    }
}
