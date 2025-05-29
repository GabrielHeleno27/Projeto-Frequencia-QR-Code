package com.example.frequenciaqr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FrequenciaQR.db";
    private static final int DATABASE_VERSION = 2; // Incrementando a versão para forçar a recriação

    // Tabelas
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String TABLE_DISCIPLINAS = "disciplinas";
    private static final String TABLE_ALUNOS_DISCIPLINAS = "alunos_disciplinas";
    private static final String TABLE_PRESENCAS = "presencas";

    // Colunas comuns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SENHA = "senha";
    private static final String COLUMN_TIPO = "tipo";

    // Colunas específicas
    private static final String COLUMN_NOME_DISCIPLINA = "nome";
    private static final String COLUMN_SEMESTRE = "semestre";
    private static final String COLUMN_PROFESSOR_ID = "professor_id";
    private static final String COLUMN_ALUNO_ID = "aluno_id";
    private static final String COLUMN_DISCIPLINA_ID = "disciplina_id";
    private static final String COLUMN_DATA = "data";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar tabela de usuários
        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_SENHA + " TEXT,"
                + COLUMN_TIPO + " TEXT)";

        // Criar tabela de disciplinas
        String CREATE_DISCIPLINAS_TABLE = "CREATE TABLE " + TABLE_DISCIPLINAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOME_DISCIPLINA + " TEXT,"
                + COLUMN_SEMESTRE + " TEXT,"
                + COLUMN_PROFESSOR_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_PROFESSOR_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";

        // Criar tabela de relação alunos-disciplinas
        String CREATE_ALUNOS_DISCIPLINAS_TABLE = "CREATE TABLE " + TABLE_ALUNOS_DISCIPLINAS + "("
                + COLUMN_ALUNO_ID + " INTEGER,"
                + COLUMN_DISCIPLINA_ID + " INTEGER,"
                + "PRIMARY KEY(" + COLUMN_ALUNO_ID + "," + COLUMN_DISCIPLINA_ID + "),"
                + "FOREIGN KEY(" + COLUMN_ALUNO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_DISCIPLINA_ID + ") REFERENCES " + TABLE_DISCIPLINAS + "(" + COLUMN_ID + "))";

        // Criar tabela de presenças
        String CREATE_PRESENCAS_TABLE = "CREATE TABLE " + TABLE_PRESENCAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ALUNO_ID + " INTEGER,"
                + COLUMN_DISCIPLINA_ID + " INTEGER,"
                + COLUMN_DATA + " DATETIME,"
                + "FOREIGN KEY(" + COLUMN_ALUNO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_DISCIPLINA_ID + ") REFERENCES " + TABLE_DISCIPLINAS + "(" + COLUMN_ID + "))";

        db.execSQL(CREATE_USUARIOS_TABLE);
        db.execSQL(CREATE_DISCIPLINAS_TABLE);
        db.execSQL(CREATE_ALUNOS_DISCIPLINAS_TABLE);
        db.execSQL(CREATE_PRESENCAS_TABLE);

        // Inserir usuários de teste
        ContentValues values = new ContentValues();

        // Inserir coordenador
        values.put(COLUMN_EMAIL, "coordenador@email.com");
        values.put(COLUMN_SENHA, "123456");
        values.put(COLUMN_TIPO, "coordenador");
        db.insert(TABLE_USUARIOS, null, values);

        // Inserir professor
        values.clear();
        values.put(COLUMN_EMAIL, "professor@email.com");
        values.put(COLUMN_SENHA, "123456");
        values.put(COLUMN_TIPO, "professor");
        db.insert(TABLE_USUARIOS, null, values);

        // Inserir alunos
        values.clear();
        values.put(COLUMN_EMAIL, "aluno@email.com");
        values.put(COLUMN_SENHA, "123456");
        values.put(COLUMN_TIPO, "aluno");
        db.insert(TABLE_USUARIOS, null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENCAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNOS_DISCIPLINAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCIPLINAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public String verificarCredenciais(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_TIPO};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_SENHA + " = ?";
        String[] selectionArgs = {email, senha};

        Cursor cursor = db.query(TABLE_USUARIOS, columns, selection, selectionArgs, null, null, null);

        String tipoUsuario = null;
        if (cursor.moveToFirst()) {
            tipoUsuario = cursor.getString(cursor.getColumnIndex(COLUMN_TIPO));
        }

        cursor.close();
        return tipoUsuario;
    }

    // Métodos para gerenciar disciplinas
    public long criarDisciplina(String nome, String semestre, int professorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME_DISCIPLINA, nome);
        values.put(COLUMN_SEMESTRE, semestre);
        values.put(COLUMN_PROFESSOR_ID, professorId);
        return db.insert(TABLE_DISCIPLINAS, null, values);
    }

    // Método para adicionar aluno à disciplina
    public void adicionarAlunoNaDisciplina(int alunoId, int disciplinaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALUNO_ID, alunoId);
        values.put(COLUMN_DISCIPLINA_ID, disciplinaId);
        db.insert(TABLE_ALUNOS_DISCIPLINAS, null, values);
    }

    // Método para registrar presença
    public void registrarPresenca(int alunoId, int disciplinaId, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ALUNO_ID, alunoId);
        values.put(COLUMN_DISCIPLINA_ID, disciplinaId);
        values.put(COLUMN_DATA, data);
        db.insert(TABLE_PRESENCAS, null, values);
    }
} 