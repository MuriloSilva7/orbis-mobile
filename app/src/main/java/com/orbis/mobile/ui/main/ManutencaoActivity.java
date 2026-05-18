package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    private TextView txtSemManutencoes;
    private int filtroMaquinaId = -1;
    private int filtroAlertaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            toolbar.setNavigationOnClickListener(v -> finish());
            if (filtroMaquinaId != -1) {
                getSupportActionBar().setTitle("Histórico da Máquina");
            } else if (filtroAlertaId != -1) {
                getSupportActionBar().setTitle("Histórico do Alerta");
            }
        }

        // Views
        recyclerManutencoes = findViewById(R.id.recyclerManutencoes);
        progressBar = findViewById(R.id.progressManutencao);
        txtSemManutencoes = findViewById(R.id.txtSemManutencoes);

        // Adapter
        adapter = new ManutencaoAdapter(listaManutencoes);
        recyclerManutencoes.setLayoutManager(new LinearLayoutManager(this));
        recyclerManutencoes.setAdapter(adapter);

        carregarManutencoes();
    }

    private void carregarManutencoes() {
        progressBar.setVisibility(View.VISIBLE);

        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        apiService.getManutencoes(1, 100).enqueue(new Callback<ManutencoesResponse>() {
            @Override
            public void onResponse(Call<ManutencoesResponse> call, Response<ManutencoesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    listaManutencoes.clear();

                    List<Manutencao> dadosBrutos = response.body().getDados();

                    if (dadosBrutos != null) {
                        if (filtroAlertaId != -1) {
                            // Filtra apenas as manutenções daquele alerta específico
                            for (Manutencao m : dadosBrutos) {
                                if (m.getAlertaId() == filtroAlertaId) {
                                    listaManutencoes.add(m);
                                }
                            }
                        } else if (filtroMaquinaId != -1) {
                            // Filtra apenas as manutenções daquela máquina
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
                        if (filtroAlertaId != -1) {
                            txtSemManutencoes.setText("Nenhum histórico para este alerta.");
                        } else if (filtroMaquinaId != -1) {
                            txtSemManutencoes.setText("Nenhum histórico para esta máquina.");
                        } else {
                            txtSemManutencoes.setText("Nenhuma manutenção encontrada.");
                        }
                    } else {
                        txtSemManutencoes.setVisibility(View.GONE);
                    }
                } else {
                    String erroMsg = "Erro " + response.code() + " ao carregar";
                    Log.e("API_ERROR", erroMsg);
                    Toast.makeText(ManutencaoActivity.this, erroMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ManutencoesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(ManutencaoActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}