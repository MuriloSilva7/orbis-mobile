package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.model.PredicaoAlertas;
import com.orbis.mobile.model.PredicaoRisco;
import com.orbis.mobile.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaquinaDetalheActivity extends AppCompatActivity {


    // --- Máquina principal ---
    private TextView txtIdVariavel, txtNomeVariavel, txtSetorVariavel, txtTipoVariavel,
            txtCriticidadeVariavel, txtIntegridadeVariavel, txtEstadoVariavel;
    private ImageView imgMaquina;
    private LinearProgressIndicator progressMaquina;
    private LinearProgressIndicator progressBarIntegridade;
    private MaterialButton btnVoltar, btnCriarPreventiva;

    // --- Card Risco ---
    private MaterialCardView cardRisco;
    private TextView txtConfiancaGeral;
    private TextView txtRiscoInstabilidadeClass, txtRiscoInstabilidade24h, txtRiscoInstabilidade72h;
    private TextView txtRiscoAlertaClass, txtRiscoAlerta24h, txtRiscoAlerta72h;
    private TextView txtRiscoManutencaoClass, txtRiscoManutencao24h, txtRiscoManutencao72h;

    // --- Card Predição de Alertas ---
    private MaterialCardView cardPredicaoAlertas;
    private LinearLayout layoutProximoAlerta, layoutInstabilidade, layoutModeloIntegridade;
    private TextView txtProximoAlertaData, txtProximoAlertaTipo, txtProximoAlertaConfianca;
    private TextView txtProximoAlertaAusencia;
    private TextView txtInstabilidadeData, txtInstabilidadeLimiar, txtInstabilidadeConfianca;
    private TextView txtInstabilidadeAusencia;
    private TextView txtModeloR2, txtModeloSlope, txtModeloPontos;

    private int maquinaId = -1;
    private int respostasRecebidas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maquina_detalhe);

        initViews();
        setupToolbar();

        maquinaId = getIntent().getIntExtra("id_maquina", -1);
        if (maquinaId != -1) {
            setLoading(true);
            carregarDetalhes(maquinaId);
            carregarPredicaoRisco(maquinaId);
            carregarPredicaoAlertas(maquinaId);
        }
    }

    private void initViews() {
        txtIdVariavel = findViewById(R.id.txtIdVariavel);
        txtNomeVariavel = findViewById(R.id.txtNomeVariavel);
        txtSetorVariavel = findViewById(R.id.txtSetorVariavel);
        txtTipoVariavel = findViewById(R.id.txtTipoVariavel);
        txtCriticidadeVariavel = findViewById(R.id.txtCriticidadeVariavel);
        txtIntegridadeVariavel = findViewById(R.id.txtIntegridadeVariavel);
        txtEstadoVariavel = findViewById(R.id.txtEstadoVariavel);
        imgMaquina = findViewById(R.id.imgMaquina);
        progressMaquina = findViewById(R.id.progressMaquina);
        progressBarIntegridade = findViewById(R.id.progressBarIntegridade);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> finish());
        
        btnCriarPreventiva = findViewById(R.id.btnCriarPreventiva);
        btnCriarPreventiva.setOnClickListener(v -> mostrarDialogPreventiva());

        cardRisco = findViewById(R.id.cardRisco);
        txtConfiancaGeral = findViewById(R.id.txtConfiancaGeral);
        txtRiscoInstabilidadeClass = findViewById(R.id.txtRiscoInstabilidadeClass);
        txtRiscoInstabilidade24h = findViewById(R.id.txtRiscoInstabilidade24h);
        txtRiscoInstabilidade72h = findViewById(R.id.txtRiscoInstabilidade72h);
        txtRiscoAlertaClass = findViewById(R.id.txtRiscoAlertaClass);
        txtRiscoAlerta24h = findViewById(R.id.txtRiscoAlerta24h);
        txtRiscoAlerta72h = findViewById(R.id.txtRiscoAlerta72h);
        txtRiscoManutencaoClass = findViewById(R.id.txtRiscoManutencaoClass);
        txtRiscoManutencao24h = findViewById(R.id.txtRiscoManutencao24h);
        txtRiscoManutencao72h = findViewById(R.id.txtRiscoManutencao72h);

        cardPredicaoAlertas = findViewById(R.id.cardPredicaoAlertas);
        layoutProximoAlerta = findViewById(R.id.layoutProximoAlerta);
        layoutInstabilidade = findViewById(R.id.layoutInstabilidade);
        layoutModeloIntegridade = findViewById(R.id.layoutModeloIntegridade);
        txtProximoAlertaData = findViewById(R.id.txtProximoAlertaData);
        txtProximoAlertaTipo = findViewById(R.id.txtProximoAlertaTipo);
        txtProximoAlertaConfianca = findViewById(R.id.txtProximoAlertaConfianca);
        txtProximoAlertaAusencia = findViewById(R.id.txtProximoAlertaAusencia);
        txtInstabilidadeData = findViewById(R.id.txtInstabilidadeData);
        txtInstabilidadeLimiar = findViewById(R.id.txtInstabilidadeLimiar);
        txtInstabilidadeConfianca = findViewById(R.id.txtInstabilidadeConfianca);
        txtInstabilidadeAusencia = findViewById(R.id.txtInstabilidadeAusencia);
        txtModeloR2 = findViewById(R.id.txtModeloR2);
        txtModeloSlope = findViewById(R.id.txtModeloSlope);
        txtModeloPontos = findViewById(R.id.txtModeloPontos);
        
        checkUserRole();
    }

    private void checkUserRole() {
        android.content.SharedPreferences prefs = getSharedPreferences("orbis_prefs", MODE_PRIVATE);
        String role = prefs.getString("user_role", "");
        if ("TECNICO".equals(role)) {
            btnCriarPreventiva.setVisibility(View.VISIBLE);
        } else {
            btnCriarPreventiva.setVisibility(View.GONE);
        }
    }

    private void mostrarDialogPreventiva() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nova Manutenção Preventiva");
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Observação (opcional)");
        builder.setView(input);

        builder.setPositiveButton("Criar", (dialog, which) -> {
            String obs = input.getText().toString().trim();
            criarPreventiva(obs);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void criarPreventiva(String observacao) {
        setLoading(true);
        OrbisApiService api = RetrofitClient.getInstance(this).getApi();
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("tipo", "PREVENTIVA");
        body.put("maquinaId", maquinaId);
        body.put("observacao", observacao);

        api.createManutencao(body).enqueue(new Callback<com.orbis.mobile.model.Manutencao>() {
            @Override
            public void onResponse(Call<com.orbis.mobile.model.Manutencao> call, Response<com.orbis.mobile.model.Manutencao> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    android.widget.Toast.makeText(MaquinaDetalheActivity.this, "Manutenção preventiva criada!", android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(MaquinaDetalheActivity.this, "Erro ao criar preventiva", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.orbis.mobile.model.Manutencao> call, Throwable t) {
                setLoading(false);
                android.widget.Toast.makeText(MaquinaDetalheActivity.this, "Erro de conexão", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setLoading(boolean isLoading) {
        if (progressMaquina != null) {
            progressMaquina.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void registrarResposta() {
        respostasRecebidas++;
        if (respostasRecebidas >= 3) {
            setLoading(false);
        }
    }

    // ----------------------------------------------------------------
    // Chamadas de rede
    // ----------------------------------------------------------------

    private void carregarDetalhes(int id) {
        OrbisApiService api = RetrofitClient.getInstance(this).getApi();
        api.getMaquina(id).enqueue(new Callback<Maquina>() {
            @Override
            public void onResponse(Call<Maquina> call, Response<Maquina> response) {
                registrarResposta();
                if (response.isSuccessful() && response.body() != null) {
                    preencherCampos(response.body());
                }
            }

            @Override
            public void onFailure(Call<Maquina> call, Throwable t) {
                registrarResposta();
            }
        });
    }

    private void carregarPredicaoRisco(int id) {
        OrbisApiService api = RetrofitClient.getInstance(this).getApi();
        api.getPredicaoRisco(id).enqueue(new Callback<PredicaoRisco>() {
            @Override
            public void onResponse(Call<PredicaoRisco> call, Response<PredicaoRisco> response) {
                registrarResposta();
                if (response.isSuccessful() && response.body() != null) {
                    preencherRisco(response.body());
                }
            }

            @Override
            public void onFailure(Call<PredicaoRisco> call, Throwable t) {
                registrarResposta();
            }
        });
    }

    private void carregarPredicaoAlertas(int id) {
        OrbisApiService api = RetrofitClient.getInstance(this).getApi();
        api.getPredicaoAlertas(id).enqueue(new Callback<PredicaoAlertas>() {
            @Override
            public void onResponse(Call<PredicaoAlertas> call, Response<PredicaoAlertas> response) {
                registrarResposta();
                if (response.isSuccessful() && response.body() != null) {
                    preencherPredicaoAlertas(response.body());
                }
            }

            @Override
            public void onFailure(Call<PredicaoAlertas> call, Throwable t) {
                registrarResposta();
            }
        });
    }

    // ----------------------------------------------------------------
    // Preencher campos
    // ----------------------------------------------------------------

    private void preencherCampos(Maquina maquina) {
        Glide.with(this).load(maquina.getImagem()).into(imgMaquina);
        txtIdVariavel.setText(String.valueOf(maquina.getId()));
        txtNomeVariavel.setText(maquina.getNome());
        txtSetorVariavel.setText(maquina.getSetor());
        txtTipoVariavel.setText(maquina.getTipo());
        txtCriticidadeVariavel.setText(maquina.getCriticidade());
        
        float integridade = maquina.getIntegridade();
        txtIntegridadeVariavel.setText(String.format(Locale.getDefault(), "%.1f%%", integridade));

        if (progressBarIntegridade != null) {
            progressBarIntegridade.setProgress((int) integridade);
            
            // Define a cor da barra de acordo com a porcentagem
            if (integridade >= 70) {
                progressBarIntegridade.setIndicatorColor(ContextCompat.getColor(this, R.color.statusGreen));
            } else if (integridade >= 30) {
                progressBarIntegridade.setIndicatorColor(ContextCompat.getColor(this, R.color.statusOrange));
            } else {
                progressBarIntegridade.setIndicatorColor(ContextCompat.getColor(this, R.color.statusRed));
            }
        }

        if (maquina.isAtivo()) {
            txtEstadoVariavel.setText(getString(R.string.status_ativo));
            txtEstadoVariavel.setTextColor(ContextCompat.getColor(this, R.color.statusGreen));
            txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_green);
        } else {
            txtEstadoVariavel.setText(getString(R.string.status_inativo));
            txtEstadoVariavel.setTextColor(ContextCompat.getColor(this, R.color.statusRed));
            txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_red);
        }
    }

    private void preencherRisco(PredicaoRisco risco) {
        cardRisco.setVisibility(View.VISIBLE);

        if (risco.getConfiancaGeral() != null) {
            int pct = (int) Math.round(risco.getConfiancaGeral() * 100);
            txtConfiancaGeral.setText(getString(R.string.label_confianca_format, pct));
        }

        if (risco.getRiscos() == null) return;

        preencherBlocoRisco(risco.getRiscos().getInstabilidade(),
                txtRiscoInstabilidadeClass, txtRiscoInstabilidade24h, txtRiscoInstabilidade72h);
        preencherBlocoRisco(risco.getRiscos().getAlerta(),
                txtRiscoAlertaClass, txtRiscoAlerta24h, txtRiscoAlerta72h);
        preencherBlocoRisco(risco.getRiscos().getManutencao(),
                txtRiscoManutencaoClass, txtRiscoManutencao24h, txtRiscoManutencao72h);
    }

    private void preencherBlocoRisco(PredicaoRisco.BlocoRisco bloco,
                                     TextView txtClass, TextView txt24h, TextView txt72h) {
        if (bloco == null || !bloco.isDisponivel()) {
            txtClass.setText(getString(R.string.label_sem_dados));
            txtClass.setTextColor(ContextCompat.getColor(this, R.color.gray));
            txtClass.setBackgroundResource(R.drawable.badge_outline_gray);
            txt24h.setText(R.string.label_placeholder_dash);
            txt72h.setText(R.string.label_placeholder_dash);
            return;
        }

        String classif = bloco.getClassificacao() != null ? bloco.getClassificacao() : "";
        txtClass.setText(classif.isEmpty() ? getString(R.string.label_placeholder_dash) : classif);

        int cor;
        int bg;
        if ("ALTO".equals(classif)) {
            cor = ContextCompat.getColor(this, R.color.statusRed);
            bg = R.drawable.badge_outline_red;
        } else if ("MEDIO".equals(classif)) {
            cor = ContextCompat.getColor(this, R.color.statusOrange);
            bg = R.drawable.badge_outline_gray;
        } else {
            cor = ContextCompat.getColor(this, R.color.statusGreen);
            bg = R.drawable.badge_outline_green;
        }
        txtClass.setTextColor(cor);
        txtClass.setBackgroundResource(bg);

        txt24h.setText(bloco.getH24() != null ? formatPct(bloco.getH24()) : getString(R.string.label_placeholder_dash));
        txt72h.setText(bloco.getH72() != null ? formatPct(bloco.getH72()) : getString(R.string.label_placeholder_dash));
    }

    private void preencherPredicaoAlertas(PredicaoAlertas predicao) {
        cardPredicaoAlertas.setVisibility(View.VISIBLE);

        // Próximo alerta
        if (predicao.getProximoAlerta() != null) {
            PredicaoAlertas.AlertaPrevisao alerta = predicao.getProximoAlerta();
            layoutProximoAlerta.setVisibility(View.VISIBLE);
            txtProximoAlertaAusencia.setVisibility(View.GONE);
            txtProximoAlertaData.setText(formatarData(alerta.getDataPrevista()));
            txtProximoAlertaTipo.setText(formatarTipo(alerta.getTipo()));
            txtProximoAlertaConfianca.setText(
                    alerta.getConfianca() != null ? formatPct(alerta.getConfianca()) : getString(R.string.label_placeholder_dash));
        } else {
            layoutProximoAlerta.setVisibility(View.GONE);
            txtProximoAlertaAusencia.setVisibility(View.VISIBLE);
            String motivo = predicao.getAusenciaProximoAlerta() != null
                    ? formatarMotivo(predicao.getAusenciaProximoAlerta().getMotivo())
                    : getString(R.string.msg_sem_previsao);
            txtProximoAlertaAusencia.setText(motivo);
        }

        // Instabilidade
        if (predicao.getInstabilidade() != null) {
            PredicaoAlertas.AlertaPrevisao inst = predicao.getInstabilidade();
            layoutInstabilidade.setVisibility(View.VISIBLE);
            txtInstabilidadeAusencia.setVisibility(View.GONE);
            txtInstabilidadeData.setText(formatarData(inst.getDataPrevista()));
            txtInstabilidadeLimiar.setText(inst.getIntegridadeLimiar() != null
                    ? getString(R.string.label_limiar_prefix) + String.format(Locale.getDefault(), "%.1f", inst.getIntegridadeLimiar())
                    : "");
            txtInstabilidadeConfianca.setText(
                    inst.getConfianca() != null ? formatPct(inst.getConfianca()) : getString(R.string.label_placeholder_dash));
        } else {
            layoutInstabilidade.setVisibility(View.GONE);
            txtInstabilidadeAusencia.setVisibility(View.VISIBLE);
            String motivo = predicao.getAusenciaInstabilidade() != null
                    ? formatarMotivo(predicao.getAusenciaInstabilidade().getMotivo())
                    : getString(R.string.msg_sem_previsao);
            txtInstabilidadeAusencia.setText(motivo);
        }

        // Modelo de integridade
        if (predicao.getModeloIntegridade() != null) {
            PredicaoAlertas.ModeloIntegridade modelo = predicao.getModeloIntegridade();
            layoutModeloIntegridade.setVisibility(View.VISIBLE);
            txtModeloR2.setText(modelo.getR2() != null
                    ? String.format(Locale.getDefault(), "%.2f", modelo.getR2()) : getString(R.string.label_placeholder_dash));
            txtModeloSlope.setText(modelo.getSlope() != null
                    ? String.format(Locale.getDefault(), "%.2f", modelo.getSlope()) : getString(R.string.label_placeholder_dash));
            txtModeloPontos.setText(modelo.getPontosUsados() != null
                    ? String.valueOf(modelo.getPontosUsados()) : getString(R.string.label_placeholder_dash));
        } else {
            layoutModeloIntegridade.setVisibility(View.GONE);
        }
    }

    // ----------------------------------------------------------------
    // Helpers de formatação
    // ----------------------------------------------------------------

    private String formatPct(double valor) {
        return (int) Math.round(valor * 100) + "%";
    }

    private String formatarData(String isoData) {
        if (isoData == null) return getString(R.string.label_placeholder_dash);
        String[] formatos = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
        };
        for (String formato : formatos) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formato, Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(isoData);
                if (date != null) {
                    SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    return out.format(date);
                }
            } catch (ParseException ignored) { }
        }
        return isoData;
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "";
        if ("TENDENCIA_CURTA".equals(tipo)) return getString(R.string.label_tendencia_curta);
        if ("TENDENCIA_LONGA".equals(tipo)) return getString(R.string.label_tendencia_longa);
        if ("INSTABILIDADE".equals(tipo))   return getString(R.string.label_instabilidade);
        return tipo;
    }

    private String formatarMotivo(String motivo) {
        if (motivo == null) return getString(R.string.msg_sem_previsao);
        if ("historico_insuficiente".equals(motivo))            return getString(R.string.motivo_historico_insuficiente);
        if ("modelo_nao_pode_ser_calculado".equals(motivo))     return getString(R.string.motivo_modelo_nao_calculado);
        if ("tendencia_nao_confiavel".equals(motivo))           return getString(R.string.motivo_tendencia_nao_confiavel);
        if ("sem_historico_de_alertas_do_tipo".equals(motivo))  return getString(R.string.motivo_sem_historico_tipo);
        if ("evento_ja_ocorrido".equals(motivo))                return getString(R.string.motivo_evento_ocorrido);
        if ("previsao_fora_da_janela".equals(motivo))           return getString(R.string.motivo_fora_da_janela);
        if ("sem_alerta_previsivel".equals(motivo))             return getString(R.string.motivo_sem_alerta_previsivel);
        if ("sem_leitura_recente".equals(motivo))               return getString(R.string.motivo_sem_leitura_recente);
        if ("leituras_insuficientes".equals(motivo))            return getString(R.string.motivo_leituras_insuficientes);
        if ("historico_de_alertas_insuficiente".equals(motivo)) return getString(R.string.motivo_historico_alertas_insuficiente);
        return motivo;
    }
}