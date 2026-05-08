package com.orbis.mobile.model;

public class Alerta {
    private int id;
    private int sensorId;
    private int maquinaId;
    private Integer tecnicoId;
    private String tipo;
    private String status;
    private String mensagem;
    private String criadoEm;
    private Sensor sensor;
    private Maquina maquina;

    public Alerta(int id, int sensorId, int maquinaId, Integer tecnicoId ,String status, String mensagem, String tipo, String criadoEm, Sensor sensor, Maquina maquina) {
        this.id = id;
        this.sensorId = sensorId;
        this.maquinaId = maquinaId;
        this.tipo = tipo;
        this.tecnicoId = tecnicoId;
        this.status = status;
        this.mensagem = mensagem;
        this.criadoEm = criadoEm;
        this.sensor = sensor;
        this.maquina = maquina;
    }

    public int getId() { return id; }
    public Integer getTecnicoId() { return tecnicoId; }
    public String getTipo() { return tipo; }
    public String getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public String getCriadoEm() { return criadoEm; }
    public Sensor getSensor() { return sensor; }
    public Maquina getMaquina() { return maquina; }
}
