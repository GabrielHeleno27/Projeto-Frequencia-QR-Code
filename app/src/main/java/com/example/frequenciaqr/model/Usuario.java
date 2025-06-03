package com.example.frequenciaqr.model;

public class Usuario {
    private String email;
    private String nome;
    private String tipo;
    private String senha;

    public Usuario() {
    }

    public Usuario(String email, String nome, String tipo, String senha) {
        this.email = email;
        this.nome = nome;
        this.tipo = tipo;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
} 