package com.orbis.mobile.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.model.Manutencao;
import com.orbis.mobile.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertaDetalheActivity extends AppCompatActivity {

    private TextView txtIdAlertaVariavel, txtMaquinaVariavel, txtSensorVariavel, 
                     txtCriadoEmVariavel, txtTipoVariavel, txtStatusVariavel, txtMensagemVariavel;
    
    private Button btnVoltar, btnAceitar, btnConcluir, btnCriarManutencao, btnVerHistorico, btnCancelar;
    private LinearLayout layoutAcoesAndamento;

    private int manutencaoId = -1;
    private int alertaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_detalhe);

        initViews();
        setupToolbar();
        setupListeners();

        alertaId = getIntent().getIntExtra("id_alerta", -1);
        carregarDetalhes(alertaId);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        txtIdAlertaVariavel = findViewById(R.id.txtIdAlertaVariavel);
        txtMaquinaVariavel = findViewById(R.id.txtMaquinaVariavel);
        txtSensorVariavel = findViewById(R.id.txtSensorVariavel);
        txtCriadoEmVariavel = findViewById(R.id.txtCriadoEmVariavel);
        txtTipoVariavel = findViewById(R.id.txtTipoVariavel);
        txtStatusVariavel = findViewById(R.id.txtStatusVariavel);
        txtMensagemVariavel = findViewById(R.id.txtMensagemVariavel);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnAceitar = findViewById(R.id.btnAceitar);
        btnConcluir = findViewById(R.id.btnConcluir);
        btnCriarManutencao = findViewById(R.id.btnCriarManutencao);
        btnVerHistorico = findViewById(R.id.btnVerHistorico);
        btnCancelar = findViewById(R.id.btnCancelar);
        layoutAcoesAndamento = findViewById(R.id.layoutAcoesAndamento);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnVoltar.setOnClickListener(v -> finish());

        btnAceitar.setOnClickListener(v -> aceitarAlerta(alertaId));

        btnConcluir.setOnClickListener(v -> {
            if (manutencaoId != -1) {
                concluirAlerta(manutencaoId);
            } else {
                Toast.makeText(this, "Manutenção não encontrada", Toast.LENGTH_SHORT).show();
            }
        });

        btnCriarManutencao.setOnClickListener(v -> {
            if (manutencaoId != -1) {
                mostrarDialogRelato();
            } else {
                Toast.makeText(this, "Aguarde o carregamento da manutenção...", Toast.LENGTH_SHORT).show();
            }
        });

        btnVerHistorico.setOnClickListener(v -> {
            if (alertaId != -1) {
                Intent intent = new Intent(this, ManutencaoActivity.class);
                intent.putExtra("alerta_id", alertaId);
                startActivity(intent);
            }
        });

        btnCancelar.setOnClickListener(v -> {
            if (manutencaoId != -1) {
                cancelarAlerta(manutencaoId);
            }
        });
    }

    private void carregarDetalhes(int id) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        apiService.getAlertas().enqueue(new Callback<List<Alerta>>() {
            @Override
            public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Alerta alerta : response.body()) {
                        if (alerta.getId() == id) {
                            preencherCampos(alerta);
                            atualizarInterfacePorStatus(alerta.getStatus());
                            if ("EM_ANDAMENTO".equals(alerta.getStatus())) {
                                carregarManutencaoDoAlerta(alerta.getId());
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Alerta>> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherCampos(Alerta alerta) {
        txtIdAlertaVariavel.setText(String.valueOf(alerta.getId()));
        txtMaquinaVariavel.setText(alerta.getMaquina().getNome());
        txtSensorVariavel.setText(alerta.getSensor().getTipo());
        txtCriadoEmVariavel.setText(alerta.getCriadoEm());
        txtTipoVariavel.setText(alerta.getTipo());
        txtStatusVariavel.setText(alerta.getStatus());
        txtMensagemVariavel.setText(alerta.getMensagem());
    }

    private void atualizarInterfacePorStatus(String status) {
        btnAceitar.setVisibility(View.GONE);
        btnCriarManutencao.setVisibility(View.GONE);
        layoutAcoesAndamento.setVisibility(View.GONE);

        if ("ATIVO".equals(status)) {
            btnAceitar.setVisibility(View.VISIBLE);
        } else if ("EM_ANDAMENTO".equals(status)) {
            btnCriarManutencao.setVisibility(View.VISIBLE);
            layoutAcoesAndamento.setVisibility(View.VISIBLE);
        }
    }

    private void aceitarAlerta(int idAlerta) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("alertaId", idAlerta);
        body.put("observacao", "Alerta aceito pelo técnico");

        apiService.createManutencao(body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful() && response.body() != null) {
                    manutencaoId = response.body().getId();
                    txtStatusVariavel.setText("EM_ANDAMENTO");
                    atualizarInterfacePorStatus("EM_ANDAMENTO");
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta aceito!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao aceitar alerta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void concluirAlerta(int idManutencao) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "RESOLVIDO");

        apiService.updateManutencao(idManutencao, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful()) {
                    txtStatusVariavel.setText("RESOLVIDO");
                    atualizarInterfacePorStatus("RESOLVIDO");
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta concluído!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao concluir", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelarAlerta(int idManutencao) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "ENCERRADO_SEM_SOLUCAO");

        apiService.updateManutencao(idManutencao, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful()) {
                    txtStatusVariavel.setText("ATIVO");
                    atualizarInterfacePorStatus("ATIVO");
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta cancelado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarManutencaoDoAlerta(int idAlerta) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        apiService.getManutencoesByAlerta(idAlerta).enqueue(new Callback<List<Manutencao>>() {
            @Override
            public void onResponse(Call<List<Manutencao>> call, Response<List<Manutencao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Manutencao m : response.body()) {
                        if ("EM_ANDAMENTO".equals(m.getStatus())) {
                            manutencaoId = m.getId();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Manutencao>> call, Throwable t) {}
        });
    }

    private void mostrarDialogRelato() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Relatar Manutenção");
        final EditText input = new EditText(this);
        input.setHint("Descreva o que foi feito...");
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String relato = input.getText().toString().trim();
            if (!relato.isEmpty()) salvarRelatoManutencao(relato);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void salvarRelatoManutencao(String relato) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("observacao", relato);

        apiService.updateManutencao(manutencaoId, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful()) Toast.makeText(AlertaDetalheActivity.this, "Relato salvo!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {}
        });
    }
}
