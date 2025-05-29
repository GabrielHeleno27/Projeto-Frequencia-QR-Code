package com.example.frequenciaqr.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.R;
import com.example.frequenciaqr.ui.coordenador.CoordenadorActivity;
import com.example.frequenciaqr.ui.professor.ProfessorActivity;
import com.example.frequenciaqr.ui.aluno.AlunoActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa o banco de dados e SharedPreferences
        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("FrequenciaQR", MODE_PRIVATE);

        // Inicializa as views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // Verifica se já existe um usuário logado
        if (isUserLoggedIn()) {
            redirectBasedOnUserType();
            finish();
            return;
        }

        loginButton.setOnClickListener(v -> realizarLogin());
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.contains("user_type");
    }

    private void realizarLogin() {
        String email = emailInput.getText().toString().trim();
        String senha = passwordInput.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica as credenciais no banco de dados
        String tipoUsuario = dbHelper.verificarCredenciais(email, senha);

        if (tipoUsuario != null) {
            // Salva o tipo de usuário nas SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_type", tipoUsuario);
            editor.putString("user_email", email);
            editor.apply();

            redirectBasedOnUserType();
        } else {
            Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectBasedOnUserType() {
        String userType = sharedPreferences.getString("user_type", "");
        Intent intent;

        switch (userType) {
            case "coordenador":
                intent = new Intent(this, CoordenadorActivity.class);
                break;
            case "professor":
                intent = new Intent(this, ProfessorActivity.class);
                break;
            case "aluno":
                intent = new Intent(this, AlunoActivity.class);
                break;
            default:
                return;
        }

        startActivity(intent);
        finish();
    }
} 