package com.orbis.mobile.model;

public class LeituraRequest {

    private int sensorId;
    private float temperatura, vibracao;

    public LeituraRequest(int sensorId, float temperatura, float vibracao) {
        this.sensorId = sensorId;
        this.temperatura = temperatura;
        this.vibracao = vibracao;
    }
}
