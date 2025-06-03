package com.example.frequenciaqr.model;

public class Disciplina {
    private int id;
    private String nome;
    private String semestre;
    private String emailProfessor;

    public Disciplina(int id, String nome, String semestre, String emailProfessor) {
        this.id = id;
        this.nome = nome;
        this.semestre = semestre;
        this.emailProfessor = emailProfessor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getEmailProfessor() {
        return emailProfessor;
    }

    public void setEmailProfessor(String emailProfessor) {
        this.emailProfessor = emailProfessor;
    }
} 