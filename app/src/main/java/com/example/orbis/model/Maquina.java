package com.example.orbis.model;

public class Maquina {

    private int id;
    private String nome, setor, tipo, criticidade;
    private float integridade;
    private boolean ativo;

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getSetor() { return setor; }
    public String getTipo() { return tipo; }
    public String getCriticidade() { return criticidade; }
    public float getIntegridade() { return integridade; }
    public boolean isAtivo() { return ativo; }
}
