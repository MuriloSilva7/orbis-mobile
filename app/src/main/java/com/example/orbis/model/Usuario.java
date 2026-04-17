package com.example.orbis.model;

public class Usuario {

    private int id;
    private String nome, email, role;
    private boolean ativo;


    public Usuario(int id,String nome, String email, String role, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean getAtivo() {return ativo;}
}
