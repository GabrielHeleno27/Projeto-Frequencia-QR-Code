package com.example.frequenciaqr.ui.aluno;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frequenciaqr.R;
import com.example.frequenciaqr.adapter.DisciplinasAlunoAdapter;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.example.frequenciaqr.websocket.PresencaClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class AlunoDisciplinasActivity extends BaseActivity {
    private DBHelper dbHelper;
    private RecyclerView recyclerViewDisciplinas;
    private TextView txtSemDisciplinas;
    private DisciplinasAlunoAdapter disciplinasAdapter;
    private FloatingActionButton fabLerQR;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private PresencaClient presencaClient;

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
        new ScanContract(),
        result -> {
            if (result.getContents() != null) {
                processarQRCode(result.getContents());
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Disciplinas");
        }

        dbHelper = new DBHelper(this);

        // Inicializar views
        recyclerViewDisciplinas = findViewById(R.id.recyclerViewDisciplinas);
        txtSemDisciplinas = findViewById(R.id.txtSemDisciplinas);
        fabLerQR = findViewById(R.id.fabLerQR);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Configurar menu lateral
        if (drawerLayout != null && navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Configurar RecyclerView
        recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(this));
        disciplinasAdapter = new DisciplinasAlunoAdapter(new ArrayList<>());
        recyclerViewDisciplinas.setAdapter(disciplinasAdapter);

        // Configurar FAB
        fabLerQR.setOnClickListener(v -> iniciarLeituraQR());

        // Carregar disciplinas
        carregarDisciplinas();
    }

    private void iniciarLeituraQR() {
        ScanOptions options = new ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            .setPrompt("Posicione o QR Code no centro da tela")
            .setCameraId(0)
            .setBeepEnabled(false)
            .setBarcodeImageEnabled(true)
            .setOrientationLocked(false);

        qrLauncher.launch(options);
    }

    private void processarQRCode(String qrContent) {
        try {
            // Formato esperado: codigoAula|ip|porta
            String[] parts = qrContent.split("\\|");
            if (parts.length != 3) {
                Toast.makeText(this, "QR Code inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            String codigoAula = parts[0];
            String serverIp = parts[1];
            int serverPort = Integer.parseInt(parts[2]);

            // Criar URL do WebSocket
            String wsUrl = String.format("ws://%s:%d", serverIp, serverPort);
            
            // Obter email do aluno
            String emailAluno = getSharedPreferences("FrequenciaQR", MODE_PRIVATE)
                    .getString("email_usuario", "");

            // Conectar ao servidor WebSocket
            if (presencaClient != null) {
                presencaClient.close();
            }
            
            presencaClient = new PresencaClient(wsUrl, emailAluno, codigoAula, new PresencaClient.PresencaListener() {
                @Override
                public void onPresencaConfirmada() {
                    runOnUiThread(() -> {
                        Toast.makeText(AlunoDisciplinasActivity.this, 
                            "Presença registrada com sucesso!", Toast.LENGTH_SHORT).show();
                        carregarDisciplinas();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(AlunoDisciplinasActivity.this, 
                            "Erro: " + message, Toast.LENGTH_SHORT).show();
                    });
                }
            });
            presencaClient.connect();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar QR Code: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDisciplinas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presencaClient != null) {
            presencaClient.close();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_aluno_disciplinas;
    }

    private void carregarDisciplinas() {
        String emailAluno = getSharedPreferences("FrequenciaQR", MODE_PRIVATE)
                .getString("email_usuario", "");
        
        List<Disciplina> disciplinas = dbHelper.getDisciplinasByAluno(emailAluno);
        
        // Log para debug
        if (disciplinas.isEmpty()) {
            Log.d("AlunoDisciplinasActivity", "Nenhuma disciplina encontrada para o aluno: " + emailAluno);
        } else {
            Log.d("AlunoDisciplinasActivity", "Encontradas " + disciplinas.size() + " disciplinas para o aluno: " + emailAluno);
            for (Disciplina d : disciplinas) {
                Log.d("AlunoDisciplinasActivity", "Disciplina: " + d.getNome() + ", Professor: " + d.getProfessor() + ", Presenças: " + d.getPresencas());
            }
        }
        
        disciplinasAdapter.atualizarDisciplinas(disciplinas);
        
        if (disciplinas.isEmpty()) {
            txtSemDisciplinas.setVisibility(View.VISIBLE);
            recyclerViewDisciplinas.setVisibility(View.GONE);
        } else {
            txtSemDisciplinas.setVisibility(View.GONE);
            recyclerViewDisciplinas.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_disciplinas) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
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