package com.example.frequenciaqr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "frequenciaqr.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tabela de usuários
        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "senha TEXT NOT NULL," +
                "tipo TEXT NOT NULL" + // coordenador, professor, aluno
                ");");

        // 2. Tabela de disciplinas
        db.execSQL("CREATE TABLE IF NOT EXISTS disciplinas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL" +
                ");");

        // 3. Relacionamento disciplinas-professores
        db.execSQL("CREATE TABLE IF NOT EXISTS disciplinas_professores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_disciplina INTEGER NOT NULL," +
                "id_professor INTEGER NOT NULL," +
                "FOREIGN KEY(id_disciplina) REFERENCES disciplinas(id)," +
                "FOREIGN KEY(id_professor) REFERENCES usuarios(id)" +
                ");");

        // 4. Relacionamento disciplinas-alunos
        db.execSQL("CREATE TABLE IF NOT EXISTS disciplinas_alunos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_disciplina INTEGER NOT NULL," +
                "id_aluno INTEGER NOT NULL," +
                "FOREIGN KEY(id_disciplina) REFERENCES disciplinas(id)," +
                "FOREIGN KEY(id_aluno) REFERENCES usuarios(id)" +
                ");");

        // 5. Tabela de chamadas
        db.execSQL("CREATE TABLE IF NOT EXISTS chamadas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_disciplina INTEGER NOT NULL," +
                "data TEXT NOT NULL," +
                "turno TEXT NOT NULL," + // A, B, C, D, E
                "codigo_qr TEXT NOT NULL," +
                "timestamp_gerado INTEGER NOT NULL," +
                "FOREIGN KEY(id_disciplina) REFERENCES disciplinas(id)" +
                ");");

        // 6. Tabela de presenças
        db.execSQL("CREATE TABLE IF NOT EXISTS presencas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_chamada INTEGER NOT NULL," +
                "id_aluno INTEGER NOT NULL," +
                "data_registro TEXT NOT NULL," +
                "FOREIGN KEY(id_chamada) REFERENCES chamadas(id)," +
                "FOREIGN KEY(id_aluno) REFERENCES usuarios(id)" +
                ");");

        // Exemplo: inserir coordenador padrão
        db.execSQL("INSERT INTO usuarios (nome, email, senha, tipo) VALUES " +
                "('Coordenador', 'coordenador@teste.com', '123456', 'coordenador');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Recriar todas as tabelas se houver upgrade de versão
        db.execSQL("DROP TABLE IF EXISTS presencas;");
        db.execSQL("DROP TABLE IF EXISTS chamadas;");
        db.execSQL("DROP TABLE IF EXISTS disciplinas_alunos;");
        db.execSQL("DROP TABLE IF EXISTS disciplinas_professores;");
        db.execSQL("DROP TABLE IF EXISTS disciplinas;");
        db.execSQL("DROP TABLE IF EXISTS usuarios;");
        onCreate(db);
    }
}