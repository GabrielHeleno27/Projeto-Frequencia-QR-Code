package com.example.frequenciaqr.ui.professor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.ui.base.BaseActivity;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class GerarQRCodeActivity extends BaseActivity {
    private Spinner spinnerDisciplinas;
    private EditText edtData;
    private CheckBox turnoA, turnoB, turnoC, turnoD, turnoE;
    private MaterialButton btnGerarQR;
    private DBHelper dbHelper;
    private int disciplinaId;
    private Calendar calendar;
    private List<Disciplina> disciplinas;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_gerar_qrcode;
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Recuperar ID da disciplina selecionada da intent
        disciplinaId = getIntent().getIntExtra("disciplina_id", -1);

        // Inicializar views
        spinnerDisciplinas = findViewById(R.id.spinnerDisciplinas);
        edtData = findViewById(R.id.edtData);
        turnoA = findViewById(R.id.turnoA);
        turnoB = findViewById(R.id.turnoB);
        turnoC = findViewById(R.id.turnoC);
        turnoD = findViewById(R.id.turnoD);
        turnoE = findViewById(R.id.turnoE);
        btnGerarQR = findViewById(R.id.btnGerarQR);

        // Configurar calendário
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        edtData.setText(dateFormat.format(calendar.getTime()));

        // Configurar seletor de data
        edtData.setOnClickListener(v -> showDatePicker());

        // Configurar botão de gerar QR Code
        btnGerarQR.setOnClickListener(v -> gerarQRCode());

        // Carregar disciplinas do professor
        carregarDisciplinas();

        // Configurar título da activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gerar QR Code");
        }
    }

    private void carregarDisciplinas() {
        // Obter email do professor logado
        String emailProfessor = getEmailUsuarioLogado();
        
        // Carregar disciplinas do banco de dados
        disciplinas = dbHelper.getDisciplinasProfessor(emailProfessor);
        
        if (disciplinas.isEmpty()) {
            Toast.makeText(this, "Nenhuma disciplina encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Criar adapter para o spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            disciplinas.stream().map(d -> d.getNome()).toArray(String[]::new)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplinas.setAdapter(adapter);

        // Selecionar a disciplina que veio por intent
        if (disciplinaId != -1) {
            for (int i = 0; i < disciplinas.size(); i++) {
                if (disciplinas.get(i).getId() == disciplinaId) {
                    spinnerDisciplinas.setSelection(i);
                    break;
                }
            }
        }

        // Configurar listener do spinner
        spinnerDisciplinas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                disciplinaId = disciplinas.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não fazer nada
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                edtData.setText(dateFormat.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void gerarQRCode() {
        // Verificar se pelo menos um turno foi selecionado
        if (!turnoA.isChecked() && !turnoB.isChecked() && !turnoC.isChecked() && 
            !turnoD.isChecked() && !turnoE.isChecked()) {
            Toast.makeText(this, "Selecione pelo menos um turno", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gerar código único para a aula
        String codigoAula = UUID.randomUUID().toString();

        // Criar string com dados para o QR Code
        StringBuilder turnos = new StringBuilder();
        if (turnoA.isChecked()) turnos.append("A");
        if (turnoB.isChecked()) turnos.append("B");
        if (turnoC.isChecked()) turnos.append("C");
        if (turnoD.isChecked()) turnos.append("D");
        if (turnoE.isChecked()) turnos.append("E");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String data = dateFormat.format(calendar.getTime());
        
        // Formato: codigoAula|disciplinaId|data|turnos
        String qrData = String.format("%s|%d|%s|%s", 
            codigoAula, disciplinaId, data, turnos.toString());
        
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            
            // Mostrar dialog com o QR Code
            showQRCodeDialog(bitmap, disciplinas.get(spinnerDisciplinas.getSelectedItemPosition()).getNome(), turnos.toString());
            
        } catch (WriterException e) {
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQRCodeDialog(Bitmap qrBitmap, String disciplinaNome, String turnos) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_qr_code);
        dialog.setCancelable(false);

        // Configurar views do dialog
        TextView txtDisciplina = dialog.findViewById(R.id.txtDisciplina);
        TextView txtTurnos = dialog.findViewById(R.id.txtTurnos);
        ImageView imgQRCode = dialog.findViewById(R.id.imgQRCode);
        MaterialButton btnFechar = dialog.findViewById(R.id.btnFechar);

        // Configurar conteúdo
        txtDisciplina.setText(disciplinaNome);
        txtTurnos.setText("Turnos: " + turnos);
        imgQRCode.setImageBitmap(qrBitmap);

        // Configurar botão de fechar
        btnFechar.setOnClickListener(v -> dialog.dismiss());

        // Mostrar dialog
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_disciplinas) {
            // Voltar para a tela de disciplinas
            finish();
            return true;
        }
        
        return super.onNavigationItemSelected(item);
    }
} 