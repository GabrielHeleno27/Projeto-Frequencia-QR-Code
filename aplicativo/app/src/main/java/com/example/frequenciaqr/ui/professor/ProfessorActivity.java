package com.example.frequenciaqr.ui.professor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.adapter.DisciplinasAdapter;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.example.frequenciaqr.ui.fragments.QRCodeDialogFragment;
import com.example.frequenciaqr.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.util.Log;
import org.json.JSONObject;

public class ProfessorActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ProfessorActivity";
    private DBHelper dbHelper;
    private RecyclerView recyclerViewDisciplinas;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DisciplinasAdapter disciplinasAdapter;
    private TextView txtSemDisciplinas;
    private FloatingActionButton fabGerarQR;
    private TextView txtTempoRestante;
    private CountDownTimer qrCodeTimer;
    private static final long QR_CODE_DURATION = 5 * 60 * 1000; // 5 minutos em milissegundos
    private String currentCodigoAula;
    private com.example.frequenciaqr.local.PresencaLocal presencaLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);
        presencaLocal = new com.example.frequenciaqr.local.PresencaLocal(this);

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

        // Inicializar views
        fabGerarQR = findViewById(R.id.fabGerarQR);
        txtTempoRestante = findViewById(R.id.txtTempoRestante);

        // Configurar FAB
        fabGerarQR.setOnClickListener(v -> {
            if (disciplinasAdapter.getItemCount() > 0) {
                Disciplina disciplina = disciplinasAdapter.getDisciplina(0);
                mostrarDetalhesDisciplina(disciplina);
            } else {
                Toast.makeText(this, "Não há disciplinas cadastradas", Toast.LENGTH_SHORT).show();
            }
        });

        // Carregar disciplinas
        carregarDisciplinas();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_professor;
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDisciplinas();
    }

    private void carregarDisciplinas() {
        // Aqui você deve implementar a lógica para carregar apenas as disciplinas do professor logado
        String emailProfessor = getSharedPreferences("FrequenciaQR", MODE_PRIVATE)
                .getString("email_usuario", "");
        
        List<Disciplina> disciplinas = dbHelper.getDisciplinasByProfessor(emailProfessor);
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
        // Implementar a lógica para gerar QR Code da disciplina
        Intent intent = new Intent(this, GerarQRCodeActivity.class);
        intent.putExtra("disciplina_id", disciplina.getId());
        intent.putExtra("disciplina_nome", disciplina.getNome());
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_disciplinas) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_gerar_qr) {
            if (disciplinasAdapter.getItemCount() > 0) {
                Disciplina disciplina = disciplinasAdapter.getDisciplina(0);
                mostrarDetalhesDisciplina(disciplina);
            } else {
                Toast.makeText(this, "Não há disciplinas cadastradas", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_sair) {
            // Limpar dados do usuário
            getSharedPreferences("FrequenciaQR", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
            
            // Voltar para a tela de login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void gerarQRCode() {
        try {
            // Criar JSON com informações da aula
            JSONObject aulaInfo = new JSONObject();
            aulaInfo.put("codigo", UUID.randomUUID().toString());
            aulaInfo.put("timestamp", System.currentTimeMillis());
            aulaInfo.put("disciplina_id", disciplinasAdapter.getDisciplina(0).getId());
            aulaInfo.put("disciplina_nome", disciplinasAdapter.getDisciplina(0).getNome());
            aulaInfo.put("professor_email", getSharedPreferences("FrequenciaQR", MODE_PRIVATE).getString("email_usuario", ""));
            
            // Registrar código no modo local
            presencaLocal.registrarCodigoAula(aulaInfo.toString());
            
            // Criar QR Code com as informações da aula
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(aulaInfo.toString(), BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Mostrar QR Code em um DialogFragment
            QRCodeDialogFragment dialogFragment = QRCodeDialogFragment.newInstance(bitmap);
            dialogFragment.show(getSupportFragmentManager(), "qrcode");

            // Iniciar timer
            iniciarTimer();
            
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao gerar QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarTimer() {
        if (qrCodeTimer != null) {
            qrCodeTimer.cancel();
        }

        txtTempoRestante.setVisibility(View.VISIBLE);

        qrCodeTimer = new CountDownTimer(QR_CODE_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutos = millisUntilFinished / 60000;
                long segundos = (millisUntilFinished % 60000) / 1000;
                txtTempoRestante.setText(String.format("Tempo restante: %02d:%02d", minutos, segundos));
            }

            @Override
            public void onFinish() {
                txtTempoRestante.setVisibility(View.GONE);
                presencaLocal.limparCodigo();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (qrCodeTimer != null) {
            qrCodeTimer.cancel();
        }
        presencaLocal.limparCodigo();
    }
} 