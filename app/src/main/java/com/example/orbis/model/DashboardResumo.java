package com.example.orbis.model;

public class DashboardResumo {

    private int totalMaquinas, maquinasEmAlerta, alertasAtivos, alertasHoje, tecnicosAtivos, sensoresOnline;
    private float integridadeMedia;

    public int getTotalMaquinas() { return totalMaquinas; }
    public int getAlertasAtivos() { return alertasAtivos; }
    public float getIntegridadeMedia() { return integridadeMedia; }
    public int getSensoresOnline() { return sensoresOnline; }
}
