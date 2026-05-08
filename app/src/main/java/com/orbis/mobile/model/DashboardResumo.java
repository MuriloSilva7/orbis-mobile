package com.orbis.mobile.model;

public class DashboardResumo {
    private int totalMaquinas;
    private int maquinasEmAlerta;
    private int maquinasFuncionando;
    private int alertasAtivos;
    private int alertasHoje;
    private int tecnicosAtivos;
    private float integridadeMedia;  // vem como { _avg: { integridade: X } } — veja nota abaixo
    private int sensoresOnline;
    private int alertaSemAtendimento;
    private int alertasAtendidosHoje;

    public int getTotalMaquinas() { return totalMaquinas; }
    public int getAlertasAtivos() { return alertasAtivos; }
    public int getMaquinasFuncionando() { return maquinasFuncionando; }
    public float getIntegridadeMedia() { return integridadeMedia; }
    public int getSensoresOnline() { return sensoresOnline; }
    public int getAlertaSemAtendimento() { return alertaSemAtendimento; }
}
