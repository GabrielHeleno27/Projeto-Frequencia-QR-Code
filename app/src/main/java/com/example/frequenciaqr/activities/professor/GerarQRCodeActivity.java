package com.example.frequenciaqr.activities.professor;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;

import java.text.SimpleDateFormat;
import java.util.*;

public class GerarQRCodeActivity extends AppCompatActivity {

    private Spinner spinnerDisciplinas;
    private EditText edtData;
    private CheckBox turnoA, turnoB, turnoC, turnoD, turnoE;
    private Button btnGerarQR;
    private ImageView imgQRCode;

    private DBHelper dbHelper;
    private Map<String, Integer> disciplinaMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerar_qrcode);

        spinnerDisciplinas = findViewById(R.id.spinnerDisciplinas);
        edtData = findViewById(R.id.edtData);
        turnoA = findViewById(R.id.turnoA);
        turnoB = findViewById(R.id.turnoB);
        turnoC = findViewById(R.id.turnoC);
        turnoD = findViewById(R.id.turnoD);
        turnoE = findViewById(R.id.turnoE);
        btnGerarQR = findViewById(R.id.btnGerarQR);
        imgQRCode = findViewById(R.id.imgQRCode);

        dbHelper = new DBHelper(this);
        carregarDisciplinas();

        edtData.setOnClickListener(v -> mostrarDatePicker());
        btnGerarQR.setOnClickListener(v -> gerarQRCode());
    }
    private void carregarDisciplinas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT d.id, d.nome FROM disciplinas d INNER JOIN disciplinas_professores dp ON dp.id_disciplina = d.id", null);

        List<String> nomes = new ArrayList<>();
        disciplinaMap = new HashMap<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nome = cursor.getString(1);
            nomes.add(nome);
            disciplinaMap.put(nome, id);
        }

        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomes);
        spinnerDisciplinas.setAdapter(adapter);
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dataFormatada = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
            edtData.setText(dataFormatada);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void gerarQRCode() {
        String nomeDisciplina = (String) spinnerDisciplinas.getSelectedItem();
        String data = edtData.getText().toString().trim();

        List<String> turnosSelecionados = new ArrayList<>();
        if (turnoA.isChecked()) turnosSelecionados.add("A");
        if (turnoB.isChecked()) turnosSelecionados.add("B");
        if (turnoC.isChecked()) turnosSelecionados.add("C");
        if (turnoD.isChecked()) turnosSelecionados.add("D");
        if (turnoE.isChecked()) turnosSelecionados.add("E");

        if (nomeDisciplina == null || data.isEmpty() || turnosSelecionados.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        int idDisciplina = disciplinaMap.get(nomeDisciplina);
        long timestamp = System.currentTimeMillis();
        String turnos = String.join(",", turnosSelecionados);

        // Estrutura do código: idDisciplina|data|turnos|timestamp
        String conteudoQR = idDisciplina + "|" + data + "|" + turnos + "|" + timestamp;

        // Inserir no banco (chamada)
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO chamadas (id_disciplina, data, turno, codigo_qr, timestamp_gerado) VALUES (?, ?, ?, ?, ?)",
                new Object[]{idDisciplina, data, turnos, conteudoQR, timestamp});

        // Gerar QR visual
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(conteudoQR, BarcodeFormat.QR_CODE, 600, 600);
            imgQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Erro ao gerar QR", Toast.LENGTH_SHORT).show();
        }
    }
}
