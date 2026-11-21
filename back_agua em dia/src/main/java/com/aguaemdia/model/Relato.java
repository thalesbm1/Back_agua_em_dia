package com.aguaemdia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entidade que representa os relatos enviados pelos cidadãos informando vazamentos.
 */
@Entity
@Table(name = "relatos")
public class Relato {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relato_seq")
    @SequenceGenerator(name = "relato_seq", sequenceName = "seq_relato", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String protocolo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "enviado_em", nullable = false, updatable = false)
    private LocalDateTime enviadoEm;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"senhaHash", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vazamento_id")
    @JsonIgnoreProperties({"relatos", "hibernateLazyInitializer", "handler"})
    private Vazamento vazamento;

    // Campos complementares para alinhamento com a interface do usuário
    @Column(length = 100)
    private String bairro;

    @Column(length = 255)
    private String referencia;

    @Column(length = 50)
    private String tipo;

    private Double latitude;

    private Double longitude;

    public Relato() {
    }

    public Relato(String protocolo, String descricao, Usuario usuario, Vazamento vazamento) {
        this.protocolo = protocolo;
        this.descricao = descricao;
        this.usuario = usuario;
        this.vazamento = vazamento;
        this.enviadoEm = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.enviadoEm == null) {
            this.enviadoEm = LocalDateTime.now();
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Vazamento getVazamento() {
        return vazamento;
    }

    public void setVazamento(Vazamento vazamento) {
        this.vazamento = vazamento;
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

    @Override
    public String toString() {
        return "Relato{" +
                "id=" + id +
                ", protocolo='" + protocolo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", enviadoEm=" + enviadoEm +
                ", usuario=" + (usuario != null ? usuario.getId() : null) +
                ", vazamento=" + (vazamento != null ? vazamento.getId() : null) +
                '}';
    }
}
