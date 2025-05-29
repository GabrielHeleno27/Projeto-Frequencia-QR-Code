package com.example.frequenciaqr.ui.professor;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frequenciaqr.ui.fragments.QRCodeDialogFragment;
import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.UUID;

import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.ui.adapter.DisciplinaAdapter;
import com.example.frequenciaqr.ui.base.BaseActivity;

import java.util.ArrayList;

public class ProfessorActivity extends BaseActivity implements DisciplinaAdapter.OnDisciplinaClickListener {
    private RecyclerView recyclerDisciplinas;
    private FloatingActionButton fabGerarQR;
    private TextView txtTempoRestante;
    private DBHelper dbHelper;
    private CountDownTimer qrCodeTimer;
    private static final long QR_CODE_DURATION = 5 * 60 * 1000; // 5 minutos em milissegundos
    private DisciplinaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        dbHelper = new DBHelper(this);

        // Inicializar views
        recyclerDisciplinas = findViewById(R.id.recyclerDisciplinas);
        fabGerarQR = findViewById(R.id.fabGerarQR);
        txtTempoRestante = findViewById(R.id.txtTempoRestante);

        // Configurar RecyclerView
        setupRecyclerView();

        // Configurar FAB
        fabGerarQR.setOnClickListener(v -> gerarQRCode());

        // Carregar disciplinas do professor
        carregarDisciplinas();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_professor;
    }

    private void setupRecyclerView() {
        recyclerDisciplinas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DisciplinaAdapter(new ArrayList<>(), this);
        recyclerDisciplinas.setAdapter(adapter);
    }

    private void carregarDisciplinas() {
        SharedPreferences sharedPreferences = getSharedPreferences("FrequenciaQR", MODE_PRIVATE);
        String emailProfessor = sharedPreferences.getString("user_email", "");

        // TODO: Implementar carregamento das disciplinas do banco de dados
    }

    private void gerarQRCode() {
        // Gerar código único para a aula
        String codigoAula = UUID.randomUUID().toString();
        
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(codigoAula, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Mostrar QR Code em um DialogFragment
            QRCodeDialogFragment dialogFragment = QRCodeDialogFragment.newInstance(bitmap);
            dialogFragment.show(getSupportFragmentManager(), "qrcode");

            // Iniciar timer
            iniciarTimer();

            // TODO: Salvar código no banco de dados para validação posterior
            
        } catch (WriterException e) {
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
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
                // TODO: Invalidar QR Code no banco de dados
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (qrCodeTimer != null) {
            qrCodeTimer.cancel();
        }
    }

    @Override
    public void onDisciplinaClick(Disciplina disciplina) {
        // TODO: Implementar geração do QR Code
    }
} 