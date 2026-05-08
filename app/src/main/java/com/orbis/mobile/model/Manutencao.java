package com.orbis.mobile.model;

public class Manutencao {

    private int id;
    private int alertaId;
    private int usuarioId;
    private String observacao;
    private String status;
    private String criadoEm;
    private Alerta alerta;
    private Usuario usuario;

    public Manutencao(int id, int alertaId, int usuarioId,String observacao, String status, String criadoEm, Alerta alerta, Usuario usuario) {
        this.id = id;
        this.alertaId = alertaId;
        this.usuarioId = usuarioId;
        this.observacao = observacao;
        this.status = status;
        this.criadoEm = criadoEm;
        this.alerta = alerta;
        this.usuario = usuario;
    }

    public int getId() { return id; }
    public int getAlertaId() { return alertaId; }
    public String getObservacao() { return observacao; }
    public String getStatus() { return status; }
    public String getCriadoEm() { return criadoEm; }
    public Alerta getAlerta() { return alerta; }
    public Usuario getUsuario() { return usuario; }
}
