package com.orbis.mobile.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orbis.mobile.R;
import com.orbis.mobile.model.LoginRequest;
import com.orbis.mobile.model.LoginResponse;
import com.orbis.mobile.model.TokenManager;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etSenha;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.inputemail);
        etSenha = findViewById(R.id.inputsenha);
        btnLogin = findViewById(R.id.btnLogar);
        progressBar = findViewById(R.id.progressBarLogin);

        btnLogin.setOnClickListener(v -> fazerLogin());
    }

    private void fazerLogin() {

        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar loading e desativar botão
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        OrbisApiService api = RetrofitClient.getInstance(this).getApi();
        Call<LoginResponse> call = api.login(new LoginRequest(email, senha));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    TokenManager tokenManager = new TokenManager(LoginActivity.this);
                    tokenManager.saveTokens(
                            response.body().getAccessToken(),
                            response.body().getRefreshToken()
                    );

                    // Salva informações do usuário para o Drawer
                    Usuario usuario = response.body().getUsuario();
                    if (usuario != null) {
                        SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
                        prefs.edit()
                                .putString("user_nome", usuario.getNome())
                                .putString("user_email", usuario.getEmail())
                                .putString("user_foto", usuario.getFotoPerfil())
                                .apply();
                    }

                    Toast.makeText(LoginActivity.this, "Login realizado!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Log.e("LOGIN", "Erro: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
