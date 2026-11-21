package com.aguaemdia.dto;

import java.time.LocalDateTime;

public class VazamentoDTO {

    private Long id;
    private String status;
    private LocalDateTime primeiroRelatoValidoEm;
    private LocalDateTime criadoEm;
    private LocalDateTime resolvidoEm;
    private String bairro;
    private String referencia;
    private String tipo;
    private String porte;
    private Double latitude;
    private Double longitude;
    private Integer vazaoEstimada;
    private Integer totalRelatos;

    public VazamentoDTO() {
    }

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

    public Integer getTotalRelatos() {
        return totalRelatos;
    }

    public void setTotalRelatos(Integer totalRelatos) {
        this.totalRelatos = totalRelatos;
    }
}
