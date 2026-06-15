package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orbis.mobile.R;
import com.orbis.mobile.adapter.ManutencaoAdapter;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Manutencao;
import com.orbis.mobile.model.ManutencoesResponse;
import com.orbis.mobile.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManutencaoActivity extends AppCompatActivity {

    private RecyclerView recyclerManutencoes;
    private ManutencaoAdapter adapter;
    private List<Manutencao> listaManutencoes = new ArrayList<>();
    private LinearProgressIndicator progressBar;
    private TextView txtSemManutencoes;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabNovaPreventiva;
    private int filtroMaquinaId = -1;
    private int filtroAlertaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao);

        // Captura os IDs para filtro, se existirem
        filtroMaquinaId = getIntent().getIntExtra("maquina_id", -1);
        filtroAlertaId = getIntent().getIntExtra("alerta_id", -1);

        // Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarManutencao);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(""); // Título vazio para manter apenas a logo à direita
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Views
        recyclerManutencoes = findViewById(R.id.recyclerManutencoes);
        progressBar = findViewById(R.id.progressManutencao);
        txtSemManutencoes = findViewById(R.id.txtSemManutencoes);
        fabNovaPreventiva = findViewById(R.id.fabNovaPreventiva);

        fabNovaPreventiva.setOnClickListener(v -> mostrarDialogPreventiva());

        checkUserRole();

        // Adapter
        adapter = new ManutencaoAdapter(listaManutencoes);
        recyclerManutencoes.setLayoutManager(new LinearLayoutManager(this));
        recyclerManutencoes.setAdapter(adapter);

        carregarManutencoes();
    }

    private void checkUserRole() {
        android.content.SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
        String role = prefs.getString("user_role", "");
        if ("TECNICO".equals(role)) {
            fabNovaPreventiva.setVisibility(View.VISIBLE);
        } else {
            fabNovaPreventiva.setVisibility(View.GONE);
        }
    }

    private void mostrarDialogPreventiva() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nova Manutenção Preventiva");
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final android.widget.EditText inputId = new android.widget.EditText(this);
        inputId.setHint("ID da Máquina");
        inputId.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (filtroMaquinaId != -1) inputId.setText(String.valueOf(filtroMaquinaId));
        layout.addView(inputId);

        final android.widget.EditText inputObs = new android.widget.EditText(this);
        inputObs.setHint("Observação");
        layout.addView(inputObs);

        builder.setView(layout);

        builder.setPositiveButton("Criar", (dialog, which) -> {
            String idStr = inputId.getText().toString().trim();
            String obs = inputObs.getText().toString().trim();
            if (!idStr.isEmpty()) {
                criarPreventiva(Integer.parseInt(idStr), obs);
            } else {
                Toast.makeText(this, "ID da máquina é obrigatório", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void criarPreventiva(int maquinaId, String observacao) {
        progressBar.setVisibility(View.VISIBLE);
        OrbisApiService api = RetrofitClient.getInstance(this).getApi();
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("tipo", "PREVENTIVA");
        body.put("maquinaId", maquinaId);
        body.put("observacao", observacao);

        api.createManutencao(body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(ManutencaoActivity.this, "Manutenção preventiva criada!", Toast.LENGTH_SHORT).show();
                    carregarManutencoes();
                } else {
                    Toast.makeText(ManutencaoActivity.this, "Erro ao criar preventiva", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManutencaoActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarManutencoes() {
        progressBar.setVisibility(View.VISIBLE);

        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();

        if (filtroAlertaId != -1) {
            apiService.getManutencoesByAlerta(filtroAlertaId).enqueue(new Callback<List<Manutencao>>() {
                @Override
                public void onResponse(Call<List<Manutencao>> call, Response<List<Manutencao>> response) {
                    progressBar.setVisibility(View.GONE);
                    listaManutencoes.clear();

                    if (response.isSuccessful() && response.body() != null) {
                        listaManutencoes.addAll(response.body());
                        adapter.notifyDataSetChanged();

                        if (listaManutencoes.isEmpty()) {
                            txtSemManutencoes.setVisibility(View.VISIBLE);
                            txtSemManutencoes.setText(R.string.label_sem_manutencoes_alerta);
                        } else {
                            txtSemManutencoes.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("API_ERROR", "Erro ao carregar histórico do alerta");
                        Toast.makeText(ManutencaoActivity.this, getString(R.string.error_carregar_detalhes), Toast.LENGTH_SHORT).show();

                        txtSemManutencoes.setVisibility(View.VISIBLE);
                        txtSemManutencoes.setText(R.string.error_carregar_historico);
                    }
                }

                @Override
                public void onFailure(Call<List<Manutencao>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("API_ERROR", "Erro ao carregar histórico do alerta", t);
                    Toast.makeText(ManutencaoActivity.this, getString(R.string.error_conexao), Toast.LENGTH_SHORT).show();

                    txtSemManutencoes.setVisibility(View.VISIBLE);
                    txtSemManutencoes.setText(R.string.error_conexao);
                }
            });

            return;
        }

        apiService.getManutencoes(1, 100).enqueue(new Callback<ManutencoesResponse>() {
            @Override
            public void onResponse(Call<ManutencoesResponse> call, Response<ManutencoesResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    listaManutencoes.clear();

                    List<Manutencao> dadosBrutos = response.body().getDados();

                    if (dadosBrutos != null) {
                        if (filtroMaquinaId != -1) {
                            for (Manutencao m : dadosBrutos) {
                                if (m.getAlerta() != null &&
                                        m.getAlerta().getMaquina() != null &&
                                        m.getAlerta().getMaquina().getId() == filtroMaquinaId) {
                                    listaManutencoes.add(m);
                                }
                            }
                        } else {
                            listaManutencoes.addAll(dadosBrutos);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (listaManutencoes.isEmpty()) {
                        txtSemManutencoes.setVisibility(View.VISIBLE);

                        if (filtroMaquinaId != -1) {
                            txtSemManutencoes.setText(R.string.label_sem_manutencoes_maquina);
                        } else {
                            txtSemManutencoes.setText(R.string.label_sem_manutencoes);
                        }
                    } else {
                        txtSemManutencoes.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("API_ERROR", "Erro ao carregar");
                    Toast.makeText(ManutencaoActivity.this, getString(R.string.error_carregar_detalhes), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ManutencoesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "Erro ao carregar manutenções", t);
                Toast.makeText(ManutencaoActivity.this, getString(R.string.error_conexao), Toast.LENGTH_SHORT).show();
            }
        });
    }
}