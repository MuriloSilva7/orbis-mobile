package com.example.orbis.model;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String role;
    private boolean ativo;
    private String especialidade;
    private String telefone;
    private String oneSignalId;
    private String atualizadoEm;
    private String criadoEm;

    public Usuario() {
    }


    public Usuario(int id,String nome, String email, String role, String especialidade, String telefone, String oneSignalId, String atualizadoEm, String criadoEm ,boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.ativo = ativo;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.oneSignalId = oneSignalId;
        this.atualizadoEm = atualizadoEm;
        this.criadoEm = criadoEm;

    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isAtivo() { return ativo; }
    public String getEspecialidade() { return especialidade; }
    public String getTelefone() { return telefone; }
    public String getOneSignalId() { return oneSignalId; }
    public String getCriadoEm() { return criadoEm; }
}
