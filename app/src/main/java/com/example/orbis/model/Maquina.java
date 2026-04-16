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

    public Maquina(int id,String nome, String setor, String tipo, String criticidade, float integridade, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.setor = setor;
        this.tipo = tipo;
        this.criticidade = criticidade;
        this.integridade = integridade;
        this.ativo = ativo;
    }
}
