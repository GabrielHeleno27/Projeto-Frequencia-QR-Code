package com.example.frequenciaqr.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected Toolbar toolbar;
    protected ActionBarDrawerToggle toggle;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("FrequenciaQR", MODE_PRIVATE);

        // Verificar se o usuário está logado
        if (!isUserLoggedIn()) {
            redirecionarParaLogin();
            return;
        }

        // Configurar Toolbar
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Configurações específicas da activity
        setupActivity();
    }

    /**
     * Método abstrato que deve ser implementado pelas activities filhas
     * para fornecer o ID do layout a ser inflado
     */
    @LayoutRes
    protected abstract int getLayoutResourceId();

    /**
     * Método que pode ser sobrescrito pelas activities filhas
     * para realizar configurações específicas
     */
    protected void setupActivity() {
        // Implementação padrão vazia
    }

    /**
     * Verifica se existe um usuário logado
     */
    protected boolean isUserLoggedIn() {
        return sharedPreferences.contains("email_usuario") && 
               sharedPreferences.contains("tipo_usuario");
    }

    /**
     * Redireciona o usuário para a tela de login
     */
    protected void redirecionarParaLogin() {
        // Limpar dados do usuário
        sharedPreferences.edit().clear().apply();
        
        // Redirecionar para login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Retorna o email do usuário logado
     */
    protected String getEmailUsuarioLogado() {
        return sharedPreferences.getString("email_usuario", "");
    }

    /**
     * Retorna o tipo do usuário logado
     */
    protected String getTipoUsuarioLogado() {
        return sharedPreferences.getString("tipo_usuario", "");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupToolbar();
        setupDrawer();
        setupNavigationHeader();
        setupBackPressedCallback();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        
        if (drawerLayout != null && navigationView != null) {
            toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void setupNavigationHeader() {
        if (navigationView == null) return;

        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) return;

        TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
        TextView tipoTextView = headerView.findViewById(R.id.nav_header_tipo);

        String email = sharedPreferences.getString("email_usuario", "");
        String tipo = sharedPreferences.getString("tipo_usuario", "");

        if (emailTextView != null) {
            emailTextView.setText(email);
        }

        if (tipoTextView != null && !tipo.isEmpty()) {
            String tipoFormatado = tipo.substring(0, 1).toUpperCase() + 
                                 (tipo.length() > 1 ? tipo.substring(1).toLowerCase() : "");
            tipoTextView.setText(tipoFormatado);
        }
    }

    private void setupBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            realizarLogout();
            return true;
        }
        
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void realizarLogout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 