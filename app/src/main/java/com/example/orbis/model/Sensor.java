package com.example.orbis.model;

public class Sensor {

    private int id, maquinaId;
    private String tipo, status;
    private float limiteTemperatura, limiteVibracao;
    private Float ultimaTemperatura, ultimaVibracao;

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getStatus() { return status; }
    public Float getUltimaTemperatura() { return ultimaTemperatura; }
    public Float getUltimaVibracao() { return ultimaVibracao; }

    public Sensor(int id, int maquinaId, String tipo, String status, float limiteTemperatura, float limiteVibracao, Float  ultimaTemperatura, Float ultimaVibracao) {
        this.id = id;
        this.maquinaId = maquinaId;
        this.tipo = tipo;
        this.status = status;
        this.limiteTemperatura = limiteTemperatura;
        this.limiteVibracao = limiteVibracao;
        this.ultimaTemperatura = ultimaTemperatura;
        this.ultimaVibracao = ultimaVibracao;

    }
}
