package com.orbis.mobile.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.model.Manutencao;
import com.orbis.mobile.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertaDetalheActivity extends AppCompatActivity {

    private TextView txtIdAlertaVariavel, txtMaquinaVariavel, txtSensorVariavel, 
                     txtCriadoEmVariavel, txtTipoVariavel, txtStatusVariavel, txtMensagemVariavel;
    
    private ImageView imgMaquinaAlerta;
    private Button btnVoltar, btnAceitar, btnConcluir, btnCriarManutencao, btnVerHistorico, btnCancelar;
    private LinearLayout layoutAcoesAndamento;
    private ProgressBar progressAlerta;

    private int manutencaoId = -1;
    private int alertaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
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
        imgMaquinaAlerta = findViewById(R.id.imgMaquinaAlerta);
        progressAlerta = findViewById(R.id.progressAlerta);

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

    private void setLoading(boolean isLoading) {
        if (progressAlerta != null) {
            progressAlerta.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnAceitar.setEnabled(!isLoading);
        btnConcluir.setEnabled(!isLoading);
        btnCancelar.setEnabled(!isLoading);
        btnCriarManutencao.setEnabled(!isLoading);
    }

    private void carregarDetalhes(int id) {
        setLoading(true);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        apiService.getAlertas().enqueue(new Callback<List<Alerta>>() {
            @Override
            public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    for (Alerta alerta : response.body()) {
                        if (alerta.getId() == id) {
                            preencherCampos(alerta);
                            atualizarInterfacePorStatus(alerta.getStatus());
                            carregarManutencaoDoAlerta(alerta.getId());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Alerta>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherCampos(Alerta alerta) {
        txtIdAlertaVariavel.setText(String.valueOf(alerta.getId()));
        txtMaquinaVariavel.setText(alerta.getMaquina().getNome());
        txtSensorVariavel.setText(alerta.getSensor().getTipo());
        txtCriadoEmVariavel.setText(formatarData(alerta.getCriadoEm()));
        txtTipoVariavel.setText(alerta.getTipo());
        txtMensagemVariavel.setText(alerta.getMensagem());

        if (alerta.getMaquina().getImagem() != null && !alerta.getMaquina().getImagem().isEmpty()) {
            Glide.with(this)
                    .load(alerta.getMaquina().getImagem())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(imgMaquinaAlerta);
        }
        
        atualizarStatusUI(alerta.getStatus());
    }

    private String formatarData(String dataIso) {
        if (dataIso == null || dataIso.isEmpty()) return "--";
        try {
            // Tenta converter de ISO-8601 para Date
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdfEntrada.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdfEntrada.parse(dataIso);

            // Formata para o padrão brasileiro
            SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
            return sdfSaida.format(date);
        } catch (Exception e) {
            try {
                // Tenta um formato alternativo sem milissegundos se falhar
                SimpleDateFormat sdfEntrada2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date date = sdfEntrada2.parse(dataIso);
                SimpleDateFormat sdfSaida = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
                return sdfSaida.format(date);
            } catch (Exception e2) {
                return dataIso; // Retorna original se não conseguir formatar
            }
        }
    }

    private void atualizarStatusUI(String status) {
        txtStatusVariavel.setText(status);
        
        switch (status) {
            case "ATIVO":
                txtStatusVariavel.setTextColor(getColor(R.color.statusRed));
                txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_red);
                break;
            case "EM_ANDAMENTO":
                txtStatusVariavel.setTextColor(getColor(R.color.statusOrange));
                txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_purple);
                break;
            case "RESOLVIDO":
                txtStatusVariavel.setTextColor(getColor(R.color.statusGreen));
                txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_green);
                break;
            default:
                txtStatusVariavel.setTextColor(getColor(R.color.gray));
                txtStatusVariavel.setBackgroundResource(R.drawable.badge_outline_gray);
                break;
        }
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
        setLoading(true);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("alertaId", idAlerta);
        body.put("observacao", "Alerta aceito pelo técnico");

        apiService.createManutencao(body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    manutencaoId = response.body().getId();
                    atualizarStatusUI("EM_ANDAMENTO");
                    atualizarInterfacePorStatus("EM_ANDAMENTO");
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta aceito!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao aceitar alerta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void concluirAlerta(int idManutencao) {
        setLoading(true);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "RESOLVIDO");

        apiService.updateManutencao(idManutencao, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    atualizarStatusUI("RESOLVIDO");
                    atualizarInterfacePorStatus("RESOLVIDO");
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta concluído!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao concluir", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelarAlerta(int idManutencao) {
        setLoading(true);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("status", "ENCERRADO_SEM_SOLUCAO");

        apiService.updateManutencao(idManutencao, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    atualizarStatusUI("ATIVO");
                    atualizarInterfacePorStatus("ATIVO");
                    Toast.makeText(AlertaDetalheActivity.this, "Alerta cancelado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
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
        setLoading(true);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, Object> body = new HashMap<>();
        body.put("observacao", relato);

        apiService.updateManutencao(manutencaoId, body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                setLoading(false);
                if (response.isSuccessful()) Toast.makeText(AlertaDetalheActivity.this, "Relato salvo!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao salvar relato", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
