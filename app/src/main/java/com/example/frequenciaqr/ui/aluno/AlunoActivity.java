package com.example.frequenciaqr.ui.aluno;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.example.frequenciaqr.websocket.PresencaClient;
import com.google.android.material.button.MaterialButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class AlunoActivity extends BaseActivity implements PresencaClient.PresencaListener {
    private RecyclerView recyclerViewDisciplinas;
    private MaterialButton btnLerQR;
    private DBHelper dbHelper;
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
    protected int getLayoutResourceId() {
        return R.layout.activity_aluno;
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();
        
        dbHelper = new DBHelper(this);

        // Inicializar views
        recyclerViewDisciplinas = findViewById(R.id.recyclerDisciplinas);
        btnLerQR = findViewById(R.id.btnLerQR);

        // Configurar RecyclerView
        recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Implementar adapter para disciplinas

        // Configurar botão
        btnLerQR.setOnClickListener(v -> iniciarLeituraQR());

        // Carregar disciplinas do aluno
        carregarDisciplinas();
    }

    private void carregarDisciplinas() {
        // TODO: Implementar carregamento de disciplinas
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
            
            presencaClient = new PresencaClient(wsUrl, emailAluno, codigoAula, this);
            presencaClient.connect();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar QR Code: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presencaClient != null) {
            presencaClient.close();
        }
    }

    @Override
    public void onPresencaConfirmada() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Presença registrada com sucesso!", Toast.LENGTH_SHORT).show();
            carregarDisciplinas();
        });
    }

    @Override
    public void onError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Erro: " + message, Toast.LENGTH_SHORT).show();
        });
    }
} 