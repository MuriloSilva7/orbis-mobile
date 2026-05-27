package com.orbis.mobile.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.orbis.mobile.R;
import com.orbis.mobile.api.OrbisApiService;
import com.orbis.mobile.model.Usuario;
import com.orbis.mobile.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TecnicoDetalheActivity extends AppCompatActivity {

    private TextView txtCargoVariavel;
    private TextView txtNomeVariavel;
    private TextView txtEspecialidadeVariavel;
    private TextView txtTelefoneVariavel;
    private TextView txtEmailVariavel;
    private TextView txtEstadoVariavel;
    private ImageView imgTecnico;
    private ProgressBar progressTecnico;

    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico_detalhe);

        setupToolbar();

        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(v -> finish());

        txtCargoVariavel = findViewById(R.id.txtCargoVariavel);
        txtNomeVariavel = findViewById(R.id.txtNomeVariavel);
        txtEspecialidadeVariavel = findViewById(R.id.txtEspecialidadeVariavel);
        txtTelefoneVariavel = findViewById(R.id.txtTelefoneVariavel);
        txtEmailVariavel = findViewById(R.id.txtEmailVariavel);
        txtEstadoVariavel = findViewById(R.id.txtEstadoVariavel);
        imgTecnico = findViewById(R.id.imgTecnicoVariavel);
        progressTecnico = findViewById(R.id.progressTecnico);

        int id = getIntent().getIntExtra("id_tecnico", -1);

        if (id != -1) {
            carregarDetalhes(id);
        }
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
        if (progressTecnico != null) {
            progressTecnico.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void carregarDetalhes(int id) {
        setLoading(true);
        OrbisApiService apiService = RetrofitClient
                .getInstance(this)
                .getApi();

        Call<Usuario> call = apiService.getUsuario(id);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {

                    Usuario tecnico = response.body();

                    Glide.with(TecnicoDetalheActivity.this)
                            .load(tecnico.getFotoPerfil())
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .into(imgTecnico);

                    txtCargoVariavel.setText(tecnico.getRole());
                    txtNomeVariavel.setText(tecnico.getNome());
                    txtEspecialidadeVariavel.setText(tecnico.getEspecialidade());
                    txtTelefoneVariavel.setText(tecnico.getTelefone());
                    txtEmailVariavel.setText(tecnico.getEmail());

                    txtEstadoVariavel.setText(tecnico.isAtivo() ? "ATIVO" : "INATIVO");
                    if (tecnico.isAtivo()) {
                        txtEstadoVariavel.setTextColor(getColor(R.color.statusGreen));
                        txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_green);
                    } else {
                        txtEstadoVariavel.setTextColor(getColor(R.color.statusRed));
                        txtEstadoVariavel.setBackgroundResource(R.drawable.badge_outline_red);
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                setLoading(false);
                Toast.makeText(
                        TecnicoDetalheActivity.this,
                        "Erro ao carregar técnico: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
