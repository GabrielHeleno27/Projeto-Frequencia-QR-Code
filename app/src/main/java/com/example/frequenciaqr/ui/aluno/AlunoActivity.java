package com.example.frequenciaqr.ui.aluno;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.ui.adapter.DisciplinaAdapter;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AlunoActivity extends BaseActivity implements DisciplinaAdapter.OnDisciplinaClickListener {
    private RecyclerView recyclerDisciplinas;
    private MaterialButton btnLerQR;
    private DisciplinaAdapter adapter;
    private DBHelper dbHelper;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    iniciarLeituraQRCode();
                } else {
                    Toast.makeText(this, "Permissão da câmera necessária para ler QR Code", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);
        setupViews();
        setupRecyclerView();
        carregarDisciplinas();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_aluno;
    }

    private void setupViews() {
        btnLerQR = findViewById(R.id.btnLerQR);
        btnLerQR.setOnClickListener(v -> verificarPermissaoCamera());
    }

    private void setupRecyclerView() {
        recyclerDisciplinas = findViewById(R.id.recyclerDisciplinas);
        recyclerDisciplinas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DisciplinaAdapter(new ArrayList<>(), this);
        recyclerDisciplinas.setAdapter(adapter);
    }

    private void carregarDisciplinas() {
        // TODO: Implementar carregamento das disciplinas do aluno
    }

    private void verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarLeituraQRCode();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void iniciarLeituraQRCode() {
        // TODO: Implementar leitura do QR Code
    }

    @Override
    public void onDisciplinaClick(Disciplina disciplina) {
        // TODO: Implementar ação ao clicar na disciplina
    }
} 