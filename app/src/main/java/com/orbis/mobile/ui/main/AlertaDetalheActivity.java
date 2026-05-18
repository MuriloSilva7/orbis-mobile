package com.orbis.mobile.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.model.Manutencao;
import com.orbis.mobile.model.ManutencoesResponse;
import com.orbis.mobile.model.TokenManager;
import com.orbis.mobile.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AlertaDetalheActivity extends AppCompatActivity {

    private TextView txtIdAlertaVariavel;
    private TextView txtIdSensorVariavel;
    private TextView txtIdMaquinaVariavel;
    private TextView txtIdTecnicoVariavel;

    private TextView txtMaquinaVariavel;
    private TextView txtSensorVariavel;
    private TextView txtCriadoEmVariavel;
    private TextView txtTipoVariavel;
    private TextView txtStatusVariavel;
    private TextView txtMensagemVariavel;

    private Button btnVoltar;
    private Button btnAceitar;
    private Button btnConcluir;
    private Button btnCriarManutencao;
    private Button btnVerHistorico;
    private Button btnCancelar;

    // ID da manutenção relacionada ao alerta
    private int manutencaoId = -1;
    private int maquinaId = -1;
    private int alertaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_detalhe);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnAceitar = findViewById(R.id.btnAceitar);
        btnConcluir = findViewById(R.id.btnConcluir);
        btnCriarManutencao = findViewById(R.id.btnCriarManutencao);
        btnVerHistorico = findViewById(R.id.btnVerHistorico);
        btnCancelar = findViewById(R.id.btnCancelar);

        txtIdAlertaVariavel = findViewById(R.id.txtIdAlertaVariavel);
        txtIdSensorVariavel = findViewById(R.id.txtIdSensorVariavel);
        txtIdMaquinaVariavel = findViewById(R.id.txtIdMaquinaVariavel);
        txtIdTecnicoVariavel = findViewById(R.id.txtIdTecnicoVariavel);

        txtMaquinaVariavel = findViewById(R.id.txtMaquinaVariavel);
        txtSensorVariavel = findViewById(R.id.txtSensorVariavel);
        txtCriadoEmVariavel = findViewById(R.id.txtCriadoEmVariavel);
        txtTipoVariavel = findViewById(R.id.txtTipoVariavel);
        txtStatusVariavel = findViewById(R.id.txtStatusVariavel);
        txtMensagemVariavel = findViewById(R.id.txtMensagemVariavel);

        btnVoltar.setOnClickListener(v -> finish());

        btnAceitar.setOnClickListener(v -> {
            int idAlerta = Integer.parseInt(txtIdAlertaVariavel.getText().toString());
            aceitarAlerta(idAlerta);
        });

        btnConcluir.setOnClickListener(v -> {
            if (manutencaoId != -1) {
                concluirAlerta(manutencaoId);
            } else {
                Toast.makeText(AlertaDetalheActivity.this, "Manutenção não encontrada", Toast.LENGTH_SHORT).show();
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
            } else {
                Toast.makeText(this, "Carregando dados do alerta...", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> {
            if (manutencaoId != -1) {
                cancelarAlerta(manutencaoId);
            } else {
                Toast.makeText(this, "Manutenção não encontrada", Toast.LENGTH_SHORT).show();
            }
        });

        alertaId = getIntent().getIntExtra("id_alerta", -1);
        carregarDetalhes(alertaId);
    }

    private void mostrarDialogRelato() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Relatar Manutenção");
        builder.setMessage("Descreva o que foi feito ou o que aconteceu:");

        final EditText input = new EditText(this);
        input.setHint("Digite aqui seu relato...");
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String relato = input.getText().toString().trim();
            if (!relato.isEmpty()) {
                salvarRelatoManutencao(relato);
            } else {
                Toast.makeText(this, "O relato não pode estar vazio", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void salvarRelatoManutencao(String relato) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("observacao", relato);

        apiService.updateManutencao(manutencaoId, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AlertaDetalheActivity.this, "Relato salvo com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertaDetalheActivity.this, "Erro ao salvar relato", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aceitarAlerta(int idAlerta) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("alertaId", idAlerta);
        body.put("observacao", "Alerta aceito pelo técnico");

        Call<Manutencao> call = apiService.createManutencao(body);
        call.enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        manutencaoId = response.body().getId();
                    }
                    txtStatusVariavel.setText("EM_ANDAMENTO");
                    txtIdTecnicoVariavel.setText("Técnico atribuído");
                    btnAceitar.setVisibility(View.GONE);
                    btnConcluir.setVisibility(View.VISIBLE);
                    btnCriarManutencao.setVisibility(View.VISIBLE);
                    btnCancelar.setVisibility(View.VISIBLE);

                    Toast.makeText(AlertaDetalheActivity.this, "Alerta aceito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertaDetalheActivity.this, "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void concluirAlerta(int idManutencao) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "RESOLVIDO");

        Call<Manutencao> call = apiService.updateManutencao(idManutencao, body);
        call.enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful()) {
                    txtStatusVariavel.setText("RESOLVIDO");
                    btnConcluir.setVisibility(View.GONE);
                    btnCriarManutencao.setVisibility(View.GONE);
                    btnCancelar.setVisibility(View.GONE);
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta concluído", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertaDetalheActivity.this, "Erro ao concluir", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelarAlerta(int idManutencao) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "CANCELADO");

        apiService.updateManutencao(idManutencao, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                if (response.isSuccessful()) {
                    txtStatusVariavel.setText("ATIVO");
                    btnAceitar.setVisibility(View.VISIBLE);
                    btnConcluir.setVisibility(View.GONE);
                    btnCriarManutencao.setVisibility(View.GONE);
                    btnCancelar.setVisibility(View.GONE);
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta cancelado e voltou para pendente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AlertaDetalheActivity.this, "Erro ao cancelar alerta", Toast.LENGTH_SHORT).show();
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
        Call<ManutencoesResponse> call = apiService.getManutencoes(1, 100);
        call.enqueue(new Callback<ManutencoesResponse>() {
            @Override
            public void onResponse(Call<ManutencoesResponse> call, Response<ManutencoesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Manutencao> manutencoes = response.body().getDados();
                    for (Manutencao manutencao : manutencoes) {
                        if (manutencao.getAlertaId() == idAlerta && !"CANCELADO".equals(manutencao.getStatus())) {
                            manutencaoId = manutencao.getId();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ManutencoesResponse> call, Throwable t) {
            }
        });
    }

    private void carregarDetalhes(int id) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Call<List<Alerta>> call = apiService.getAlertas();

        call.enqueue(new Callback<List<Alerta>>() {
            @Override
            public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Alerta alerta : response.body()) {
                        if (alerta.getId() == id) {
                            maquinaId = alerta.getMaquina().getId();
                            txtIdAlertaVariavel.setText(String.valueOf(alerta.getId()));
                            txtIdSensorVariavel.setText(String.valueOf(alerta.getSensor().getId()));
                            txtIdMaquinaVariavel.setText(String.valueOf(alerta.getMaquina().getId()));
                            txtIdTecnicoVariavel.setText(alerta.getTecnicoId() != null ? String.valueOf(alerta.getTecnicoId()) : "Sem técnico");
                            txtMaquinaVariavel.setText(alerta.getMaquina().getNome());
                            txtSensorVariavel.setText(alerta.getSensor().getTipo());
                            txtCriadoEmVariavel.setText(alerta.getCriadoEm());
                            txtTipoVariavel.setText(alerta.getTipo());
                            txtStatusVariavel.setText(alerta.getStatus());
                            txtMensagemVariavel.setText(alerta.getMensagem());

                            if ("ATIVO".equals(alerta.getStatus())) {
                                btnAceitar.setVisibility(View.VISIBLE);
                                btnConcluir.setVisibility(View.GONE);
                                btnCriarManutencao.setVisibility(View.GONE);
                                btnCancelar.setVisibility(View.GONE);
                            } else if ("EM_ANDAMENTO".equals(alerta.getStatus())) {
                                btnAceitar.setVisibility(View.GONE);
                                btnConcluir.setVisibility(View.VISIBLE);
                                btnCriarManutencao.setVisibility(View.VISIBLE);
                                btnCancelar.setVisibility(View.VISIBLE);
                                carregarManutencaoDoAlerta(alerta.getId());
                            } else {
                                btnAceitar.setVisibility(View.GONE);
                                btnConcluir.setVisibility(View.GONE);
                                btnCriarManutencao.setVisibility(View.GONE);
                                btnCancelar.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Alerta>> call, Throwable t) {
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao carregar alerta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
