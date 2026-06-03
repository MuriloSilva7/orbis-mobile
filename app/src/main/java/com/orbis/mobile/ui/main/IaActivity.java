package com.orbis.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.orbis.mobile.R;
import com.orbis.mobile.adapter.ChatAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.AppDatabase;
import com.orbis.mobile.model.ChatDao;
import com.orbis.mobile.model.ChatMessage;
import com.orbis.mobile.model.ChatMessageEntity;
import com.orbis.mobile.model.ChatSession;
import com.orbis.mobile.model.IaRequest;
import com.orbis.mobile.model.IaResponse;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IaActivity extends AppCompatActivity {

    public static final String EXTRA_SESSION_ID = "extra_session_id";
    public static final String EXTRA_SESSION_TITLE = "extra_session_title";

    private RecyclerView recyclerChat;
    private ChatAdapter adapter;
    private EditText edtPergunta;
    private FloatingActionButton btnEnviar;
    private LinearProgressIndicator progressBar;
    private MaterialToolbar toolbar;
    private ImageButton btnHistory;

    private final List<ChatMessage> historico = new ArrayList<>();
    private ChatDao chatDao;
    private int currentSessionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia);

        chatDao = AppDatabase.getInstance(this).chatDao();

        // Inicializar Views
        toolbar = findViewById(R.id.toolbarIa);
        recyclerChat = findViewById(R.id.recyclerChat);
        edtPergunta = findViewById(R.id.edtPergunta);
        btnEnviar = findViewById(R.id.btnEnviar);
        progressBar = findViewById(R.id.progressIa);
        btnHistory = findViewById(R.id.btnHistoryIa);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Ação do Botão de Histórico (XML)
        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatHistoryActivity.class));
        });

        adapter = new ChatAdapter(historico);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        btnEnviar.setOnClickListener(v -> enviarPergunta());

        // Carregar sessão se existir
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SESSION_ID)) {
            currentSessionId = intent.getIntExtra(EXTRA_SESSION_ID, -1);
            loadSessionMessages();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        
        // Limpar conversa atual para carregar a nova ou iniciar uma limpa
        historico.clear();
        currentSessionId = -1;
        
        if (intent.hasExtra(EXTRA_SESSION_ID)) {
            currentSessionId = intent.getIntExtra(EXTRA_SESSION_ID, -1);
            loadSessionMessages();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadSessionMessages() {
        if (currentSessionId == -1) return;
        List<ChatMessageEntity> entities = chatDao.getMessagesBySession(currentSessionId);
        for (ChatMessageEntity entity : entities) {
            historico.add(new ChatMessage(entity.getRole(), entity.getContent()));
        }
        adapter.notifyDataSetChanged();
        if (!historico.isEmpty()) {
            recyclerChat.scrollToPosition(historico.size() - 1);
        }
    }

    private void enviarPergunta() {
        String pergunta = edtPergunta.getText().toString().trim();
        if (pergunta.isEmpty()) return;

        if (currentSessionId == -1) {
            String titulo = pergunta.length() > 30 ? pergunta.substring(0, 30) + "..." : pergunta;
            ChatSession session = new ChatSession(titulo, System.currentTimeMillis());
            currentSessionId = (int) chatDao.insertSession(session);
        }

        List<ChatMessage> historicoEnvio = new ArrayList<>(historico);
        historico.add(new ChatMessage("user", pergunta));
        chatDao.insertMessage(new ChatMessageEntity(currentSessionId, "user", pergunta, System.currentTimeMillis()));

        adapter.notifyItemInserted(historico.size() - 1);
        recyclerChat.scrollToPosition(historico.size() - 1);
        edtPergunta.setText("");

        progressBar.setVisibility(View.VISIBLE);
        btnEnviar.setEnabled(false);

        OrbisApiService api = RetrofitClient.getInstance(this).getApi();
        api.perguntarIa(new IaRequest(pergunta, historicoEnvio)).enqueue(new Callback<IaResponse>() {
            @Override
            public void onResponse(Call<IaResponse> call, Response<IaResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    String respostaIa = response.body().getResposta();
                    historico.add(new ChatMessage("assistant", respostaIa));
                    chatDao.insertMessage(new ChatMessageEntity(currentSessionId, "assistant", respostaIa, System.currentTimeMillis()));
                    adapter.notifyItemInserted(historico.size() - 1);
                    recyclerChat.scrollToPosition(historico.size() - 1);
                }
            }
            @Override
            public void onFailure(Call<IaResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);
                Toast.makeText(IaActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
