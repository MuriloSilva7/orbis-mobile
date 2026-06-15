package com.orbis.mobile.model;

import com.google.gson.annotations.SerializedName;

public class AlertaEvento {
    private int id;
    private int alertaId;
    private String tipo;
    private String mensagem;
    private String descricao;
    private String criadoEm;
    private Usuario usuario;
    private Object manutencao;

    public int getId() { return id; }
    public int getAlertaId() { return alertaId; }
    public String getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public String getDescricao() { return descricao; }
    public String getCriadoEm() { return criadoEm; }
    public Usuario getUsuario() { return usuario; }
    public Object getManutencao() { return manutencao; }
}
