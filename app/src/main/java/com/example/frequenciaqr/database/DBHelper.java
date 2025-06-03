package com.example.frequenciaqr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FrequenciaQR.db";
    private static final int DATABASE_VERSION = 5;

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
    private static final String COLUMN_NOME = "nome";

    // Colunas específicas
    private static final String COLUMN_NOME_DISCIPLINA = "nome";
    private static final String COLUMN_SEMESTRE = "semestre";
    private static final String COLUMN_EMAIL_PROFESSOR = "email_professor";
    private static final String COLUMN_ID_DISCIPLINA = "id_disciplina";
    private static final String COLUMN_EMAIL_ALUNO = "email_aluno";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar tabela de usuários
        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY,"
                + COLUMN_SENHA + " TEXT NOT NULL,"
                + COLUMN_TIPO + " TEXT NOT NULL,"
                + COLUMN_NOME + " TEXT"
                + ")";

        // Criar tabela de disciplinas
        String CREATE_DISCIPLINAS_TABLE = "CREATE TABLE " + TABLE_DISCIPLINAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOME_DISCIPLINA + " TEXT NOT NULL,"
                + COLUMN_SEMESTRE + " TEXT NOT NULL,"
                + COLUMN_EMAIL_PROFESSOR + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + COLUMN_EMAIL_PROFESSOR + ") REFERENCES " 
                + TABLE_USUARIOS + "(" + COLUMN_EMAIL + ")"
                + ")";

        // Criar tabela de relação alunos-disciplinas
        String CREATE_ALUNOS_DISCIPLINAS_TABLE = "CREATE TABLE " + TABLE_ALUNOS_DISCIPLINAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ID_DISCIPLINA + " INTEGER,"
                + COLUMN_EMAIL_ALUNO + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_ID_DISCIPLINA + ") REFERENCES " 
                + TABLE_DISCIPLINAS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_EMAIL_ALUNO + ") REFERENCES " 
                + TABLE_USUARIOS + "(" + COLUMN_EMAIL + ")"
                + ")";

        // Criar tabela de presenças
        String CREATE_PRESENCAS_TABLE = "CREATE TABLE " + TABLE_PRESENCAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ID_DISCIPLINA + " INTEGER,"
                + COLUMN_EMAIL_ALUNO + " TEXT,"
                + "data_presenca DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_ID_DISCIPLINA + ") REFERENCES " 
                + TABLE_DISCIPLINAS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_EMAIL_ALUNO + ") REFERENCES " 
                + TABLE_USUARIOS + "(" + COLUMN_EMAIL + ")"
                + ")";

        db.execSQL(CREATE_USUARIOS_TABLE);
        db.execSQL(CREATE_DISCIPLINAS_TABLE);
        db.execSQL(CREATE_ALUNOS_DISCIPLINAS_TABLE);
        db.execSQL(CREATE_PRESENCAS_TABLE);

        // Inserir usuário coordenador padrão
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, "coordenador@email.com");
        values.put(COLUMN_SENHA, "123456");
        values.put(COLUMN_TIPO, "coordenador");
        values.put(COLUMN_NOME, "Coordenador");
        long id = db.insert(TABLE_USUARIOS, null, values);
        Log.d("DBHelper", "Coordenador criado com ID: " + id);

        // Inserir usuário professor padrão
        values = new ContentValues();
        values.put(COLUMN_EMAIL, "professor@email.com");
        values.put(COLUMN_SENHA, "123456");
        values.put(COLUMN_TIPO, "professor");
        values.put(COLUMN_NOME, "Professor");
        id = db.insert(TABLE_USUARIOS, null, values);
        Log.d("DBHelper", "Professor criado com ID: " + id);

        // Inserir usuário aluno padrão
        values = new ContentValues();
        values.put(COLUMN_EMAIL, "aluno@email.com");
        values.put(COLUMN_SENHA, "123456");
        values.put(COLUMN_TIPO, "aluno");
        values.put(COLUMN_NOME, "Aluno");
        id = db.insert(TABLE_USUARIOS, null, values);
        Log.d("DBHelper", "Aluno criado com ID: " + id);

        // Criar disciplina de teste
        values = new ContentValues();
        values.put(COLUMN_NOME_DISCIPLINA, "Programação Mobile");
        values.put(COLUMN_SEMESTRE, "2024.1");
        values.put(COLUMN_EMAIL_PROFESSOR, "professor@email.com");
        long disciplinaId = db.insert(TABLE_DISCIPLINAS, null, values);
        Log.d("DBHelper", "Disciplina criada com ID: " + disciplinaId);

        // Vincular aluno à disciplina
        if (disciplinaId != -1) {
            values = new ContentValues();
            values.put(COLUMN_ID_DISCIPLINA, disciplinaId);
            values.put(COLUMN_EMAIL_ALUNO, "aluno@email.com");
            long vinculoId = db.insert(TABLE_ALUNOS_DISCIPLINAS, null, values);
            Log.d("DBHelper", "Vínculo aluno-disciplina criado com ID: " + vinculoId);

            // Adicionar algumas presenças de teste
            values = new ContentValues();
            values.put(COLUMN_ID_DISCIPLINA, disciplinaId);
            values.put(COLUMN_EMAIL_ALUNO, "aluno@email.com");
            db.insert(TABLE_PRESENCAS, null, values);
            db.insert(TABLE_PRESENCAS, null, values);
            Log.d("DBHelper", "Presenças de teste adicionadas");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dropar todas as tabelas existentes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNOS_DISCIPLINAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCIPLINAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        
        // Recriar todas as tabelas
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Em caso de downgrade, vamos recriar o banco de dados
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNOS_DISCIPLINAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCIPLINAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public boolean verificarUsuarioExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COLUMN_EMAIL},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    public String getTipoUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COLUMN_TIPO},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        String tipo = null;
        if (cursor.moveToFirst()) {
            tipo = cursor.getString(cursor.getColumnIndex(COLUMN_TIPO));
        }
        cursor.close();
        return tipo;
    }

    public long criarDisciplina(String nome, String semestre, String emailProfessor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME_DISCIPLINA, nome);
        values.put(COLUMN_SEMESTRE, semestre);
        values.put(COLUMN_EMAIL_PROFESSOR, emailProfessor);

        long id = -1;
        try {
            id = db.insertOrThrow(TABLE_DISCIPLINAS, null, values);
        } catch (Exception e) {
            Log.e("DBHelper", "Erro ao criar disciplina: " + e.getMessage());
        }
        return id;
    }

    public boolean adicionarAlunoADisciplina(int idDisciplina, String emailAluno) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_DISCIPLINA, idDisciplina);
        values.put(COLUMN_EMAIL_ALUNO, emailAluno);

        long id = -1;
        try {
            id = db.insertOrThrow(TABLE_ALUNOS_DISCIPLINAS, null, values);
        } catch (Exception e) {
            Log.e("DBHelper", "Erro ao adicionar aluno à disciplina: " + e.getMessage());
        }
        return id != -1;
    }

    public List<Disciplina> getAllDisciplinas() {
        List<Disciplina> disciplinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
            COLUMN_ID,
            COLUMN_NOME_DISCIPLINA,
            COLUMN_SEMESTRE,
            COLUMN_EMAIL_PROFESSOR
        };

        Cursor cursor = db.query(TABLE_DISCIPLINAS, columns, null, null, null, null, 
            COLUMN_SEMESTRE + " DESC, " + COLUMN_NOME_DISCIPLINA + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Disciplina disciplina = new Disciplina(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOME_DISCIPLINA)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SEMESTRE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_PROFESSOR))
                );
                disciplinas.add(disciplina);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return disciplinas;
    }

    public List<String> getAlunosDaDisciplina(int idDisciplina) {
        List<String> alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_EMAIL_ALUNO + " FROM " + TABLE_ALUNOS_DISCIPLINAS +
                " WHERE " + COLUMN_ID_DISCIPLINA + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idDisciplina)});

        if (cursor.moveToFirst()) {
            do {
                alunos.add(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_ALUNO)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return alunos;
    }

    public boolean verificarCredenciais(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_EMAIL};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_SENHA + " = ?";
        String[] selectionArgs = {email, senha};
        
        Cursor cursor = db.query(TABLE_USUARIOS, columns, selection, selectionArgs, null, null, null);
        boolean existe = cursor.moveToFirst();
        cursor.close();
        
        return existe;
    }

    public Usuario getProfessorByDisciplinaId(int disciplinaId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u." + COLUMN_EMAIL + ", u." + COLUMN_NOME + ", u." + COLUMN_TIPO +
                      " FROM " + TABLE_USUARIOS + " u " +
                      "INNER JOIN " + TABLE_DISCIPLINAS + " d ON d." + COLUMN_EMAIL_PROFESSOR + " = u." + COLUMN_EMAIL +
                      " WHERE d." + COLUMN_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(disciplinaId)});
        
        if (cursor.moveToFirst()) {
            Usuario professor = new Usuario();
            professor.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            professor.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)));
            professor.setTipo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)));
            cursor.close();
            return professor;
        }
        
        cursor.close();
        return null;
    }

    public List<Usuario> getAlunosByDisciplinaId(int disciplinaId) {
        List<Usuario> alunos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT u." + COLUMN_EMAIL + ", u." + COLUMN_NOME + ", u." + COLUMN_TIPO +
                      " FROM " + TABLE_USUARIOS + " u " +
                      "INNER JOIN " + TABLE_ALUNOS_DISCIPLINAS + " ad ON ad." + COLUMN_EMAIL_ALUNO + " = u." + COLUMN_EMAIL +
                      " WHERE ad." + COLUMN_ID_DISCIPLINA + " = ? AND u." + COLUMN_TIPO + " = 'aluno'";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(disciplinaId)});
        
        while (cursor.moveToNext()) {
            Usuario aluno = new Usuario();
            aluno.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            aluno.setNome(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)));
            aluno.setTipo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)));
            alunos.add(aluno);
        }
        
        cursor.close();
        return alunos;
    }

    public List<Disciplina> getDisciplinasByProfessor(String emailProfessor) {
        List<Disciplina> disciplinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
            COLUMN_ID,
            COLUMN_NOME_DISCIPLINA,
            COLUMN_SEMESTRE,
            COLUMN_EMAIL_PROFESSOR
        };

        String selection = COLUMN_EMAIL_PROFESSOR + " = ?";
        String[] selectionArgs = {emailProfessor};
        String orderBy = COLUMN_SEMESTRE + " DESC, " + COLUMN_NOME_DISCIPLINA + " ASC";

        Cursor cursor = db.query(TABLE_DISCIPLINAS, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                Disciplina disciplina = new Disciplina(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME_DISCIPLINA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PROFESSOR))
                );
                disciplinas.add(disciplina);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return disciplinas;
    }

    public List<Disciplina> getDisciplinasProfessor(String emailProfessor) {
        List<Disciplina> disciplinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
            COLUMN_ID,
            COLUMN_NOME_DISCIPLINA,
            COLUMN_SEMESTRE,
            COLUMN_EMAIL_PROFESSOR
        };

        String selection = COLUMN_EMAIL_PROFESSOR + " = ?";
        String[] selectionArgs = {emailProfessor};

        Cursor cursor = db.query(
            TABLE_DISCIPLINAS,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            COLUMN_NOME_DISCIPLINA + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String nome = cursor.getString(cursor.getColumnIndex(COLUMN_NOME_DISCIPLINA));
                String semestre = cursor.getString(cursor.getColumnIndex(COLUMN_SEMESTRE));
                String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_PROFESSOR));

                Disciplina disciplina = new Disciplina(id, nome, semestre, email);
                disciplinas.add(disciplina);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return disciplinas;
    }

    public List<Disciplina> getDisciplinasByAluno(String emailAluno) {
        List<Disciplina> disciplinas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT d.*, " +
                      "(SELECT COUNT(*) FROM " + TABLE_PRESENCAS + " p " +
                      "WHERE p.id_disciplina = d.id AND p.email_aluno = ?) as presencas " +
                      "FROM " + TABLE_DISCIPLINAS + " d " +
                      "INNER JOIN " + TABLE_ALUNOS_DISCIPLINAS + " ad ON d.id = ad.id_disciplina " +
                      "WHERE ad.email_aluno = ? " +
                      "ORDER BY d.semestre DESC, d.nome ASC";

        Cursor cursor = db.rawQuery(query, new String[]{emailAluno, emailAluno});

        if (cursor.moveToFirst()) {
            do {
                Disciplina disciplina = new Disciplina(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME_DISCIPLINA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PROFESSOR)),
                    cursor.getInt(cursor.getColumnIndexOrThrow("presencas"))
                );
                disciplinas.add(disciplina);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return disciplinas;
    }

    public Disciplina getDisciplinaById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT d.*, " +
                      "(SELECT COUNT(*) FROM " + TABLE_PRESENCAS + " p " +
                      "WHERE p.id_disciplina = d.id) as presencas " +
                      "FROM " + TABLE_DISCIPLINAS + " d " +
                      "WHERE d.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Disciplina disciplina = null;
        if (cursor.moveToFirst()) {
            disciplina = new Disciplina(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME_DISCIPLINA)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_PROFESSOR)),
                cursor.getInt(cursor.getColumnIndexOrThrow("presencas"))
            );
        }
        cursor.close();
        return disciplina;
    }
} 