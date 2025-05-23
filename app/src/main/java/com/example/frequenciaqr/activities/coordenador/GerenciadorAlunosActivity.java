package com.example.frequenciaqr.activities.coordenador;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class GerenciadorAlunosActivity extends AppCompatActivity {

    private Spinner spinnerDisciplinas;
    private ListView listViewAlunos;
    private Button btnSalvar;

    private DBHelper dbHelper;
    private HashMap<String, Integer> mapDisciplinaId;
    private HashMap<String, Integer> mapAlunoId;
    private ArrayList<String> nomesAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_alunos);

        spinnerDisciplinas = findViewById(R.id.spinnerDisciplinas);
        listViewAlunos = findViewById(R.id.listViewAlunos);
        btnSalvar = findViewById(R.id.btnSalvar);

        dbHelper = new DBHelper(this);
        carregarDisciplinas();
        carregarAlunos();

        btnSalvar.setOnClickListener(v -> salvarAssociacoes());
    }

    private void carregarDisciplinas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nome FROM disciplinas", null);

        ArrayList<String> nomesDisciplinas = new ArrayList<>();
        mapDisciplinaId = new HashMap<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nome = cursor.getString(1);
            nomesDisciplinas.add(nome);
            mapDisciplinaId.put(nome, id);
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesDisciplinas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplinas.setAdapter(adapter);
    }

    private void carregarAlunos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nome FROM usuarios WHERE tipo = 'aluno'", null);

        nomesAlunos = new ArrayList<>();
        mapAlunoId = new HashMap<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nome = cursor.getString(1);
            nomesAlunos.add(nome);
            mapAlunoId.put(nome, id);
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, nomesAlunos);
        listViewAlunos.setAdapter(adapter);
    }

    private void salvarAssociacoes() {
        String nomeDisciplina = (String) spinnerDisciplinas.getSelectedItem();
        int idDisciplina = mapDisciplinaId.get(nomeDisciplina);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Remove vínculos anteriores
        db.delete("disciplinas_alunos", "id_disciplina = ?", new String[]{String.valueOf(idDisciplina)});

        // Insere os novos selecionados
        for (int i = 0; i < listViewAlunos.getCount(); i++) {
            if (listViewAlunos.isItemChecked(i)) {
                String nomeAluno = nomesAlunos.get(i);
                int idAluno = mapAlunoId.get(nomeAluno);

                ContentValues values = new ContentValues();
                values.put("id_disciplina", idDisciplina);
                values.put("id_aluno", idAluno);
                db.insert("disciplinas_alunos", null, values);
            }
        }

        Toast.makeText(this, "Alunos atualizados para a disciplina!", Toast.LENGTH_SHORT).show();
    }
}