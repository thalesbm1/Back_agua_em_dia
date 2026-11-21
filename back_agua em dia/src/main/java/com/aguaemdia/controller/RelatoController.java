package com.aguaemdia.controller;

import com.aguaemdia.dto.RelatoDTO;
import com.aguaemdia.service.RelatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatos")
@CrossOrigin(origins = "*")
public class RelatoController {

    private final RelatoService relatoService;

    @Autowired
    public RelatoController(RelatoService relatoService) {
        this.relatoService = relatoService;
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody RelatoDTO dto) {
        try {
            RelatoDTO criado = relatoService.registrarRelato(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(criado);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<RelatoDTO>> listarTodos() {
        return ResponseEntity.ok(relatoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(relatoService.buscarPorId(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
        }
    }

    @GetMapping("/protocolo/{protocolo}")
    public ResponseEntity<?> buscarPorProtocolo(@PathVariable String protocolo) {
        try {
            return ResponseEntity.ok(relatoService.buscarPorProtocolo(protocolo));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
        }
    }
}
