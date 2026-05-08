package com.orbis.mobile.model;

public class Sensor {

    private int id, maquinaId;
    private String tipo, status;
    private float limiteTemperatura, limiteVibracao;
    private Float ultimaTemperatura, ultimaVibracao;


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

    public int getId() { return id; }
    public int getMaquinaId() {return maquinaId;}
    public String getTipo() { return tipo; }
    public String getStatus() { return status; }
    public float getLimiteTemperatura() {return limiteTemperatura;}
    public float getLimiteVibracao() {return limiteVibracao;}
    public Float getUltimaTemperatura() { return ultimaTemperatura; }
    public Float getUltimaVibracao() { return ultimaVibracao; }
}
