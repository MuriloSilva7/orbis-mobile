package com.orbis.mobile.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.orbis.mobile.adapter.AlertaEventoAdapter;
import com.orbis.mobile.model.Alerta;
import com.orbis.mobile.model.AlertaEvento;
import com.orbis.mobile.model.Manutencao;
import com.orbis.mobile.network.RetrofitClient;
import android.content.SharedPreferences;
import java.util.ArrayList;

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
    private MaterialButton btnVoltar, btnAceitar, btnConcluir, btnCriarManutencao, btnVerHistorico, btnCancelar;
    private LinearLayout layoutAcoesAndamento;
    private LinearProgressIndicator progressAlerta;
    private RecyclerView recyclerEventos;
    private AlertaEventoAdapter eventoAdapter;
    private MaterialCardView cardNovoComentario;
    private TextInputEditText edtComentario;
    private MaterialButton btnEnviarComentario;

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

        recyclerEventos = findViewById(R.id.recyclerEventos);
        cardNovoComentario = findViewById(R.id.cardNovoComentario);
        edtComentario = findViewById(R.id.edtComentario);
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario);

        setupRecyclerView();
        checkUserRole();
    }

    private void setupRecyclerView() {
        eventoAdapter = new AlertaEventoAdapter(new ArrayList<>());
        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));
        recyclerEventos.setAdapter(eventoAdapter);
    }

    private void checkUserRole() {
        SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
        String role = prefs.getString("user_role", "");
        
        // Apenas ADMIN e TECNICO podem comentar
        if ("ADMIN".equals(role) || "TECNICO".equals(role)) {
            cardNovoComentario.setVisibility(View.VISIBLE);
        } else {
            cardNovoComentario.setVisibility(View.GONE);
        }
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
                Toast.makeText(this, getString(R.string.error_manutencao_nao_encontrada), Toast.LENGTH_SHORT).show();
            }
        });

        btnCriarManutencao.setOnClickListener(v -> {
            if (manutencaoId != -1) {
                mostrarDialogRelato();
            } else {
                Toast.makeText(this, getString(R.string.msg_aguarde_manutencao), Toast.LENGTH_SHORT).show();
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

        btnEnviarComentario.setOnClickListener(v -> {
            String msg = edtComentario.getText().toString().trim();
            if (!msg.isEmpty()) {
                enviarComentario(msg);
            } else {
                Toast.makeText(this, "Digite um comentário", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarComentario(String mensagem) {
        setLoading(true);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Map<String, String> body = new HashMap<>();
        body.put("mensagem", mensagem);

        apiService.criarComentario(alertaId, body).enqueue(new Callback<AlertaEvento>() {
            @Override
            public void onResponse(Call<AlertaEvento> call, Response<AlertaEvento> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    edtComentario.setText("");
                    carregarEventos(alertaId);
                    Toast.makeText(AlertaDetalheActivity.this, "Comentário enviado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AlertaEvento> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, "Erro ao enviar comentário", Toast.LENGTH_SHORT).show();
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
                            carregarEventos(alerta.getId());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Alerta>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, getString(R.string.error_carregar_detalhes), Toast.LENGTH_SHORT).show();
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
        body.put("observacao", getString(R.string.msg_alerta_aceito_tecnico));

        apiService.createManutencao(body).enqueue(new Callback<Manutencao>() {
            @Override
            public void onResponse(Call<Manutencao> call, Response<Manutencao> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    manutencaoId = response.body().getId();
                    atualizarStatusUI("EM_ANDAMENTO");
                    atualizarInterfacePorStatus("EM_ANDAMENTO");
                    Toast.makeText(AlertaDetalheActivity.this, getString(R.string.msg_alerta_aceito), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, getString(R.string.error_aceitar_alerta), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AlertaDetalheActivity.this, getString(R.string.msg_alerta_concluido), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, getString(R.string.error_concluir), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AlertaDetalheActivity.this, getString(R.string.msg_alerta_cancelado), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, getString(R.string.error_conexao), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarEventos(int idAlerta) {
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        apiService.getAlertaEventos(idAlerta).enqueue(new Callback<List<AlertaEvento>>() {
            @Override
            public void onResponse(Call<List<AlertaEvento>> call, Response<List<AlertaEvento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventoAdapter.updateList(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AlertaEvento>> call, Throwable t) {}
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
        builder.setTitle(getString(R.string.title_relatar_manutencao));
        final EditText input = new EditText(this);
        input.setHint(getString(R.string.hint_descricao_feito));
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.btn_salvar), (dialog, which) -> {
            String relato = input.getText().toString().trim();
            if (!relato.isEmpty()) salvarRelatoManutencao(relato);
        });
        builder.setNegativeButton(getString(R.string.btn_cancelar), null);
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
                if (response.isSuccessful()) Toast.makeText(AlertaDetalheActivity.this, getString(R.string.msg_relato_salvo), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Manutencao> call, Throwable t) {
                setLoading(false);
                Toast.makeText(AlertaDetalheActivity.this, getString(R.string.error_salvar_relato), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
