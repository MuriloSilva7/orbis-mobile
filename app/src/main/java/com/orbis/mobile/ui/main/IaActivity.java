package com.orbis.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    private final List<ChatMessage> historico = new ArrayList<>();

    private ChatDao chatDao;
    private int currentSessionId = -1; // -1 = nova sessão ainda não criada
    private boolean isNewSession = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia);

        chatDao = AppDatabase.getInstance(this).chatDao();

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
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Configurar RecyclerView
        adapter = new ChatAdapter(historico);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        btnEnviar.setOnClickListener(v -> enviarPergunta());

        // Verificar se está abrindo uma sessão existente
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SESSION_ID)) {
            currentSessionId = intent.getIntExtra(EXTRA_SESSION_ID, -1);
            String title = intent.getStringExtra(EXTRA_SESSION_TITLE);
            isNewSession = false;
            if (getSupportActionBar() != null && title != null) {
                getSupportActionBar().setTitle(title);
            }
            loadSessionMessages();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Nova Conversa");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_history) {
            startActivity(new Intent(this, ChatHistoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        // Criar sessão no banco na primeira mensagem
        if (currentSessionId == -1) {
            String titulo = pergunta.length() > 50
                    ? pergunta.substring(0, 50) + "…"
                    : pergunta;
            ChatSession session = new ChatSession(titulo, System.currentTimeMillis());
            currentSessionId = (int) chatDao.insertSession(session);
            isNewSession = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(titulo);
            }
        }

        // Cópia do histórico antes de adicionar a nova mensagem (para enviar ao backend)
        List<ChatMessage> historicoEnvio = new ArrayList<>(historico);

        // Adicionar mensagem do usuário na tela e no banco
        ChatMessage msgUsuario = new ChatMessage("user", pergunta);
        historico.add(msgUsuario);
        chatDao.insertMessage(new ChatMessageEntity(
                currentSessionId, "user", pergunta, System.currentTimeMillis()
        ));

        adapter.notifyItemInserted(historico.size() - 1);
        recyclerChat.scrollToPosition(historico.size() - 1);
        edtPergunta.setText("");

        // Loading
        progressBar.setVisibility(View.VISIBLE);
        btnEnviar.setEnabled(false);

        // Chamada ao backend
        IaRequest request = new IaRequest(pergunta, historicoEnvio);
        OrbisApiService api = RetrofitClient.getInstance(this).getApi();

        api.perguntarIa(request).enqueue(new Callback<IaResponse>() {
            @Override
            public void onResponse(Call<IaResponse> call, Response<IaResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String respostaIa = response.body().getResposta();

                    // Adicionar resposta na tela e no banco
                    historico.add(new ChatMessage("assistant", respostaIa));
                    chatDao.insertMessage(new ChatMessageEntity(
                            currentSessionId, "assistant", respostaIa, System.currentTimeMillis()
                    ));

                    adapter.notifyItemInserted(historico.size() - 1);
                    recyclerChat.scrollToPosition(historico.size() - 1);

                } else {
                    try {
                        String erro = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Erro desconhecido";
                        Log.e("IA_DEBUG", "CODE: " + response.code() + " BODY: " + erro);
                        Toast.makeText(IaActivity.this, "Erro: " + response.code(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<IaResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnEnviar.setEnabled(true);
                Log.e("IA_FAILURE", t.getMessage(), t);
                Toast.makeText(IaActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
