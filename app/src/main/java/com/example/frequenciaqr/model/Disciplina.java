package com.example.frequenciaqr.model;

public class Disciplina {
    private int id;
    private String nome;
    private String semestre;
    private String professor;
    private int professorId;

    public Disciplina(int id, String nome, String semestre, String professor, int professorId) {
        this.id = id;
        this.nome = nome;
        this.semestre = semestre;
        this.professor = professor;
        this.professorId = professorId;
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

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }
} 