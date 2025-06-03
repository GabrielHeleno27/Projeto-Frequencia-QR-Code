package com.example.frequenciaqr.ui.aluno;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.model.Disciplina;
import android.widget.TextView;

public class DetalhesFrequenciaActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private TextView txtNomeDisciplina;
    private TextView txtProfessor;
    private TextView txtSemestre;
    private TextView txtTotalPresencas;
    private TextView txtPorcentagemPresenca;
    private RecyclerView recyclerViewPresencas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_frequencia);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalhes da Frequência");
        }

        // Inicializar views
        txtNomeDisciplina = findViewById(R.id.txtNomeDisciplina);
        txtProfessor = findViewById(R.id.txtProfessor);
        txtSemestre = findViewById(R.id.txtSemestre);
        txtTotalPresencas = findViewById(R.id.txtTotalPresencas);
        txtPorcentagemPresenca = findViewById(R.id.txtPorcentagemPresenca);
        recyclerViewPresencas = findViewById(R.id.recyclerViewPresencas);

        dbHelper = new DBHelper(this);

        // Recuperar dados da disciplina
        int disciplinaId = getIntent().getIntExtra("disciplina_id", -1);
        String emailAluno = getSharedPreferences("FrequenciaQR", MODE_PRIVATE)
                .getString("email_usuario", "");

        if (disciplinaId != -1 && !emailAluno.isEmpty()) {
            carregarDetalhesDisciplina(disciplinaId, emailAluno);
        }
    }

    private void carregarDetalhesDisciplina(int disciplinaId, String emailAluno) {
        Disciplina disciplina = dbHelper.getDisciplinaById(disciplinaId);
        if (disciplina != null) {
            txtNomeDisciplina.setText(disciplina.getNome());
            txtProfessor.setText("Professor: " + disciplina.getProfessor());
            txtSemestre.setText("Semestre: " + disciplina.getSemestre());
            
            int totalPresencas = disciplina.getPresencas();
            txtTotalPresencas.setText("Total de Presenças: " + totalPresencas);
            
            // TODO: Implementar cálculo de porcentagem quando tivermos o total de aulas
            txtPorcentagemPresenca.setText("Porcentagem de Presença: Em breve");
            
            // TODO: Implementar adapter para lista de presenças
            recyclerViewPresencas.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 