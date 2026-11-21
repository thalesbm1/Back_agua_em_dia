package com.aguaemdia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma ocorrência de vazamento no sistema Água em Dia.
 */
@Entity
@Table(name = "vazamentos")
public class Vazamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vazamento_seq")
    @SequenceGenerator(name = "vazamento_seq", sequenceName = "seq_vazamento", allocationSize = 1)
    private Long id;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "primeiro_relato_valido_em")
    private LocalDateTime primeiroRelatoValidoEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "resolvido_em")
    private LocalDateTime resolvidoEm;

    // Campos complementares para suporte integral ao frontend
    @Column(length = 100)
    private String bairro;

    @Column(length = 255)
    private String referencia;

    @Column(length = 50)
    private String tipo;

    @Column(length = 50)
    private String porte;

    private Double latitude;

    private Double longitude;

    @Column(name = "vazao_estimada")
    private Integer vazaoEstimada;

    @OneToMany(mappedBy = "vazamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Relato> relatos = new ArrayList<>();

    public Vazamento() {
    }

    public Vazamento(String status, String bairro, String referencia, String tipo, String porte, Double latitude, Double longitude, Integer vazaoEstimada) {
        this.status = status != null ? status : "ABERTO";
        this.bairro = bairro;
        this.referencia = referencia;
        this.tipo = tipo;
        this.porte = porte;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vazaoEstimada = vazaoEstimada;
        this.criadoEm = LocalDateTime.now();
        this.primeiroRelatoValidoEm = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.primeiroRelatoValidoEm == null) {
            this.primeiroRelatoValidoEm = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ABERTO";
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPrimeiroRelatoValidoEm() {
        return primeiroRelatoValidoEm;
    }

    public void setPrimeiroRelatoValidoEm(LocalDateTime primeiroRelatoValidoEm) {
        this.primeiroRelatoValidoEm = primeiroRelatoValidoEm;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getResolvidoEm() {
        return resolvidoEm;
    }

    public void setResolvidoEm(LocalDateTime resolvidoEm) {
        this.resolvidoEm = resolvidoEm;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getVazaoEstimada() {
        return vazaoEstimada;
    }

    public void setVazaoEstimada(Integer vazaoEstimada) {
        this.vazaoEstimada = vazaoEstimada;
    }

    public List<Relato> getRelatos() {
        return relatos;
    }

    public void setRelatos(List<Relato> relatos) {
        this.relatos = relatos;
    }

    @Override
    public String toString() {
        return "Vazamento{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", primeiroRelatoValidoEm=" + primeiroRelatoValidoEm +
                ", criadoEm=" + criadoEm +
                ", resolvidoEm=" + resolvidoEm +
                ", bairro='" + bairro + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
