package com.example.frequenciaqr.ui.coordenador;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;

public class CoordenadorActivity extends BaseActivity {
    private DBHelper dbHelper;
    private TextInputEditText edtNomeDisciplina;
    private Spinner spinnerSemestre;
    private TextInputEditText edtEmailProfessor;
    private TextInputEditText edtEmailAluno;
    private Button btnCriarDisciplina;
    private Button btnAdicionarAluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);

        // Inicializar views
        edtNomeDisciplina = findViewById(R.id.edtNomeDisciplina);
        spinnerSemestre = findViewById(R.id.spinnerSemestre);
        edtEmailProfessor = findViewById(R.id.edtEmailProfessor);
        edtEmailAluno = findViewById(R.id.edtEmailAluno);
        btnCriarDisciplina = findViewById(R.id.btnCriarDisciplina);
        btnAdicionarAluno = findViewById(R.id.btnAdicionarAluno);

        // Configurar spinner de semestres
        setupSemestreSpinner();

        // Configurar listeners
        btnCriarDisciplina.setOnClickListener(v -> criarDisciplina());
        btnAdicionarAluno.setOnClickListener(v -> adicionarAluno());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_coordenador;
    }

    private void setupSemestreSpinner() {
        ArrayList<String> semestres = new ArrayList<>();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        
        // Adiciona os próximos 4 anos com seus respectivos semestres
        for (int i = 0; i < 4; i++) {
            semestres.add((anoAtual + i) + ".1");
            semestres.add((anoAtual + i) + ".2");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            semestres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemestre.setAdapter(adapter);
    }

    private void criarDisciplina() {
        String nomeDisciplina = edtNomeDisciplina.getText().toString().trim();
        String semestre = spinnerSemestre.getSelectedItem().toString();
        String emailProfessor = edtEmailProfessor.getText().toString().trim();

        if (nomeDisciplina.isEmpty() || emailProfessor.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implementar a lógica para criar disciplina no banco de dados
        // Aqui você deve:
        // 1. Verificar se o professor existe no banco
        // 2. Criar a disciplina
        // 3. Associar o professor à disciplina

        Toast.makeText(this, "Disciplina criada com sucesso", Toast.LENGTH_SHORT).show();
        limparCamposDisciplina();
    }

    private void adicionarAluno() {
        String emailAluno = edtEmailAluno.getText().toString().trim();

        if (emailAluno.isEmpty()) {
            Toast.makeText(this, "Digite o email do aluno", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implementar a lógica para adicionar aluno à disciplina
        // Aqui você deve:
        // 1. Verificar se o aluno existe no banco
        // 2. Verificar se a disciplina existe
        // 3. Adicionar o aluno à disciplina

        Toast.makeText(this, "Aluno adicionado com sucesso", Toast.LENGTH_SHORT).show();
        limparCamposAluno();
    }

    private void limparCamposDisciplina() {
        edtNomeDisciplina.setText("");
        edtEmailProfessor.setText("");
    }

    private void limparCamposAluno() {
        edtEmailAluno.setText("");
    }
} 