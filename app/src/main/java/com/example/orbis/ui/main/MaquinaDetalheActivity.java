package com.example.orbis.ui.main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.orbis.R;
import com.example.orbis.api.OrbisApiService;
import com.example.orbis.model.Maquina;
import com.example.orbis.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaquinaDetalheActivity extends AppCompatActivity {

    private TextView txtNomeVariavel;
    private TextView txtSetorVariavel;
    private TextView txtTipoVariavel;
    private TextView txtCriticidadeVariavel;
    private TextView txtIntegridadeVariavel;
    private TextView txtEstadoVariavel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maquina_detalhe);

        txtNomeVariavel = findViewById(R.id.txtNomeVariavel);
        txtSetorVariavel = findViewById(R.id.txtSetorVariavel);
        txtTipoVariavel = findViewById(R.id.txtTipoVariavel);
        txtCriticidadeVariavel = findViewById(R.id.txtCriticidadeVariavel);
        txtIntegridadeVariavel = findViewById(R.id.txtIntegridadeVariavel);
        txtEstadoVariavel = findViewById(R.id.txtEstadoVariavel);

        int id = getIntent().getIntExtra("id_maquina", -1);

        carregarDetalhes(id);
    }

    private void carregarDetalhes(int id) {
        OrbisApiService apiService = RetrofitClient
                .getInstance()
                .getApi();

        Call<Maquina> call = apiService.getMaquina(id);

        call.enqueue(new Callback<Maquina>() {
            @Override
            public void onResponse(Call<Maquina> call, Response<Maquina> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Maquina maquina = response.body();

                    txtNomeVariavel.setText(maquina.getNome());
                    txtSetorVariavel.setText(maquina.getSetor());
                    txtTipoVariavel.setText(maquina.getTipo());
                    txtCriticidadeVariavel.setText(maquina.getCriticidade());
                    txtIntegridadeVariavel.setText(
                            String.valueOf(maquina.getIntegridade())
                    );
                    txtEstadoVariavel.setText(
                            maquina.isAtivo() ? "Ativo" : "Inativo"
                    );
                }
            }

            @Override
            public void onFailure(Call<Maquina> call, Throwable t) {

            }
        });
    }

}