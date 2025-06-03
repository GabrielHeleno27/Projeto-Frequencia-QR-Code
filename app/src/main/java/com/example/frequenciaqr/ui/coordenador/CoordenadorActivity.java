package com.example.frequenciaqr.ui.coordenador;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.adapter.AlunosAdapter;
import com.example.frequenciaqr.adapter.DisciplinasAdapter;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.model.Usuario;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class CoordenadorActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DBHelper dbHelper;
    private RecyclerView recyclerViewDisciplinas;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DisciplinasAdapter disciplinasAdapter;
    private TextView txtSemDisciplinas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);

        // Configurar DrawerLayout e NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        txtSemDisciplinas = findViewById(R.id.txtSemDisciplinas);
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Configurar RecyclerView
        recyclerViewDisciplinas = findViewById(R.id.recyclerViewDisciplinas);
        recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(this));
        disciplinasAdapter = new DisciplinasAdapter(new ArrayList<>(), disciplina -> mostrarDetalhesDisciplina(disciplina));
        recyclerViewDisciplinas.setAdapter(disciplinasAdapter);

        // Carregar disciplinas
        carregarDisciplinas();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_coordenador;
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDisciplinas();
    }

    private void carregarDisciplinas() {
        List<Disciplina> disciplinas = dbHelper.getAllDisciplinas();
        disciplinasAdapter.atualizarDisciplinas(disciplinas);
        
        if (disciplinas.isEmpty()) {
            txtSemDisciplinas.setVisibility(View.VISIBLE);
            recyclerViewDisciplinas.setVisibility(View.GONE);
        } else {
            txtSemDisciplinas.setVisibility(View.GONE);
            recyclerViewDisciplinas.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDetalhesDisciplina(Disciplina disciplina) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_detalhes_disciplina);

        // Configurar o tamanho do dialog para ocupar quase toda a largura da tela
        android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setAttributes(params);

        TextView txtNomeDisciplina = dialog.findViewById(R.id.txtNomeDisciplina);
        TextView txtSemestre = dialog.findViewById(R.id.txtSemestre);
        TextView txtProfessor = dialog.findViewById(R.id.txtProfessor);
        RecyclerView recyclerViewAlunos = dialog.findViewById(R.id.recyclerViewAlunos);

        txtNomeDisciplina.setText(disciplina.getNome());
        txtSemestre.setText(disciplina.getSemestre());

        // Buscar professor responsável
        Usuario professor = dbHelper.getProfessorByDisciplinaId(disciplina.getId());
        if (professor != null) {
            txtProfessor.setText(professor.getNome());
        }

        // Configurar lista de alunos
        recyclerViewAlunos.setLayoutManager(new LinearLayoutManager(this));
        List<Usuario> alunos = dbHelper.getAlunosByDisciplinaId(disciplina.getId());
        AlunosAdapter alunosAdapter = new AlunosAdapter(alunos);
        recyclerViewAlunos.setAdapter(alunosAdapter);

        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_disciplinas) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_gerenciar_disciplinas) {
            startActivity(new Intent(this, GerenciarDisciplinasActivity.class));
        }

        return super.onNavigationItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 