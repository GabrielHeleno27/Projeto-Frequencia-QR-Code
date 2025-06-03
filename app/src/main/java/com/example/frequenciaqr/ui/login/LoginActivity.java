package com.example.frequenciaqr.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.database.DBHelper;
import com.example.frequenciaqr.ui.aluno.AlunoActivity;
import com.example.frequenciaqr.ui.coordenador.CoordenadorActivity;
import com.example.frequenciaqr.ui.professor.ProfessorActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText edtEmail;
    private TextInputEditText edtSenha;
    private MaterialButton btnEntrar;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar banco de dados e SharedPreferences
        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("FrequenciaQR", MODE_PRIVATE);

        // Verificar se já existe um usuário logado
        if (isUserLoggedIn()) {
            redirecionarUsuario();
            finish();
            return;
        }

        // Inicializar views
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);

        // Configurar listener do botão
        btnEntrar.setOnClickListener(v -> realizarLogin());
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.contains("email_usuario") && 
               sharedPreferences.contains("tipo_usuario");
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar credenciais no banco de dados
        if (dbHelper.verificarCredenciais(email, senha)) {
            String tipoUsuario = dbHelper.getTipoUsuario(email);
            
            // Salvar dados do usuário
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email_usuario", email);
            editor.putString("tipo_usuario", tipoUsuario);
            editor.apply();

            // Redirecionar para a tela apropriada
            redirecionarUsuario();
            finish();
        } else {
            Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirecionarUsuario() {
        String tipoUsuario = sharedPreferences.getString("tipo_usuario", "");
        Intent intent;

        switch (tipoUsuario.toLowerCase()) {
            case "aluno":
                intent = new Intent(this, AlunoActivity.class);
                break;
            case "professor":
                intent = new Intent(this, ProfessorActivity.class);
                break;
            case "coordenador":
                intent = new Intent(this, CoordenadorActivity.class);
                break;
            default:
                Toast.makeText(this, "Tipo de usuário inválido", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
    }
} 