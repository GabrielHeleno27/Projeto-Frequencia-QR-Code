package com.example.frequenciaqr.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.activities.aluno.AlunoActivity;
import com.example.frequenciaqr.activities.coordenador.CoordenadorActivity;
import com.example.frequenciaqr.activities.professor.ProfessorActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnLogin;

    // Dados de exemplo
    // email: coordenador@teste.com, professor@teste.com, aluno@teste.com
    // senha: 123456

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> realizarLogin());
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mock simples de autenticação
        if (email.equals("coordenador@teste.com") && senha.equals("123456")) {
            salvarSessao("coordenador");
            startActivity(new Intent(this, CoordenadorActivity.class));
            finish();
        } else if (email.equals("professor@teste.com") && senha.equals("123456")) {
            salvarSessao("professor");
            startActivity(new Intent(this, ProfessorActivity.class));
            finish();
        } else if (email.equals("aluno@teste.com") && senha.equals("123456")) {
            salvarSessao("aluno");
            startActivity(new Intent(this, AlunoActivity.class));
            finish();
        } else {
            Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarSessao(String tipoUsuario) {
        SharedPreferences prefs = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("tipoUsuario", tipoUsuario);
        editor.apply();
    }
}