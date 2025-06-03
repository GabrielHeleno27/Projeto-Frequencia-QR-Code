package com.example.frequenciaqr.model;

public class Disciplina {
    private int id;
    private String nome;
    private String semestre;
    private String emailProfessor;
    private int presencas;

    public Disciplina(int id, String nome, String semestre, String emailProfessor) {
        this.id = id;
        this.nome = nome;
        this.semestre = semestre;
        this.emailProfessor = emailProfessor;
        this.presencas = 0;
    }

    public Disciplina(int id, String nome, String semestre, String emailProfessor, int presencas) {
        this.id = id;
        this.nome = nome;
        this.semestre = semestre;
        this.emailProfessor = emailProfessor;
        this.presencas = presencas;
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

    public String getProfessor() {
        return emailProfessor;
    }

    public int getPresencas() {
        return presencas;
    }

    public void setPresencas(int presencas) {
        this.presencas = presencas;
    }
} 