package com.example.frequenciaqr.ui.coordenador;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.adapter.DisciplinaSpinnerAdapter;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class GerenciarDisciplinasActivity extends BaseActivity {
    private DBHelper dbHelper;
    private TextInputEditText edtNomeDisciplina;
    private Spinner spinnerSemestre;
    private TextInputEditText edtEmailProfessor;
    private TextInputEditText edtEmailAluno;
    private Button btnCriarDisciplina;
    private Button btnAdicionarAluno;
    private Spinner spinnerDisciplinas;
    private DisciplinaSpinnerAdapter disciplinaAdapter;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Inicializar views
        edtNomeDisciplina = findViewById(R.id.edtNomeDisciplina);
        spinnerSemestre = findViewById(R.id.spinnerSemestre);
        edtEmailProfessor = findViewById(R.id.edtEmailProfessor);
        edtEmailAluno = findViewById(R.id.edtEmailAluno);
        btnCriarDisciplina = findViewById(R.id.btnCriarDisciplina);
        btnAdicionarAluno = findViewById(R.id.btnAdicionarAluno);
        spinnerDisciplinas = findViewById(R.id.spinnerDisciplinas);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Configurar spinners
        setupSemestreSpinner();
        setupDisciplinasSpinner();

        // Configurar listeners
        btnCriarDisciplina.setOnClickListener(v -> criarDisciplina());
        btnAdicionarAluno.setOnClickListener(v -> adicionarAluno());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_gerenciar_disciplinas;
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

    private void setupDisciplinasSpinner() {
        List<Disciplina> disciplinas = dbHelper.getAllDisciplinas();
        disciplinaAdapter = new DisciplinaSpinnerAdapter(this, disciplinas);
        spinnerDisciplinas.setAdapter(disciplinaAdapter);
    }

    private void atualizarDisciplinasSpinner() {
        List<Disciplina> disciplinas = dbHelper.getAllDisciplinas();
        disciplinaAdapter = new DisciplinaSpinnerAdapter(this, disciplinas);
        spinnerDisciplinas.setAdapter(disciplinaAdapter);
    }

    private void criarDisciplina() {
        String nomeDisciplina = edtNomeDisciplina.getText().toString().trim();
        String semestre = spinnerSemestre.getSelectedItem().toString();
        String emailProfessor = edtEmailProfessor.getText().toString().trim();

        if (nomeDisciplina.isEmpty() || emailProfessor.isEmpty()) {
            Toast.makeText(this, R.string.erro_campos_vazios, Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar se o professor existe
        if (!dbHelper.verificarUsuarioExiste(emailProfessor)) {
            Toast.makeText(this, "Professor não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar se o usuário é realmente um professor
        String tipoUsuario = dbHelper.getTipoUsuario(emailProfessor);
        if (!"professor".equals(tipoUsuario)) {
            Toast.makeText(this, "O email informado não pertence a um professor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar a disciplina
        long idDisciplina = dbHelper.criarDisciplina(nomeDisciplina, semestre, emailProfessor);
        if (idDisciplina != -1) {
            Toast.makeText(this, "Disciplina criada com sucesso", Toast.LENGTH_SHORT).show();
            limparCamposDisciplina();
            atualizarDisciplinasSpinner();
        } else {
            Toast.makeText(this, "Erro ao criar disciplina", Toast.LENGTH_SHORT).show();
        }
    }

    private void adicionarAluno() {
        String emailAluno = edtEmailAluno.getText().toString().trim();

        if (emailAluno.isEmpty()) {
            Toast.makeText(this, R.string.erro_campos_vazios, Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar se o aluno existe
        if (!dbHelper.verificarUsuarioExiste(emailAluno)) {
            Toast.makeText(this, "Aluno não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar se o usuário é realmente um aluno
        String tipoUsuario = dbHelper.getTipoUsuario(emailAluno);
        if (!"aluno".equals(tipoUsuario)) {
            Toast.makeText(this, "O email informado não pertence a um aluno", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obter a disciplina selecionada
        Disciplina disciplinaSelecionada = (Disciplina) spinnerDisciplinas.getSelectedItem();
        if (disciplinaSelecionada == null) {
            Toast.makeText(this, "Selecione uma disciplina", Toast.LENGTH_SHORT).show();
            return;
        }

        // Adicionar o aluno à disciplina
        if (dbHelper.adicionarAlunoADisciplina(disciplinaSelecionada.getId(), emailAluno)) {
            Toast.makeText(this, "Aluno adicionado com sucesso", Toast.LENGTH_SHORT).show();
            limparCamposAluno();
        } else {
            Toast.makeText(this, "Erro ao adicionar aluno", Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCamposDisciplina() {
        edtNomeDisciplina.setText("");
        edtEmailProfessor.setText("");
    }

    private void limparCamposAluno() {
        edtEmailAluno.setText("");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_disciplinas) {
            finish();
            return true;
        } else if (id == R.id.nav_gerenciar_disciplinas) {
            // Já estamos na tela de gerenciar disciplinas, apenas fechar o drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        
        return super.onNavigationItemSelected(item);
    }
} 