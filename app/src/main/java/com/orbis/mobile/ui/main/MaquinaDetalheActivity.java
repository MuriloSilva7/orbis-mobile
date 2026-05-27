package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Maquina;
import com.orbis.mobile.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaquinaDetalheActivity extends AppCompatActivity {

    private TextView txtIdVariavel, txtNomeVariavel, txtSetorVariavel, txtTipoVariavel, 
                     txtCriticidadeVariavel, txtIntegridadeVariavel, txtEstadoVariavel;
    private ImageView imgMaquina;
    private ProgressBar progressMaquina;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maquina_detalhe);

        initViews();
        setupToolbar();

        int id = getIntent().getIntExtra("id_maquina", -1);
        carregarDetalhes(id);
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

    private void carregarDetalhes(int id) {
        setLoading(true);
        OrbisApiService apiService = RetrofitClient.getInstance(this).getApi();
        Call<Maquina> call = apiService.getMaquina(id);

        call.enqueue(new Callback<Maquina>() {
            @Override
            public void onResponse(Call<Maquina> call, Response<Maquina> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    preencherCampos(response.body());
                }
            }

            @Override
            public void onFailure(Call<Maquina> call, Throwable t) {
                setLoading(false);
            }
        });
    }

    private void preencherCampos(Maquina maquina) {
        Glide.with(this).load(maquina.getImagem()).into(imgMaquina);
        txtIdVariavel.setText(String.valueOf(maquina.getId()));
        txtNomeVariavel.setText(maquina.getNome());
        txtSetorVariavel.setText(maquina.getSetor());
        txtTipoVariavel.setText(maquina.getTipo());
        txtCriticidadeVariavel.setText(maquina.getCriticidade());
        txtIntegridadeVariavel.setText(String.valueOf(maquina.getIntegridade()));
        
        txtEstadoVariavel.setText(maquina.isAtivo() ? "ATIVO" : "INATIVO");
        if (maquina.isAtivo()) {
            txtEstadoVariavel.setTextColor(getColor(R.color.statusGreen));
            txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_green);
        } else {
            txtEstadoVariavel.setTextColor(getColor(R.color.statusRed));
            txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_red);
        }
    }
}