package com.example.frequenciaqr.activities.coordenador;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class CadastroDisciplinaActivity extends AppCompatActivity {

    private EditText edtNomeDisciplina;
    private Spinner spinnerProfessores;
    private Button btnSalvar;

    private DBHelper dbHelper;
    private HashMap<String, Integer> mapNomeIdProfessor; // nome -> id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_disciplina);

        edtNomeDisciplina = findViewById(R.id.edtNomeDisciplina);
        spinnerProfessores = findViewById(R.id.spinnerProfessores);
        btnSalvar = findViewById(R.id.btnSalvar);
        dbHelper = new DBHelper(this);

        carregarProfessores();

        btnSalvar.setOnClickListener(v -> salvarDisciplina());
    }

    private void carregarProfessores() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, nome FROM usuarios WHERE tipo = ?", new String[]{"professor"});

        ArrayList<String> nomes = new ArrayList<>();
        mapNomeIdProfessor = new HashMap<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nome = cursor.getString(1);
                nomes.add(nome);
                mapNomeIdProfessor.put(nome, id);
            } while (cursor.moveToNext());
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProfessores.setAdapter(adapter);
    }

    private void salvarDisciplina() {
        String nomeDisciplina = edtNomeDisciplina.getText().toString().trim();
        String nomeProfessor = (String) spinnerProfessores.getSelectedItem();

        if (nomeDisciplina.isEmpty() || nomeProfessor == null) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idProfessor = mapNomeIdProfessor.get(nomeProfessor);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues valuesDisc = new ContentValues();
            valuesDisc.put("nome", nomeDisciplina);
            long idDisciplina = db.insert("disciplinas", null, valuesDisc);

            ContentValues valuesRel = new ContentValues();
            valuesRel.put("id_disciplina", idDisciplina);
            valuesRel.put("id_professor", idProfessor);
            db.insert("disciplinas_professores", null, valuesRel);

            db.setTransactionSuccessful();
            Toast.makeText(this, "Disciplina cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
            edtNomeDisciplina.setText("");

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }
}
