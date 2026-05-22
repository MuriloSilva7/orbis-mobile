package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orbis.mobile.R;
import com.orbis.mobile.adapter.ChatAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.ChatMessage;
import com.orbis.mobile.model.IaRequest;
import com.orbis.mobile.model.IaResponse;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IaActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private ChatAdapter adapter;
    private EditText edtPergunta;
    private FloatingActionButton btnEnviar;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    private final List<ChatMessage> historico = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(""); // Previne o flash do título do manifesto
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia);

        // Inicializar Views
        toolbar = findViewById(R.id.toolbarIa);
        recyclerChat = findViewById(R.id.recyclerChat);
        edtPergunta = findViewById(R.id.edtPergunta);
        btnEnviar = findViewById(R.id.btnEnviar);
        progressBar = findViewById(R.id.progressIa);

        // Configurar Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(""); // Título vazio para manter apenas a logo à direita
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Configurar RecyclerView
        adapter = new ChatAdapter(historico);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        btnEnviar.setOnClickListener(v -> enviarPergunta());
    }

    private void enviarPergunta() {
        String pergunta = edtPergunta.getText().toString().trim();

        if (pergunta.isEmpty()) {
            return;
        }

        // Adicionar mensagem do usuário ao chat
        historico.add(new ChatMessage("user", pergunta));
        adapter.notifyItemInserted(historico.size() - 1);
        recyclerChat.scrollToPosition(historico.size() - 1);
        
        // Limpar campo de texto
        edtPergunta.setText("");

        // Mostrar loading
        progressBar.setVisibility(View.VISIBLE);
        btnEnviar.setEnabled(false);

        IaRequest request = new IaRequest(pergunta, historico);

        OrbisApiService api = RetrofitClient.getInstance(this).getApi();

        api.perguntarIa(request).enqueue(new Callback<IaResponse>() {
            @Override
            public void onResponse(Call<IaResponse> call, Response<IaResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String respostaIa = response.body().getResposta();

                    // Adicionar resposta da IA ao chat
                    historico.add(new ChatMessage("assistant", respostaIa));
                    adapter.notifyItemInserted(historico.size() - 1);
                    recyclerChat.scrollToPosition(historico.size() - 1);
                } else {
                    Toast.makeText(IaActivity.this, "Erro ao consultar IA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<IaResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);
                Toast.makeText(IaActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
