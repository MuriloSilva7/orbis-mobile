package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
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
    private Button btnVoltar;

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
        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> finish());

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
        txtIntegridadeVariavel.setText(String.valueOf(maquina.getIntegridade()));

        if (maquina.isAtivo()) {
            txtEstadoVariavel.setText("ATIVO");
            txtEstadoVariavel.setTextColor(ContextCompat.getColor(this, R.color.statusGreen));
            txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_green);
        } else {
            txtEstadoVariavel.setText("INATIVO");
            txtEstadoVariavel.setTextColor(ContextCompat.getColor(this, R.color.statusRed));
            txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_red);
        }
    }

    private void preencherRisco(PredicaoRisco risco) {
        cardRisco.setVisibility(View.VISIBLE);

        if (risco.getConfiancaGeral() != null) {
            int pct = (int) Math.round(risco.getConfiancaGeral() * 100);
            txtConfiancaGeral.setText(pct + "% conf.");
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
            txtClass.setText("S/D");
            txtClass.setTextColor(ContextCompat.getColor(this, R.color.gray));
            txtClass.setBackgroundResource(R.drawable.badge_outline_gray);
            txt24h.setText("--");
            txt72h.setText("--");
            return;
        }

        String classif = bloco.getClassificacao() != null ? bloco.getClassificacao() : "";
        txtClass.setText(classif.isEmpty() ? "--" : classif);

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

        txt24h.setText(bloco.getH24() != null ? formatPct(bloco.getH24()) : "--");
        txt72h.setText(bloco.getH72() != null ? formatPct(bloco.getH72()) : "--");
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
                    alerta.getConfianca() != null ? formatPct(alerta.getConfianca()) : "--");
        } else {
            layoutProximoAlerta.setVisibility(View.GONE);
            txtProximoAlertaAusencia.setVisibility(View.VISIBLE);
            String motivo = predicao.getAusenciaProximoAlerta() != null
                    ? formatarMotivo(predicao.getAusenciaProximoAlerta().getMotivo())
                    : "Sem previsão disponível";
            txtProximoAlertaAusencia.setText(motivo);
        }

        // Instabilidade
        if (predicao.getInstabilidade() != null) {
            PredicaoAlertas.AlertaPrevisao inst = predicao.getInstabilidade();
            layoutInstabilidade.setVisibility(View.VISIBLE);
            txtInstabilidadeAusencia.setVisibility(View.GONE);
            txtInstabilidadeData.setText(formatarData(inst.getDataPrevista()));
            txtInstabilidadeLimiar.setText(inst.getIntegridadeLimiar() != null
                    ? "Limiar: " + String.format(Locale.getDefault(), "%.1f", inst.getIntegridadeLimiar())
                    : "");
            txtInstabilidadeConfianca.setText(
                    inst.getConfianca() != null ? formatPct(inst.getConfianca()) : "--");
        } else {
            layoutInstabilidade.setVisibility(View.GONE);
            txtInstabilidadeAusencia.setVisibility(View.VISIBLE);
            String motivo = predicao.getAusenciaInstabilidade() != null
                    ? formatarMotivo(predicao.getAusenciaInstabilidade().getMotivo())
                    : "Sem previsão disponível";
            txtInstabilidadeAusencia.setText(motivo);
        }

        // Modelo de integridade
        if (predicao.getModeloIntegridade() != null) {
            PredicaoAlertas.ModeloIntegridade modelo = predicao.getModeloIntegridade();
            layoutModeloIntegridade.setVisibility(View.VISIBLE);
            txtModeloR2.setText(modelo.getR2() != null
                    ? String.format(Locale.getDefault(), "%.2f", modelo.getR2()) : "--");
            txtModeloSlope.setText(modelo.getSlope() != null
                    ? String.format(Locale.getDefault(), "%.2f", modelo.getSlope()) : "--");
            txtModeloPontos.setText(modelo.getPontosUsados() != null
                    ? String.valueOf(modelo.getPontosUsados()) : "--");
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
        if (isoData == null) return "--";
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
        if ("TENDENCIA_CURTA".equals(tipo)) return "Tendência curta";
        if ("TENDENCIA_LONGA".equals(tipo)) return "Tendência longa";
        if ("INSTABILIDADE".equals(tipo))   return "Instabilidade";
        return tipo;
    }

    private String formatarMotivo(String motivo) {
        if (motivo == null) return "Sem previsão disponível";
        if ("historico_insuficiente".equals(motivo))            return "Histórico insuficiente";
        if ("modelo_nao_pode_ser_calculado".equals(motivo))     return "Modelo não pôde ser calculado";
        if ("tendencia_nao_confiavel".equals(motivo))           return "Tendência não confiável";
        if ("sem_historico_de_alertas_do_tipo".equals(motivo))  return "Sem histórico de alertas do tipo";
        if ("evento_ja_ocorrido".equals(motivo))                return "Evento já ocorrido";
        if ("previsao_fora_da_janela".equals(motivo))           return "Previsão fora da janela";
        if ("sem_alerta_previsivel".equals(motivo))             return "Sem alerta previsível";
        if ("sem_leitura_recente".equals(motivo))               return "Sem leitura recente";
        if ("leituras_insuficientes".equals(motivo))            return "Leituras insuficientes";
        if ("historico_de_alertas_insuficiente".equals(motivo)) return "Histórico de alertas insuficiente";
        return motivo;
    }
}