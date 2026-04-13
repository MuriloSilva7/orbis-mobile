package com.example.orbis.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orbis.R;
import com.example.orbis.model.LoginRequest;
import com.example.orbis.model.LoginResponse;
import com.example.orbis.network.RetrofitClient;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.inputemail);
        etSenha = findViewById(R.id.inputsenha);
        btnLogin = findViewById(R.id.btnLogar);

        btnLogin.setOnClickListener(v -> fazerLogin());
    }

    private void fazerLogin() {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        OrbisApiService api = RetrofitClient.getInstance().getApi();

        Call<LoginResponse> call = api.login(new LoginRequest(email, senha));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().getAccessToken();

                    // salvar token
                    SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
                    prefs.edit().putString("access_token", token).apply();


                    RetrofitClient.accessToken = token;

                    Toast.makeText(LoginActivity.this, "Login realizado!", Toast.LENGTH_SHORT).show();

                    // ir para MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN", "Erro: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}