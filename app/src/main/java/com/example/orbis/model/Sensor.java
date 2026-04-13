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
}
