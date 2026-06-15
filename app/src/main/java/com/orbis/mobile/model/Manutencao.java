package com.orbis.mobile.model;

public class Manutencao {

    private int id;
    private Integer alertaId;
    private int usuarioId;
    private int maquinaId;
    private String tipo;
    private String observacao;
    private String status;
    private String criadoEm;
    private Alerta alerta;
    private Maquina maquina;
    private Usuario usuario;

    public int getId() { return id; }
    public Integer getAlertaId() { return alertaId; }
    public int getMaquinaId() { return maquinaId; }
    public String getTipo() { return tipo; }
    public String getObservacao() { return observacao; }
    public String getStatus() { return status; }
    public String getCriadoEm() { return criadoEm; }
    public Alerta getAlerta() { return alerta; }
    public Maquina getMaquina() { return maquina; }
    public Usuario getUsuario() { return usuario; }
}
