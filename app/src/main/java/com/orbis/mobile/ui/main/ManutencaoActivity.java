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

        // Adapter
        adapter = new ManutencaoAdapter(listaManutencoes);
        recyclerManutencoes.setLayoutManager(new LinearLayoutManager(this));
        recyclerManutencoes.setAdapter(adapter);

        carregarManutencoes();
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
                            txtSemManutencoes.setText("Nenhum histórico para este alerta.");
                        } else {
                            txtSemManutencoes.setVisibility(View.GONE);
                        }
                    } else {
                        String erroMsg = "Erro " + response.code() + " ao carregar histórico do alerta";
                        Log.e("API_ERROR", erroMsg);
                        Toast.makeText(ManutencaoActivity.this, erroMsg, Toast.LENGTH_SHORT).show();

                        txtSemManutencoes.setVisibility(View.VISIBLE);
                        txtSemManutencoes.setText("Não foi possível carregar o histórico deste alerta.");
                    }
                }

                @Override
                public void onFailure(Call<List<Manutencao>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("API_ERROR", "Erro ao carregar histórico do alerta", t);
                    Toast.makeText(ManutencaoActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();

                    txtSemManutencoes.setVisibility(View.VISIBLE);
                    txtSemManutencoes.setText("Erro de conexão ao carregar o histórico.");
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
                Log.e("API_ERROR", "Erro ao carregar manutenções", t);
                Toast.makeText(ManutencaoActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}