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
import com.example.frequenciaqr.websocket.PresencaServer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.UUID;
import android.util.Log;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProfessorActivity extends AppCompatActivity implements PresencaServer.PresencaListener {
    private static final String TAG = "ProfessorActivity";
    private RecyclerView recyclerViewDisciplinas;
    private FloatingActionButton fabGerarQR;
    private TextView txtTempoRestante;
    private DBHelper dbHelper;
    private CountDownTimer qrCodeTimer;
    private static final long QR_CODE_DURATION = 5 * 60 * 1000; // 5 minutos em milissegundos
    private static final int WEBSOCKET_PORT = 8887;
    private PresencaServer presencaServer;
    private String currentCodigoAula;
    private ExecutorService executorService;
    private Future<?> serverFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        dbHelper = new DBHelper(this);
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar views
        recyclerViewDisciplinas = findViewById(R.id.recyclerDisciplinas);
        fabGerarQR = findViewById(R.id.fabGerarQR);
        txtTempoRestante = findViewById(R.id.txtTempoRestante);

        // Configurar RecyclerView
        recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Implementar adapter para disciplinas

        // Configurar FAB
        fabGerarQR.setOnClickListener(v -> gerarQRCode());

        // Carregar disciplinas do professor
        carregarDisciplinas();
    }

    private void carregarDisciplinas() {
        SharedPreferences sharedPreferences = getSharedPreferences("FrequenciaQR", MODE_PRIVATE);
        String emailProfessor = sharedPreferences.getString("user_email", "");
        // TODO: Implementar carregamento das disciplinas do banco de dados
    }

    private void pararServidor() {
        if (serverFuture != null) {
            serverFuture.cancel(true);
        }
        
        if (presencaServer != null) {
            executorService.execute(() -> {
                try {
                    presencaServer.stop();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Erro ao parar servidor WebSocket", e);
                    runOnUiThread(() -> {
                        Toast.makeText(ProfessorActivity.this, 
                            "Erro ao parar servidor: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
                } finally {
                    presencaServer = null;
                }
            });
        }
    }

    private void iniciarServidor() {
        if (presencaServer != null) {
            pararServidor();
        }

        presencaServer = new PresencaServer(WEBSOCKET_PORT, currentCodigoAula, this);
        serverFuture = executorService.submit(() -> {
            try {
                presencaServer.start();
            } catch (Exception e) {
                Log.e(TAG, "Erro ao iniciar servidor WebSocket", e);
                runOnUiThread(() -> {
                    Toast.makeText(ProfessorActivity.this, 
                        "Erro ao iniciar servidor. Verifique sua conexão.", 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void gerarQRCode() {
        // Gerar código único para a aula
        currentCodigoAula = UUID.randomUUID().toString();
        
        // Iniciar servidor WebSocket
        iniciarServidor();
        
        // Obter IP local
        String ipAddress = PresencaServer.getLocalIpAddress();
        if (ipAddress == null) {
            Toast.makeText(this, "Erro ao obter endereço IP", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Criar string com dados para o QR Code (código|ip|porta)
        String qrData = String.format("%s|%s|%d", currentCodigoAula, ipAddress, WEBSOCKET_PORT);
        
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Mostrar QR Code em um DialogFragment
            QRCodeDialogFragment dialogFragment = QRCodeDialogFragment.newInstance(bitmap);
            dialogFragment.show(getSupportFragmentManager(), "qrcode");

            // Iniciar timer
            iniciarTimer();
            
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
                pararServidor();
            }
        }.start();
    }

    @Override
    public void onPresencaRegistrada(String alunoEmail) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Presença registrada: " + alunoEmail, Toast.LENGTH_SHORT).show();
            // TODO: Atualizar lista de presenças na interface
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (qrCodeTimer != null) {
            qrCodeTimer.cancel();
        }
        pararServidor();
        executorService.shutdown();
    }
} 