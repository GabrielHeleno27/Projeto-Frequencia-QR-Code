package com.example.frequenciaqr.ui.professor;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class GerarQRCodeActivity extends AppCompatActivity {
    private EditText edtData;
    private CheckBox turnoA, turnoB, turnoC, turnoD, turnoE;
    private Button btnGerarQR;
    private ImageView imgQRCode;
    private DBHelper dbHelper;
    private int disciplinaId;
    private String disciplinaNome;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerar_qrcode);

        // Recuperar dados da intent
        disciplinaId = getIntent().getIntExtra("disciplina_id", -1);
        disciplinaNome = getIntent().getStringExtra("disciplina_nome");

        if (disciplinaId == -1 || disciplinaNome == null) {
            Toast.makeText(this, "Erro ao carregar disciplina", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar views
        edtData = findViewById(R.id.edtData);
        turnoA = findViewById(R.id.turnoA);
        turnoB = findViewById(R.id.turnoB);
        turnoC = findViewById(R.id.turnoC);
        turnoD = findViewById(R.id.turnoD);
        turnoE = findViewById(R.id.turnoE);
        btnGerarQR = findViewById(R.id.btnGerarQR);
        imgQRCode = findViewById(R.id.imgQRCode);

        // Configurar calendário
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        edtData.setText(dateFormat.format(calendar.getTime()));

        // Configurar seletor de data
        edtData.setOnClickListener(v -> showDatePicker());

        // Configurar botão de gerar QR Code
        btnGerarQR.setOnClickListener(v -> gerarQRCode());

        // Configurar título da activity
        setTitle(disciplinaNome);
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
            imgQRCode.setVisibility(View.VISIBLE);
            imgQRCode.setImageBitmap(bitmap);
            
        } catch (WriterException e) {
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
        }
    }
} 